package com.mathtrainer.app.ui.screen.wrongquestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.data.entity.WrongQuestionStats
import com.mathtrainer.app.domain.repository.MathTrainerRepository
import com.mathtrainer.app.domain.wrongquestion.WrongQuestionAnalyzer
import com.mathtrainer.app.domain.wrongquestion.TargetedPracticeGenerator
import com.mathtrainer.app.domain.generator.QuestionGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
/**
 * 错题本界面ViewModel
 */
class WrongQuestionsViewModel(
    private val repository: MathTrainerRepository,
    private val questionGenerator: QuestionGenerator = QuestionGenerator()
) : ViewModel() {

    private val analyzer = WrongQuestionAnalyzer()
    private val targetedPracticeGenerator = TargetedPracticeGenerator(questionGenerator, analyzer)
    
    private val _currentFilter = MutableStateFlow(WrongQuestionFilter())
    
    private val _uiState = MutableStateFlow(WrongQuestionsUiState())
    val uiState: StateFlow<WrongQuestionsUiState> = _uiState.asStateFlow()
    
    init {
        loadWrongQuestions()
        loadStats()
    }
    
    private fun loadWrongQuestions() {
        viewModelScope.launch {
            combine(
                repository.getAllWrongQuestions(),
                _currentFilter
            ) { allQuestions, filter ->
                filterQuestions(allQuestions, filter)
            }.collect { filteredQuestions ->
                _uiState.value = _uiState.value.copy(
                    wrongQuestions = filteredQuestions,
                    currentFilter = _currentFilter.value
                )
            }
        }
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            val stats = repository.getWrongQuestionStats()
            _uiState.value = _uiState.value.copy(stats = stats)
        }
    }
    
    private fun filterQuestions(
        questions: List<WrongQuestion>,
        filter: WrongQuestionFilter
    ): List<WrongQuestion> {
        return questions.filter { question ->
            // 按解决状态筛选
            val matchesResolvedFilter = when (filter.showResolved) {
                null -> true
                true -> question.isResolved
                false -> !question.isResolved
            }
            
            // 按运算类型筛选
            val matchesOperationFilter = filter.operationType?.let { 
                question.operationType == it 
            } ?: true
            
            matchesResolvedFilter && matchesOperationFilter
        }.sortedWith(
            compareByDescending<WrongQuestion> { !it.isResolved }
                .thenByDescending { it.wrongCount }
                .thenByDescending { it.lastWrongTime }
        )
    }
    
    /**
     * 应用筛选条件
     */
    fun applyFilter(filter: WrongQuestionFilter) {
        _currentFilter.value = filter
    }
    
    /**
     * 标记错题为已掌握
     */
    fun markAsResolved(wrongQuestion: WrongQuestion) {
        viewModelScope.launch {
            repository.markWrongQuestionAsResolved(wrongQuestion)
            loadStats() // 重新加载统计信息
        }
    }
    
    /**
     * 删除错题
     */
    fun deleteWrongQuestion(wrongQuestion: WrongQuestion) {
        viewModelScope.launch {
            repository.deleteWrongQuestion(wrongQuestion)
            loadStats() // 重新加载统计信息
        }
    }
    
    /**
     * 开始智能错题练习
     */
    fun startWrongQuestionsPractice(onNavigateToPractice: (OperationType) -> Unit) {
        val unresolvedQuestions = _uiState.value.wrongQuestions.filter { !it.isResolved }
        if (unresolvedQuestions.isNotEmpty()) {
            // 分析错题模式
            val analysis = analyzer.analyzeWrongQuestions(unresolvedQuestions)

            // 选择最需要练习的运算类型
            val targetOperation = analysis.mostProblematicOperation ?: run {
                unresolvedQuestions
                    .groupBy { it.operationType }
                    .maxByOrNull { it.value.size }
                    ?.key ?: OperationType.ADDITION
            }

            onNavigateToPractice(targetOperation)
        }
    }

    /**
     * 获取错题分析结果
     */
    fun getWrongQuestionAnalysis(): com.mathtrainer.app.domain.wrongquestion.WrongQuestionAnalysis? {
        val unresolvedQuestions = _uiState.value.wrongQuestions.filter { !it.isResolved }
        return if (unresolvedQuestions.isNotEmpty()) {
            analyzer.analyzeWrongQuestions(unresolvedQuestions)
        } else null
    }
}

/**
 * 错题本界面UI状态
 */
data class WrongQuestionsUiState(
    val wrongQuestions: List<WrongQuestion> = emptyList(),
    val stats: WrongQuestionStats? = null,
    val currentFilter: WrongQuestionFilter = WrongQuestionFilter(),
    val isLoading: Boolean = false
)

/**
 * 错题筛选条件
 */
data class WrongQuestionFilter(
    val showResolved: Boolean? = null, // null=全部, true=已掌握, false=未掌握
    val operationType: OperationType? = null // null=全部运算类型
)

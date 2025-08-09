package com.mathtrainer.app.ui.screen.mixedpractice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.MixedPracticeConfig
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.domain.repository.MathTrainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 混合练习配置ViewModel
 */
class MixedPracticeConfigViewModel(
    private val repository: MathTrainerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MixedPracticeConfigUiState())
    val uiState: StateFlow<MixedPracticeConfigUiState> = _uiState.asStateFlow()
    
    init {
        loadConfig()
    }
    
    private fun loadConfig() {
        viewModelScope.launch {
            repository.getMixedPracticeConfig().collect { config ->
                val defaultConfig = config ?: MixedPracticeConfig()
                _uiState.value = MixedPracticeConfigUiState(
                    selectedOperations = defaultConfig.selectedOperations,
                    operationRanges = defaultConfig.operationRanges,
                    questionCount = defaultConfig.questionCount,
                    difficulty = defaultConfig.difficulty,
                    allowComplexExpressions = defaultConfig.allowComplexExpressions,
                    maxOperatorsInExpression = defaultConfig.maxOperatorsInExpression
                )
            }
        }
    }
    
    /**
     * 切换运算类型选择
     */
    fun toggleOperation(operation: OperationType) {
        val currentOperations = _uiState.value.selectedOperations.toMutableList()
        if (currentOperations.contains(operation)) {
            if (currentOperations.size > 1) { // 至少保留一个
                currentOperations.remove(operation)
            }
        } else {
            currentOperations.add(operation)
        }
        _uiState.value = _uiState.value.copy(selectedOperations = currentOperations)
    }
    
    /**
     * 更新运算类型的数字范围
     */
    fun updateOperationRange(operation: OperationType, range: NumberRange) {
        val currentRanges = _uiState.value.operationRanges.toMutableMap()
        currentRanges[operation] = range
        _uiState.value = _uiState.value.copy(operationRanges = currentRanges)
    }
    
    /**
     * 更新题目数量
     */
    fun updateQuestionCount(count: Int) {
        // 验证题目数量范围
        val validatedCount = when {
            count < 5 -> 5
            count > 200 -> 200
            else -> count
        }
        _uiState.value = _uiState.value.copy(questionCount = validatedCount)
    }
    
    /**
     * 更新难度
     */
    fun updateDifficulty(difficulty: Difficulty) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }
    
    /**
     * 更新是否允许复合表达式
     */
    fun updateAllowComplexExpressions(allow: Boolean) {
        _uiState.value = _uiState.value.copy(allowComplexExpressions = allow)
    }
    
    /**
     * 更新最大运算符数量
     */
    fun updateMaxOperators(count: Int) {
        _uiState.value = _uiState.value.copy(maxOperatorsInExpression = count)
    }
    
    /**
     * 重置为默认配置
     */
    fun resetToDefault() {
        val defaultConfig = MixedPracticeConfig()
        _uiState.value = MixedPracticeConfigUiState(
            selectedOperations = defaultConfig.selectedOperations,
            operationRanges = defaultConfig.operationRanges,
            questionCount = defaultConfig.questionCount,
            difficulty = defaultConfig.difficulty,
            allowComplexExpressions = defaultConfig.allowComplexExpressions,
            maxOperatorsInExpression = defaultConfig.maxOperatorsInExpression
        )
    }
    
    /**
     * 保存配置
     */
    fun saveConfig() {
        viewModelScope.launch {
            val config = MixedPracticeConfig(
                selectedOperations = _uiState.value.selectedOperations,
                operationRanges = _uiState.value.operationRanges,
                questionCount = _uiState.value.questionCount,
                difficulty = _uiState.value.difficulty,
                allowComplexExpressions = _uiState.value.allowComplexExpressions,
                maxOperatorsInExpression = _uiState.value.maxOperatorsInExpression
            )
            repository.saveMixedPracticeConfig(config)
        }
    }
}

/**
 * 混合练习配置UI状态
 */
data class MixedPracticeConfigUiState(
    val selectedOperations: List<OperationType> = OperationType.values().toList(),
    val operationRanges: Map<OperationType, NumberRange> = mapOf(
        OperationType.ADDITION to NumberRange(1, 20),
        OperationType.SUBTRACTION to NumberRange(1, 20),
        OperationType.MULTIPLICATION to NumberRange(1, 10),
        OperationType.DIVISION to NumberRange(1, 10)
    ),
    val questionCount: Int = 20,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val allowComplexExpressions: Boolean = false,
    val maxOperatorsInExpression: Int = 2,
    val isLoading: Boolean = false
)

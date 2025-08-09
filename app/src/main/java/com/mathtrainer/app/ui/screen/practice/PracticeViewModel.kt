package com.mathtrainer.app.ui.screen.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathtrainer.app.data.entity.PracticeRecord
import com.mathtrainer.app.domain.generator.QuestionGenerator
import com.mathtrainer.app.domain.model.Question
import com.mathtrainer.app.domain.repository.MathTrainerRepository
import com.mathtrainer.app.data.entity.OperationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
/**
 * 练习界面ViewModel
 */
class PracticeViewModel(
    private val repository: MathTrainerRepository,
    private val questionGenerator: QuestionGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()
    
    private var startTime: Long = 0
    
    /**
     * 开始练习
     */
    fun startPractice(operationType: OperationType) {
        viewModelScope.launch {
            val settings = repository.getUserSettingsSync()
            val numberRange = repository.getNumberRangeForOperation(operationType)

            val questions = questionGenerator.generateQuestions(
                operationType = operationType,
                difficulty = settings.defaultDifficulty,
                numberRange = numberRange,
                count = settings.questionsPerSession
            )

            _uiState.value = PracticeUiState(
                questions = questions,
                currentQuestionIndex = 0,
                totalQuestions = questions.size,
                showSteps = settings.showStepByStep,
                useCustomKeyboard = settings.useCustomKeyboard
            )

            startTime = System.currentTimeMillis()
        }
    }

    /**
     * 开始混合练习
     */
    fun startMixedPractice() {
        viewModelScope.launch {
            val settings = repository.getUserSettingsSync()
            val mixedConfig = repository.getMixedPracticeConfigSync()

            val questions = questionGenerator.generateMixedQuestions(mixedConfig)

            _uiState.value = PracticeUiState(
                questions = questions,
                currentQuestionIndex = 0,
                totalQuestions = questions.size,
                showSteps = settings.showStepByStep,
                useCustomKeyboard = settings.useCustomKeyboard,
                isMixedPractice = true
            )

            startTime = System.currentTimeMillis()
        }
    }
    
    /**
     * 更新用户答案
     */
    fun updateAnswer(answer: String) {
        // 只允许数字和负号
        val filteredAnswer = answer.filter { it.isDigit() || it == '-' }
        _uiState.value = _uiState.value.copy(userAnswer = filteredAnswer)
    }
    
    /**
     * 提交答案
     */
    fun submitAnswer() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return

        val userAnswer = currentState.userAnswer.toIntOrNull()
        val isCorrect = userAnswer != null && currentQuestion.checkAnswer(userAnswer)

        val feedback = if (isCorrect) {
            "恭喜答对了！答案是 ${currentQuestion.correctAnswer}"
        } else {
            "答错了，正确答案是 ${currentQuestion.correctAnswer}"
        }

        _uiState.value = currentState.copy(
            isAnswered = true,
            feedback = feedback,
            correctAnswers = if (isCorrect) currentState.correctAnswers + 1 else currentState.correctAnswers
        )

        // 记录练习结果
        recordPracticeResult(currentQuestion, userAnswer, isCorrect)

        // 如果答错，添加到错题本
        if (!isCorrect && userAnswer != null) {
            addToWrongQuestions(currentQuestion, userAnswer)
        }
    }
    
    /**
     * 下一题
     */
    fun nextQuestion() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentQuestionIndex + 1
        
        if (nextIndex >= currentState.totalQuestions) {
            // 练习完成
            _uiState.value = currentState.copy(isCompleted = true)
        } else {
            // 下一题
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                userAnswer = "",
                isAnswered = false,
                feedback = null
            )
            startTime = System.currentTimeMillis()
        }
    }
    
    /**
     * 切换显示解题步骤
     */
    fun toggleShowSteps() {
        _uiState.value = _uiState.value.copy(
            showSteps = !_uiState.value.showSteps
        )
    }



    /**
     * 退格删除
     */
    fun backspaceAnswer() {
        val currentAnswer = _uiState.value.userAnswer
        if (currentAnswer.isNotEmpty()) {
            updateAnswer(currentAnswer.dropLast(1))
        }
    }

    /**
     * 清空答案
     */
    fun clearAnswer() {
        updateAnswer("")
    }
    
    private fun recordPracticeResult(
        question: Question,
        userAnswer: Int?,
        isCorrect: Boolean
    ) {
        viewModelScope.launch {
            val timeSpent = System.currentTimeMillis() - startTime
            val record = PracticeRecord(
                operationType = question.operationType,
                operand1 = question.operand1,
                operand2 = question.operand2,
                correctAnswer = question.correctAnswer,
                userAnswer = userAnswer,
                isCorrect = isCorrect,
                difficulty = question.difficulty,
                timeSpentMs = timeSpent,
                timestamp = Date()
            )
            repository.insertPracticeRecord(record)
        }
    }
    
    private fun addToWrongQuestions(question: Question, userAnswer: Int) {
        viewModelScope.launch {
            repository.addOrUpdateWrongQuestion(
                operand1 = question.operand1,
                operand2 = question.operand2,
                operationType = question.operationType,
                correctAnswer = question.correctAnswer,
                userAnswer = userAnswer,
                difficulty = question.difficulty
            )
        }
    }
}

/**
 * 练习界面UI状态
 */
data class PracticeUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val userAnswer: String = "",
    val isAnswered: Boolean = false,
    val feedback: String? = null,
    val correctAnswers: Int = 0,
    val showSteps: Boolean = true,
    val isCompleted: Boolean = false,
    val isMixedPractice: Boolean = false,
    val useCustomKeyboard: Boolean = true
) {
    val currentQuestion: Question? = questions.getOrNull(currentQuestionIndex)
}

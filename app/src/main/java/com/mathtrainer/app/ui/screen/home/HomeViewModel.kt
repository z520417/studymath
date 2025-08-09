package com.mathtrainer.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathtrainer.app.domain.repository.MathTrainerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
/**
 * 主页面ViewModel
 */
class HomeViewModel(
    private val repository: MathTrainerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            combine(
                repository.getTotalRecordsCount(),
                repository.getCorrectRecordsCount(),
                repository.getUnresolvedWrongQuestionsCount()
            ) { total, correct, wrong ->
                HomeUiState(
                    totalQuestions = total,
                    correctAnswers = correct,
                    wrongQuestions = wrong
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}

/**
 * 主页面UI状态
 */
data class HomeUiState(
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val wrongQuestions: Int = 0,
    val isLoading: Boolean = false
)

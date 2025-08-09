package com.mathtrainer.app.ui.screen.teaching

import androidx.lifecycle.ViewModel
import com.mathtrainer.app.data.entity.OperationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 方法教学界面ViewModel
 */
class TeachingViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(TeachingUiState())
    val uiState: StateFlow<TeachingUiState> = _uiState.asStateFlow()
    
    /**
     * 切换展开状态
     */
    fun toggleExpanded(operationType: OperationType) {
        val currentExpanded = _uiState.value.expandedOperation
        _uiState.value = _uiState.value.copy(
            expandedOperation = if (currentExpanded == operationType) null else operationType
        )
    }
}

/**
 * 方法教学界面UI状态
 */
data class TeachingUiState(
    val expandedOperation: OperationType? = null
)

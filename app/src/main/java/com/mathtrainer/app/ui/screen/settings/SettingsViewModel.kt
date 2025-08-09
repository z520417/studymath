package com.mathtrainer.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.UserSettings
import com.mathtrainer.app.domain.repository.MathTrainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
/**
 * 设置界面ViewModel
 */
class SettingsViewModel(
    private val repository: MathTrainerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            repository.getUserSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    settings = settings ?: UserSettings(),
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * 更新默认难度
     */
    fun updateDefaultDifficulty(difficulty: Difficulty) {
        viewModelScope.launch {
            val currentSettings = repository.getUserSettingsSync()
            val updatedSettings = currentSettings.copy(defaultDifficulty = difficulty)
            repository.updateUserSettings(updatedSettings)
        }
    }
    
    /**
     * 更新每次练习题数
     */
    fun updateQuestionsPerSession(count: Int) {
        viewModelScope.launch {
            val currentSettings = repository.getUserSettingsSync()
            val updatedSettings = currentSettings.copy(questionsPerSession = count)
            repository.updateUserSettings(updatedSettings)
        }
    }
    
    /**
     * 更新数字范围
     */
    fun updateNumberRange(operationType: OperationType, range: NumberRange) {
        viewModelScope.launch {
            repository.updateNumberRangeForOperation(operationType, range)
        }
    }
    
    /**
     * 更新是否显示解题步骤
     */
    fun updateShowStepByStep(show: Boolean) {
        viewModelScope.launch {
            val currentSettings = repository.getUserSettingsSync()
            val updatedSettings = currentSettings.copy(showStepByStep = show)
            repository.updateUserSettings(updatedSettings)
        }
    }
    
    /**
     * 更新是否自动收集错题
     */
    fun updateAutoCollectWrongQuestions(autoCollect: Boolean) {
        viewModelScope.launch {
            val currentSettings = repository.getUserSettingsSync()
            val updatedSettings = currentSettings.copy(autoCollectWrongQuestions = autoCollect)
            repository.updateUserSettings(updatedSettings)
        }
    }

    /**
     * 更新是否使用自定义键盘
     */
    fun updateUseCustomKeyboard(useCustom: Boolean) {
        viewModelScope.launch {
            val currentSettings = repository.getUserSettingsSync()
            val updatedSettings = currentSettings.copy(useCustomKeyboard = useCustom)
            repository.updateUserSettings(updatedSettings)
        }
    }
    
    /**
     * 清除练习记录
     */
    fun clearPracticeRecords() {
        viewModelScope.launch {
            val thirtyDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -30)
            }.time
            repository.deletePracticeRecordsBefore(Date()) // 删除所有记录
        }
    }
    
    /**
     * 清除错题记录
     */
    fun clearWrongQuestions() {
        viewModelScope.launch {
            // 这里需要在Repository中添加清除所有错题的方法
            // repository.deleteAllWrongQuestions()
        }
    }
    
    /**
     * 重置设置
     */
    fun resetSettings() {
        viewModelScope.launch {
            val defaultSettings = UserSettings()
            repository.updateUserSettings(defaultSettings)
        }
    }
}

/**
 * 设置界面UI状态
 */
data class SettingsUiState(
    val settings: UserSettings? = null,
    val isLoading: Boolean = true
)

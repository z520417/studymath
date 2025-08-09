package com.mathtrainer.app.domain.repository

import com.mathtrainer.app.data.dao.OperationStats
import com.mathtrainer.app.data.dao.PracticeRecordDao
import com.mathtrainer.app.data.dao.UserSettingsDao
import com.mathtrainer.app.data.dao.WrongQuestionDao
import com.mathtrainer.app.data.dao.MixedPracticeConfigDao
import com.mathtrainer.app.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
/**
 * 数学练习器数据仓库
 */
class MathTrainerRepository(
    private val practiceRecordDao: PracticeRecordDao,
    private val wrongQuestionDao: WrongQuestionDao,
    private val userSettingsDao: UserSettingsDao,
    private val mixedPracticeConfigDao: MixedPracticeConfigDao
) {
    
    // ==================== 练习记录相关 ====================
    
    fun getAllPracticeRecords(): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getAllRecords()
    }
    
    fun getPracticeRecordsByOperation(operationType: OperationType): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getRecordsByOperation(operationType)
    }
    
    fun getTotalRecordsCount(): Flow<Int> {
        return practiceRecordDao.getTotalRecordsCount()
    }
    
    fun getCorrectRecordsCount(): Flow<Int> {
        return practiceRecordDao.getCorrectRecordsCount()
    }
    
    fun getWrongRecordsCount(): Flow<Int> {
        return practiceRecordDao.getWrongRecordsCount()
    }
    
    suspend fun getStatsByOperation(operationType: OperationType): OperationStats {
        return practiceRecordDao.getStatsByOperation(operationType)
    }
    
    suspend fun insertPracticeRecord(record: PracticeRecord): Long {
        return practiceRecordDao.insertRecord(record)
    }
    
    suspend fun deletePracticeRecordsBefore(beforeDate: Date) {
        practiceRecordDao.deleteRecordsBefore(beforeDate)
    }
    
    // ==================== 错题相关 ====================
    
    fun getUnresolvedWrongQuestions(): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getUnresolvedWrongQuestions()
    }
    
    fun getResolvedWrongQuestions(): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getResolvedWrongQuestions()
    }
    
    fun getAllWrongQuestions(): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getAllWrongQuestions()
    }
    
    fun getUnresolvedByOperation(operationType: OperationType): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getUnresolvedByOperation(operationType)
    }
    
    fun getUnresolvedWrongQuestionsCount(): Flow<Int> {
        return wrongQuestionDao.getUnresolvedCount()
    }
    
    suspend fun addOrUpdateWrongQuestion(
        operand1: Int,
        operand2: Int,
        operationType: OperationType,
        correctAnswer: Int,
        userAnswer: Int,
        difficulty: Difficulty
    ) {
        val existing = wrongQuestionDao.findExistingWrongQuestion(operand1, operand2, operationType)
        
        if (existing != null) {
            // 更新现有错题
            val updated = existing.copy(
                wrongCount = existing.wrongCount + 1,
                lastWrongTime = Date(),
                isResolved = false,
                resolvedTime = null
            )
            wrongQuestionDao.updateWrongQuestion(updated)
        } else {
            // 添加新错题
            val newWrongQuestion = WrongQuestion(
                operationType = operationType,
                operand1 = operand1,
                operand2 = operand2,
                correctAnswer = correctAnswer,
                userAnswer = userAnswer,
                difficulty = difficulty,
                wrongCount = 1,
                lastWrongTime = Date(),
                isResolved = false
            )
            wrongQuestionDao.insertWrongQuestion(newWrongQuestion)
        }
    }
    
    suspend fun markWrongQuestionAsResolved(wrongQuestion: WrongQuestion) {
        val resolved = wrongQuestion.copy(
            isResolved = true,
            resolvedTime = Date()
        )
        wrongQuestionDao.updateWrongQuestion(resolved)
    }
    
    suspend fun deleteWrongQuestion(wrongQuestion: WrongQuestion) {
        wrongQuestionDao.deleteWrongQuestion(wrongQuestion)
    }
    
    suspend fun getWrongQuestionStats(): WrongQuestionStats {
        val rawStats = wrongQuestionDao.getWrongQuestionStats()
        val mostWrongOperation = wrongQuestionDao.getMostWrongOperation()?.operationType
        
        return WrongQuestionStats(
            totalWrongQuestions = rawStats.totalWrongQuestions,
            resolvedQuestions = rawStats.resolvedQuestions,
            unresolvedQuestions = rawStats.unresolvedQuestions,
            mostWrongOperation = mostWrongOperation,
            averageWrongCount = rawStats.averageWrongCount
        )
    }
    
    // ==================== 用户设置相关 ====================
    
    fun getUserSettings(): Flow<UserSettings?> {
        return userSettingsDao.getUserSettings()
    }
    
    suspend fun getUserSettingsSync(): UserSettings {
        return userSettingsDao.getUserSettingsSync() ?: UserSettings()
    }
    
    suspend fun updateUserSettings(settings: UserSettings) {
        userSettingsDao.insertOrUpdateSettings(settings)
    }
    
    suspend fun getNumberRangeForOperation(operationType: OperationType): NumberRange {
        val settings = getUserSettingsSync()
        return when (operationType) {
            OperationType.ADDITION -> settings.additionRange
            OperationType.SUBTRACTION -> settings.subtractionRange
            OperationType.MULTIPLICATION -> settings.multiplicationRange
            OperationType.DIVISION -> settings.divisionRange
        }
    }
    
    suspend fun updateNumberRangeForOperation(operationType: OperationType, range: NumberRange) {
        val currentSettings = getUserSettingsSync()
        val updatedSettings = when (operationType) {
            OperationType.ADDITION -> currentSettings.copy(additionRange = range)
            OperationType.SUBTRACTION -> currentSettings.copy(subtractionRange = range)
            OperationType.MULTIPLICATION -> currentSettings.copy(multiplicationRange = range)
            OperationType.DIVISION -> currentSettings.copy(divisionRange = range)
        }
        updateUserSettings(updatedSettings)
    }

    // ==================== 混合练习配置相关方法 ====================

    /**
     * 获取混合练习配置
     */
    fun getMixedPracticeConfig(): Flow<MixedPracticeConfig?> {
        return mixedPracticeConfigDao.getMixedPracticeConfig()
    }

    /**
     * 获取混合练习配置（同步）
     */
    suspend fun getMixedPracticeConfigSync(): MixedPracticeConfig {
        return mixedPracticeConfigDao.getMixedPracticeConfigSync() ?: MixedPracticeConfig()
    }

    /**
     * 保存混合练习配置
     */
    suspend fun saveMixedPracticeConfig(config: MixedPracticeConfig) {
        mixedPracticeConfigDao.insertOrUpdateMixedPracticeConfig(config)
    }

    /**
     * 重置混合练习配置为默认值
     */
    suspend fun resetMixedPracticeConfig() {
        mixedPracticeConfigDao.resetToDefault()
    }
}

package com.mathtrainer.app.data.dao

import androidx.room.*
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.PracticeRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * 练习记录数据访问对象
 */
@Dao
interface PracticeRecordDao {
    
    @Query("SELECT * FROM practice_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<PracticeRecord>>
    
    @Query("SELECT * FROM practice_records WHERE operationType = :operationType ORDER BY timestamp DESC")
    fun getRecordsByOperation(operationType: OperationType): Flow<List<PracticeRecord>>
    
    @Query("SELECT * FROM practice_records WHERE difficulty = :difficulty ORDER BY timestamp DESC")
    fun getRecordsByDifficulty(difficulty: Difficulty): Flow<List<PracticeRecord>>
    
    @Query("SELECT * FROM practice_records WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    fun getRecordsSince(startDate: Date): Flow<List<PracticeRecord>>
    
    @Query("SELECT COUNT(*) FROM practice_records")
    fun getTotalRecordsCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM practice_records WHERE isCorrect = 1")
    fun getCorrectRecordsCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM practice_records WHERE isCorrect = 0")
    fun getWrongRecordsCount(): Flow<Int>
    
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) as correct,
            AVG(timeSpentMs) as avgTime
        FROM practice_records 
        WHERE operationType = :operationType
    """)
    suspend fun getStatsByOperation(operationType: OperationType): OperationStats
    
    @Insert
    suspend fun insertRecord(record: PracticeRecord): Long
    
    @Insert
    suspend fun insertRecords(records: List<PracticeRecord>)
    
    @Delete
    suspend fun deleteRecord(record: PracticeRecord)
    
    @Query("DELETE FROM practice_records WHERE timestamp < :beforeDate")
    suspend fun deleteRecordsBefore(beforeDate: Date)
    
    @Query("DELETE FROM practice_records")
    suspend fun deleteAllRecords()
}

/**
 * 运算统计数据
 */
data class OperationStats(
    val total: Int,
    val correct: Int,
    val avgTime: Double
) {
    val accuracy: Double get() = if (total > 0) correct.toDouble() / total else 0.0
}

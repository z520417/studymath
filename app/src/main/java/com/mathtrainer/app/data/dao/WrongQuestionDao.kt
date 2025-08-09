package com.mathtrainer.app.data.dao

import androidx.room.*
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.data.entity.WrongQuestionStats
import kotlinx.coroutines.flow.Flow

/**
 * 错题记录数据访问对象
 */
@Dao
interface WrongQuestionDao {
    
    @Query("SELECT * FROM wrong_questions WHERE isResolved = 0 ORDER BY wrongCount DESC, lastWrongTime DESC")
    fun getUnresolvedWrongQuestions(): Flow<List<WrongQuestion>>
    
    @Query("SELECT * FROM wrong_questions WHERE isResolved = 1 ORDER BY resolvedTime DESC")
    fun getResolvedWrongQuestions(): Flow<List<WrongQuestion>>
    
    @Query("SELECT * FROM wrong_questions ORDER BY lastWrongTime DESC")
    fun getAllWrongQuestions(): Flow<List<WrongQuestion>>
    
    @Query("SELECT * FROM wrong_questions WHERE operationType = :operationType AND isResolved = 0")
    fun getUnresolvedByOperation(operationType: OperationType): Flow<List<WrongQuestion>>
    
    @Query("""
        SELECT * FROM wrong_questions 
        WHERE operand1 = :operand1 AND operand2 = :operand2 AND operationType = :operationType
        LIMIT 1
    """)
    suspend fun findExistingWrongQuestion(
        operand1: Int, 
        operand2: Int, 
        operationType: OperationType
    ): WrongQuestion?
    
    @Query("SELECT COUNT(*) FROM wrong_questions WHERE isResolved = 0")
    fun getUnresolvedCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM wrong_questions WHERE isResolved = 1")
    fun getResolvedCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM wrong_questions")
    fun getTotalCount(): Flow<Int>
    
    @Query("""
        SELECT 
            COUNT(*) as totalWrongQuestions,
            SUM(CASE WHEN isResolved = 1 THEN 1 ELSE 0 END) as resolvedQuestions,
            SUM(CASE WHEN isResolved = 0 THEN 1 ELSE 0 END) as unresolvedQuestions,
            AVG(wrongCount) as averageWrongCount
        FROM wrong_questions
    """)
    suspend fun getWrongQuestionStats(): WrongQuestionStatsRaw
    
    @Query("""
        SELECT operationType, COUNT(*) as count 
        FROM wrong_questions 
        WHERE isResolved = 0 
        GROUP BY operationType 
        ORDER BY count DESC 
        LIMIT 1
    """)
    suspend fun getMostWrongOperation(): OperationCount?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongQuestion(wrongQuestion: WrongQuestion): Long
    
    @Update
    suspend fun updateWrongQuestion(wrongQuestion: WrongQuestion)
    
    @Delete
    suspend fun deleteWrongQuestion(wrongQuestion: WrongQuestion)
    
    @Query("DELETE FROM wrong_questions WHERE isResolved = 1")
    suspend fun deleteResolvedQuestions()
    
    @Query("DELETE FROM wrong_questions")
    suspend fun deleteAllWrongQuestions()
}

/**
 * 原始统计数据（用于Room查询）
 */
data class WrongQuestionStatsRaw(
    val totalWrongQuestions: Int,
    val resolvedQuestions: Int,
    val unresolvedQuestions: Int,
    val averageWrongCount: Double
)

/**
 * 运算计数数据
 */
data class OperationCount(
    val operationType: OperationType,
    val count: Int
)

package com.mathtrainer.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 错题记录实体
 */
@Entity(tableName = "wrong_questions")
data class WrongQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operationType: OperationType,
    val operand1: Int,
    val operand2: Int,
    val correctAnswer: Int,
    val userAnswer: Int,
    val difficulty: Difficulty,
    val wrongCount: Int = 1, // 错误次数
    val lastWrongTime: Date = Date(),
    val isResolved: Boolean = false, // 是否已经掌握
    val resolvedTime: Date? = null
)

/**
 * 错题统计数据
 */
data class WrongQuestionStats(
    val totalWrongQuestions: Int,
    val resolvedQuestions: Int,
    val unresolvedQuestions: Int,
    val mostWrongOperation: OperationType?,
    val averageWrongCount: Double
)

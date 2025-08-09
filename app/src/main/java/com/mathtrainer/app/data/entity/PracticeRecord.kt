package com.mathtrainer.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 练习记录实体
 */
@Entity(tableName = "practice_records")
data class PracticeRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operationType: OperationType,
    val operand1: Int,
    val operand2: Int,
    val correctAnswer: Int,
    val userAnswer: Int?,
    val isCorrect: Boolean,
    val difficulty: Difficulty,
    val timeSpentMs: Long,
    val timestamp: Date = Date()
)

/**
 * 运算类型枚举
 */
enum class OperationType(val symbol: String, val displayName: String) {
    ADDITION("+", "加法"),
    SUBTRACTION("-", "减法"),
    MULTIPLICATION("×", "乘法"),
    DIVISION("÷", "除法")
}

/**
 * 难度级别枚举
 */
enum class Difficulty(val level: Int, val displayName: String) {
    EASY(1, "简单"),
    MEDIUM(2, "中等"),
    HARD(3, "困难")
}

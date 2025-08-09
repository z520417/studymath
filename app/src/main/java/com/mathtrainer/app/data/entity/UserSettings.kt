package com.mathtrainer.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户设置实体
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1, // 单例设置
    val defaultDifficulty: Difficulty = Difficulty.EASY,
    val additionRange: NumberRange = NumberRange(1, 10),
    val subtractionRange: NumberRange = NumberRange(1, 10),
    val multiplicationRange: NumberRange = NumberRange(1, 10),
    val divisionRange: NumberRange = NumberRange(1, 10),
    val enabledOperations: Set<OperationType> = setOf(OperationType.ADDITION),
    val questionsPerSession: Int = 10,
    val showStepByStep: Boolean = true,
    val autoCollectWrongQuestions: Boolean = true,
    val useCustomKeyboard: Boolean = true
)

/**
 * 数字范围数据类
 */
data class NumberRange(
    val min: Int,
    val max: Int
) {
    fun isValid(): Boolean = min <= max && min >= 0
    
    fun contains(number: Int): Boolean = number in min..max
    
    override fun toString(): String = "$min-$max"
}

/**
 * 预定义的数字范围
 */
object PredefinedRanges {
    val RANGE_1_10 = NumberRange(1, 10)
    val RANGE_1_20 = NumberRange(1, 20)
    val RANGE_1_50 = NumberRange(1, 50)
    val RANGE_1_100 = NumberRange(1, 100)
    
    val allRanges = listOf(RANGE_1_10, RANGE_1_20, RANGE_1_50, RANGE_1_100)
}

package com.mathtrainer.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mathtrainer.app.data.converter.Converters

/**
 * 混合练习配置
 */
@Entity(tableName = "mixed_practice_configs")
@TypeConverters(Converters::class)
data class MixedPracticeConfig(
    @PrimaryKey
    val id: String = "default",
    
    /**
     * 选中的运算类型列表
     */
    val selectedOperations: List<OperationType> = OperationType.values().toList(),
    
    /**
     * 每种运算类型的数字范围配置
     */
    val operationRanges: Map<OperationType, NumberRange> = mapOf(
        OperationType.ADDITION to NumberRange(1, 20),
        OperationType.SUBTRACTION to NumberRange(1, 20),
        OperationType.MULTIPLICATION to NumberRange(1, 10),
        OperationType.DIVISION to NumberRange(1, 10)
    ),
    
    /**
     * 混合练习题目数量
     */
    val questionCount: Int = 20,
    
    /**
     * 是否允许在同一题目中混合多种运算
     */
    val allowComplexExpressions: Boolean = false,
    
    /**
     * 复合表达式的最大运算符数量
     */
    val maxOperatorsInExpression: Int = 2,
    
    /**
     * 默认难度
     */
    val difficulty: Difficulty = Difficulty.MEDIUM
)

/**
 * 混合运算表达式
 */
data class MixedExpression(
    val operands: List<Int>,
    val operators: List<OperationType>,
    val result: Int
) {
    /**
     * 获取表达式字符串
     */
    fun getExpressionString(): String {
        if (operands.size != operators.size + 1) {
            throw IllegalStateException("操作数数量应该比运算符数量多1")
        }
        
        val expression = StringBuilder()
        for (i in operands.indices) {
            expression.append(operands[i])
            if (i < operators.size) {
                expression.append(" ${operators[i].symbol} ")
            }
        }
        return expression.toString()
    }
    
    /**
     * 计算表达式结果（按运算优先级）
     */
    fun calculateResult(): Int {
        if (operands.size == 1) return operands[0]
        
        val mutableOperands = operands.toMutableList()
        val mutableOperators = operators.toMutableList()
        
        // 先处理乘除法
        var i = 0
        while (i < mutableOperators.size) {
            when (mutableOperators[i]) {
                OperationType.MULTIPLICATION -> {
                    val result = mutableOperands[i] * mutableOperands[i + 1]
                    mutableOperands[i] = result
                    mutableOperands.removeAt(i + 1)
                    mutableOperators.removeAt(i)
                }
                OperationType.DIVISION -> {
                    val result = mutableOperands[i] / mutableOperands[i + 1]
                    mutableOperands[i] = result
                    mutableOperands.removeAt(i + 1)
                    mutableOperators.removeAt(i)
                }
                else -> i++
            }
        }
        
        // 再处理加减法
        i = 0
        while (i < mutableOperators.size) {
            when (mutableOperators[i]) {
                OperationType.ADDITION -> {
                    val result = mutableOperands[i] + mutableOperands[i + 1]
                    mutableOperands[i] = result
                    mutableOperands.removeAt(i + 1)
                    mutableOperators.removeAt(i)
                }
                OperationType.SUBTRACTION -> {
                    val result = mutableOperands[i] - mutableOperands[i + 1]
                    mutableOperands[i] = result
                    mutableOperands.removeAt(i + 1)
                    mutableOperators.removeAt(i)
                }
                else -> i++
            }
        }
        
        return mutableOperands[0]
    }
}

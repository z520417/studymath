package com.mathtrainer.app

import com.mathtrainer.app.data.entity.*
import com.mathtrainer.app.domain.model.Question
import org.junit.Test
import org.junit.Assert.*

/**
 * 复合表达式解题步骤测试
 */
class MixedExpressionStepsTest {
    
    @Test
    fun testSimpleAdditionMultiplicationSteps() {
        // 测试 2 + 3 × 4 = 14 的解题步骤
        val expression = MixedExpression(
            operands = listOf(2, 3, 4),
            operators = listOf(OperationType.ADDITION, OperationType.MULTIPLICATION),
            result = 0
        )
        
        val question = Question(
            operand1 = 2,
            operand2 = 3,
            operationType = OperationType.ADDITION,
            correctAnswer = 14,
            difficulty = Difficulty.MEDIUM,
            expressionText = "2 + 3 × 4",
            isMixedExpression = true,
            mixedExpression = expression
        )
        
        val steps = question.getSolutionSteps()
        
        // 验证步骤数量
        assertTrue("应该有多个解题步骤", steps.size >= 5)
        
        // 验证关键步骤
        val stepTitles = steps.map { it.title }
        assertTrue("应包含题目分析", stepTitles.contains("题目分析"))
        assertTrue("应包含运算优先级", stepTitles.contains("运算优先级"))
        assertTrue("应包含最终答案", stepTitles.contains("最终答案"))
        
        // 验证具体内容
        val analysisStep = steps.find { it.title == "题目分析" }
        assertNotNull("题目分析步骤不能为空", analysisStep)
        assertTrue("应包含表达式", analysisStep!!.description.contains("2 + 3 × 4"))
        
        val finalStep = steps.find { it.title == "最终答案" }
        assertNotNull("最终答案步骤不能为空", finalStep)
        assertTrue("应包含正确答案", finalStep!!.description.contains("14"))
    }
    
    @Test
    fun testComplexExpressionSteps() {
        // 测试 10 - 2 × 3 = 4 的解题步骤
        val expression = MixedExpression(
            operands = listOf(10, 2, 3),
            operators = listOf(OperationType.SUBTRACTION, OperationType.MULTIPLICATION),
            result = 0
        )
        
        val question = Question(
            operand1 = 10,
            operand2 = 2,
            operationType = OperationType.SUBTRACTION,
            correctAnswer = 4,
            difficulty = Difficulty.MEDIUM,
            expressionText = "10 - 2 × 3",
            isMixedExpression = true,
            mixedExpression = expression
        )
        
        val steps = question.getSolutionSteps()
        
        // 验证步骤包含乘法优先计算
        val stepDescriptions = steps.map { it.description }
        val hasMultiplicationFirst = stepDescriptions.any { 
            it.contains("先计算乘法") && it.contains("2 × 3 = 6")
        }
        assertTrue("应该先计算乘法", hasMultiplicationFirst)
        
        // 验证后续减法计算
        val hasSubtractionSecond = stepDescriptions.any { 
            it.contains("10 - 6 = 4") || it.contains("计算减法")
        }
        assertTrue("应该有减法计算步骤", hasSubtractionSecond)
    }
    
    @Test
    fun testLeftToRightCalculationSteps() {
        // 测试 8 + 5 - 3 = 10 的解题步骤（同级运算从左到右）
        val expression = MixedExpression(
            operands = listOf(8, 5, 3),
            operators = listOf(OperationType.ADDITION, OperationType.SUBTRACTION),
            result = 0
        )
        
        val question = Question(
            operand1 = 8,
            operand2 = 5,
            operationType = OperationType.ADDITION,
            correctAnswer = 10,
            difficulty = Difficulty.MEDIUM,
            expressionText = "8 + 5 - 3",
            isMixedExpression = true,
            mixedExpression = expression
        )
        
        val steps = question.getSolutionSteps()
        
        // 验证运算顺序说明
        val orderStep = steps.find { it.title == "运算顺序" }
        assertNotNull("应该有运算顺序说明", orderStep)
        assertTrue("应说明从左到右计算", orderStep!!.description.contains("从左到右"))
        
        // 验证计算步骤
        val stepDescriptions = steps.map { it.description }
        val hasFirstAddition = stepDescriptions.any { 
            it.contains("8 + 5 = 13")
        }
        assertTrue("应该先计算加法", hasFirstAddition)
        
        val hasSecondSubtraction = stepDescriptions.any { 
            it.contains("13 - 3 = 10")
        }
        assertTrue("应该再计算减法", hasSecondSubtraction)
    }
    
    @Test
    fun testDivisionWithRemainderSteps() {
        // 测试包含除法的表达式
        val expression = MixedExpression(
            operands = listOf(15, 4, 2),
            operators = listOf(OperationType.DIVISION, OperationType.ADDITION),
            result = 0
        )
        
        val question = Question(
            operand1 = 15,
            operand2 = 4,
            operationType = OperationType.DIVISION,
            correctAnswer = 5, // 15 ÷ 4 = 3, 3 + 2 = 5
            difficulty = Difficulty.MEDIUM,
            expressionText = "15 ÷ 4 + 2",
            isMixedExpression = true,
            mixedExpression = expression
        )
        
        val steps = question.getSolutionSteps()
        
        // 验证除法计算
        val stepDescriptions = steps.map { it.description }
        val hasDivisionStep = stepDescriptions.any { 
            it.contains("15 ÷ 4") && (it.contains("= 3") || it.contains("余"))
        }
        assertTrue("应该有除法计算步骤", hasDivisionStep)
    }
    
    @Test
    fun testNonMixedExpressionSteps() {
        // 测试普通表达式仍然正常工作
        val question = Question(
            operand1 = 25,
            operand2 = 17,
            operationType = OperationType.ADDITION,
            correctAnswer = 42,
            difficulty = Difficulty.MEDIUM,
            isMixedExpression = false
        )
        
        val steps = question.getSolutionSteps()
        
        // 验证普通加法步骤
        assertTrue("应该有解题步骤", steps.isNotEmpty())
        val finalStep = steps.last()
        assertTrue("最终答案应该正确", finalStep.description.contains("42"))
    }
}

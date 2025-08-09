package com.mathtrainer.app

import com.mathtrainer.app.data.entity.*
import com.mathtrainer.app.domain.generator.QuestionGenerator
import org.junit.Test
import org.junit.Assert.*

/**
 * 混合练习功能单元测试
 */
class MixedPracticeTest {
    
    private val questionGenerator = QuestionGenerator()
    
    @Test
    fun testMixedExpressionCalculation() {
        // 测试简单加法表达式
        val addExpression = MixedExpression(
            operands = listOf(5, 3),
            operators = listOf(OperationType.ADDITION),
            result = 0
        )
        assertEquals("表达式字符串应正确", "5 + 3", addExpression.getExpressionString())
        assertEquals("加法计算应正确", 8, addExpression.calculateResult())
        
        // 测试乘法优先级
        val mixedExpression = MixedExpression(
            operands = listOf(2, 3, 4),
            operators = listOf(OperationType.ADDITION, OperationType.MULTIPLICATION),
            result = 0
        )
        assertEquals("混合表达式字符串应正确", "2 + 3 × 4", mixedExpression.getExpressionString())
        assertEquals("应按运算优先级计算", 14, mixedExpression.calculateResult()) // 2 + (3 × 4) = 14
        
        // 测试复杂表达式
        val complexExpression = MixedExpression(
            operands = listOf(10, 2, 3),
            operators = listOf(OperationType.SUBTRACTION, OperationType.MULTIPLICATION),
            result = 0
        )
        assertEquals("复杂表达式应正确计算", 4, complexExpression.calculateResult()) // 10 - (2 × 3) = 4
    }
    
    @Test
    fun testMixedPracticeConfigDefaults() {
        val config = MixedPracticeConfig()
        
        assertEquals("默认应包含所有运算类型", 4, config.selectedOperations.size)
        assertTrue("应包含加法", config.selectedOperations.contains(OperationType.ADDITION))
        assertTrue("应包含减法", config.selectedOperations.contains(OperationType.SUBTRACTION))
        assertTrue("应包含乘法", config.selectedOperations.contains(OperationType.MULTIPLICATION))
        assertTrue("应包含除法", config.selectedOperations.contains(OperationType.DIVISION))
        
        assertEquals("默认题目数量应为20", 20, config.questionCount)
        assertEquals("默认难度应为中等", Difficulty.MEDIUM, config.difficulty)
        assertFalse("默认不允许复合表达式", config.allowComplexExpressions)
        assertEquals("默认最大运算符数量为2", 2, config.maxOperatorsInExpression)
    }
    
    @Test
    fun testMixedQuestionGeneration() {
        val config = MixedPracticeConfig(
            selectedOperations = listOf(OperationType.ADDITION, OperationType.SUBTRACTION),
            questionCount = 5,
            allowComplexExpressions = false
        )
        
        val questions = questionGenerator.generateMixedQuestions(config)
        
        assertEquals("应生成指定数量的题目", 5, questions.size)
        
        questions.forEach { question ->
            assertTrue("题目应使用选定的运算类型", 
                config.selectedOperations.contains(question.operationType))
            assertTrue("答案应为正确值", question.checkAnswer(question.correctAnswer))
        }
    }
    
    @Test
    fun testMixedQuestionGenerationWithComplexExpressions() {
        val config = MixedPracticeConfig(
            selectedOperations = listOf(OperationType.ADDITION, OperationType.MULTIPLICATION),
            questionCount = 10,
            allowComplexExpressions = true,
            maxOperatorsInExpression = 2
        )
        
        val questions = questionGenerator.generateMixedQuestions(config)
        
        assertEquals("应生成指定数量的题目", 10, questions.size)
        
        // 检查是否有复合表达式题目
        val complexQuestions = questions.filter { it.isMixedExpression }
        
        questions.forEach { question ->
            assertTrue("答案应正确", question.checkAnswer(question.correctAnswer))
            if (question.isMixedExpression) {
                assertNotNull("复合表达式应有表达式文本", question.expressionText)
                assertTrue("表达式文本应包含运算符", 
                    question.expressionText!!.contains("+") || 
                    question.expressionText!!.contains("×"))
            }
        }
    }
    
    @Test
    fun testNumberRangeConfiguration() {
        val customRanges = mapOf(
            OperationType.ADDITION to NumberRange(1, 50),
            OperationType.SUBTRACTION to NumberRange(10, 100),
            OperationType.MULTIPLICATION to NumberRange(1, 12),
            OperationType.DIVISION to NumberRange(1, 10)
        )
        
        val config = MixedPracticeConfig(
            selectedOperations = listOf(OperationType.ADDITION),
            operationRanges = customRanges,
            questionCount = 5,
            allowComplexExpressions = false
        )
        
        val questions = questionGenerator.generateMixedQuestions(config)
        
        questions.forEach { question ->
            if (question.operationType == OperationType.ADDITION) {
                val range = customRanges[OperationType.ADDITION]!!
                assertTrue("操作数1应在指定范围内", 
                    question.operand1 in range.min..range.max)
                assertTrue("操作数2应在指定范围内", 
                    question.operand2 in range.min..range.max)
            }
        }
    }
    
    @Test
    fun testDifficultyLevels() {
        listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD).forEach { difficulty ->
            val config = MixedPracticeConfig(
                selectedOperations = listOf(OperationType.ADDITION),
                difficulty = difficulty,
                questionCount = 3
            )
            
            val questions = questionGenerator.generateMixedQuestions(config)
            
            questions.forEach { question ->
                assertEquals("题目难度应匹配配置", difficulty, question.difficulty)
            }
        }
    }
    
    @Test
    fun testSingleOperationTypeSelection() {
        OperationType.values().forEach { operationType ->
            val config = MixedPracticeConfig(
                selectedOperations = listOf(operationType),
                questionCount = 3,
                allowComplexExpressions = false
            )
            
            val questions = questionGenerator.generateMixedQuestions(config)
            
            questions.forEach { question ->
                assertEquals("所有题目应使用指定的运算类型", 
                    operationType, question.operationType)
            }
        }
    }
    
    @Test
    fun testQuestionAnswerValidation() {
        val config = MixedPracticeConfig(
            selectedOperations = listOf(OperationType.ADDITION, OperationType.MULTIPLICATION),
            questionCount = 10,
            allowComplexExpressions = true
        )
        
        val questions = questionGenerator.generateMixedQuestions(config)
        
        questions.forEach { question ->
            // 测试正确答案
            assertTrue("正确答案应通过验证", question.checkAnswer(question.correctAnswer))
            
            // 测试错误答案
            val wrongAnswer = question.correctAnswer + 1
            assertFalse("错误答案应不通过验证", question.checkAnswer(wrongAnswer))
            
            // 测试题目文本生成
            val questionText = question.getQuestionText()
            assertNotNull("题目文本不应为空", questionText)
            assertTrue("题目文本应包含等号", questionText.contains("="))
            assertTrue("题目文本应包含问号", questionText.contains("?"))
        }
    }
}

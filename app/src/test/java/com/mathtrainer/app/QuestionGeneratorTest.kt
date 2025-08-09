package com.mathtrainer.app

import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.domain.generator.QuestionGenerator
import org.junit.Test
import org.junit.Assert.*

/**
 * 题目生成器单元测试
 */
class QuestionGeneratorTest {
    
    private val questionGenerator = QuestionGenerator()
    
    @Test
    fun testAdditionGeneration() {
        val question = questionGenerator.generateQuestion(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 10)
        )
        
        assertEquals(OperationType.ADDITION, question.operationType)
        assertTrue("操作数1应在范围内", question.operand1 in 1..10)
        assertTrue("操作数2应在范围内", question.operand2 in 1..10)
        assertEquals("答案应正确", question.operand1 + question.operand2, question.correctAnswer)
    }
    
    @Test
    fun testSubtractionGeneration() {
        val question = questionGenerator.generateQuestion(
            operationType = OperationType.SUBTRACTION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 20)
        )
        
        assertEquals(OperationType.SUBTRACTION, question.operationType)
        assertTrue("被减数应大于减数", question.operand1 >= question.operand2)
        assertTrue("答案应为正数", question.correctAnswer >= 0)
        assertEquals("答案应正确", question.operand1 - question.operand2, question.correctAnswer)
    }
    
    @Test
    fun testMultiplicationGeneration() {
        val question = questionGenerator.generateQuestion(
            operationType = OperationType.MULTIPLICATION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 9)
        )
        
        assertEquals(OperationType.MULTIPLICATION, question.operationType)
        assertTrue("操作数1应在范围内", question.operand1 in 1..9)
        assertTrue("操作数2应在范围内", question.operand2 in 1..9)
        assertEquals("答案应正确", question.operand1 * question.operand2, question.correctAnswer)
    }
    
    @Test
    fun testDivisionGeneration() {
        val question = questionGenerator.generateQuestion(
            operationType = OperationType.DIVISION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 10)
        )
        
        assertEquals(OperationType.DIVISION, question.operationType)
        assertTrue("除数不应为0", question.operand2 != 0)
        assertTrue("应能整除", question.operand1 % question.operand2 == 0)
        assertEquals("答案应正确", question.operand1 / question.operand2, question.correctAnswer)
    }
    
    @Test
    fun testQuestionAnswerCheck() {
        val question = questionGenerator.generateQuestion(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 5)
        )
        
        assertTrue("正确答案应通过检查", question.checkAnswer(question.correctAnswer))
        assertFalse("错误答案应不通过检查", question.checkAnswer(question.correctAnswer + 1))
    }
    
    @Test
    fun testBatchGeneration() {
        val questions = questionGenerator.generateQuestions(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 10),
            count = 5
        )
        
        assertEquals("应生成指定数量的题目", 5, questions.size)
        questions.forEach { question ->
            assertEquals("所有题目应为加法", OperationType.ADDITION, question.operationType)
            assertTrue("操作数应在范围内", question.operand1 in 1..10)
            assertTrue("操作数应在范围内", question.operand2 in 1..10)
        }
    }
    
    @Test
    fun testDifficultyProgression() {
        val easyQuestion = questionGenerator.generateQuestion(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            numberRange = NumberRange(1, 10)
        )

        val hardQuestion = questionGenerator.generateQuestion(
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.HARD,
            numberRange = NumberRange(10, 50)
        )

        assertEquals(Difficulty.EASY, easyQuestion.difficulty)
        assertEquals(Difficulty.HARD, hardQuestion.difficulty)

        // 验证题目生成成功
        assertTrue("简单题目答案应为正数", easyQuestion.correctAnswer > 0)
        assertTrue("困难题目答案应为正数", hardQuestion.correctAnswer > 0)
    }
}

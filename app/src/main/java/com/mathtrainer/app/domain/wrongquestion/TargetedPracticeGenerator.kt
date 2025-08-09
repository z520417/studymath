package com.mathtrainer.app.domain.wrongquestion

import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.domain.generator.QuestionGenerator
import com.mathtrainer.app.domain.model.Question
import kotlin.random.Random

/**
 * 针对性练习生成器
 * 根据错题分析结果生成针对性的练习题目
 */
class TargetedPracticeGenerator(
    private val questionGenerator: QuestionGenerator,
    private val analyzer: WrongQuestionAnalyzer
) {
    
    /**
     * 生成针对性练习题目
     */
    fun generateTargetedPractice(
        wrongQuestions: List<WrongQuestion>,
        practiceCount: Int = 10
    ): TargetedPracticeSession {
        
        val analysis = analyzer.analyzeWrongQuestions(wrongQuestions)
        val questions = mutableListOf<Question>()
        val strategies = mutableListOf<PracticeStrategy>()
        
        // 根据分析结果生成不同类型的练习题
        analysis.patterns.forEach { pattern ->
            val strategyQuestions = generateQuestionsForPattern(pattern, practiceCount / analysis.patterns.size)
            questions.addAll(strategyQuestions)
            
            strategies.add(
                PracticeStrategy(
                    type = pattern.type,
                    description = pattern.description,
                    recommendation = pattern.recommendation,
                    questionCount = strategyQuestions.size
                )
            )
        }
        
        // 如果没有足够的题目，补充相似题目
        if (questions.size < practiceCount) {
            val additionalQuestions = generateSimilarQuestions(
                wrongQuestions, 
                practiceCount - questions.size
            )
            questions.addAll(additionalQuestions)
        }
        
        // 打乱题目顺序
        questions.shuffle()
        
        return TargetedPracticeSession(
            questions = questions.take(practiceCount),
            strategies = strategies,
            analysis = analysis,
            focusAreas = analysis.patterns.map { it.description }
        )
    }
    
    private fun generateQuestionsForPattern(
        pattern: WrongQuestionPattern,
        count: Int
    ): List<Question> {
        return when (pattern.type) {
            PatternType.OPERATION_SPECIFIC -> {
                generateOperationSpecificQuestions(pattern, count)
            }
            PatternType.NUMBER_RANGE -> {
                generateNumberRangeQuestions(pattern, count)
            }
            PatternType.CALCULATION_ERROR -> {
                generateCalculationErrorQuestions(pattern, count)
            }
            PatternType.CONCEPTUAL_ERROR -> {
                generateConceptualQuestions(pattern, count)
            }
        }
    }
    
    private fun generateOperationSpecificQuestions(
        pattern: WrongQuestionPattern,
        count: Int
    ): List<Question> {
        val operationType = pattern.operationType ?: return emptyList()
        val questions = mutableListOf<Question>()
        
        repeat(count) {
            val question = when (operationType) {
                com.mathtrainer.app.data.entity.OperationType.ADDITION -> {
                    // 重点生成进位加法
                    generateCarryAddition()
                }
                com.mathtrainer.app.data.entity.OperationType.SUBTRACTION -> {
                    // 重点生成借位减法
                    generateBorrowSubtraction()
                }
                com.mathtrainer.app.data.entity.OperationType.MULTIPLICATION -> {
                    // 重点生成乘法口诀相关题目
                    generateMultiplicationTable()
                }
                com.mathtrainer.app.data.entity.OperationType.DIVISION -> {
                    // 重点生成整除题目
                    generateSimpleDivision()
                }
            }
            questions.add(question)
        }
        
        return questions
    }
    
    private fun generateNumberRangeQuestions(
        pattern: WrongQuestionPattern,
        count: Int
    ): List<Question> {
        // 生成适合的数字范围题目，逐步增加难度
        val questions = mutableListOf<Question>()
        val startRange = com.mathtrainer.app.data.entity.NumberRange(1, 10)
        
        repeat(count) {
            val difficulty = com.mathtrainer.app.data.entity.Difficulty.EASY
            val operationType = com.mathtrainer.app.data.entity.OperationType.ADDITION
            
            val question = questionGenerator.generateQuestion(operationType, difficulty, startRange)
            questions.add(question)
        }
        
        return questions
    }
    
    private fun generateCalculationErrorQuestions(
        pattern: WrongQuestionPattern,
        count: Int
    ): List<Question> {
        // 生成容易出现进位/借位错误的题目
        val questions = mutableListOf<Question>()
        
        repeat(count) {
            val question = if (Random.nextBoolean()) {
                generateCarryAddition()
            } else {
                generateBorrowSubtraction()
            }
            questions.add(question)
        }
        
        return questions
    }
    
    private fun generateConceptualQuestions(
        pattern: WrongQuestionPattern,
        count: Int
    ): List<Question> {
        // 生成概念理解相关的题目
        return generateOperationSpecificQuestions(pattern, count)
    }
    
    private fun generateSimilarQuestions(
        wrongQuestions: List<WrongQuestion>,
        count: Int
    ): List<Question> {
        val questions = mutableListOf<Question>()
        
        repeat(count) {
            val randomWrongQuestion = wrongQuestions.randomOrNull()
            if (randomWrongQuestion != null) {
                val originalQuestion = Question(
                    operand1 = randomWrongQuestion.operand1,
                    operand2 = randomWrongQuestion.operand2,
                    operationType = randomWrongQuestion.operationType,
                    correctAnswer = randomWrongQuestion.correctAnswer,
                    difficulty = randomWrongQuestion.difficulty
                )
                
                val similarQuestion = questionGenerator.generateSimilarQuestion(originalQuestion)
                questions.add(similarQuestion)
            }
        }
        
        return questions
    }
    
    private fun generateCarryAddition(): Question {
        // 生成需要进位的加法题目
        val operand1 = Random.nextInt(6, 10)
        val operand2 = Random.nextInt(6, 10)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = com.mathtrainer.app.data.entity.OperationType.ADDITION,
            correctAnswer = operand1 + operand2,
            difficulty = com.mathtrainer.app.data.entity.Difficulty.MEDIUM
        )
    }
    
    private fun generateBorrowSubtraction(): Question {
        // 生成需要借位的减法题目
        val operand1 = Random.nextInt(20, 50)
        val operand2 = Random.nextInt(10, operand1)
        
        // 确保个位数需要借位
        val adjustedOperand2 = if (operand1 % 10 < operand2 % 10) {
            operand2
        } else {
            (operand2 / 10) * 10 + Random.nextInt(operand1 % 10 + 1, 10)
        }
        
        return Question(
            operand1 = operand1,
            operand2 = adjustedOperand2,
            operationType = com.mathtrainer.app.data.entity.OperationType.SUBTRACTION,
            correctAnswer = operand1 - adjustedOperand2,
            difficulty = com.mathtrainer.app.data.entity.Difficulty.MEDIUM
        )
    }
    
    private fun generateMultiplicationTable(): Question {
        // 生成乘法口诀题目
        val operand1 = Random.nextInt(2, 10)
        val operand2 = Random.nextInt(2, 10)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = com.mathtrainer.app.data.entity.OperationType.MULTIPLICATION,
            correctAnswer = operand1 * operand2,
            difficulty = com.mathtrainer.app.data.entity.Difficulty.EASY
        )
    }
    
    private fun generateSimpleDivision(): Question {
        // 生成简单的整除题目
        val divisor = Random.nextInt(2, 10)
        val quotient = Random.nextInt(2, 10)
        val dividend = divisor * quotient
        
        return Question(
            operand1 = dividend,
            operand2 = divisor,
            operationType = com.mathtrainer.app.data.entity.OperationType.DIVISION,
            correctAnswer = quotient,
            difficulty = com.mathtrainer.app.data.entity.Difficulty.EASY
        )
    }
}

/**
 * 针对性练习会话
 */
data class TargetedPracticeSession(
    val questions: List<Question>,
    val strategies: List<PracticeStrategy>,
    val analysis: WrongQuestionAnalysis,
    val focusAreas: List<String>
)

/**
 * 练习策略
 */
data class PracticeStrategy(
    val type: PatternType,
    val description: String,
    val recommendation: String,
    val questionCount: Int
)

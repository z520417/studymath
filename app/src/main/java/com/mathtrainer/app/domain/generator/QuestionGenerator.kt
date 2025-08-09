package com.mathtrainer.app.domain.generator

import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.MixedPracticeConfig
import com.mathtrainer.app.data.entity.MixedExpression
import com.mathtrainer.app.domain.model.Question
import kotlin.random.Random

/**
 * 题目生成器
 */
class QuestionGenerator {
    
    /**
     * 生成单个题目
     */
    fun generateQuestion(
        operationType: OperationType,
        difficulty: Difficulty,
        numberRange: NumberRange
    ): Question {
        return when (operationType) {
            OperationType.ADDITION -> generateAddition(difficulty, numberRange)
            OperationType.SUBTRACTION -> generateSubtraction(difficulty, numberRange)
            OperationType.MULTIPLICATION -> generateMultiplication(difficulty, numberRange)
            OperationType.DIVISION -> generateDivision(difficulty, numberRange)
        }
    }
    
    /**
     * 生成多个题目
     */
    fun generateQuestions(
        operationType: OperationType,
        difficulty: Difficulty,
        numberRange: NumberRange,
        count: Int
    ): List<Question> {
        return (1..count).map {
            generateQuestion(operationType, difficulty, numberRange)
        }
    }

    /**
     * 根据混合练习配置生成题目
     */
    fun generateMixedQuestions(config: MixedPracticeConfig): List<Question> {
        val questions = mutableListOf<Question>()

        repeat(config.questionCount) {
            if (config.allowComplexExpressions && Random.nextFloat() < 0.3) {
                // 30%概率生成复合表达式
                questions.add(generateComplexExpression(config))
            } else {
                // 生成单一运算题目
                val randomOperation = config.selectedOperations.random()
                val range = config.operationRanges[randomOperation] ?: NumberRange(1, 10)
                questions.add(generateQuestion(randomOperation, config.difficulty, range))
            }
        }

        return questions
    }

    /**
     * 生成复合表达式题目
     */
    private fun generateComplexExpression(config: MixedPracticeConfig): Question {
        val operatorCount = Random.nextInt(2, config.maxOperatorsInExpression + 1)
        val operators = mutableListOf<OperationType>()
        val operands = mutableListOf<Int>()

        // 生成运算符
        repeat(operatorCount) {
            operators.add(config.selectedOperations.random())
        }

        // 生成操作数
        repeat(operatorCount + 1) { index ->
            val operationType = if (index < operators.size) operators[index] else operators.last()
            val range = config.operationRanges[operationType] ?: NumberRange(1, 10)
            operands.add(Random.nextInt(range.min, range.max + 1))
        }

        val expression = MixedExpression(operands, operators, 0)
        val result = expression.calculateResult()

        return Question(
            operand1 = operands[0],
            operand2 = if (operands.size > 1) operands[1] else 0,
            operationType = operators[0],
            correctAnswer = result,
            difficulty = config.difficulty,
            expressionText = expression.getExpressionString(),
            isMixedExpression = true,
            mixedExpression = expression
        )
    }
    
    /**
     * 根据错题生成相似题目
     */
    fun generateSimilarQuestion(originalQuestion: Question): Question {
        val range = when (originalQuestion.difficulty) {
            Difficulty.EASY -> NumberRange(1, 10)
            Difficulty.MEDIUM -> NumberRange(1, 50)
            Difficulty.HARD -> NumberRange(1, 100)
        }
        
        return when (originalQuestion.operationType) {
            OperationType.ADDITION -> generateSimilarAddition(originalQuestion, range)
            OperationType.SUBTRACTION -> generateSimilarSubtraction(originalQuestion, range)
            OperationType.MULTIPLICATION -> generateSimilarMultiplication(originalQuestion, range)
            OperationType.DIVISION -> generateSimilarDivision(originalQuestion, range)
        }
    }
    
    private fun generateAddition(difficulty: Difficulty, range: NumberRange): Question {
        val operand1 = Random.nextInt(range.min, range.max + 1)
        val operand2 = when (difficulty) {
            Difficulty.EASY -> {
                val maxOperand2 = maxOf(range.min, minOf(range.max, 10 - operand1))
                if (maxOperand2 >= range.min) {
                    Random.nextInt(range.min, maxOperand2 + 1)
                } else {
                    Random.nextInt(range.min, range.max + 1)
                }
            }
            Difficulty.MEDIUM -> Random.nextInt(range.min, range.max + 1)
            Difficulty.HARD -> Random.nextInt(range.min, range.max + 1)
        }
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.ADDITION,
            correctAnswer = operand1 + operand2,
            difficulty = difficulty
        )
    }
    
    private fun generateSubtraction(difficulty: Difficulty, range: NumberRange): Question {
        val operand1 = Random.nextInt(range.min + 1, range.max + 1)
        val operand2 = when (difficulty) {
            Difficulty.EASY -> Random.nextInt(range.min, minOf(operand1, 10) + 1)
            Difficulty.MEDIUM -> Random.nextInt(range.min, operand1 + 1)
            Difficulty.HARD -> Random.nextInt(range.min, operand1 + 1)
        }
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.SUBTRACTION,
            correctAnswer = operand1 - operand2,
            difficulty = difficulty
        )
    }
    
    private fun generateMultiplication(difficulty: Difficulty, range: NumberRange): Question {
        val maxFactor = when (difficulty) {
            Difficulty.EASY -> minOf(range.max, 5)
            Difficulty.MEDIUM -> minOf(range.max, 10)
            Difficulty.HARD -> range.max
        }
        
        val operand1 = Random.nextInt(range.min, maxFactor + 1)
        val operand2 = Random.nextInt(range.min, maxFactor + 1)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.MULTIPLICATION,
            correctAnswer = operand1 * operand2,
            difficulty = difficulty
        )
    }
    
    private fun generateDivision(difficulty: Difficulty, range: NumberRange): Question {
        val divisor = Random.nextInt(range.min + 1, minOf(range.max, 12) + 1)
        val quotient = when (difficulty) {
            Difficulty.EASY -> Random.nextInt(1, 6)
            Difficulty.MEDIUM -> Random.nextInt(1, 11)
            Difficulty.HARD -> Random.nextInt(1, 21)
        }
        val dividend = divisor * quotient
        
        return Question(
            operand1 = dividend,
            operand2 = divisor,
            operationType = OperationType.DIVISION,
            correctAnswer = quotient,
            difficulty = difficulty
        )
    }
    
    private fun generateSimilarAddition(original: Question, range: NumberRange): Question {
        // 生成相似的加法题目，保持相似的数值范围
        val variance = 3
        val operand1 = (original.operand1 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min, range.max)
        val operand2 = (original.operand2 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min, range.max)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.ADDITION,
            correctAnswer = operand1 + operand2,
            difficulty = original.difficulty
        )
    }
    
    private fun generateSimilarSubtraction(original: Question, range: NumberRange): Question {
        val variance = 3
        val operand1 = (original.operand1 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min + 1, range.max)
        val operand2 = (original.operand2 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min, operand1)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.SUBTRACTION,
            correctAnswer = operand1 - operand2,
            difficulty = original.difficulty
        )
    }
    
    private fun generateSimilarMultiplication(original: Question, range: NumberRange): Question {
        val variance = 2
        val operand1 = (original.operand1 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min, range.max)
        val operand2 = (original.operand2 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min, range.max)
        
        return Question(
            operand1 = operand1,
            operand2 = operand2,
            operationType = OperationType.MULTIPLICATION,
            correctAnswer = operand1 * operand2,
            difficulty = original.difficulty
        )
    }
    
    private fun generateSimilarDivision(original: Question, range: NumberRange): Question {
        val variance = 2
        val divisor = (original.operand2 + Random.nextInt(-variance, variance + 1))
            .coerceIn(range.min + 1, range.max)
        val quotient = (original.correctAnswer + Random.nextInt(-variance, variance + 1))
            .coerceIn(1, 20)
        val dividend = divisor * quotient
        
        return Question(
            operand1 = dividend,
            operand2 = divisor,
            operationType = OperationType.DIVISION,
            correctAnswer = quotient,
            difficulty = original.difficulty
        )
    }
}

package com.mathtrainer.app.domain.wrongquestion

import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.domain.model.Question

/**
 * 错题分析器
 * 分析错题模式，生成针对性练习
 */
class WrongQuestionAnalyzer {
    
    /**
     * 分析错题模式
     */
    fun analyzeWrongQuestions(wrongQuestions: List<WrongQuestion>): WrongQuestionAnalysis {
        if (wrongQuestions.isEmpty()) {
            return WrongQuestionAnalysis()
        }
        
        val patterns = mutableListOf<WrongQuestionPattern>()
        
        // 按运算类型分组分析
        wrongQuestions.groupBy { it.operationType }.forEach { (operationType, questions) ->
            val pattern = analyzeOperationPattern(operationType, questions)
            if (pattern != null) {
                patterns.add(pattern)
            }
        }
        
        // 分析数字范围模式
        val rangePatterns = analyzeNumberRangePatterns(wrongQuestions)
        patterns.addAll(rangePatterns)
        
        // 分析错误类型模式
        val errorPatterns = analyzeErrorTypePatterns(wrongQuestions)
        patterns.addAll(errorPatterns)
        
        return WrongQuestionAnalysis(
            patterns = patterns,
            mostProblematicOperation = findMostProblematicOperation(wrongQuestions),
            averageWrongCount = wrongQuestions.map { it.wrongCount }.average(),
            totalWrongQuestions = wrongQuestions.size
        )
    }
    
    private fun analyzeOperationPattern(
        operationType: OperationType,
        questions: List<WrongQuestion>
    ): WrongQuestionPattern? {
        if (questions.isEmpty()) return null
        
        val totalWrongCount = questions.sumOf { it.wrongCount }
        val avgWrongCount = questions.map { it.wrongCount }.average()
        
        val description = when (operationType) {
            OperationType.ADDITION -> {
                val carryProblems = questions.filter { it.operand1 + it.operand2 > 10 }
                if (carryProblems.size > questions.size * 0.6) {
                    "进位加法是主要难点"
                } else {
                    "基础加法需要加强"
                }
            }
            OperationType.SUBTRACTION -> {
                val borrowProblems = questions.filter { 
                    it.operand1.toString().last().digitToInt() < it.operand2.toString().last().digitToInt()
                }
                if (borrowProblems.size > questions.size * 0.6) {
                    "借位减法是主要难点"
                } else {
                    "基础减法需要加强"
                }
            }
            OperationType.MULTIPLICATION -> {
                val largeNumbers = questions.filter { it.operand1 > 5 || it.operand2 > 5 }
                if (largeNumbers.size > questions.size * 0.6) {
                    "大数乘法需要更多练习"
                } else {
                    "乘法口诀需要熟练掌握"
                }
            }
            OperationType.DIVISION -> {
                "除法运算需要加强练习"
            }
        }
        
        return WrongQuestionPattern(
            type = PatternType.OPERATION_SPECIFIC,
            operationType = operationType,
            description = description,
            severity = when {
                avgWrongCount > 3 -> PatternSeverity.HIGH
                avgWrongCount > 2 -> PatternSeverity.MEDIUM
                else -> PatternSeverity.LOW
            },
            affectedQuestions = questions.size,
            recommendation = generateOperationRecommendation(operationType, avgWrongCount)
        )
    }
    
    private fun analyzeNumberRangePatterns(wrongQuestions: List<WrongQuestion>): List<WrongQuestionPattern> {
        val patterns = mutableListOf<WrongQuestionPattern>()
        
        // 分析大数问题
        val largeNumberQuestions = wrongQuestions.filter { 
            it.operand1 > 20 || it.operand2 > 20 
        }
        
        if (largeNumberQuestions.size > wrongQuestions.size * 0.4) {
            patterns.add(
                WrongQuestionPattern(
                    type = PatternType.NUMBER_RANGE,
                    description = "大数运算是主要困难",
                    severity = PatternSeverity.MEDIUM,
                    affectedQuestions = largeNumberQuestions.size,
                    recommendation = "建议从小数开始，逐步增加数字范围"
                )
            )
        }
        
        return patterns
    }
    
    private fun analyzeErrorTypePatterns(wrongQuestions: List<WrongQuestion>): List<WrongQuestionPattern> {
        val patterns = mutableListOf<WrongQuestionPattern>()
        
        // 分析计算错误模式
        val calculationErrors = wrongQuestions.filter { question ->
            val userAnswer = question.userAnswer
            val correctAnswer = question.correctAnswer
            val difference = kotlin.math.abs(userAnswer - correctAnswer)
            
            // 检查是否是常见的计算错误
            when (question.operationType) {
                OperationType.ADDITION -> {
                    // 检查是否忘记进位
                    val simpleSum = question.operand1 + question.operand2
                    difference == 10 || userAnswer == simpleSum % 10
                }
                OperationType.SUBTRACTION -> {
                    // 检查是否忘记借位
                    difference == 10
                }
                else -> false
            }
        }
        
        if (calculationErrors.size > wrongQuestions.size * 0.3) {
            patterns.add(
                WrongQuestionPattern(
                    type = PatternType.CALCULATION_ERROR,
                    description = "经常出现进位/借位错误",
                    severity = PatternSeverity.HIGH,
                    affectedQuestions = calculationErrors.size,
                    recommendation = "重点练习进位和借位的计算方法"
                )
            )
        }
        
        return patterns
    }
    
    private fun findMostProblematicOperation(wrongQuestions: List<WrongQuestion>): OperationType? {
        return wrongQuestions
            .groupBy { it.operationType }
            .maxByOrNull { (_, questions) -> 
                questions.sumOf { it.wrongCount } 
            }?.key
    }
    
    private fun generateOperationRecommendation(
        operationType: OperationType,
        avgWrongCount: Double
    ): String {
        return when (operationType) {
            OperationType.ADDITION -> {
                if (avgWrongCount > 3) {
                    "建议重新学习加法的基本概念，多练习凑十法"
                } else {
                    "加强进位加法的练习"
                }
            }
            OperationType.SUBTRACTION -> {
                if (avgWrongCount > 3) {
                    "建议重新学习减法的基本概念，多练习借位方法"
                } else {
                    "加强借位减法的练习"
                }
            }
            OperationType.MULTIPLICATION -> {
                if (avgWrongCount > 3) {
                    "建议重新背诵乘法口诀表"
                } else {
                    "加强大数乘法的练习"
                }
            }
            OperationType.DIVISION -> {
                if (avgWrongCount > 3) {
                    "建议重新学习除法的基本概念"
                } else {
                    "加强长除法的练习"
                }
            }
        }
    }
}

/**
 * 错题分析结果
 */
data class WrongQuestionAnalysis(
    val patterns: List<WrongQuestionPattern> = emptyList(),
    val mostProblematicOperation: OperationType? = null,
    val averageWrongCount: Double = 0.0,
    val totalWrongQuestions: Int = 0
)

/**
 * 错题模式
 */
data class WrongQuestionPattern(
    val type: PatternType,
    val operationType: OperationType? = null,
    val description: String,
    val severity: PatternSeverity,
    val affectedQuestions: Int,
    val recommendation: String
)

/**
 * 模式类型
 */
enum class PatternType {
    OPERATION_SPECIFIC,    // 特定运算类型问题
    NUMBER_RANGE,          // 数字范围问题
    CALCULATION_ERROR,     // 计算错误模式
    CONCEPTUAL_ERROR       // 概念理解错误
}

/**
 * 模式严重程度
 */
enum class PatternSeverity {
    LOW,     // 轻微问题
    MEDIUM,  // 中等问题
    HIGH     // 严重问题
}

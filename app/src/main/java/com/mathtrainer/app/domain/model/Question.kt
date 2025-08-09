package com.mathtrainer.app.domain.model

import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.MixedExpression
import com.mathtrainer.app.data.entity.OperationType

/**
 * 数学题目数据模型
 */
data class Question(
    val operand1: Int,
    val operand2: Int,
    val operationType: OperationType,
    val correctAnswer: Int,
    val difficulty: Difficulty,
    val expressionText: String? = null,
    val isMixedExpression: Boolean = false,
    val mixedExpression: MixedExpression? = null
) {
    /**
     * 获取题目显示文本
     */
    fun getQuestionText(): String {
        return if (isMixedExpression && expressionText != null) {
            "$expressionText = ?"
        } else {
            "$operand1 ${operationType.symbol} $operand2 = ?"
        }
    }
    
    /**
     * 检查答案是否正确
     */
    fun checkAnswer(userAnswer: Int): Boolean {
        return userAnswer == correctAnswer
    }
    
    /**
     * 获取解题步骤
     */
    fun getSolutionSteps(): List<SolutionStep> {
        return if (isMixedExpression && mixedExpression != null) {
            getMixedExpressionSteps()
        } else {
            when (operationType) {
                OperationType.ADDITION -> getAdditionSteps()
                OperationType.SUBTRACTION -> getSubtractionSteps()
                OperationType.MULTIPLICATION -> getMultiplicationSteps()
                OperationType.DIVISION -> getDivisionSteps()
            }
        }
    }
    
    private fun getAdditionSteps(): List<SolutionStep> {
        val steps = mutableListOf<SolutionStep>()
        val sum = operand1 + operand2

        steps.add(SolutionStep("题目分析", "计算 $operand1 + $operand2，这是一个${if (sum > 10) "需要进位的" else "简单的"}加法运算"))

        if (sum <= 10) {
            // 简单加法
            steps.add(SolutionStep("方法选择", "由于两数相加不超过10，可以直接相加"))
            steps.add(SolutionStep("计算过程", "从 $operand1 开始，向前数 $operand2 个数：${generateCountingSequence(operand1, operand2)}"))
            steps.add(SolutionStep("验证结果", "检验：$sum - $operand2 = $operand1 ✓"))
        } else if (operand1 < 10 && operand2 < 10) {
            // 凑十法
            val complement = 10 - operand1
            val remaining = operand2 - complement
            steps.add(SolutionStep("方法选择", "使用凑十法：先凑成10，再加剩余的数"))
            steps.add(SolutionStep("分解数字", "将 $operand2 分解为 $complement + $remaining，因为 $operand1 + $complement = 10"))
            steps.add(SolutionStep("第一步计算", "$operand1 + $complement = 10"))
            steps.add(SolutionStep("第二步计算", "10 + $remaining = $sum"))
            steps.add(SolutionStep("验证结果", "检验：$sum - $operand1 = $operand2 ✓"))
        } else {
            // 复杂加法
            steps.add(SolutionStep("方法选择", "使用竖式加法或分位计算"))
            if (operand1 >= 10 || operand2 >= 10) {
                steps.add(SolutionStep("分位分析", "分析各位数字：${analyzeDigits(operand1, operand2, "addition")}"))
            }
            steps.add(SolutionStep("计算结果", "$operand1 + $operand2 = $sum"))
            steps.add(SolutionStep("验证结果", "检验：$sum - $operand1 = $operand2 ✓"))
        }

        steps.add(SolutionStep("最终答案", "因此，$operand1 + $operand2 = $sum"))
        return steps
    }
    
    private fun getSubtractionSteps(): List<SolutionStep> {
        val steps = mutableListOf<SolutionStep>()
        val result = operand1 - operand2

        steps.add(SolutionStep("题目分析", "计算 $operand1 - $operand2，这是一个${if (needsBorrowing(operand1, operand2)) "需要借位的" else "简单的"}减法运算"))

        if (operand1 < 20 && operand2 < 10 && !needsBorrowing(operand1, operand2)) {
            // 简单减法
            steps.add(SolutionStep("方法选择", "由于被减数较小且不需要借位，可以直接相减"))
            steps.add(SolutionStep("计算过程", "从 $operand1 开始，向后退 $operand2 个数：${generateCountingBackSequence(operand1, operand2)}"))
            steps.add(SolutionStep("验证结果", "检验：$result + $operand2 = $operand1 ✓"))
        } else if (needsBorrowing(operand1, operand2)) {
            // 借位减法
            steps.add(SolutionStep("方法选择", "需要使用借位减法"))
            steps.add(SolutionStep("分位分析", analyzeDigits(operand1, operand2, "subtraction")))
            steps.add(SolutionStep("借位过程", describeBorrowingProcess(operand1, operand2)))
            steps.add(SolutionStep("计算结果", "$operand1 - $operand2 = $result"))
            steps.add(SolutionStep("验证结果", "检验：$result + $operand2 = $operand1 ✓"))
        } else {
            // 一般减法
            steps.add(SolutionStep("方法选择", "使用标准减法计算"))
            steps.add(SolutionStep("计算过程", "$operand1 - $operand2 = $result"))
            steps.add(SolutionStep("验证结果", "检验：$result + $operand2 = $operand1 ✓"))
        }

        steps.add(SolutionStep("最终答案", "因此，$operand1 - $operand2 = $result"))
        return steps
    }
    
    private fun getMultiplicationSteps(): List<SolutionStep> {
        val steps = mutableListOf<SolutionStep>()
        val result = operand1 * operand2

        steps.add(SolutionStep("题目分析", "计算 $operand1 × $operand2，这是一个${if (operand1 <= 10 && operand2 <= 10) "基础乘法口诀" else "较大数的乘法"}运算"))

        if (operand1 <= 10 && operand2 <= 10) {
            // 乘法口诀
            val smaller = minOf(operand1, operand2)
            val larger = maxOf(operand1, operand2)
            steps.add(SolutionStep("方法选择", "使用乘法口诀表：${smaller}的乘法口诀"))
            steps.add(SolutionStep("口诀应用", "根据口诀：${smaller} × ${larger} = $result"))
            steps.add(SolutionStep("理解含义", "$operand1 × $operand2 表示 $operand1 个 $operand2 相加，或 $operand2 个 $operand1 相加"))
            steps.add(SolutionStep("验证方法", "可以通过连加验证：${generateAdditionSequence(operand1, operand2)}"))
            steps.add(SolutionStep("验证结果", "检验：$result ÷ $operand1 = $operand2 ✓"))
        } else {
            // 复杂乘法
            steps.add(SolutionStep("方法选择", "使用竖式乘法或分解计算"))
            if (operand1 > 10 || operand2 > 10) {
                steps.add(SolutionStep("数字分解", "分析数字结构：${analyzeMultiplicationStructure(operand1, operand2)}"))
            }
            steps.add(SolutionStep("计算过程", "$operand1 × $operand2 = $result"))
            steps.add(SolutionStep("验证结果", "检验：$result ÷ $operand1 = $operand2 ✓"))
        }

        steps.add(SolutionStep("最终答案", "因此，$operand1 × $operand2 = $result"))
        return steps
    }
    
    private fun getDivisionSteps(): List<SolutionStep> {
        val steps = mutableListOf<SolutionStep>()
        val result = operand1 / operand2
        val remainder = operand1 % operand2

        steps.add(SolutionStep("题目分析", "计算 $operand1 ÷ $operand2，这是一个${if (remainder == 0) "整除" else "有余数的"}除法运算"))

        if (remainder == 0) {
            // 整除
            steps.add(SolutionStep("方法选择", "这是整除运算，可以直接计算商"))
            steps.add(SolutionStep("理解含义", "$operand1 ÷ $operand2 表示将 $operand1 平均分成 $operand2 份，每份是多少"))
            steps.add(SolutionStep("计算过程", "思考：$operand2 × ? = $operand1，答案是 $result"))
            steps.add(SolutionStep("验证结果", "检验：$result × $operand2 = ${result * operand2} = $operand1 ✓"))
        } else {
            // 有余数的除法
            steps.add(SolutionStep("方法选择", "这是有余数的除法，需要计算商和余数"))
            steps.add(SolutionStep("理解含义", "$operand1 ÷ $operand2 表示将 $operand1 分成若干个 $operand2，看能分几组"))
            steps.add(SolutionStep("计算商", "最大的整数商是 $result，因为 $result × $operand2 = ${result * operand2}"))
            steps.add(SolutionStep("计算余数", "余数 = $operand1 - ${result * operand2} = $remainder"))
            steps.add(SolutionStep("验证结果", "检验：$result × $operand2 + $remainder = ${result * operand2} + $remainder = $operand1 ✓"))
        }

        steps.add(SolutionStep("最终答案", "因此，$operand1 ÷ $operand2 = $result${if (remainder > 0) "...余$remainder" else ""}"))
        return steps
    }

    // 辅助方法
    private fun generateCountingSequence(start: Int, count: Int): String {
        val sequence = mutableListOf<Int>()
        for (i in 1..count) {
            sequence.add(start + i)
        }
        return "$start → ${sequence.joinToString(" → ")}"
    }

    private fun generateCountingBackSequence(start: Int, count: Int): String {
        val sequence = mutableListOf<Int>()
        for (i in 1..count) {
            sequence.add(start - i)
        }
        return "$start → ${sequence.joinToString(" → ")}"
    }

    private fun generateAdditionSequence(multiplier: Int, multiplicand: Int): String {
        val terms = mutableListOf<String>()
        repeat(multiplier) {
            terms.add(multiplicand.toString())
        }
        return terms.joinToString(" + ") + " = ${multiplier * multiplicand}"
    }

    private fun analyzeDigits(num1: Int, num2: Int, operation: String): String {
        return when (operation) {
            "addition" -> {
                if (num1 >= 10 && num2 >= 10) {
                    "十位数相加：${num1/10} + ${num2/10}，个位数相加：${num1%10} + ${num2%10}"
                } else {
                    "个位数运算"
                }
            }
            "subtraction" -> {
                if (num1 >= 10) {
                    "被减数 $num1：十位是 ${num1/10}，个位是 ${num1%10}"
                } else {
                    "简单个位数减法"
                }
            }
            else -> "数字分析"
        }
    }

    private fun needsBorrowing(minuend: Int, subtrahend: Int): Boolean {
        if (minuend < 10) return false
        val minuendOnes = minuend % 10
        val subtrahendOnes = subtrahend % 10
        return minuendOnes < subtrahendOnes
    }

    private fun describeBorrowingProcess(minuend: Int, subtrahend: Int): String {
        val minuendTens = minuend / 10
        val minuendOnes = minuend % 10
        val subtrahendOnes = subtrahend % 10

        return if (minuendOnes < subtrahendOnes) {
            "个位 $minuendOnes < $subtrahendOnes，需要向十位借1，变成 ${minuendOnes + 10} - $subtrahendOnes = ${minuendOnes + 10 - subtrahendOnes}"
        } else {
            "不需要借位，直接计算"
        }
    }

    private fun analyzeMultiplicationStructure(num1: Int, num2: Int): String {
        return when {
            num1 > 10 && num2 <= 10 -> "$num1 可以分解为 ${num1/10}×10 + ${num1%10}，然后分别与 $num2 相乘"
            num2 > 10 && num1 <= 10 -> "$num2 可以分解为 ${num2/10}×10 + ${num2%10}，然后分别与 $num1 相乘"
            else -> "使用标准乘法算法"
        }
    }

    /**
     * 获取复合表达式的解题步骤
     */
    private fun getMixedExpressionSteps(): List<SolutionStep> {
        val expression = mixedExpression ?: return emptyList()
        val steps = mutableListOf<SolutionStep>()

        // 题目分析
        steps.add(SolutionStep(
            "题目分析",
            "这是一个复合表达式：${expression.getExpressionString()}，需要按照运算优先级进行计算"
        ))

        // 运算优先级说明
        val hasMultiplyOrDivide = expression.operators.any {
            it == OperationType.MULTIPLICATION || it == OperationType.DIVISION
        }
        val hasAddOrSubtract = expression.operators.any {
            it == OperationType.ADDITION || it == OperationType.SUBTRACTION
        }

        if (hasMultiplyOrDivide && hasAddOrSubtract) {
            steps.add(SolutionStep(
                "运算优先级",
                "根据运算优先级，先计算乘法(×)和除法(÷)，再计算加法(+)和减法(-)"
            ))
        } else {
            steps.add(SolutionStep(
                "运算顺序",
                "按照从左到右的顺序依次计算"
            ))
        }

        // 详细计算过程
        val calculationSteps = getDetailedCalculationSteps(expression)
        steps.addAll(calculationSteps)

        // 最终答案
        steps.add(SolutionStep(
            "最终答案",
            "因此，${expression.getExpressionString()} = $correctAnswer"
        ))

        return steps
    }

    /**
     * 获取详细的计算步骤
     */
    private fun getDetailedCalculationSteps(expression: MixedExpression): List<SolutionStep> {
        val steps = mutableListOf<SolutionStep>()
        val operands = expression.operands.toMutableList()
        val operators = expression.operators.toMutableList()
        var stepNumber = 1

        // 先处理乘除法
        var i = 0
        while (i < operators.size) {
            when (operators[i]) {
                OperationType.MULTIPLICATION -> {
                    val result = operands[i] * operands[i + 1]
                    steps.add(SolutionStep(
                        "第${stepNumber}步",
                        "先计算乘法：${operands[i]} × ${operands[i + 1]} = $result"
                    ))

                    // 更新表达式
                    operands[i] = result
                    operands.removeAt(i + 1)
                    operators.removeAt(i)
                    stepNumber++

                    // 显示当前表达式状态
                    if (operators.isNotEmpty()) {
                        val currentExpression = buildCurrentExpression(operands, operators)
                        steps.add(SolutionStep(
                            "表达式更新",
                            "现在表达式变为：$currentExpression"
                        ))
                    }
                }
                OperationType.DIVISION -> {
                    val result = operands[i] / operands[i + 1]
                    val remainder = operands[i] % operands[i + 1]

                    val divisionText = if (remainder == 0) {
                        "先计算除法：${operands[i]} ÷ ${operands[i + 1]} = $result"
                    } else {
                        "先计算除法：${operands[i]} ÷ ${operands[i + 1]} = $result...余$remainder（取整数部分$result）"
                    }

                    steps.add(SolutionStep("第${stepNumber}步", divisionText))

                    // 更新表达式
                    operands[i] = result
                    operands.removeAt(i + 1)
                    operators.removeAt(i)
                    stepNumber++

                    // 显示当前表达式状态
                    if (operators.isNotEmpty()) {
                        val currentExpression = buildCurrentExpression(operands, operators)
                        steps.add(SolutionStep(
                            "表达式更新",
                            "现在表达式变为：$currentExpression"
                        ))
                    }
                }
                else -> i++
            }
        }

        // 再处理加减法
        i = 0
        while (i < operators.size) {
            when (operators[i]) {
                OperationType.ADDITION -> {
                    val result = operands[i] + operands[i + 1]
                    steps.add(SolutionStep(
                        "第${stepNumber}步",
                        "计算加法：${operands[i]} + ${operands[i + 1]} = $result"
                    ))

                    operands[i] = result
                    operands.removeAt(i + 1)
                    operators.removeAt(i)
                    stepNumber++

                    if (operators.isNotEmpty()) {
                        val currentExpression = buildCurrentExpression(operands, operators)
                        steps.add(SolutionStep(
                            "表达式更新",
                            "现在表达式变为：$currentExpression"
                        ))
                    }
                }
                OperationType.SUBTRACTION -> {
                    val result = operands[i] - operands[i + 1]
                    steps.add(SolutionStep(
                        "第${stepNumber}步",
                        "计算减法：${operands[i]} - ${operands[i + 1]} = $result"
                    ))

                    operands[i] = result
                    operands.removeAt(i + 1)
                    operators.removeAt(i)
                    stepNumber++

                    if (operators.isNotEmpty()) {
                        val currentExpression = buildCurrentExpression(operands, operators)
                        steps.add(SolutionStep(
                            "表达式更新",
                            "现在表达式变为：$currentExpression"
                        ))
                    }
                }
                else -> i++
            }
        }

        return steps
    }

    /**
     * 构建当前表达式字符串
     */
    private fun buildCurrentExpression(operands: List<Int>, operators: List<OperationType>): String {
        val expression = StringBuilder()
        for (i in operands.indices) {
            expression.append(operands[i])
            if (i < operators.size) {
                expression.append(" ${operators[i].symbol} ")
            }
        }
        return expression.toString()
    }
}

/**
 * 解题步骤
 */
data class SolutionStep(
    val title: String,
    val description: String
)

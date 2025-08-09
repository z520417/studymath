package com.mathtrainer.app.ui.screen.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.domain.model.Question
import com.mathtrainer.app.domain.model.SolutionStep
import com.mathtrainer.app.ui.ViewModelFactory
import com.mathtrainer.app.ui.component.FloatingKeyboard

/**
 * 练习界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    operationType: OperationType?,
    isMixedPractice: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as MathTrainerApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(operationType, isMixedPractice) {
        if (isMixedPractice) {
            viewModel.startMixedPractice()
        } else {
            operationType?.let { viewModel.startPractice(it) }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isMixedPractice) "混合练习" else "${operationType?.displayName}练习")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.currentQuestion != null) {
                        IconButton(
                            onClick = { viewModel.toggleShowSteps() }
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "显示解题步骤",
                                tint = if (uiState.showSteps) MaterialTheme.colorScheme.primary 
                                      else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            uiState.currentQuestion?.let { question ->
                // 现在统一使用垂直布局，自定义键盘改为悬浮显示
                VerticalPracticeLayout(
                    uiState = uiState,
                    question = question,
                    viewModel = viewModel
                )
            }

            // 悬浮数字键盘 - 底部显示，紧凑设计
            FloatingKeyboard(
                visible = uiState.useCustomKeyboard && !uiState.isAnswered,
                onNumberClick = { number ->
                    viewModel.updateAnswer(uiState.userAnswer + number)
                },
                onBackspaceClick = viewModel::backspaceAnswer,
                onClearClick = viewModel::clearAnswer,
                onConfirmClick = viewModel::submitAnswer,
                enabled = !uiState.isAnswered
            )

            // 练习完成
            if (uiState.isCompleted) {
                CompletionCard(
                    totalQuestions = uiState.totalQuestions,
                    correctAnswers = uiState.correctAnswers,
                    onRestart = {
                        if (isMixedPractice) {
                            viewModel.startMixedPractice()
                        } else {
                            operationType?.let { viewModel.startPractice(it) }
                        }
                    },
                    onFinish = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(
    currentQuestion: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "第 $currentQuestion 题 / 共 $totalQuestions 题",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "正确: $correctAnswers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            LinearProgressIndicator(
                progress = (currentQuestion - 1).toFloat() / totalQuestions,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuestionCard(
    question: String,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    feedback: String?,
    isAnswered: Boolean,
    useCustomKeyboard: Boolean = true,
    onSubmitAnswer: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            // 答案输入区域（包含输入框和反馈）
            AnswerInputWithFeedback(
                userAnswer = userAnswer,
                onAnswerChange = onAnswerChange,
                feedback = feedback,
                isAnswered = isAnswered,
                useCustomKeyboard = useCustomKeyboard,
                modifier = Modifier.width(200.dp),
                onSubmitAnswer = onSubmitAnswer
            )



        }
    }
}

@Composable
private fun SolutionStepsCard(steps: List<SolutionStep>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "解题步骤",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}. ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isAnswered: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    isLastQuestion: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isAnswered) {
            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier.weight(1f)
            ) {
                Text("提交答案")
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isLastQuestion) "完成练习" else "下一题")
            }
        }
    }
}

@Composable
private fun CompletionCard(
    totalQuestions: Int,
    correctAnswers: Int,
    onRestart: () -> Unit,
    onFinish: () -> Unit
) {
    val accuracy = if (totalQuestions > 0) {
        (correctAnswers.toFloat() / totalQuestions * 100).toInt()
    } else 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "练习完成！",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "正确率: $accuracy%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "答对 $correctAnswers 题，共 $totalQuestions 题",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("再练一次")
                }
                
                Button(
                    onClick = onFinish,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("返回主页")
                }
            }
        }
    }
}

@Composable
private fun CompactSolutionStepsCard(steps: List<SolutionStep>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📚 详细解题步骤",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )

            // 可滚动的解题步骤内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // 限制最大高度
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "步骤 ${index + 1}：${step.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 滚动提示
            if (steps.size > 3) {
                Text(
                    text = "💡 向上滑动查看更多步骤",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EnhancedSolutionStepsCard(steps: List<SolutionStep>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📚 详细解题步骤",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )

            // 解题步骤内容 - 不限制高度，让外层滚动处理
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "步骤 ${index + 1}：${step.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// 水平布局 - 简化版本（现在只用于特殊情况）
@Composable
private fun HorizontalPracticeLayout(
    uiState: PracticeUiState,
    question: Question,
    viewModel: PracticeViewModel
) {


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 进度指示器 - 顶部全宽显示
        if (uiState.totalQuestions > 0) {
            ProgressCard(
                modifier = Modifier.padding(12.dp),
                currentQuestion = uiState.currentQuestionIndex + 1,
                totalQuestions = uiState.totalQuestions,
                correctAnswers = uiState.correctAnswers
            )
        }

        // 主要内容区域 - 简化为单列布局
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 题目卡片 - 不显示内嵌键盘
            HorizontalQuestionCard(
                question = question.getQuestionText(),
                userAnswer = uiState.userAnswer,
                onAnswerChange = viewModel::updateAnswer,
                feedback = uiState.feedback,
                isAnswered = uiState.isAnswered,
                onSubmitAnswer = viewModel::submitAnswer
            )

            // 解题步骤 - 只在回答后显示
            if (uiState.showSteps && uiState.isAnswered && question.getSolutionSteps().isNotEmpty()) {
                EnhancedSolutionStepsCard(steps = question.getSolutionSteps())
            }
        }

        // 底部操作按钮 - 全宽显示
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            ActionButtons(
                modifier = Modifier.padding(16.dp),
                isAnswered = uiState.isAnswered,
                canSubmit = uiState.userAnswer.isNotBlank(),
                onSubmit = viewModel::submitAnswer,
                onNext = viewModel::nextQuestion,
                isLastQuestion = uiState.currentQuestionIndex >= uiState.totalQuestions - 1
            )
        }
    }
}

// 垂直布局 - 传统布局（系统键盘）
@Composable
private fun VerticalPracticeLayout(
    uiState: PracticeUiState,
    question: Question,
    viewModel: PracticeViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 主要内容区域 - 可滚动
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(bottom = 80.dp) // 为底部按钮预留空间
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 进度指示器
            if (uiState.totalQuestions > 0) {
                ProgressCard(
                    currentQuestion = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.totalQuestions,
                    correctAnswers = uiState.correctAnswers
                )
            }

            // 题目显示
            QuestionCard(
                question = question.getQuestionText(),
                userAnswer = uiState.userAnswer,
                onAnswerChange = viewModel::updateAnswer,
                feedback = uiState.feedback,
                isAnswered = uiState.isAnswered,
                useCustomKeyboard = uiState.useCustomKeyboard,
                onSubmitAnswer = viewModel::submitAnswer
            )

            // 解题步骤 - 只在回答后显示
            if (uiState.showSteps && uiState.isAnswered && question.getSolutionSteps().isNotEmpty()) {
                EnhancedSolutionStepsCard(steps = question.getSolutionSteps())
            }
        }

        // 固定在底部的操作按钮
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            ActionButtons(
                modifier = Modifier.padding(16.dp),
                isAnswered = uiState.isAnswered,
                canSubmit = uiState.userAnswer.isNotBlank(),
                onSubmit = viewModel::submitAnswer,
                onNext = viewModel::nextQuestion,
                isLastQuestion = uiState.currentQuestionIndex >= uiState.totalQuestions - 1
            )
        }
    }
}

// 水平布局专用的题目卡片 - 不包含内嵌键盘
@Composable
private fun HorizontalQuestionCard(
    question: String,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    feedback: String?,
    isAnswered: Boolean,
    onSubmitAnswer: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // 答案输入区域（包含输入框和反馈）
            AnswerInputWithFeedback(
                userAnswer = userAnswer,
                onAnswerChange = onAnswerChange,
                feedback = feedback,
                isAnswered = isAnswered,
                useCustomKeyboard = true, // 水平布局总是使用自定义键盘
                modifier = Modifier.fillMaxWidth(),
                isHorizontalLayout = true,
                onSubmitAnswer = onSubmitAnswer
            )
        }
    }
}

/**
 * 带有视觉反馈的答案输入组件
 */
@Composable
private fun AnswerInputWithFeedback(
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    feedback: String?,
    isAnswered: Boolean,
    useCustomKeyboard: Boolean,
    modifier: Modifier = Modifier,
    isHorizontalLayout: Boolean = false,
    onSubmitAnswer: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 答案输入框
        if (useCustomKeyboard) {
            // 使用自定义键盘时，显示只读的答案框
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { },
                label = { Text("你的答案") },
                enabled = false,
                modifier = modifier,
                singleLine = true,
                readOnly = true,
                textStyle = if (isHorizontalLayout) {
                    MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                } else {
                    MaterialTheme.typography.titleLarge
                }
            )
        } else {
            // 使用系统键盘
            OutlinedTextField(
                value = userAnswer,
                onValueChange = onAnswerChange,
                label = { Text("你的答案") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // 系统键盘确认时自动提交答案
                        if (userAnswer.isNotBlank() && !isAnswered) {
                            onSubmitAnswer()
                        }
                    }
                ),
                enabled = !isAnswered,
                modifier = modifier,
                singleLine = true
            )
        }

        // 答案反馈（只在回答后显示）
        if (isAnswered && feedback != null) {
            AnswerFeedback(
                feedback = feedback,
                isHorizontalLayout = isHorizontalLayout
            )
        }
    }
}

/**
 * 答案反馈组件
 */
@Composable
private fun AnswerFeedback(
    feedback: String,
    isHorizontalLayout: Boolean = false
) {
    // 简化的反馈检测逻辑 - 只检查是否包含"恭喜答对了"
    val isCorrect = feedback.contains("恭喜答对了")

    val correctColor = Color(0xFF4CAF50)
    val incorrectColor = Color(0xFFF44336)

    // 增强的动画效果
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(400)) + scaleIn(
            animationSpec = tween(400),
            initialScale = 0.7f
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isCorrect) {
                    correctColor.copy(alpha = 0.1f)
                } else {
                    incorrectColor.copy(alpha = 0.1f)
                }
            ),
            border = BorderStroke(
                width = 2.dp,
                color = if (isCorrect) correctColor else incorrectColor
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 增强的反馈图标
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (isCorrect) "正确" else "错误",
                    tint = if (isCorrect) correctColor else incorrectColor,
                    modifier = Modifier.size(28.dp)
                )

                // 反馈文字
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCorrect) correctColor else incorrectColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = if (isHorizontalLayout) TextAlign.Center else TextAlign.Start,
                    modifier = if (isHorizontalLayout) Modifier.fillMaxWidth() else Modifier.weight(1f)
                )

                // 额外的视觉元素
                if (isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = correctColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

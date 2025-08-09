package com.mathtrainer.app.ui.screen.mixedpractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.ui.ViewModelFactory

/**
 * 混合练习配置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MixedPracticeConfigScreen(
    onNavigateBack: () -> Unit,
    onStartPractice: () -> Unit,
    viewModel: MixedPracticeConfigViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as MathTrainerApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("混合练习配置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetToDefault() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("重置默认")
                    }
                    
                    Button(
                        onClick = {
                            viewModel.saveConfig()
                            onStartPractice()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.selectedOperations.isNotEmpty()
                    ) {
                        Text("开始练习")
                    }
                }
            }
        }
    ) { paddingValues ->
        ResponsiveConfigLayout(
            paddingValues = paddingValues,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@Composable
private fun OperationSelectionCard(
    selectedOperations: List<OperationType>,
    onOperationToggle: (OperationType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "选择运算类型",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "至少选择一种运算类型",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OperationType.values().forEach { operation ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedOperations.contains(operation),
                        onCheckedChange = { onOperationToggle(operation) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${operation.displayName} (${operation.symbol})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberRangeConfigCard(
    operationRanges: Map<OperationType, NumberRange>,
    selectedOperations: List<OperationType>,
    onRangeChange: (OperationType, NumberRange) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "数字范围设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            selectedOperations.forEach { operation ->
                val currentRange = operationRanges[operation] ?: NumberRange(1, 10)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = operation.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var minText by remember(currentRange) { mutableStateOf(currentRange.min.toString()) }
                        var maxText by remember(currentRange) { mutableStateOf(currentRange.max.toString()) }
                        
                        OutlinedTextField(
                            value = minText,
                            onValueChange = { 
                                minText = it.filter { char -> char.isDigit() }
                                val min = minText.toIntOrNull()
                                if (min != null && min <= currentRange.max) {
                                    onRangeChange(operation, NumberRange(min, currentRange.max))
                                }
                            },
                            label = { Text("最小值") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        Text("-")
                        
                        OutlinedTextField(
                            value = maxText,
                            onValueChange = { 
                                maxText = it.filter { char -> char.isDigit() }
                                val max = maxText.toIntOrNull()
                                if (max != null && max >= currentRange.min) {
                                    onRangeChange(operation, NumberRange(currentRange.min, max))
                                }
                            },
                            label = { Text("最大值") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeSettingsCard(
    questionCount: Int,
    difficulty: Difficulty,
    allowComplexExpressions: Boolean,
    maxOperatorsInExpression: Int,
    onQuestionCountChange: (Int) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onAllowComplexExpressionsChange: (Boolean) -> Unit,
    onMaxOperatorsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "练习设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 题目数量设置
            QuestionCountSelector(
                questionCount = questionCount,
                onQuestionCountChange = onQuestionCountChange
            )
            
            // 难度选择
            Column {
                Text("难度级别:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.values().forEach { diff ->
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { onDifficultyChange(diff) },
                            label = { Text(diff.displayName) }
                        )
                    }
                }
            }
            
            // 复合表达式设置
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "允许复合表达式",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "如: 5 + 3 × 2",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = allowComplexExpressions,
                    onCheckedChange = onAllowComplexExpressionsChange
                )
            }
            
            if (allowComplexExpressions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("最大运算符数量: $maxOperatorsInExpression")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 3, 4).forEach { count ->
                            FilterChip(
                                selected = maxOperatorsInExpression == count,
                                onClick = { onMaxOperatorsChange(count) },
                                label = { Text(count.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 题目数量选择器组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionCountSelector(
    questionCount: Int,
    onQuestionCountChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var customInputVisible by remember { mutableStateOf(false) }
    var customCountText by remember { mutableStateOf("") }

    // 预设的题目数量选项
    val presetCounts = listOf(10, 20, 30, 50, 100)
    val isCustomCount = !presetCounts.contains(questionCount)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "题目数量",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        // 下拉选择框
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (isCustomCount) "$questionCount (自定义)" else "$questionCount 题",
                onValueChange = { },
                readOnly = true,
                label = { Text("选择题目数量") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // 预设选项
                presetCounts.forEach { count ->
                    DropdownMenuItem(
                        text = { Text("$count 题") },
                        onClick = {
                            onQuestionCountChange(count)
                            expanded = false
                            customInputVisible = false
                        },
                        leadingIcon = if (questionCount == count) {
                            { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                        } else null
                    )
                }

                Divider()

                // 自定义选项
                DropdownMenuItem(
                    text = { Text("自定义数量...") },
                    onClick = {
                        expanded = false
                        customInputVisible = true
                        customCountText = if (isCustomCount) questionCount.toString() else ""
                    }
                )
            }
        }

        // 自定义输入框
        if (customInputVisible) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customCountText,
                    onValueChange = { newValue ->
                        // 只允许数字输入
                        val filteredValue = newValue.filter { it.isDigit() }
                        if (filteredValue.length <= 3) { // 限制最多3位数
                            customCountText = filteredValue
                        }
                    },
                    label = { Text("自定义题目数量") },
                    placeholder = { Text("请输入5-200之间的数字") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { Text("范围：5-200题") },
                    isError = customCountText.isNotEmpty() &&
                             (customCountText.toIntOrNull()?.let { it < 5 || it > 200 } == true),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            customInputVisible = false
                            customCountText = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            val count = customCountText.toIntOrNull()
                            if (count != null && count in 5..200) {
                                onQuestionCountChange(count)
                                customInputVisible = false
                                customCountText = ""
                            }
                        },
                        enabled = customCountText.toIntOrNull()?.let { it in 5..200 } == true,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("确认")
                    }
                }
            }
        }

        // 快速选择按钮（在非自定义输入状态下显示）
        if (!customInputVisible) {
            Text(
                text = "快速选择：",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presetCounts.forEach { count ->
                    FilterChip(
                        selected = questionCount == count,
                        onClick = {
                            onQuestionCountChange(count)
                            customInputVisible = false
                        },
                        label = { Text("$count 题") }
                    )
                }
            }
        }
    }
}

/**
 * 响应式配置布局
 */
@Composable
private fun ResponsiveConfigLayout(
    paddingValues: PaddingValues,
    uiState: MixedPracticeConfigUiState,
    viewModel: MixedPracticeConfigViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp

    if (isLandscape && screenWidth > 600.dp) {
        // 横屏且屏幕较宽时使用两列布局
        LandscapeConfigLayout(
            paddingValues = paddingValues,
            uiState = uiState,
            viewModel = viewModel
        )
    } else {
        // 竖屏或较窄屏幕时使用单列布局
        PortraitConfigLayout(
            paddingValues = paddingValues,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

/**
 * 竖屏布局（单列）
 */
@Composable
private fun PortraitConfigLayout(
    paddingValues: PaddingValues,
    uiState: MixedPracticeConfigUiState,
    viewModel: MixedPracticeConfigViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OperationSelectionCard(
                selectedOperations = uiState.selectedOperations,
                onOperationToggle = viewModel::toggleOperation
            )
        }

        item {
            NumberRangeConfigCard(
                operationRanges = uiState.operationRanges,
                selectedOperations = uiState.selectedOperations,
                onRangeChange = viewModel::updateOperationRange
            )
        }

        item {
            PracticeSettingsCard(
                questionCount = uiState.questionCount,
                difficulty = uiState.difficulty,
                allowComplexExpressions = uiState.allowComplexExpressions,
                maxOperatorsInExpression = uiState.maxOperatorsInExpression,
                onQuestionCountChange = viewModel::updateQuestionCount,
                onDifficultyChange = viewModel::updateDifficulty,
                onAllowComplexExpressionsChange = viewModel::updateAllowComplexExpressions,
                onMaxOperatorsChange = viewModel::updateMaxOperators
            )
        }
    }
}

/**
 * 横屏布局（两列）
 */
@Composable
private fun LandscapeConfigLayout(
    paddingValues: PaddingValues,
    uiState: MixedPracticeConfigUiState,
    viewModel: MixedPracticeConfigViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左列
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OperationSelectionCard(
                selectedOperations = uiState.selectedOperations,
                onOperationToggle = viewModel::toggleOperation
            )

            PracticeSettingsCard(
                questionCount = uiState.questionCount,
                difficulty = uiState.difficulty,
                allowComplexExpressions = uiState.allowComplexExpressions,
                maxOperatorsInExpression = uiState.maxOperatorsInExpression,
                onQuestionCountChange = viewModel::updateQuestionCount,
                onDifficultyChange = viewModel::updateDifficulty,
                onAllowComplexExpressionsChange = viewModel::updateAllowComplexExpressions,
                onMaxOperatorsChange = viewModel::updateMaxOperators
            )
        }

        // 右列
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberRangeConfigCard(
                operationRanges = uiState.operationRanges,
                selectedOperations = uiState.selectedOperations,
                onRangeChange = viewModel::updateOperationRange
            )
        }
    }
}

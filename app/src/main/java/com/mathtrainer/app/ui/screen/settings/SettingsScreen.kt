package com.mathtrainer.app.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.PredefinedRanges
import com.mathtrainer.app.ui.ViewModelFactory

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as MathTrainerApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(title = "练习设置") {
                    // 默认难度设置
                    DifficultySettingCard(
                        currentDifficulty = uiState.settings?.defaultDifficulty ?: Difficulty.EASY,
                        onDifficultyChange = viewModel::updateDefaultDifficulty
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 每次练习题数
                    QuestionsPerSessionCard(
                        questionsPerSession = uiState.settings?.questionsPerSession ?: 10,
                        onQuestionsPerSessionChange = viewModel::updateQuestionsPerSession
                    )
                }
            }
            
            item {
                SettingsSection(title = "数字范围设置") {
                    OperationType.values().forEach { operationType ->
                        val range = when (operationType) {
                            OperationType.ADDITION -> uiState.settings?.additionRange
                            OperationType.SUBTRACTION -> uiState.settings?.subtractionRange
                            OperationType.MULTIPLICATION -> uiState.settings?.multiplicationRange
                            OperationType.DIVISION -> uiState.settings?.divisionRange
                        } ?: NumberRange(1, 10)
                        
                        NumberRangeCard(
                            operationType = operationType,
                            currentRange = range,
                            onRangeChange = { newRange ->
                                viewModel.updateNumberRange(operationType, newRange)
                            }
                        )
                        
                        if (operationType != OperationType.values().last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            
            item {
                SettingsSection(title = "功能设置") {
                    // 显示解题步骤
                    SwitchSettingCard(
                        title = "显示解题步骤",
                        description = "在练习时显示详细的解题步骤",
                        checked = uiState.settings?.showStepByStep ?: true,
                        onCheckedChange = viewModel::updateShowStepByStep
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 自动收集错题
                    SwitchSettingCard(
                        title = "自动收集错题",
                        description = "答错的题目自动添加到错题本",
                        checked = uiState.settings?.autoCollectWrongQuestions ?: true,
                        onCheckedChange = viewModel::updateAutoCollectWrongQuestions
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 使用自定义键盘
                    SwitchSettingCard(
                        title = "使用自定义键盘",
                        description = "在练习时使用应用内置的数字键盘",
                        checked = uiState.settings?.useCustomKeyboard ?: true,
                        onCheckedChange = viewModel::updateUseCustomKeyboard
                    )
                }
            }
            
            item {
                SettingsSection(title = "数据管理") {
                    DataManagementCard(
                        onClearPracticeRecords = viewModel::clearPracticeRecords,
                        onClearWrongQuestions = viewModel::clearWrongQuestions,
                        onResetSettings = viewModel::resetSettings
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun DifficultySettingCard(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "默认难度",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.values().forEach { difficulty ->
                    FilterChip(
                        selected = currentDifficulty == difficulty,
                        onClick = { onDifficultyChange(difficulty) },
                        label = { Text(difficulty.displayName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionsPerSessionCard(
    questionsPerSession: Int,
    onQuestionsPerSessionChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(questionsPerSession.toString()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "每次练习题数",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "当前设置：$questionsPerSession 题",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            TextButton(onClick = { showDialog = true }) {
                Text("修改")
            }
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("设置练习题数") },
            text = {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it.filter { char -> char.isDigit() } },
                    label = { Text("题数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newValue = inputValue.toIntOrNull()
                        if (newValue != null && newValue in 5..50) {
                            onQuestionsPerSessionChange(newValue)
                            showDialog = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun NumberRangeCard(
    operationType: OperationType,
    currentRange: NumberRange,
    onRangeChange: (NumberRange) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${operationType.displayName}数字范围",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "当前范围：${currentRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            TextButton(onClick = { showDialog = true }) {
                Text("修改")
            }
        }
    }
    
    if (showDialog) {
        NumberRangeDialog(
            operationType = operationType,
            currentRange = currentRange,
            onRangeChange = onRangeChange,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun NumberRangeDialog(
    operationType: OperationType,
    currentRange: NumberRange,
    onRangeChange: (NumberRange) -> Unit,
    onDismiss: () -> Unit
) {
    var minValue by remember { mutableStateOf(currentRange.min.toString()) }
    var maxValue by remember { mutableStateOf(currentRange.max.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置${operationType.displayName}数字范围") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("预设范围：")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PredefinedRanges.allRanges.forEach { range ->
                        FilterChip(
                            selected = currentRange == range,
                            onClick = {
                                minValue = range.min.toString()
                                maxValue = range.max.toString()
                            },
                            label = { Text(range.toString()) }
                        )
                    }
                }
                
                Text("自定义范围：")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minValue,
                        onValueChange = { minValue = it.filter { char -> char.isDigit() } },
                        label = { Text("最小值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Text("-")
                    OutlinedTextField(
                        value = maxValue,
                        onValueChange = { maxValue = it.filter { char -> char.isDigit() } },
                        label = { Text("最大值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val min = minValue.toIntOrNull()
                    val max = maxValue.toIntOrNull()
                    if (min != null && max != null && min <= max && min >= 1) {
                        onRangeChange(NumberRange(min, max))
                        onDismiss()
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun SwitchSettingCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun DataManagementCard(
    onClearPracticeRecords: () -> Unit,
    onClearWrongQuestions: () -> Unit,
    onResetSettings: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf<String?>(null) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "数据管理",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            OutlinedButton(
                onClick = { showConfirmDialog = "practice" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("清除练习记录")
            }
            
            OutlinedButton(
                onClick = { showConfirmDialog = "wrong" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("清除错题记录")
            }
            
            OutlinedButton(
                onClick = { showConfirmDialog = "settings" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重置所有设置")
            }
        }
    }
    
    showConfirmDialog?.let { action ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = { Text("确认操作") },
            text = {
                Text(
                    when (action) {
                        "practice" -> "确定要清除所有练习记录吗？此操作不可撤销。"
                        "wrong" -> "确定要清除所有错题记录吗？此操作不可撤销。"
                        "settings" -> "确定要重置所有设置为默认值吗？"
                        else -> ""
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            "practice" -> onClearPracticeRecords()
                            "wrong" -> onClearWrongQuestions()
                            "settings" -> onResetSettings()
                        }
                        showConfirmDialog = null
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

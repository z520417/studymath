package com.mathtrainer.app.ui.screen.wrongquestions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * 错题本界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongQuestionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPractice: (OperationType) -> Unit,
    viewModel: WrongQuestionsViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as MathTrainerApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("错题本") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "筛选")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.wrongQuestions.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.startWrongQuestionsPractice(onNavigateToPractice) }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "开始错题练习")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 统计信息
            uiState.stats?.let { stats ->
                StatsCard(stats = stats)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 错题列表
            if (uiState.wrongQuestions.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.wrongQuestions) { wrongQuestion ->
                        WrongQuestionCard(
                            wrongQuestion = wrongQuestion,
                            onMarkResolved = { viewModel.markAsResolved(it) },
                            onDelete = { viewModel.deleteWrongQuestion(it) }
                        )
                    }
                }
            }
        }
    }
    
    // 筛选对话框
    if (showFilterDialog) {
        FilterDialog(
            currentFilter = uiState.currentFilter,
            onFilterChange = { filter ->
                viewModel.applyFilter(filter)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun StatsCard(stats: com.mathtrainer.app.data.entity.WrongQuestionStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "错题统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "总错题", value = stats.totalWrongQuestions.toString())
                StatItem(label = "已掌握", value = stats.resolvedQuestions.toString())
                StatItem(label = "未掌握", value = stats.unresolvedQuestions.toString())
            }
            
            stats.mostWrongOperation?.let { operation ->
                Text(
                    text = "最容易出错: ${operation.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun WrongQuestionCard(
    wrongQuestion: WrongQuestion,
    onMarkResolved: (WrongQuestion) -> Unit,
    onDelete: (WrongQuestion) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (wrongQuestion.isResolved) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${wrongQuestion.operand1} ${wrongQuestion.operationType.symbol} ${wrongQuestion.operand2} = ?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "正确答案: ${wrongQuestion.correctAnswer}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "你的答案: ${wrongQuestion.userAnswer}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF44336)
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "错误次数: ${wrongQuestion.wrongCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "最后错误: ${dateFormat.format(wrongQuestion.lastWrongTime)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!wrongQuestion.isResolved) {
                        IconButton(
                            onClick = { onMarkResolved(wrongQuestion) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "标记为已掌握",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "已掌握",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onDelete(wrongQuestion) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = Color(0xFFF44336)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "太棒了！\n目前没有错题",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "继续练习保持好成绩吧！",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FilterDialog(
    currentFilter: WrongQuestionFilter,
    onFilterChange: (WrongQuestionFilter) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("筛选错题") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("显示状态:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentFilter.showResolved == null,
                        onClick = { onFilterChange(currentFilter.copy(showResolved = null)) },
                        label = { Text("全部") }
                    )
                    FilterChip(
                        selected = currentFilter.showResolved == false,
                        onClick = { onFilterChange(currentFilter.copy(showResolved = false)) },
                        label = { Text("未掌握") }
                    )
                    FilterChip(
                        selected = currentFilter.showResolved == true,
                        onClick = { onFilterChange(currentFilter.copy(showResolved = true)) },
                        label = { Text("已掌握") }
                    )
                }
                
                Text("运算类型:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentFilter.operationType == null,
                        onClick = { onFilterChange(currentFilter.copy(operationType = null)) },
                        label = { Text("全部") }
                    )
                }
                
                val operations = OperationType.values().toList()
                operations.chunked(2).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { operation ->
                            FilterChip(
                                selected = currentFilter.operationType == operation,
                                onClick = { onFilterChange(currentFilter.copy(operationType = operation)) },
                                label = { Text(operation.displayName) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

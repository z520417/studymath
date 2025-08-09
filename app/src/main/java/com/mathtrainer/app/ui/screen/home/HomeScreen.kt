package com.mathtrainer.app.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.R
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.ui.ViewModelFactory

/**
 * 主页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPractice: (OperationType) -> Unit,
    onNavigateToWrongQuestions: () -> Unit,
    onNavigateToTeaching: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMixedPractice: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as MathTrainerApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 统计信息卡片
            StatsCard(
                totalQuestions = uiState.totalQuestions,
                correctAnswers = uiState.correctAnswers,
                wrongQuestions = uiState.wrongQuestions
            )
            
            // 功能按钮网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 四种运算练习
                items(OperationType.values()) { operationType ->
                    OperationCard(
                        operationType = operationType,
                        onClick = { onNavigateToPractice(operationType) }
                    )
                }
                
                // 其他功能
                item {
                    FeatureCard(
                        title = "混合练习",
                        icon = Icons.Default.Shuffle,
                        onClick = onNavigateToMixedPractice
                    )
                }

                item {
                    FeatureCard(
                        title = stringResource(R.string.nav_wrong_questions),
                        icon = Icons.Default.ErrorOutline,
                        badgeCount = uiState.wrongQuestions,
                        onClick = onNavigateToWrongQuestions
                    )
                }

                item {
                    FeatureCard(
                        title = stringResource(R.string.nav_teaching),
                        icon = Icons.Default.School,
                        onClick = onNavigateToTeaching
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    totalQuestions: Int,
    correctAnswers: Int,
    wrongQuestions: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "练习统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = stringResource(R.string.total_questions),
                    value = totalQuestions.toString()
                )
                StatItem(
                    label = stringResource(R.string.correct_answers),
                    value = correctAnswers.toString()
                )
                StatItem(
                    label = stringResource(R.string.wrong_questions_count),
                    value = wrongQuestions.toString()
                )
            }
            
            if (totalQuestions > 0) {
                val accuracy = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
                LinearProgressIndicator(
                    progress = correctAnswers.toFloat() / totalQuestions,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${stringResource(R.string.accuracy_rate)}: $accuracy%",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
private fun OperationCard(
    operationType: OperationType,
    onClick: () -> Unit
) {
    val icon = when (operationType) {
        OperationType.ADDITION -> Icons.Default.Add
        OperationType.SUBTRACTION -> Icons.Default.Remove
        OperationType.MULTIPLICATION -> Icons.Default.Close
        OperationType.DIVISION -> Icons.Default.Percent
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = operationType.displayName,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = operationType.displayName,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    icon: ImageVector,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
            
            if (badgeCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(text = badgeCount.toString())
                }
            }
        }
    }
}

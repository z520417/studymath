package com.mathtrainer.app.ui.screen.teaching

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathtrainer.app.data.entity.OperationType

/**
 * 方法教学界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TeachingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计算方法教学") },
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
            items(OperationType.values()) { operationType ->
                OperationTeachingCard(
                    operationType = operationType,
                    isExpanded = uiState.expandedOperation == operationType,
                    onToggleExpanded = { viewModel.toggleExpanded(operationType) }
                )
            }
        }
    }
}

@Composable
private fun OperationTeachingCard(
    operationType: OperationType,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val icon = when (operationType) {
        OperationType.ADDITION -> Icons.Default.Add
        OperationType.SUBTRACTION -> Icons.Default.Remove
        OperationType.MULTIPLICATION -> Icons.Default.Close
        OperationType.DIVISION -> Icons.Default.Percent
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = operationType.displayName,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${operationType.displayName}方法",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开"
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                when (operationType) {
                    OperationType.ADDITION -> AdditionTeaching()
                    OperationType.SUBTRACTION -> SubtractionTeaching()
                    OperationType.MULTIPLICATION -> MultiplicationTeaching()
                    OperationType.DIVISION -> DivisionTeaching()
                }
            }
        }
    }
}

@Composable
private fun AdditionTeaching() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TeachingMethod(
            title = "1. 直接相加法",
            description = "适用于简单的加法运算",
            example = "3 + 5 = 8",
            steps = listOf(
                "直接将两个数相加",
                "得出结果"
            )
        )
        
        TeachingMethod(
            title = "2. 凑十法",
            description = "适用于和超过10的加法",
            example = "8 + 7 = 15",
            steps = listOf(
                "将7分解为2和5",
                "8 + 2 = 10",
                "10 + 5 = 15"
            )
        )
        
        TeachingMethod(
            title = "3. 进位加法",
            description = "适用于多位数加法",
            example = "47 + 28 = 75",
            steps = listOf(
                "个位：7 + 8 = 15，写5进1",
                "十位：4 + 2 + 1 = 7",
                "结果：75"
            )
        )
    }
}

@Composable
private fun SubtractionTeaching() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TeachingMethod(
            title = "1. 直接相减法",
            description = "适用于简单的减法运算",
            example = "8 - 3 = 5",
            steps = listOf(
                "直接将被减数减去减数",
                "得出结果"
            )
        )
        
        TeachingMethod(
            title = "2. 借位减法",
            description = "适用于需要借位的减法",
            example = "52 - 27 = 25",
            steps = listOf(
                "个位：2 < 7，向十位借1",
                "个位：12 - 7 = 5",
                "十位：4 - 2 = 2",
                "结果：25"
            )
        )
        
        TeachingMethod(
            title = "3. 分解减法",
            description = "将减数分解为更容易计算的数",
            example = "15 - 8 = 7",
            steps = listOf(
                "将8分解为5和3",
                "15 - 5 = 10",
                "10 - 3 = 7"
            )
        )
    }
}

@Composable
private fun MultiplicationTeaching() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TeachingMethod(
            title = "1. 乘法口诀",
            description = "适用于1-9的乘法",
            example = "6 × 7 = 42",
            steps = listOf(
                "背诵乘法口诀表",
                "六七四十二"
            )
        )
        
        TeachingMethod(
            title = "2. 连加法",
            description = "将乘法转换为连续加法",
            example = "4 × 3 = 12",
            steps = listOf(
                "4 × 3 = 4 + 4 + 4",
                "= 12"
            )
        )
        
        TeachingMethod(
            title = "3. 竖式乘法",
            description = "适用于多位数乘法",
            example = "23 × 15 = 345",
            steps = listOf(
                "23 × 5 = 115",
                "23 × 10 = 230",
                "115 + 230 = 345"
            )
        )
    }
}

@Composable
private fun DivisionTeaching() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TeachingMethod(
            title = "1. 整除法",
            description = "适用于能整除的除法",
            example = "24 ÷ 6 = 4",
            steps = listOf(
                "想：6乘以几等于24？",
                "6 × 4 = 24",
                "所以 24 ÷ 6 = 4"
            )
        )
        
        TeachingMethod(
            title = "2. 长除法",
            description = "适用于多位数除法",
            example = "84 ÷ 4 = 21",
            steps = listOf(
                "8 ÷ 4 = 2",
                "4 ÷ 4 = 1",
                "结果：21"
            )
        )
        
        TeachingMethod(
            title = "3. 估算法",
            description = "通过估算简化计算",
            example = "96 ÷ 8 = 12",
            steps = listOf(
                "想：8 × 10 = 80",
                "96 - 80 = 16",
                "16 ÷ 8 = 2",
                "所以：10 + 2 = 12"
            )
        )
    }
}

@Composable
private fun TeachingMethod(
    title: String,
    description: String,
    example: String,
    steps: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "例题：$example",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "解题步骤：",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            steps.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

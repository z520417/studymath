package com.mathtrainer.app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 自定义数字键盘组件 - 重新设计确保按钮高度不被压缩
 */
@Composable
fun CustomKeyboard(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(), // 使用wrapContentHeight而不是固定高度
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 第一行: 1, 2, 3
            KeyboardRow {
                KeyboardNumberButton("1", enabled) { onNumberClick("1") }
                KeyboardNumberButton("2", enabled) { onNumberClick("2") }
                KeyboardNumberButton("3", enabled) { onNumberClick("3") }
            }

            // 第二行: 4, 5, 6
            KeyboardRow {
                KeyboardNumberButton("4", enabled) { onNumberClick("4") }
                KeyboardNumberButton("5", enabled) { onNumberClick("5") }
                KeyboardNumberButton("6", enabled) { onNumberClick("6") }
            }

            // 第三行: 7, 8, 9
            KeyboardRow {
                KeyboardNumberButton("7", enabled) { onNumberClick("7") }
                KeyboardNumberButton("8", enabled) { onNumberClick("8") }
                KeyboardNumberButton("9", enabled) { onNumberClick("9") }
            }

            // 第四行: -, 0, 退格
            KeyboardRow {
                KeyboardNumberButton("-", enabled) { onNumberClick("-") }
                KeyboardNumberButton("0", enabled) { onNumberClick("0") }
                KeyboardFunctionButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    text = "退格",
                    enabled = enabled,
                    onClick = onBackspaceClick
                )
            }

            // 第五行: 清空, 确认
            KeyboardRow {
                KeyboardActionButton(
                    icon = Icons.Default.Clear,
                    text = "清空",
                    enabled = enabled,
                    onClick = onClearClick,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
                KeyboardActionButton(
                    icon = Icons.Default.Done,
                    text = "确认",
                    enabled = enabled,
                    onClick = onConfirmClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// 键盘行容器
@Composable
private fun KeyboardRow(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), // 固定行高度
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

// 数字按钮
@Composable
private fun RowScope.KeyboardNumberButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(), // 填满行高度
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// 功能按钮（退格）
@Composable
private fun RowScope.KeyboardFunctionButton(
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(), // 填满行高度
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 操作按钮（清空、确认）
@Composable
private fun RowScope.KeyboardActionButton(
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(), // 填满行高度
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



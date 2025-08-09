package com.mathtrainer.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * 悬浮数字键盘组件 - 底部显示，紧凑设计
 */
@Composable
fun FloatingKeyboard(
    visible: Boolean,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (visible) {
        Popup(
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                excludeFromSystemGesture = true,
                clippingEnabled = false
            )
        ) {
            // 直接显示键盘，无背景遮罩
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                // 紧凑的键盘主体
                CompactKeyboardContent(
                    onNumberClick = onNumberClick,
                    onBackspaceClick = onBackspaceClick,
                    onClearClick = onClearClick,
                    onConfirmClick = onConfirmClick,
                    enabled = enabled
                )
            }
        }
    }
}

/**
 * 紧凑的键盘内容 - 更小尺寸，不遮盖题目
 */
@Composable
private fun CompactKeyboardContent(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onConfirmClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 第一行: 1, 2, 3
        CompactKeyboardRow {
            CompactNumberButton("1", enabled) { onNumberClick("1") }
            CompactNumberButton("2", enabled) { onNumberClick("2") }
            CompactNumberButton("3", enabled) { onNumberClick("3") }
        }

        // 第二行: 4, 5, 6
        CompactKeyboardRow {
            CompactNumberButton("4", enabled) { onNumberClick("4") }
            CompactNumberButton("5", enabled) { onNumberClick("5") }
            CompactNumberButton("6", enabled) { onNumberClick("6") }
        }

        // 第三行: 7, 8, 9
        CompactKeyboardRow {
            CompactNumberButton("7", enabled) { onNumberClick("7") }
            CompactNumberButton("8", enabled) { onNumberClick("8") }
            CompactNumberButton("9", enabled) { onNumberClick("9") }
        }

        // 第四行: -, 0, 退格
        CompactKeyboardRow {
            CompactNumberButton("-", enabled) { onNumberClick("-") }
            CompactNumberButton("0", enabled) { onNumberClick("0") }
            CompactFunctionButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                enabled = enabled,
                onClick = onBackspaceClick
            )
        }

        // 第五行: 清空, 确认
        CompactKeyboardRow {
            CompactActionButton(
                icon = Icons.Default.Clear,
                text = "清空",
                enabled = enabled,
                onClick = onClearClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
            CompactActionButton(
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

// 紧凑键盘行容器
@Composable
private fun CompactKeyboardRow(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp), // 更小的按钮高度
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

// 紧凑数字按钮
@Composable
private fun RowScope.CompactNumberButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// 紧凑功能按钮（退格）
@Composable
private fun RowScope.CompactFunctionButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "退格",
            modifier = Modifier.size(16.dp)
        )
    }
}

// 紧凑操作按钮（清空、确认）
@Composable
private fun RowScope.CompactActionButton(
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
            .fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

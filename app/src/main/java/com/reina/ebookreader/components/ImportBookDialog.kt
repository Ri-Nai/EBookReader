package com.reina.ebookreader.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImportBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var bookTitle by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入书籍") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("请输入书籍标题")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bookTitle,
                    onValueChange = { 
                        bookTitle = it
                        isError = it.isBlank()
                    },
                    label = { Text("书籍标题") },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("标题不能为空")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bookTitle.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(bookTitle)
                    }
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 

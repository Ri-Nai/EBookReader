package com.reina.ebookreader.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reina.ebookreader.viewmodel.BookViewModel

private const val TAG = "BookDetailScreen"

@Composable
fun BookDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookViewModel = viewModel()
) {
    // 解码 bookId，以防它是 URL 编码的
    val decodedBookId = try {
        Uri.decode(bookId)
    } catch (e: Exception) {
        Log.e(TAG, "解码 bookId 失败: $bookId", e)
        bookId
    }
    
    Log.d(TAG, "接收到的 bookId: $bookId, 解码后: $decodedBookId")
    
    // 从 ViewModel 获取书籍
    val book = viewModel.books.find { it.id == decodedBookId }
    val content by viewModel.currentBookContent.collectAsState()
    var bookNotFound by remember { mutableStateOf(false) }
    
    // 记录所有可用的书籍
    LaunchedEffect(Unit) {
        Log.d(TAG, "可用书籍列表 (${viewModel.books.size} 本):")
        viewModel.books.forEachIndexed { index, b ->
            Log.d(TAG, "$index: ${b.title}, ID: ${b.id}, 路径: ${b.filePath}")
        }
    }
    
    // 加载书籍内容
    LaunchedEffect(decodedBookId) {
        Log.d(TAG, "加载书籍内容，ID: $decodedBookId")
        if (book != null) {
            Log.d(TAG, "找到书籍: ${book.title}, ID: ${book.id}, 路径: ${book.filePath}")
            viewModel.loadBookContent(book)
        } else {
            Log.e(TAG, "未找到书籍，ID: $decodedBookId")
            bookNotFound = true
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 标题行带返回按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
                    )
                }
                
                Text(
                    text = book?.title ?: "未知书籍",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            // 内容区域
            if (bookNotFound) {
                // 显示书籍未找到的错误信息
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "找不到书籍",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "无法找到ID为 $decodedBookId 的书籍。可能是书籍已被删除或ID无效。",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onBackClick) {
                        Text("返回书籍列表")
                    }
                }
            } else {
                when {
                    content == "加载中..." -> {
                        // 显示加载指示器
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "正在加载内容...")
                        }
                    }
                    content.startsWith("加载失败") || content.startsWith("读取错误") || content.startsWith("无法读取") -> {
                        // 显示错误信息
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(onClick = onBackClick) {
                                Text("返回书籍列表")
                            }
                        }
                    }
                    else -> {
                        // 显示书籍内容
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}

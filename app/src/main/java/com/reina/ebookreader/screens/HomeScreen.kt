package com.reina.ebookreader.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reina.ebookreader.components.BookCard
import com.reina.ebookreader.components.ImportBookDialog
import com.reina.ebookreader.model.Book
import com.reina.ebookreader.navigation.AppDestinations
import com.reina.ebookreader.viewmodel.BookViewModel
import android.util.Log

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val books = viewModel.books
    val importStatus by viewModel.importStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showImportDialog by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    
    // 记录当前书籍列表
    LaunchedEffect(books.size) {
        Log.d("HomeScreen", "当前书籍列表 (${books.size} 本):")
        books.forEachIndexed { index, book ->
            Log.d("HomeScreen", "$index: ${book.title}, ID: ${book.id}, 路径: ${book.filePath}")
        }
    }
    
    // 文件选择器
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            showImportDialog = true
        }
    }
    
    // 处理导入状态
    LaunchedEffect(importStatus) {
        when (importStatus) {
            is BookViewModel.ImportStatus.Success -> {
                snackbarHostState.showSnackbar("书籍导入成功")
                viewModel.resetImportStatus()
            }
            is BookViewModel.ImportStatus.Error -> {
                snackbarHostState.showSnackbar("书籍导入失败: ${(importStatus as BookViewModel.ImportStatus.Error).message}")
                viewModel.resetImportStatus()
            }
            else -> {}
        }
    }
    
    // 导入对话框
    if (showImportDialog) {
        ImportBookDialog(
            onDismiss = { 
                showImportDialog = false
                selectedUri = null
            },
            onConfirm = { title ->
                selectedUri?.let { uri ->
                    viewModel.importBook(uri, title)
                }
                showImportDialog = false
                selectedUri = null
            }
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { filePickerLauncher.launch("text/plain") }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "导入书籍"
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (books.isEmpty()) {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "没有书籍",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "点击右下角的 + 按钮导入 TXT 文件",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // 书籍列表
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(books) { book ->
                        BookCard(
                            book = book,
                            onClick = { 
                                Log.d("HomeScreen", "导航到书籍详情页: ${book.title}, ID: ${book.id}")
                                navController.navigate(AppDestinations.bookDetailRoute(book.id)) 
                            },
                            onDeleteClick = { viewModel.removeBook(book) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

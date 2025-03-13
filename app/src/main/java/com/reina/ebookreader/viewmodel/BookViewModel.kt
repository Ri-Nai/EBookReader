package com.reina.ebookreader.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reina.ebookreader.model.Book
import com.reina.ebookreader.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class BookViewModel(application: Application) : AndroidViewModel(application) {
    
    private val TAG = "BookViewModel"
    private val repository = BookRepository(application.applicationContext)
    
    // 书籍列表
    val books = repository.books
    
    // 当前选中的书籍内容
    private val _currentBookContent = MutableStateFlow<String>("加载中...")
    val currentBookContent: StateFlow<String> = _currentBookContent.asStateFlow()
    
    // 导入状态
    private val _importStatus = MutableStateFlow<ImportStatus>(ImportStatus.Idle)
    val importStatus: StateFlow<ImportStatus> = _importStatus.asStateFlow()
    
    // 导入书籍
    fun importBook(uri: Uri, title: String) {
        viewModelScope.launch {
            _importStatus.value = ImportStatus.Loading
            Log.d(TAG, "开始导入书籍: $title, URI: $uri")
            val result = repository.importBookFromUri(uri, title)
            _importStatus.value = if (result != null) {
                Log.d(TAG, "书籍导入成功: ${result.title}, 路径: ${result.filePath}")
                ImportStatus.Success(result)
            } else {
                Log.e(TAG, "书籍导入失败: $title")
                ImportStatus.Error("导入失败")
            }
        }
    }
    
    // 加载书籍内容
    fun loadBookContent(book: Book) {
        viewModelScope.launch {
            _currentBookContent.value = "加载中..."
            Log.d(TAG, "开始加载书籍内容: ${book.title}, 路径: ${book.filePath}, URI: ${book.fileUri}")
            
            // 检查文件是否存在
            val fileExists = book.filePath?.let { path ->
                val file = File(path)
                val exists = file.exists() && file.length() > 0
                Log.d(TAG, "检查文件是否存在: $path, 结果: $exists, 大小: ${file.length()} 字节")
                exists
            } ?: false
            
            if (!fileExists && book.fileUri == null) {
                Log.e(TAG, "书籍文件不存在或为空: ${book.title}")
                _currentBookContent.value = "无法加载书籍内容：文件不存在或为空"
                return@launch
            }
            
            try {
                val content = repository.readBookContent(book)
                if (content.isBlank()) {
                    Log.e(TAG, "书籍内容为空: ${book.title}")
                    _currentBookContent.value = "书籍内容为空，请检查文件是否有效"
                } else {
                    Log.d(TAG, "书籍内容加载完成: ${book.title}, 内容长度: ${content.length}")
                    _currentBookContent.value = content
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载书籍内容失败: ${e.message}", e)
                _currentBookContent.value = "加载失败: ${e.localizedMessage}"
            }
        }
    }
    
    // 删除书籍
    fun removeBook(book: Book) {
        repository.removeBook(book)
    }
    
    // 重置导入状态
    fun resetImportStatus() {
        _importStatus.value = ImportStatus.Idle
    }
    
    // 导入状态密封类
    sealed class ImportStatus {
        object Idle : ImportStatus()
        object Loading : ImportStatus()
        data class Success(val book: Book) : ImportStatus()
        data class Error(val message: String) : ImportStatus()
    }
} 

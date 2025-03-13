package com.reina.ebookreader.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.reina.ebookreader.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class BookRepository(private val context: Context) {
    
    private val TAG = "BookRepository"
    
    // 使用 mutableStateListOf 以便在 UI 中自动更新
    private val _books = mutableStateListOf<Book>()
    val books: List<Book> = _books
    
    init {
        // 初始化时加载已保存的书籍
        loadSavedBooks()
    }
    
    private fun loadSavedBooks() {
        try {
            // 从内部存储加载已保存的书籍
            val booksDir = File(context.filesDir, "books")
            Log.d(TAG, "加载已保存的书籍，目录路径: ${booksDir.absolutePath}")
            
            if (!booksDir.exists()) {
                Log.d(TAG, "书籍目录不存在，创建目录")
                booksDir.mkdirs()
                return
            }
            
            val files = booksDir.listFiles()
            if (files == null || files.isEmpty()) {
                Log.d(TAG, "没有找到已保存的书籍文件")
                return
            }
            
            Log.d(TAG, "找到 ${files.size} 个文件")
            
            files.forEach { file ->
                if (file.isFile && file.name.endsWith(".txt")) {
                    val book = Book.fromFile(file)
                    Log.d(TAG, "加载书籍: ${book.title}, ID: ${book.id}, 路径: ${book.filePath}, 大小: ${file.length()} 字节")
                    _books.add(book)
                }
            }
            
            Log.d(TAG, "成功加载 ${_books.size} 本书籍")
        } catch (e: Exception) {
            Log.e(TAG, "加载已保存书籍时出错: ${e.message}", e)
        }
    }
    
    suspend fun importBookFromUri(uri: Uri, title: String): Book? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始从 URI 导入书籍: $title, URI: $uri")
            
            // 创建书籍目录（如果不存在）
            val booksDir = File(context.filesDir, "books")
            if (!booksDir.exists()) {
                Log.d(TAG, "书籍目录不存在，创建目录: ${booksDir.absolutePath}")
                booksDir.mkdirs()
            }
            
            // 创建目标文件
            val fileName = "${title.replace(" ", "_")}.txt"
            val targetFile = File(booksDir, fileName)
            Log.d(TAG, "目标文件路径: ${targetFile.absolutePath}")
            
            // 检查文件是否已存在
            if (targetFile.exists()) {
                Log.d(TAG, "文件已存在，将被覆盖: ${targetFile.absolutePath}")
                targetFile.delete()
            }
            
            // 先检查内容是否有效
            var contentIsValid = false
            var contentLength = 0
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                val previewBuilder = StringBuilder()
                var lineCount = 0
                
                while (reader.readLine().also { line = it } != null && lineCount < 5) {
                    previewBuilder.append(line)
                    previewBuilder.append('\n')
                    lineCount++
                    contentLength += (line?.length ?: 0) + 1
                }
                
                if (lineCount > 0 || previewBuilder.isNotEmpty()) {
                    contentIsValid = true
                    Log.d(TAG, "文件内容有效，预览: ${previewBuilder.toString().take(100)}...")
                } else {
                    Log.e(TAG, "文件内容为空或无效")
                }
            } ?: run {
                Log.e(TAG, "无法打开输入流: $uri")
                return@withContext null
            }
            
            if (!contentIsValid) {
                Log.e(TAG, "文件内容无效，取消导入")
                return@withContext null
            }
            
            // 复制文件内容
            var bytesCopied = 0L
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    bytesCopied = inputStream.copyTo(outputStream)
                }
            } ?: run {
                Log.e(TAG, "无法打开输入流进行复制: $uri")
                return@withContext null
            }
            
            Log.d(TAG, "文件复制完成，大小: $bytesCopied 字节")
            
            if (bytesCopied <= 0) {
                Log.e(TAG, "文件复制失败，复制了 0 字节")
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                return@withContext null
            }
            
            // 验证文件是否成功写入
            if (!targetFile.exists() || targetFile.length() == 0L) {
                Log.e(TAG, "文件写入失败，文件不存在或大小为0")
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                return@withContext null
            }
            
            // 创建并添加新书籍
            val book = Book.fromUri(uri, title, targetFile.absolutePath)
            
            // 添加到列表中
            _books.add(book)
            Log.d(TAG, "书籍导入成功: $title, ID: ${book.id}, 路径: ${targetFile.absolutePath}")
            return@withContext book
        } catch (e: Exception) {
            Log.e(TAG, "导入书籍时出错: ${e.message}", e)
            e.printStackTrace()
            return@withContext null
        }
    }
    
    suspend fun readBookContent(book: Book): String = withContext(Dispatchers.IO) {
        try {
            val stringBuilder = StringBuilder()
            
            // 从文件路径读取
            book.filePath?.let { path ->
                val file = File(path)
                Log.d(TAG, "尝试从文件路径读取书籍内容: $path, 文件存在: ${file.exists()}, 大小: ${file.length()} 字节")
                
                if (file.exists()) {
                    try {
                        file.bufferedReader().use { reader ->
                            var line: String?
                            var lineCount = 0
                            while (reader.readLine().also { line = it } != null) {
                                stringBuilder.append(line)
                                stringBuilder.append('\n')
                                lineCount++
                            }
                            Log.d(TAG, "从文件读取了 $lineCount 行内容")
                        }
                        val content = stringBuilder.toString()
                        if (content.isNotEmpty()) {
                            Log.d(TAG, "成功从文件读取内容，长度: ${content.length}")
                            return@withContext content
                        } else {
                            Log.w(TAG, "文件内容为空")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "从文件读取内容时出错: ${e.message}", e)
                    }
                } else {
                    Log.e(TAG, "文件不存在: $path")
                }
            } ?: Log.w(TAG, "书籍没有文件路径")
            
            // 如果文件路径不可用，尝试从 URI 读取
            book.fileUri?.let { uri ->
                Log.d(TAG, "尝试从 URI 读取书籍内容: $uri")
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        var line: String?
                        var lineCount = 0
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                            stringBuilder.append('\n')
                            lineCount++
                        }
                        Log.d(TAG, "从 URI 读取了 $lineCount 行内容")
                    } ?: Log.e(TAG, "无法打开 URI 输入流: $uri")
                    
                    val content = stringBuilder.toString()
                    if (content.isNotEmpty()) {
                        Log.d(TAG, "成功从 URI 读取内容，长度: ${content.length}")
                        return@withContext content
                    } else {
                        Log.w(TAG, "URI 内容为空")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "从 URI 读取内容时出错: ${e.message}", e)
                }
            } ?: Log.w(TAG, "书籍没有 URI")
            
            Log.w(TAG, "无法读取书籍内容，返回默认消息")
            return@withContext "无法读取书籍内容，请检查文件是否有效。"
        } catch (e: Exception) {
            Log.e(TAG, "读取书籍内容时出错: ${e.message}", e)
            return@withContext "读取错误: ${e.localizedMessage}"
        }
    }
    
    fun removeBook(book: Book) {
        try {
            // 从列表中移除
            _books.remove(book)
            Log.d(TAG, "从列表中移除书籍: ${book.title}")
            
            // 删除文件
            book.filePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d(TAG, "删除文件: $path, 结果: $deleted")
                } else {
                    Log.w(TAG, "要删除的文件不存在: $path")
                }
            } ?: Log.w(TAG, "书籍没有文件路径，无法删除文件")
        } catch (e: Exception) {
            Log.e(TAG, "删除书籍时出错: ${e.message}", e)
        }
    }
} 

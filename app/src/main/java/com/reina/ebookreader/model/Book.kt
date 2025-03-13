package com.reina.ebookreader.model

import android.net.Uri
import java.io.File
import java.util.UUID

data class Book(
    val id: String,
    val title: String,
    val description: String = "",
    val fileUri: Uri? = null,
    val filePath: String? = null
) {
    companion object {
        fun fromFile(file: File): Book {
            val title = file.nameWithoutExtension
            return Book(
                id = title,
                title = title,
                description = "导入的书籍: $title",
                filePath = file.absolutePath
            )
        }
        
        fun fromUri(uri: Uri, title: String, filePath: String): Book {
            return Book(
                id = title.replace(" ", "_"),
                title = title,
                description = "导入的书籍: $title",
                fileUri = uri,
                filePath = filePath
            )
        }
    }
} 

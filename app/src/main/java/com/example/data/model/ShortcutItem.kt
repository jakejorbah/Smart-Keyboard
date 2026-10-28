package com.example.data.model

data class TextShortcut(
    val id: String,
    val trigger: String,
    val replacement: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ClipboardSnippet(
    val id: String,
    val text: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

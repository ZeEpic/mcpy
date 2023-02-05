package com.rimlang.rim.errors

class CodeFile(val text: String, val fileName: String) {
    fun readLine(line: Int): String {
        return text.lineSequence().take(line).last()
    }
}

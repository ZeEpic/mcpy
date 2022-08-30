package com.rimlang.rim.util

fun title(string: String)
    = string.split(" ")
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

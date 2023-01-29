package com.rimlang.rim.translation

data class FunctionIdentifier(
    val name: String,
    val parameters: Map<String, String>,
    val returnType: String,
    val line: Int,
) : Identifier

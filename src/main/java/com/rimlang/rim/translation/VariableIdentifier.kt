package com.rimlang.rim.translation

data class VariableIdentifier(
    val name: String,
    val type: String,
    val startingValue: String,
    val line: Int
) : Identifier
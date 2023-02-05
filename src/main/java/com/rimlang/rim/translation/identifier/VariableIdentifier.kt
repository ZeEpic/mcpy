package com.rimlang.rim.translation.identifier

import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type

data class VariableIdentifier(
    val name: Name,
    val type: Type,
    val startingValue: String,
    val line: Int
) : Identifier
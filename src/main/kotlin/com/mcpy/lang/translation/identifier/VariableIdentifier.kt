package com.mcpy.lang.translation.identifier

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type

data class VariableIdentifier(
    val name: Name,
    val type: Type,
    val startingValue: String,
    val line: Int
) : Identifier
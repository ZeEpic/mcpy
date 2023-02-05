package com.rimlang.rim.translation.identifier

import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type

abstract class Function(
    open val annotations: MutableList<String>,
    open val returnType: Type,
    open val name: Name,
    open val parameters: String,
    open var body: String
) : Identifier {
    fun build()
        = "private ${returnType.type} ${name.value}($parameters) " +
            "{\n$body\n}"
}
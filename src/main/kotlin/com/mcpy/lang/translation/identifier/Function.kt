package com.mcpy.lang.translation.identifier

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type

abstract class Function(
    open val annotations: MutableList<String>,
    open val returnType: Type,
    open val name: Name,
    open val parameters: String,
    open var body: String
) : Identifier {
    fun build()
        = "${annotations.joinToString { "@$it\n" }}private ${returnType.type} ${name.converted.value}($parameters) " +
            "{\n$body\n}"
}

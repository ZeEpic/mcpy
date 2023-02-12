package com.mcpy.lang.abstractions

import com.mcpy.lang.errors.error
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.TokenType
import kotlin.reflect.KClass

class Type(val type: String) {
    constructor(clazz: KClass<*>) : this(clazz.qualifiedName!!)

    companion object {
        fun toJava(typeToken: StringToken): Name? {
            val type = typeToken.value
            if (type.startsWith("list[") && type.endsWith("]")) {
                val innerType = StringToken(
                    TokenType.ID,
                    type.substring(5, type.length - 1),
                    typeToken.line,
                    typeToken.character,
                    typeToken.file
                )
                return Name("java.util.List<" + (toJava(innerType) ?: error("Unknown type. This must be something like 'num' or 'str'", innerType)) + ">", Name.NameType.CLASS_NAME
                )
            }

            return Name(when (type) {
                "str" -> "String"
                "num" -> "double"
                "bool" -> "boolean"
                "void" -> "void"
                "player" -> "Player"
                else -> return null
            }, Name.NameType.CLASS_NAME
            )

        }
    }
}
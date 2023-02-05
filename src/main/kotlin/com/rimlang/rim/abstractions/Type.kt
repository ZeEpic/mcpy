package com.rimlang.rim.abstractions

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.TokenType
import kotlin.reflect.KClass

class Type(val type: String) {
    constructor(clazz: KClass<*>) : this(clazz.qualifiedName!!)

    companion object {
        fun toJava(typeToken: StringToken): Name {
            val type = typeToken.value
            if (type.startsWith("list[") && type.endsWith("]")) {
                return Name("java.util.List<" + toJava(
                    StringToken(
                        TokenType.TYPE,
                        type.substring(5, type.length - 1),
                        typeToken.line,
                        typeToken.character,
                        typeToken.file
                    )
                ) + ">", Name.NameType.CLASS_NAME
                )
            }

            return Name(when (type) {
                "str" -> "String"
                "num" -> "double"
                "bool" -> "boolean"
                "void" -> "void"
                "player" -> "Player"
                else -> com.rimlang.rim.errors.error(
                    "Type " + type + " not found (on line " + typeToken.line + ").",
                    typeToken
                )
            }, Name.NameType.CLASS_NAME
            )

        }
    }
}
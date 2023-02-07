package com.mcpy.lang.abstractions

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.TokenType
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
                else -> com.mcpy.lang.errors.error(
                    "Type " + type + " not found (on line " + typeToken.line + ").",
                    typeToken
                )
            }, Name.NameType.CLASS_NAME
            )

        }
    }
}
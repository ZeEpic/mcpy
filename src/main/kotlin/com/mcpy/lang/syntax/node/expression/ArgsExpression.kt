package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.camelCase
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.translation.context.Context

class ArgsExpression(tokens: List<Token>, firstToken: Token) : Expression(firstToken) {
    val args = mutableListOf<Argument>()

    init {
        for (arg in tokens.split(TokenType.COMMA) { it.type }) {
            require(arg[0].type == TokenType.ID, arg[0]) {
                "Argument must have identifier"
            }
            val identifier = arg[0] as StringToken
            require(arg[1].type == TokenType.COLON, arg[1]) {
                "Argument must have a separating colon between the identifier and the argument type"
            }
            require(arg[2].type == TokenType.ID, arg[2]) {
                "Argument must have type"
            }
            val type = arg[2] as? StringToken ?: continue
            args += if (arg.size > 4 && arg[3].type == TokenType.ASSIGNMENT_OPERATOR) {
                Argument(type, identifier, arg.subList(4, arg.size))
            } else {
                Argument(type, identifier)
            }
        }
    }

    data class Argument(
        val type: StringToken,
        val identifier: StringToken,
        val defaultValue: List<Token> = listOf()
    ) {
        fun translate(): String {
            val typeValue = Type.toJava(type.value)?.value
            require(typeValue != null, type) {
                "Invalid argument type"
            }
            return typeValue + " " + identifier.value.camelCase()
        }
    }

    override fun translate(context: Context): String {
        return args.joinToString { it.translate() }
    }

}
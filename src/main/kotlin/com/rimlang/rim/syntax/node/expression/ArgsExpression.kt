package com.rimlang.rim.syntax.node.expression

import com.rimlang.rim.abstractions.Type
import com.rimlang.rim.camelCase
import com.rimlang.rim.lexer.token.*
import com.rimlang.rim.translation.context.Context

class ArgsExpression(tokens: List<Token>, firstToken: Token) : Expression(firstToken) {
    val args = mutableListOf<Argument>()

    init {
        for (arg in tokens.split(TokenType.COMMA)) {
            com.rimlang.rim.errors.require(arg[0].type === TokenType.ID, arg[0]) {
                "Argument must have identifier"
            }
            val identifier = arg[0] as StringToken
            com.rimlang.rim.errors.require(arg[1].type === TokenType.COLON, arg[1]) {
                "Argument must have a separating colon between the identifier and the argument type"
            }
            com.rimlang.rim.errors.require(arg[2].type === TokenType.TYPE, arg[2]) {
                "Argument must have type"
            }
            val type = arg[2] as? StringToken ?: continue
            args += if (arg.size > 4 && arg[3].type === TokenType.ASSIGNMENT_OPERATOR) {
                Argument(type, identifier, sub(arg, 4))
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
            return Type.toJava(type).value + " " + identifier.value.camelCase()
        }
    }

    override fun translate(context: Context): String {
        return args.joinToString { it.translate() }
    }

}
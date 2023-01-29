package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken
import com.rimlang.rim.lexer.Token
import com.rimlang.rim.lexer.TokenType
import com.rimlang.rim.translation.Context
import com.rimlang.rim.translation.Translator
import com.rimlang.rim.util.camelCase
import com.rimlang.rim.util.split
import com.rimlang.rim.util.sub

class ArgsExpression(tokens: List<Token>) : Expression() {
    val args = mutableListOf<Argument>()

    init {
        for (arg in tokens.split(TokenType.COMMA)) {
            if (arg[0].type !== TokenType.ID) {
                throw RimSyntaxException("Argument must have identifier", arg[0].line)
            }
            val identifier = arg[0] as StringToken
            if (arg[1].type !== TokenType.COLON) {
                throw RimSyntaxException(
                    "Argument must have a separating colon between the identifier and the argument type",
                    arg[1].line
                )
            }
            if (arg[2].type !== TokenType.TYPE) {
                throw RimSyntaxException("Argument must have type", arg[2].line)
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
            return Translator.toJavaType(type) + " " + identifier.value.camelCase()
        }
    }

    override fun translate(context: Context): String {
        return args.joinToString { it.translate() }
    }

}
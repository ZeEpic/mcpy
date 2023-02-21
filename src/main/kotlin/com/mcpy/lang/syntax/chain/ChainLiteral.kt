package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.TokenType
import com.mcpy.lang.translation.context.Context

class ChainLiteral(private val literal: StringToken, context: Context, chains: List<ChainLink>, index: Int) : ChainLink(context, chains, literal, index) {
    override fun generate() {
        returnType = when (literal.type) {
            TokenType.NUMBER_LITERAL -> Type("double")
            TokenType.STRING_LITERAL -> Type("String")
            TokenType.BOOLEAN_LITERAL -> Type("boolean")
            else -> Type("Object")
        }
        idInJava = Name(literal.value, Name.NameType.PARAMETER)
    }
}
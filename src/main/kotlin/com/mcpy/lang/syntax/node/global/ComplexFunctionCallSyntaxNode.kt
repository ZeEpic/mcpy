package com.mcpy.lang.syntax.node.global

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.GenericExpression

data class ComplexFunctionCallSyntaxNode(
    val functionIdentifier: StringToken,
    val args: List<GenericExpression>,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken) {
    enum class Type(val code: String) {
        EVENT("on"), TIMER("timer"), METADATA("trait");
    }
}
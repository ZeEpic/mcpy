package com.rimlang.rim.syntax.node.global

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

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
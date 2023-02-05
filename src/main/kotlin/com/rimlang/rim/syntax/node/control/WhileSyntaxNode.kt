package com.rimlang.rim.syntax.node.control

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.BooleanExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class WhileSyntaxNode(
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)

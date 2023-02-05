package com.rimlang.rim.syntax.node.control

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class ReturnSyntaxNode(
    val genericExpression: GenericExpression,
    override val firstToken: Token
) : SyntaxNode(firstToken)
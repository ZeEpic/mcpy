package com.rimlang.rim.syntax.node.control

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.BooleanExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class IfSyntaxNode(
    val branches: List<ConditionalBranch>,
    override val firstToken: Token
) : SyntaxNode(firstToken)

data class ConditionalBranch(
    val type: StringToken,
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>
)
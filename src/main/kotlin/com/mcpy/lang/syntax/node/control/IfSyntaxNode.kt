package com.mcpy.lang.syntax.node.control

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.BooleanExpression

data class IfSyntaxNode(
    val branches: List<ConditionalBranch>,
    override val firstToken: Token
) : SyntaxNode(firstToken)

data class ConditionalBranch(
    val type: StringToken,
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>
)

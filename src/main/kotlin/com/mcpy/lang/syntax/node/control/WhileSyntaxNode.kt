package com.mcpy.lang.syntax.node.control

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.BooleanExpression

data class WhileSyntaxNode(
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)

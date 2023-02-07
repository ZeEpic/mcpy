package com.mcpy.lang.syntax.node.control

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.GenericExpression

data class ReturnSyntaxNode(
    val genericExpression: GenericExpression,
    override val firstToken: Token
) : SyntaxNode(firstToken)
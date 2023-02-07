package com.mcpy.lang.syntax.node.control

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.GenericExpression

data class ForeachSyntaxNode(
    val loopIdentifier: StringToken,
    val genericExpression: GenericExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
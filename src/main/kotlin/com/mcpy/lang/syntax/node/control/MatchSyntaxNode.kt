package com.mcpy.lang.syntax.node.control

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.GenericExpression

data class MatchSyntaxNode(
    val genericExpression: GenericExpression,
    val branches: HashMap<List<Token>, List<SyntaxNode>>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
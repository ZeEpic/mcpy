package com.rimlang.rim.syntax.node.control

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class MatchSyntaxNode(
    val genericExpression: GenericExpression,
    val branches: HashMap<List<Token>, List<SyntaxNode>>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
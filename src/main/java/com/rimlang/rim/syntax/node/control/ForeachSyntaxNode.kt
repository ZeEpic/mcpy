package com.rimlang.rim.syntax.node.control

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class ForeachSyntaxNode(
    val loopIdentifier: StringToken,
    val genericExpression: GenericExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
package com.mcpy.lang.syntax.node.global

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.ArgsExpression
import com.mcpy.lang.syntax.node.expression.EventExpression

data class EventDefinitionSyntaxNode(
    val event: EventExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
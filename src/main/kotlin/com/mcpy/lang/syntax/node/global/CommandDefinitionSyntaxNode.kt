package com.mcpy.lang.syntax.node.global

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.ArgsExpression

data class CommandDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val senderType: StringToken?,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
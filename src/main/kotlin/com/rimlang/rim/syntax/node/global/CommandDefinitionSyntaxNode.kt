package com.rimlang.rim.syntax.node.global

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.ArgsExpression
import com.rimlang.rim.syntax.node.syntax.SyntaxNode

data class CommandDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
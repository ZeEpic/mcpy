package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class CommandDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val body: List<SyntaxNode>
) : SyntaxNode()
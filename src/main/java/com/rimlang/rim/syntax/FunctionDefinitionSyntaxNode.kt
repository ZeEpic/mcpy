package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class FunctionDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val returnType: StringToken,
    val body: List<SyntaxNode>
) : SyntaxNode()
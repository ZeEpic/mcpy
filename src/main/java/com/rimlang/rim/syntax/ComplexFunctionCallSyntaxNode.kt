package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class ComplexFunctionCallSyntaxNode(
    val functionIdentifier: StringToken,
    val args: List<GenericExpression>,
    val body: List<SyntaxNode>
) : SyntaxNode()
package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class ForeachSyntaxNode(
    val loopIdentifier: StringToken,
    val genericExpression: GenericExpression,
    val body: List<SyntaxNode>
) : SyntaxNode()
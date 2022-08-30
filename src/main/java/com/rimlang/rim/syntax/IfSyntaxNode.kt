package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class IfSyntaxNode(
    val branches: List<ConditionalBranch>
) : SyntaxNode()

data class ConditionalBranch(
    val type: StringToken,
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>
)
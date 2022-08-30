package com.rimlang.rim.syntax

data class WhileSyntaxNode(
    val booleanExpression: BooleanExpression,
    val body: List<SyntaxNode>
) : SyntaxNode()

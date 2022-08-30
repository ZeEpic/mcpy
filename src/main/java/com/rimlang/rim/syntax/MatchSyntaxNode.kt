package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.Token

data class MatchSyntaxNode(
    val genericExpression: GenericExpression,
    val branches: HashMap<List<Token>, List<SyntaxNode>>
) : SyntaxNode()
package com.rimlang.rim.syntax.node.syntax

import com.rimlang.rim.lexer.token.Token

data class ExpressionSyntaxNode(val code: List<Token>, override val firstToken: Token) : SyntaxNode(firstToken)

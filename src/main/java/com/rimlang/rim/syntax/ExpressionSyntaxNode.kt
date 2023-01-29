package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.Token

data class ExpressionSyntaxNode(val code: List<Token>) : SyntaxNode()

package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode

data class ExpressionSyntaxNode(val code: List<Token>, override val firstToken: Token) : SyntaxNode(firstToken)

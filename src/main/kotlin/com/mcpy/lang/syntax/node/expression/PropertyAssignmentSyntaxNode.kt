package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode

data class PropertyAssignmentSyntaxNode(val left: List<Token>, val right: List<Token>) : SyntaxNode(left.first())
package com.mcpy.lang.syntax.node.global

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.expression.ArgsExpression

data class TraitDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val targetType: StringToken,
    override val firstToken: Token
) : SyntaxNode(firstToken)
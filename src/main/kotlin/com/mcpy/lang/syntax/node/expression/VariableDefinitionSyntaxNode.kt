package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode

data class VariableDefinitionSyntaxNode(
    val identifier: StringToken,
    val initialValue: GenericExpression,
    override val firstToken: Token
) : SyntaxNode(firstToken)
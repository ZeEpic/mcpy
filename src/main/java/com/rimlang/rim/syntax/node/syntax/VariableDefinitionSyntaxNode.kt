package com.rimlang.rim.syntax.node.syntax

import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression

data class VariableDefinitionSyntaxNode(
    val identifier: StringToken,
    val initialValue: GenericExpression,
    override val firstToken: Token
) : SyntaxNode(firstToken)
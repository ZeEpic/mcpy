package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken

data class VariableDefinitionSyntaxNode(
    val identifier: StringToken,
    val initialValue: GenericExpression
) : SyntaxNode()
package com.mcpy.lang.syntax.node.global

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.control.MatchSyntaxNode
import com.mcpy.lang.syntax.node.expression.ArgsExpression
import com.mcpy.lang.syntax.node.expression.GenericExpression

data class GuiDefinitionSyntaxNode(
    val identifier: StringToken,
    val args: ArgsExpression,
    val title: GenericExpression,
    val pattern: GenericExpression,
    val legend: GenericExpression,
    val actionMatcher: MatchSyntaxNode?,
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
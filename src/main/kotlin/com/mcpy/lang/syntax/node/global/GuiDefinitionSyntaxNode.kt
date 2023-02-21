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
    val title: String,
    val pattern: GenericExpression, // a list of strings representing each row of the gui
    val legend: GenericExpression, // a dictionary of strings to items representing the items in the gui
    val actionMatcher: MatchSyntaxNode?, // a match statement that runs code when a player clicks on an item in the gui
    val body: List<SyntaxNode>,
    override val firstToken: Token
) : SyntaxNode(firstToken)
package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.Node
import com.mcpy.lang.translation.context.Context

abstract class Expression(open val firstToken: Token) : Node {
    abstract fun translate(context: Context): String
}
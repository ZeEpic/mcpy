package com.rimlang.rim.syntax.node.expression

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.Node
import com.rimlang.rim.translation.context.Context

abstract class Expression(open val firstToken: Token) : Node {
    abstract fun translate(context: Context): String
}
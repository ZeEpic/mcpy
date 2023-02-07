package com.mcpy.lang.syntax.node

import com.mcpy.lang.lexer.token.Token

abstract class SyntaxNode(open val firstToken: Token) : Node
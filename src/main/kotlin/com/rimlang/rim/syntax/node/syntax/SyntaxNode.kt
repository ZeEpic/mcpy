package com.rimlang.rim.syntax.node.syntax

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.Node

abstract class SyntaxNode(open val firstToken: Token) : Node
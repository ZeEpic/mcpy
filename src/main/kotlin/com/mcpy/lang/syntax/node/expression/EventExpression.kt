package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.errors.error
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.translation.Events
import com.mcpy.lang.translation.context.Context

class EventExpression(val value: List<StringToken>, firstToken: Token) : Expression(firstToken) {
    override fun translate(context: Context)
        = Events.spigotEvent(Name(value.joinToString("") { it.value }, Name.NameType.EVENT))
            ?: error("That event doesn't exist! Check the examples folder on GitHub for a list of events", firstToken)

}
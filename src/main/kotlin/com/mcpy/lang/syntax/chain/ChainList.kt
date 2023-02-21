package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context

class ChainList(private val items: List<GenericExpression>, context: Context, chains: List<ChainLink>, firstToken: Token, index: Int) : ChainLink(context, chains, firstToken, index) {
    override fun generate() {
        require(chains.first() == this, firstToken) {
            "A list can't be created here"
        }
        if (items.isEmpty()) {
            returnType = Type("java.util.ArrayList<Object>")
            idInJava = Name("List.of", Name.NameType.CLASS_NAME)
            parametersInJava.addAll(items)
            TODO("Make it so when an empty list is manipulated, it goes back and changes the return type on this")
        }
        val itemType = if (items.all { it.resultType == items.first().resultType }) {
            items.first().resultType
        } else {
            Type(Object::class)
        }
        returnType = Type("java.util.ArrayList<${itemType?.type ?: "Object"}>")
        idInJava = Name("List.of", Name.NameType.CLASS_NAME)
        parametersInJava.addAll(items)
    }
}
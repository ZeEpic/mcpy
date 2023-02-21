package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context

class ChainDictionary(private val pairs: Map<GenericExpression, GenericExpression>, context: Context, chains: List<ChainLink>, firstToken: Token, index: Int) : ChainLink(context, chains, firstToken, index) {
    override fun generate() {
        require(chains.first() == this, firstToken) {
            "A dictionary can't be created here"
        }
        if (pairs.isEmpty()) {
            returnType = Type("java.util.HashMap<Object, Object>")
            idInJava = Name("Map.of", Name.NameType.CLASS_NAME)
            parametersInJava.addAll(pairs.keys + pairs.values)
            TODO("Make it so when an empty dictionary is manipulated, it goes back and changes the return type on this")
        }
        val keyType = if (pairs.all { it.key.resultType == pairs.keys.first().resultType }) {
            pairs.keys.first().resultType
        } else {
            Type(Object::class)
        }
        val valueType = if (pairs.all { it.value.resultType == pairs.values.first().resultType }) {
            pairs.values.first().resultType
        } else {
            Type(Object::class)
        }
        returnType = Type("java.util.HashMap<${keyType?.type ?: "Object"}, ${valueType?.type ?: "Object"}>")
        idInJava = Name("Map.of", Name.NameType.CLASS_NAME)
        parametersInJava.addAll(pairs.keys + pairs.values)
    }
}
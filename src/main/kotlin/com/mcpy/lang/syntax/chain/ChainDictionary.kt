package com.mcpy.lang.syntax.chain

import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context

class ChainDictionary(val pairs: Map<GenericExpression, GenericExpression>, context: Context, chains: List<ChainLink>, index: Int) : ChainLink(context, chains, index) {
    override fun generate() {
        TODO("Not yet implemented")
    }
}
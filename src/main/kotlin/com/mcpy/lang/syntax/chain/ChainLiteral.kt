package com.mcpy.lang.syntax.chain

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.translation.context.Context

class ChainLiteral(val literal: StringToken, context: Context, chains: List<ChainLink>, index: Int) : ChainLink(context, chains, index) {
    override fun generate() {
        TODO("Not yet implemented")
    }
}
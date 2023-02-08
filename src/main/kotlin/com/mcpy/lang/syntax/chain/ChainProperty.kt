package com.mcpy.lang.syntax.chain

import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.translation.context.Context

class ChainProperty(val idToken: StringToken, context: Context, chains: List<ChainLink>, index: Int) : ChainLink(context, chains, index) {
    override fun generate() {
        TODO("Not yet implemented")
    }
}
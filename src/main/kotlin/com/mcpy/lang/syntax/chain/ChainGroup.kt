package com.mcpy.lang.syntax.chain

import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.translation.context.Context

class ChainGroup(val group: List<Token>, context: Context, chains: List<ChainLink>, index: Int) : ChainLink(context, chains, index) {
    override fun generate() {
        TODO("Not yet implemented")
    }
}
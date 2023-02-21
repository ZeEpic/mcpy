package com.mcpy.lang.syntax.chain

import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.translation.context.Context

class ChainGroup(val group: List<Token>, context: Context, chains: List<ChainLink>, firstToken: Token, index: Int) : ChainLink(context, chains, firstToken, index) {
    override fun generate() {
        require(chains.first() == this, firstToken) {
            "A cannot use parentheses here. Did you forget to provide the name of a function?"
        }

        TODO("Not yet implemented. This is when a group of tokens is passed in, like (1 + 2) or (a and b)")
    }
}

package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.clazz
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.NumberToken
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.lexer.token.TokenType
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.*
import com.mcpy.lang.translation.function.CustomFunction
import com.mcpy.lang.translation.identifier.VariableIdentifier
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class ChainFunction(private val idToken: StringToken, private val callArgs: List<List<Token>>?, context: Context, chains: List<ChainLink>, firstToken: Token, index: Int) : ChainLink(context, chains, firstToken, index) {
    override fun generate(): Pair<FunctionCall, Type> {
        val id = idToken.value
        if (isFirst) {
            val bukkitObjectClass = when (id) {
                "server" -> Bukkit::class.qualifiedName
                else -> null
            }
            if (bukkitObjectClass != null) {
                return FunctionCall(
                    bukkitObjectClass,
                    firstToken
                ) to Type(bukkitObjectClass)
            }
            if (id in Material.values().map(Material::name)) {
                return FunctionCall(
                    "Material.$id",
                    firstToken
                ) to Type(Material::class)
            }
            if (id in EntityType.values().map(EntityType::name)) {
                return FunctionCall(
                    "EntityType.$id",
                    firstToken
                ) to Type(EntityType::class)
            }
            val matchingFunction = context.identifiers.filterIsInstance<CustomFunction>().firstOrNull { it.name.value == id }
            if (matchingFunction != null) {
                require(callArgs != null, idToken) {
                    "You must include () after a function name"
                }
                val args = callArgs.map { GenericExpression(it) }
                return FunctionCall(
                    matchingFunction.name.converted.value,
                    firstToken,
                    args
                ) to matchingFunction.returnType
            }
            val matchingVariable = context.identifiers.filterIsInstance<VariableIdentifier>().firstOrNull { it.name.value == id }
            if (matchingVariable != null) {
                require(callArgs == null, idToken) {
                    "You can't include () after a variable name, because it isn't a function"
                }
                return FunctionCall(
                    matchingVariable.name.converted.value,
                    firstToken
                ) to matchingVariable.type
            }
            if (id == "print") {
                require(callArgs != null, idToken) {
                    "print isn't a property - you must include () after it"
                }
                return FunctionCall(
                    "Bukkit.getLogger().info",
                    firstToken,
                    callArgs.firstOrNull()?.let { GenericExpression(it) }?.let { listOf(it) } ?: emptyList()
                ) to Type.VOID
            }
            if (id == "range") {
                require(callArgs != null && callArgs.size in 1..3, idToken) {
                    "range must have 1 to 3 arguments, where the first is the lower bound, the second is the upper bound, and the third is the step between each value"
                }
                when (callArgs.size) {
                    1, 2 -> {
                        val params = mutableListOf<GenericExpression>()
                        params.addAll(callArgs.map { GenericExpression(it) })
                        if (callArgs.size == 1) {
                            params.add(0,
                                GenericExpression(listOf(NumberToken(TokenType.NUMBER_LITERAL, 0.0, idToken.line, idToken.character, idToken.file)))
                            )
                        }
                        return FunctionCall(
                            "IntStream.range",
                            firstToken,
                            params
                        ) to Type("IntStream")
                    }
                    3 -> {
                        val lowerBound = callArgs[0]
                        val upperBound = callArgs[1]
                        val step = callArgs[2]
                        return FunctionCall(
                            "IntStream.iterate($lowerBound, i -> i + $step).limit(($upperBound - $lowerBound) / $step)",
                            firstToken
                        ) to Type("IntStream")
                    }
                }
            }
            when (context) {
                is CommandContext -> {
                    if (id == "sender") {
                        return FunctionCall(
                            "sender",
                            firstToken,
                            emptyList(),
                            Type(Player::class)
                        ) to Type(Player::class)
                    }
                    if (id == "args") {
                        return FunctionCall(
                            "args",
                            firstToken
                        ) to Type("String[]")
                    }
                }
                is ForeachContext -> {
                }
                is TimerContext -> {

                }
                is VariableDefinitionContext -> {

                }
            }
            error("Could not find $id. Did you misspell it?", idToken)
        }
        if (previous != null) {
            val returnType = previous.generate().second
            if (returnType.type == Block::class.qualifiedName) {
                // TODO: Get persistent data container value
            } else if (Entity::class.java.isAssignableFrom(returnType.type.clazz)) { // if previous return type is an Entity
                // Same thing as above
            } else {
                validateMethod(returnType, Name(id, Name.NameType.FUNCTION), callArgs, firstToken)
            }
        }
        error("Could not find identifier $id, at line ${idToken.line}", idToken)
    }
}
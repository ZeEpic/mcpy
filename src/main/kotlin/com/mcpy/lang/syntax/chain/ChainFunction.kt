package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.classNameFromQualifiedName
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
    override fun generate() {
        val id = idToken.value
        if (isFirst) {
            val bukkitObjectClass = when (id) {
                "server" -> Bukkit::class.qualifiedName
                else -> null
            }
            if (bukkitObjectClass != null) {
                returnType = Type(bukkitObjectClass)
                idInJava = Name(bukkitObjectClass.classNameFromQualifiedName(), Name.NameType.CLASS_NAME)
                return
            }
            if (id in Material.values().map(Material::name)) {
                returnType = Type(Material::class)
                idInJava = Name("Material.$id", Name.NameType.ENUM_VALUE)
                return
            }
            if (id in EntityType.values().map(EntityType::name)) {
                returnType = Type(EntityType::class)
                idInJava = Name("EntityType.$id", Name.NameType.ENUM_VALUE)
                return
            }
            val matchingFunction = context.identifiers.filterIsInstance<CustomFunction>().firstOrNull { it.name.value == id }
            if (matchingFunction != null) {
                require(callArgs != null, idToken) {
                    "You must include () after a function name"
                }
                val args = callArgs.map { GenericExpression(it) }
                returnType = matchingFunction.returnType
                idInJava = matchingFunction.name.convertedValue
                parametersInJava.addAll(args)
                return
            }
            val matchingVariable = context.identifiers.filterIsInstance<VariableIdentifier>().firstOrNull { it.name.value == id }
            if (matchingVariable != null) {
                require(callArgs == null, idToken) {
                    "You can't include () after a variable name, because it isn't a function"
                }
                returnType = matchingVariable.type
                idInJava = matchingVariable.name.convertedValue
                return
            }
            if (id == "print") {
                returnType = Type.VOID
                idInJava = Name("System.out.println", Name.NameType.FUNCTION)
                require(callArgs != null, idToken) {
                    "print isn't a property, so you must include () after it"
                }
                if (callArgs.isNotEmpty()) {
                    parametersInJava.add(GenericExpression(callArgs[0]))
                }
                return
            }
            if (id == "range") {
                returnType = Type("IntStream")
                idInJava = Name("IntStream.range", Name.NameType.CLASS_NAME)
                require(callArgs != null && callArgs.size in 1..3, idToken) {
                    "range must have 1 to 3 arguments, where the first is the lower bound, the second is the upper bound, and the third is the step between each value"
                }
                when (callArgs.size) {
                    1, 2 -> {
                        parametersInJava.addAll(callArgs.map { GenericExpression(it) })
                        if (callArgs.size == 1) {
                            parametersInJava.add(0,
                                GenericExpression(listOf(NumberToken(TokenType.NUMBER_LITERAL, 0.0, idToken.line, idToken.character, idToken.file)))
                            )
                        }
                    }
                    3 -> {
                        val lowerBound = callArgs[0]
                        val upperBound = callArgs[1]
                        val step = callArgs[2]
                        idInJava = Name("IntStream.iterate($lowerBound, i -> i + $step).limit(($upperBound - $lowerBound) / $step)", Name.NameType.CLASS_NAME)
                    }
                }
                return
            }
            when (context) {
                is CommandContext -> {
                    if (id == "sender") {
                        returnType = Type(Player::class)
                        idInJava = Name("sender", Name.NameType.VARIABLE)
                        return
                    }
                    if (id == "args") {
                        returnType = Type("String[]")
                        idInJava = Name("args", Name.NameType.VARIABLE)
                        return
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
            if (previous.returnType.type == Block::class.qualifiedName) {
                // TODO: Get persistent data container value
            } else if (Entity::class.java.isAssignableFrom(previous.returnType.type.clazz)) { // if previous return type is an Entity
                // Same thing as above
            } else {
                validateMethod(previous.returnType, Name(id, Name.NameType.FUNCTION), callArgs)
            }
        }
        error("Could not find identifier $id, at line ${idToken.line}", idToken)
    }
}
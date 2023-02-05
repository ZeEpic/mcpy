package com.rimlang.rim.syntax

import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type
import com.rimlang.rim.lexer.token.StringToken
import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.syntax.node.expression.GenericExpression
import com.rimlang.rim.translation.*
import com.rimlang.rim.translation.context.*
import com.rimlang.rim.translation.identifier.VariableIdentifier
import com.rimlang.rim.classNameFromQualifiedName
import com.rimlang.rim.clazz
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

data class ChainLink(
    val idToken: StringToken,
    val callArgs: List<List<Token>>?,
    val context: Context,
    val chains: List<ChainLink>,
    val i: Int
) {

    private val previous = chains.getOrNull(i - 1)
    private val isFirst = (i == 0)

    lateinit var returnType: Type
    lateinit var idInJava: Name
    val parametersInJava = mutableListOf<GenericExpression>()

    init {
        generate()
    }

    private fun generate() {
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
                returnType = Type(Material::class.qualifiedName!!)
                idInJava = Name("Material.$id", Name.NameType.ENUM_VALUE)
                return
            }
            if (id in EntityType.values().map(EntityType::name)) {
                returnType = Type(EntityType::class.qualifiedName!!)
                idInJava = Name("EntityType.$id", Name.NameType.ENUM_VALUE)
                return
            }
            val matchingFunction = context.identifiers.filterIsInstance<com.rimlang.rim.translation.identifier.Function>().firstOrNull { it.name.value == id }
            if (callArgs != null && matchingFunction != null) {
                val args = callArgs.map { GenericExpression(it, it[0]) }
                returnType = matchingFunction.returnType
                idInJava = matchingFunction.name.convertedValue
                parametersInJava.addAll(args)
                return
            }
            val matchingVariable = context.identifiers.filterIsInstance<VariableIdentifier>().firstOrNull { it.name.value == id }
            if (matchingVariable != null) {
                returnType = matchingVariable.type
                idInJava = matchingVariable.name.convertedValue
                return
            }
            when (context) {
                is EventContext -> {
                    val eventClass = context.event
                    if (eventClass.value != "onEnable") {
                        validateMethod(eventClass.toType(), Name(id, Name.NameType.FUNCTION), callArgs)
                    }
                    val spigotEvent = Events.spigotEvent(eventClass)
                }
                is CommandContext -> {
                    if (id == "player") {
                        returnType = Type(Player::class)
                        idInJava = Name("sender", Name.NameType.VARIABLE)
                        return
                    }
                    if (id == "args") {
                        returnType = Type("String[]")
                        idInJava = Name("args", Name.NameType.VARIABLE)
                    }
                }
                is TimerContext -> {

                }
                is VariableDefinitionContext -> {

                }
            }
            com.rimlang.rim.errors.error("Could not find identifier $id, at line ${idToken.line}", idToken)
        }
        if (previous != null) {
            // TODO: Verify that the previous return type has trait with this name
            if (previous.returnType.type == Block::class.qualifiedName) {
                // TODO: Get persistent data container value
            } else if (Entity::class.java.isAssignableFrom(previous.returnType.type.clazz)) { // if previous return type is an Entity
                // Same thing as above
            } else {
                validateMethod(previous.returnType, Name(id, Name.NameType.FUNCTION), callArgs)
            }
        }
        com.rimlang.rim.errors.error("Could not find identifier $id, at line ${idToken.line}", idToken)
    }

    private fun validateMethod(
        baseClass: Type,
        methodName: Name,
        callArgs: List<List<Token>>?
    ) {
        val method = baseClass.type.clazz.methods
            .filter { it.isDefault }
            .firstOrNull { it.name == methodName.convertedValue.value || it.name.endsWith(methodName.convertedValue.value) } ?: return
        returnType = Type(method.genericReturnType.typeName)
        idInJava = Name(returnType.type.classNameFromQualifiedName(), Name.NameType.CLASS_NAME)
        if (callArgs != null) {
            parametersInJava.addAll(callArgs.map { GenericExpression(it, it[0]) })
        }
    }
}
package com.mcpy.lang.errors

import com.mcpy.lang.bukkitMode
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.log
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.max

data class McPyError(
    val message: String,
    val line: Int,
    val column: Int,
    val file: CodeFile,
) {
    private fun getSpacing(): Int {
        val before = 0.coerceAtLeast(line - 2)
        return (max(before, line) + 1).toString().length
    }

    private fun getLines(windowSize: Int, useBukkitColors: Boolean): List<String> {
        val lines = mutableListOf<String>()

        val before = 0.coerceAtLeast(line - windowSize + 1)
        val length = getSpacing()

        val white = if (useBukkitColors) "&7" else "\u001b[37;1m"
        val blue = if (useBukkitColors) "&b" else "\u001b[34m"
        val reset = if (useBukkitColors) "&r" else "\u001b[0m"

        for (i in before..(line + 1)) {
            val numberLength = i.toString().length
            val prefix = if (numberLength < length) " ".repeat(length - numberLength) else ""
            if (i < 1) continue
            lines.add("$blue${i+1}$prefix$white | $reset${file.readLine(i)}")
        }

        return lines
    }

    fun message(useBukkitColors: Boolean): String {
        val lines = getLines(3, useBukkitColors)
        val spaces = getSpacing()

        val red = if (useBukkitColors) "&c" else "\u001b[31m"
        val reset = if (useBukkitColors) "&r" else "\u001b[0m"
        val white = if (useBukkitColors) "&7" else "\u001b[37;1m"
        val blue = if (useBukkitColors) "&b" else "\u001b[34m"

        val message = listOf(
            "\n$white ——> $blue${file.fileName}$white:$blue${line + 1}$white:$blue$column$reset",
            lines.joinToString("\n"),
            "${" ".repeat(spaces + 2 + column)}$red ^ $message"
        ).joinToString("\n")
        return message
    }

}

fun error(message: String, token: Token): Nothing {
    val error = McPyError(message, token.line, token.character, token.file)
    log(error.message(bukkitMode))
    throw Exception()
}

fun error(message: String, character: Int, line: Int, file: CodeFile): Nothing {
    val error = McPyError(message, line, character, file)
    log(error.message(bukkitMode))
    throw Exception()
}

@OptIn(ExperimentalContracts::class)
fun require(condition: Boolean, token: Token, message: () -> String) {
    contract {
        returns() implies condition // only continues the code if the condition is true and the requirement is met
    }
    if (!condition) {
        error(message(), token)
    }
}

@OptIn(ExperimentalContracts::class)
fun require(condition: Boolean, character: Int, line: Int, file: CodeFile, message: () -> String) {
    contract {
        returns() implies condition // only continues the code if the condition is true and the requirement is met
    }
    if (!condition) {
        error(message(), character, line, file)
    }
}

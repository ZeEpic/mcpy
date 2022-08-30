package com.rimlang.rim.lexer

import com.rimlang.rim.util.nextToken
import com.rimlang.rim.util.sub

object Lexer {

    private var line = -1
    private var lineCharacter = -1
    private var i = 0

    fun lex(code: String): List<Token> {
        line = 0
        lineCharacter = 0
        i = 0
        val tokens = mutableListOf<Token>()
        var token = ""
        val charArray = code.toCharArray()
        while (i < charArray.size) {

            // Add to token
            var string = charArray[i].toString()
            if (string matches Regex("\\w")) {
                token += string
                if (nextCharacter(charArray)) break
                continue
            }

            // Identifier or number
            if (token.isNotEmpty()) {
                if (token matches Regex("-?\\d+(\\.\\d+)?")) {
                    tokens.add(NumberToken(TokenType.NUMBER_LITERAL, token.toDouble(), line, lineCharacter))
                } else {
                    val type = findTokenType(token) // Search for matching keywords
                    tokens.add(
                        StringToken(
                            type ?: TokenType.ID,
                            token,
                            line,
                            lineCharacter
                        )
                    )
                }
                token = ""
            }

            // New line
            if (string == "\n") {
                tokens.add(StringToken(TokenType.EOL, "\\n", line, lineCharacter))
                if (nextCharacter(charArray)) break
                continue
            }

            // Comment
            if (string == "#") {
                while (string != "\n") {
                    if (nextCharacter(charArray)) break
                    string = charArray[i].toString()
                }
                tokens.add(StringToken(TokenType.EOL, "\\n", line, lineCharacter))
                if (nextCharacter(charArray)) break
                continue
            }

            // String literal
            if (string == "'" || string == "\"") {
                val stringType = string
                if (nextCharacter(charArray)) break
                string = charArray[i].toString()
                while (string != stringType) {
                    token += string
                    if (nextCharacter(charArray)) break
                    string = charArray[i].toString()
                }
                tokens.add(StringToken(TokenType.STRING_LITERAL, token, line, lineCharacter))
                token = ""
            }

            // Check for last character
            val type = findTokenType(string)
            if (i == charArray.size - 1) {
                if (type != null) {
                    tokens.add(StringToken(type, string, line, lineCharacter))
                }
                if (nextCharacter(charArray)) break
                continue
            }

            // Two character tokens
            val doubleToken = string + charArray[i + 1]
            val doubleTokenType = findTokenType(doubleToken)
            if (doubleTokenType == null) {
                if (type != null) {
                    tokens.add(StringToken(type, string, line, lineCharacter))
                }
                if (nextCharacter(charArray)) break
                continue
            }
            tokens.add(StringToken(doubleTokenType, doubleToken, line, lineCharacter))
            if (nextCharacter(charArray)) break
            if (nextCharacter(charArray)) break
        }
        return groupBrackets(tokens)
    }

    private fun groupBrackets(code: List<Token>): List<Token> {
        val groupedCode = mutableListOf<Token>()
        var j = 0
        while (j < code.size) {
            val token = code[j]
            val value = token.value
            if (value in listOf("{", "[", "(")) {
                val end = nextToken(code, j + 1, token.type)
                val group = GroupToken(
                    token.type,
                    groupBrackets(sub(code, j + 1, end)),
                    token.line,
                    token.character
                )
                groupedCode.add(group)
                j = end + 1
                continue
            }
            groupedCode.add(token)
            j++
        }
        return groupedCode
    }

    private fun nextCharacter(code: CharArray): Boolean {
        if (i >= code.size - 1) return true
        i++
        lineCharacter++
        if (code[i] == '\n') {
            line++
            lineCharacter = 0
        }
        return false
    }
}
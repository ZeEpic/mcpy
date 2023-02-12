package com.mcpy.lang.lexer.token

enum class TokenType(vararg values: String) {
    ID, NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL("true", "false"),
    EOL("\\n"),
    ASSIGNMENT_OPERATOR("="),
    BOOLEAN_OPERATOR(
        ">",
        "<",
        ">=",
        "<=",
        "!=",
        "=="
    ),
    AND("and"), OR("or"),
    NOT("not"), IS("is"), IN("in"),
    MATH_OPERATOR("+", "-", "*", "**", "/", "%"),
    PARENTHESES("(", ")"), BRACKET("[", "]"), BRACE("{", "}"),
    COMMA(","), DOT("."), COLON(":"), AT("@"),
    IF("if"), ELSE("else"), ELIF("elif"),
    WHILE("while"), FOR("for"), MATCH("match"),
    COMMAND("cmd"), BY("by"),
    TRAIT("trait"), WHEN("when"), TIMER("timer"), GUI("gui"),
    FUNCTION("def"), RETURN("return"), PASS("pass");
    val values = values.toList()
}

fun findTokenType(type: String)
    = TokenType.values().firstOrNull { type in it.values }

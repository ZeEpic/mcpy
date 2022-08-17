package com.rimlang.rim.lexer;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public enum TokenType {
    ID, NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL("true", "false"),
    EOL("\\n"),
    BOOLEAN_OPERATOR( ">", "<", ">=", "<=", "!=", "==", ""),
    MATH_OPERATOR("+", "-", "*", "/", "%"),
    LOGICAL_OPERATOR("and", "or", "not"),
    ASSIGNMENT_OPERATOR("="),
    PARENTHESIS("(", ")"), BRACKET("[", "]"), BRACE("{", "}"),
    COMMA(","), DOT("."), COLON(":"),
    AT("@"),
    IF("if"), ELSE("else"), ELIF("elif"),
    WHILE("while"), FOR("for"), MATCH("match"),
    PRINT("print"),
    RETURN("return"), FUNCTION("fn"), PASS("pass"),
    IS("is"), BY("by"),
    COMMAND("command"), TIMER("timer"), ON_EVENT("on"), TRAIT("trait"),
    TYPE(
            "int", "string", "bool", "list", "function",
            "player", "location", "world", "entity",
            "event", "args",
            "block", "item", "material",
            "permission"
    );
    private final @Nullable List<String> values;

    TokenType(String... values) {
        this.values = (values == null || values.length == 0) ? null : Arrays.asList(values);
    }

    public static @Nullable TokenType findType(String s) {
        AtomicReference<TokenType> result = new AtomicReference<>();
        Arrays.stream(TokenType.values())
                .filter(t -> t.getValues() != null && t.getValues().contains(s))
                .findFirst()
                .ifPresent(result::set);
        return result.get();
    }

    public @Nullable List<String>getValues() {
        return values;
    }

}

package com.rimlang.rim.lexer;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public enum TokenType {
    ID(null), NUMBER_LITERAL(null), STRING_LITERAL(null), EOL("\\n"),
    GT(">"), LT("<"), GTE(">="), LTE("<="), NEQ("!="), NOT("!"), EQ("=="),
    EQUALS("="), PLUS("+"), MINUS("-"), MUL("*"), DIV("/"), MOD("%"),
    LEFT_PARENTHESES("("), RIGHT_PARENTHESES(")"), LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"), LEFT_CURLY_BRACKET("{"), RIGHT_CURLY_BRACKET("}"),
    COMMA(","), PERIOD("."), COLON(":"), AT("@"),
    BY("by"), IF("if"), ELSE("else"), ELIF("elif"), FOR("for"), MATCH("match"), PRINT("print"), FUNCTION("fn"), RETURN("return"), PASS("pass"), IS("is"),
    COMMAND("command"), TIMER("timer"), ON_EVENT("on"),
    TRUE("true"), FALSE("false"),
    INT_TYPE("int"), STRING_TYPE("string"), BOOL_TYPE("bool"), PLAYER_TYPE("player"), BLOCK_TYPE("block"), ITEM_TYPE("item"), LIST_TYPE("list"), ARGS_TYPE("args"),
    FUNCTION_TYPE("function"), LOCATION_TYPE("location"), MATERIAL_TYPE("material"), PERMISSION_TYPE("permission"), WORLD_TYPE("world"), EVENT_TYPE("event"), ENTITY_TYPE("entity");

    private final @Nullable String value;

    TokenType(@Nullable String value) {
        this.value = value;
    }

    public static @Nullable TokenType findType(String s) {
        AtomicReference<TokenType> result = new AtomicReference<>();
        Arrays.stream(TokenType.values())
                .filter(t -> t.getValue() != null && t.getValue().equals(s))
                .findFirst()
                .ifPresent(result::set);
        return result.get();
    }

    public @Nullable String getValue() {
        return value;
    }

}

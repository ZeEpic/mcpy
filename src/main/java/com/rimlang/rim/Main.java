package com.rimlang.rim;

import com.rimlang.rim.lexer.Lexer;
import com.rimlang.rim.lexer.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Main {

    private static String code;

    public static String getCode() {
        return code;
    }

    public static void main(String[] args) {
        code = loadResource("code.rim");
        if (code == null) {
            System.out.println("Could not load code.rim");
            return;
        }
//        System.out.println(TokenType.findType("="));
        System.out.println(Lexer.lex(code));
    }

    private static @Nullable String loadResource(@NotNull String resource) {
        InputStream stream = Main.class.getResourceAsStream("/" + resource);
        if (stream == null) return null;
        return new BufferedReader(new InputStreamReader(stream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
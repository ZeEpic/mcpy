package com.rimlang.rim;

import com.google.googlejavaformat.java.FormatterException;
import com.rimlang.rim.lexer.Lexer;
import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.syntax.RimSyntaxException;
import com.rimlang.rim.syntax.SyntaxAnalyzer;
import com.rimlang.rim.syntax.SyntaxNode;
import com.rimlang.rim.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static String code;

    public static String getCode() {
        return code;
    }

    public static void main(String[] args) throws RimSyntaxException, FormatterException {
        code = loadResource("code.rim");
        if (code == null) {
            System.out.println("Could not load code.rim");
            return;
        }
        List<Token> tokens = Lexer.lex(code);
//        for (int i = 0; i < tokens.size(); i++) {
//            if (i > 50) continue;
//            System.out.println(i + ": " + tokens.get(i));
//        }
        List<SyntaxNode> nodes = SyntaxAnalyzer.analyze(tokens);
//        Compiler.compile(javaCode);
//        JarMaker.create();
        Translator.translate(nodes);
        System.out.println(Translator.complete("ExamplePlugin"));
    }

    public static @Nullable String loadResource(@NotNull String resource) {
        InputStream stream = Main.class.getResourceAsStream("/" + resource);
        if (stream == null) return null;
        return new BufferedReader(new InputStreamReader(stream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
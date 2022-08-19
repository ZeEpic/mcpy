package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.util.TokenUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArgsSyntax implements Syntax {

    private final List<Argument> args = new ArrayList<>();

    public List<Argument> getArgs() {
        return args;
    }

    @Override
    public @Nullable Syntax create(List<Token> tokens) {
        for (List<Token> arg : TokenUtil.split(tokens, TokenType.COMMA)) {
            if (arg.get(0).type() != TokenType.TYPE) return null;
            String type = arg.get(0).value();
            if (arg.get(1).type() != TokenType.ID) return null;
            String name = arg.get(1).value();
            if (arg.get(2).type() != TokenType.COLON) return null;
            if (arg.get(3).type() == TokenType.ASSIGNMENT_OPERATOR) {
                args.add(new Argument(type, name, arg.subList(4, arg.size())));
            } else {
                args.add(new Argument(type, name, null));
            }
        }
        return this;
    }
    public record Argument(String type, String name, @Nullable List<Token> defaultValue) { }

}

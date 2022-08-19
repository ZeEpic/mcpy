package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.util.TokenUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefinitionSyntax implements Syntax {

    private String name;
    private @Nullable String returnType;
    private List<ArgsSyntax.Argument> args = new ArrayList<>();
    private final List<Token> body = new ArrayList<>();

    @Override
    public @Nullable Syntax create(List<Token> tokens) {
        if (tokens.get(0).type() != TokenType.FUNCTION) return null;
        if (tokens.get(1).type() != TokenType.ID) return null;
        name = tokens.get(1).value();
        int index = TokenUtil.next(tokens.subList(3, tokens.size()), TokenType.PARENTHESIS);
        if (index == -1) return null;
        ArgsSyntax syntax = new ArgsSyntax();
        syntax = (ArgsSyntax) syntax.create(tokens.subList(3, index));
        if (syntax == null) return null;
        args = syntax.getArgs();
        if (tokens.get(index + 1).type() == TokenType.COLON) {
            if (tokens.get(index + 2).type() != TokenType.TYPE) return null;
            returnType = tokens.get(index + 2).value();
        }
        Token last = tokens.get(index + 3);
        if (last.type() == TokenType.BRACE) {
            body.addAll(tokens.subList(index + 4, tokens.size()));
        } else if (last.type() == TokenType.ASSIGNMENT_OPERATOR || (last.type() == TokenType.EOL && tokens.get(index + 4).type() == TokenType.ASSIGNMENT_OPERATOR)) {
            body.addAll(TokenUtil.untilEOL(tokens.subList(index + 4, tokens.size())));
        } else {
            return null;
        }
        return this;
    }

    public List<ArgsSyntax.Argument> getArgs() {
        return args;
    }

    public List<Token> getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public @Nullable String getReturnType() {
        return returnType;
    }

}

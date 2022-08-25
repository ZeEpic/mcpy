package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.util.TokenUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArgsExpression extends Expression {

    private final List<Argument> args = new ArrayList<>();

    public ArgsExpression(List<Token> tokens) throws RimSyntaxException {
        for (List<Token> arg : TokenUtil.split(tokens, TokenType.COMMA)) {
            if (arg.get(0).getType() != TokenType.ID) {
                throw new RimSyntaxException("Argument must have identifier", arg.get(0).getLine());
            }
            Token identifier = arg.get(0);
            if (arg.get(1).getType() != TokenType.COLON) {
                throw new RimSyntaxException("Argument must have a separating colon between the identifier and the argument type", arg.get(1).getLine());
            }
            if (arg.get(2).getType() != TokenType.TYPE) {
                throw new RimSyntaxException("Argument must have type", arg.get(2).getLine());
            }
            Token type = arg.get(2);
            if (arg.size() > 4 && arg.get(3).getType() == TokenType.ASSIGNMENT_OPERATOR) {
                args.add(new Argument(type, identifier, TokenUtil.sub(arg, 4)));
            } else {
                args.add(new Argument(type, identifier, null));
            }
        }
    }

    public List<Argument> getArgs() {
        return args;
    }

    public record Argument(Token type, Token identifier, @Nullable List<Token> defaultValue) { }

}

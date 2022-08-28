package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.StringToken;
import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.translation.Translator;
import com.rimlang.rim.util.TokenUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArgsExpression extends Expression {

    private final List<Argument> args = new ArrayList<>();

    public ArgsExpression(List<Token> tokens) throws RimSyntaxException {
        for (List<Token> arg : TokenUtil.split(tokens, TokenType.COMMA)) {
            if (arg.get(0).getType() != TokenType.ID) {
                throw new RimSyntaxException("Argument must have identifier", arg.get(0).getLine());
            }
            StringToken identifier = (StringToken) arg.get(0);
            if (arg.get(1).getType() != TokenType.COLON) {
                throw new RimSyntaxException("Argument must have a separating colon between the identifier and the argument type", arg.get(1).getLine());
            }
            if (arg.get(2).getType() != TokenType.TYPE) {
                System.out.println(arg);
                throw new RimSyntaxException("Argument must have type", arg.get(2).getLine());
            }
            StringToken type = (StringToken) arg.get(2);
            if (arg.size() > 4 && arg.get(3).getType() == TokenType.ASSIGNMENT_OPERATOR) {
                args.add(new Argument(type, identifier, TokenUtil.sub(arg, 4)));
            } else {
                args.add(new Argument(type, identifier, null));
            }
        }
    }

    public String translate() {
        return args.stream().map(Argument::translate).collect(Collectors.joining(", "));
    }

    public List<Argument> getArgs() {
        return args;
    }

    public static class Argument {
        private final StringToken identifier;
        private final StringToken type;
        private final List<Token> defaultValue;

        public Argument(StringToken type, StringToken identifier, @Nullable List<Token> defaultValue) {
            this.type = type;
            this.identifier = identifier;
            this.defaultValue = defaultValue;
        }

        public String translate() {
            return Translator.toJavaType(type) + " " + Translator.getCamelCase(identifier.getValue());
        }
    }

}

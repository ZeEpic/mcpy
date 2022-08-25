package com.rimlang.rim.syntax;

public class RimSyntaxException extends Throwable {

    public RimSyntaxException(String message, int line) {
        super(message + " (on line " + line + ").");
    }
}

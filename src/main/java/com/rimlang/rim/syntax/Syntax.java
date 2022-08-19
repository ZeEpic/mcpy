package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Syntax {

    @Nullable Syntax create(List<Token> tokens);

}

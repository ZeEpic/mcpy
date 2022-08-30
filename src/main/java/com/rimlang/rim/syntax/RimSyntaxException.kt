package com.rimlang.rim.syntax

class RimSyntaxException(message: String, line: Int) : Throwable("$message (on line $line).")
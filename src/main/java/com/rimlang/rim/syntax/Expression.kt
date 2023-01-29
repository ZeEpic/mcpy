package com.rimlang.rim.syntax

import com.rimlang.rim.translation.Context

abstract class Expression : Node {
    abstract fun translate(context: Context): String
}
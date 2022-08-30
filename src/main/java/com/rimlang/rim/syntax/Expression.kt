package com.rimlang.rim.syntax

abstract class Expression : Node {
    abstract fun translate(): String
}
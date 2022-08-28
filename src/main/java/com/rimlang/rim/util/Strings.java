package com.rimlang.rim.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Strings {
    public static String title(String string) {
        return Arrays.stream(string.split(" "))
                .map(word -> String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}

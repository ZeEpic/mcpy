package com.rimlang.rim.util;

import java.util.Arrays;

public class Strings {
    public static String title(String string) {
        return String.join(" ", Arrays.stream(string.split(" "))
                .map(word -> String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1)).toList()
        );
    }
}

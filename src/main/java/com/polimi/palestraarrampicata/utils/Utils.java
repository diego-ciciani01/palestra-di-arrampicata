package com.polimi.palestraarrampicata.utils;

import org.springframework.validation.BindingResult;

public class Utils {

    public static String getErrori(BindingResult result) {
        StringBuilder errori = new StringBuilder();
        result.getFieldErrors().forEach(e -> errori.append(e.getDefaultMessage()).append("\n"));
        return errori.toString();
    }
}

package com.pynexia.week1.signup;

import java.util.regex.Pattern;
public final class FormRules {
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+");
    private FormRules() {}
    public static boolean validName(String value) { return value.trim().length() >= 2 && value.trim().length() <= 80; }
    public static boolean validEmail(String value) { return value.trim().length() <= 254 && EMAIL.matcher(value.trim()).matches(); }
    public static boolean validPassword(String value) {
        return value.length() >= 8 && value.length() <= 128
                && value.codePoints().anyMatch(Character::isLetter)
                && value.codePoints().anyMatch(Character::isDigit);
    }
}

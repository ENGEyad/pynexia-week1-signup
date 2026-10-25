package com.pynexia.week1.signup;

import org.junit.Test;
import static org.junit.Assert.*;
public class FormRulesTest {
    @Test public void validatesNames() { assertTrue(FormRules.validName("إياد")); assertFalse(FormRules.validName("  ")); assertFalse(FormRules.validName("A")); }
    @Test public void validatesEmail() {
        for (String email : new String[]{"learner@example.com", " first.last+tag@sub.example.org "}) assertTrue(FormRules.validEmail(email));
        for (String email : new String[]{"", "user@", "user@localhost", "user name@example.com", "@example.com", "user@-example.com"}) assertFalse(FormRules.validEmail(email));
    }
    @Test public void requiresLettersDigitsAndLength() {
        assertTrue(FormRules.validPassword("Learn@123"));
        for (String value : new String[]{"", "12345678", "abcdefgh", "Abc123", "A1".repeat(65)}) assertFalse(FormRules.validPassword(value));
    }
}

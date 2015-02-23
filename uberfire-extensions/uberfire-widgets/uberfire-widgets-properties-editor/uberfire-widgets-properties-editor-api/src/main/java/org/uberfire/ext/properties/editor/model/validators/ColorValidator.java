package org.uberfire.ext.properties.editor.model.validators;

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColorValidator implements PropertyFieldValidator {

    public static final List<Character> _hexLetters = Arrays.asList('a','b','c','d','e','f');

    public static boolean isValid(String aColor) {
        try {
            String color = aColor.trim().toLowerCase();
            if (color.length() != 6) return false;

            for (int i = 0; i < color.length(); i++) {
                char c = color.charAt(i);
                if (!Character.isDigit(c) && !_hexLetters.contains(c)) {
                    return false;
                }
            }
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    @Override
    public boolean validate(Object value) {
        if (value == null) return false;
        return isValid(value.toString());
    }

    @Override
    public String getValidatorErrorMessage() {
        return "Value must be valid color. Example: 'FFFFFF'";
    }
}

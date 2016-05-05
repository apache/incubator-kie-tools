/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

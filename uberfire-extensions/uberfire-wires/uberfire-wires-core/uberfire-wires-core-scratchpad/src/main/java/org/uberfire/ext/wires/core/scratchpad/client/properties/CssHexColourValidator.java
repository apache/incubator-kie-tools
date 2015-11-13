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
package org.uberfire.ext.wires.core.scratchpad.client.properties;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

/**
 * Validator for CSS colours
 */
@ApplicationScoped
public class CssHexColourValidator implements PropertyFieldValidator {

    private static final List<Character> HEX_DIGITS = Arrays.asList( new Character[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' } );

    @Override
    public boolean validate( Object value ) {
        String hex = value.toString().toLowerCase();
        if ( hex.length() != 7 ) {
            return false;
        }
        if ( !hex.startsWith( "#" ) ) {
            return false;
        }
        for ( Character c : hex.substring( 1 ).toCharArray() ) {
            if ( !HEX_DIGITS.contains( c ) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getValidatorErrorMessage() {
        return "Value must be a CSS colour #rrggbb.";
    }

}

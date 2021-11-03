/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.validator;

import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

import java.util.Arrays;
import java.util.List;

public class LayoutEditorPropertyLengthValidator implements PropertyFieldValidator {

    List<String> SUFFIX_LIST = Arrays.asList("pt", "pc", "cm", "mm", "in", "px", "%");

    @Override
    public boolean validate(Object value) {
        String str = value.toString().toLowerCase();
        if (str.length() == 0) {
            return true;
        }
        for (String suffix : SUFFIX_LIST) {
            if (str.endsWith(suffix)) {
                String n = str.substring(0, str.length()-suffix.length());
                return validateNumber(n);
            }
        }
        return false;
    }

    public boolean validateNumber(String str) {
        if (str.length() == 0) {
            return true;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    @Override
    public String getValidatorErrorMessage() {
        return "";
    }
}
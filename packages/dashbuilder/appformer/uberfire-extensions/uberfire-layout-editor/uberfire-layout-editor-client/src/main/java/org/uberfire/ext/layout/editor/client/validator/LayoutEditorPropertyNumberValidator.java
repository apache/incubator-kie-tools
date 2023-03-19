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

public class LayoutEditorPropertyNumberValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        String str = value.toString();
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
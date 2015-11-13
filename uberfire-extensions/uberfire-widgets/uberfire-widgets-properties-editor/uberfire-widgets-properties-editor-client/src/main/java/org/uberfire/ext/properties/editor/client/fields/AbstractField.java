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

package org.uberfire.ext.properties.editor.client.fields;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

public abstract class AbstractField {

    public abstract Widget widget( PropertyEditorFieldInfo property );

    protected boolean validate( PropertyEditorFieldInfo property,
                                String value ) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                return false;
            }
        }

        return true;
    }

    protected String getValidatorErrorMessage( PropertyEditorFieldInfo property,
                                               String value) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                return validator.getValidatorErrorMessage();
            }
        }

        return "";
    }
}

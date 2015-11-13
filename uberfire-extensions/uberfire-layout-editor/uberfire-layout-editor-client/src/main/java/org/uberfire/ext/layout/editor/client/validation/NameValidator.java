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
package org.uberfire.ext.layout.editor.client.validation;

import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

public class NameValidator {

    public static final String VALID_DIR_REGEX = "^([^*\"\\/><?\\\\\\!|;:]*)$";

    private String error;

    private NameValidator( String error ) {
        this.error = error;
    }

    public static NameValidator layoutNameValidador() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidLayoutName() );
    }

    public static NameValidator tagNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidTagName() );
    }

    public static NameValidator parameterNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidParameterName() );
    }

    public static NameValidator activityIdValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidActivityID() );
    }

    public static NameValidator menuLabelValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidMenuLabel() );
    }

    public String getValidationError() {
        return error;
    }

    public boolean isValid( String dirName ) {
        if ( dirName == null || dirName.isEmpty() ) {
            return Boolean.FALSE;
        }
        if ( !dirName.matches( VALID_DIR_REGEX ) ) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}

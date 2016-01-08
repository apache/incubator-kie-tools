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
package org.uberfire.ext.plugin.client.validation;

import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;

public class NameValidator extends RuleValidator {

    public static final String VALID_DIR_REGEX = "^([^*\"\\/><?\\\\\\!|;:]*)$";

    private String emptyError;

    private String invalidError;

    private String error;

    private NameValidator( String emptyError,
                           String invalidError ) {
        this.emptyError = emptyError;
        this.invalidError = invalidError;
    }

    public static NameValidator createNameValidator( String emptyError,
                                                     String invalidError ) {
        return new NameValidator( emptyError, invalidError );
    }

    public static NameValidator tagNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.EmptyTagName(), CommonConstants.INSTANCE.InvalidTagName() );
    }

    public static NameValidator parameterNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.EmptyParameterName(), CommonConstants.INSTANCE.InvalidParameterName() );
    }

    public String getValidationError() {
        return error;
    }

    public boolean isValid( String dirName ) {
        if ( dirName == null || dirName.trim().isEmpty() ) {
            this.error = this.emptyError;
            return Boolean.FALSE;
        }

        if ( !dirName.matches( VALID_DIR_REGEX ) ) {
            this.error = this.invalidError;
            return Boolean.FALSE;
        }

        this.error = null;
        return Boolean.TRUE;
    }
}

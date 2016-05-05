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

package org.uberfire.ext.apps.client.home.components.popup;

import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.client.resources.i18n.CommonConstants;

public class DirectoryNameValidator {

    public static final String VALID_DIR_REGEX = "^([^*\"\\/><?\\\\\\!|;:]*)$";
    private final Directory currentDirectory;

    public DirectoryNameValidator( Directory currentDirectory ) {
        this.currentDirectory = currentDirectory;
    }

    public String getValidationError() {
        return CommonConstants.INSTANCE.InvalidDirName();
    }

    public boolean isValid( String dirName ) {
        if ( dirName == null || dirName.trim().isEmpty() ) {
            return Boolean.FALSE;
        }
        if ( !dirName.matches( VALID_DIR_REGEX ) ) {
            return Boolean.FALSE;
        }
        if ( currentDirectory.alreadyHasChild( dirName ) ) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}

package org.uberfire.ext.apps.client.home.components.popup;

import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.client.resources.i18n.CommonConstants;

public class DirectoryNameValidator {

    public static final String VALID_DIR_REGEX = "^([a-zA-Z0-9][^*\"\\/><?\\\\\\!|;:]*)$";
    private final Directory currentDirectory;

    public DirectoryNameValidator( Directory currentDirectory ) {
        this.currentDirectory = currentDirectory;
    }

    public String getValidationError() {
        return CommonConstants.INSTANCE.InvalidDirName();
    }

    public boolean isValid( String dirName ) {
        if ( dirName == null || dirName.isEmpty() ) {
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

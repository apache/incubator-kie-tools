package org.uberfire.ext.perspective.editor.client.panels.components.popup;

import org.uberfire.ext.perspective.editor.client.resources.i18n.CommonConstants;

public class NameValidator {

    public static final String VALID_DIR_REGEX = "^([a-zA-Z0-9][^*\"\\/><?\\\\\\!|;:]*)$";

    private String error;

    private NameValidator( String error ){
        this.error = error;
    }

    public static NameValidator perspectiveNameValidator(){
        return new NameValidator( CommonConstants.INSTANCE.InvalidPerspectiveName());
    }

    public static NameValidator tagNameValidator(){
        return new NameValidator( CommonConstants.INSTANCE.InvalidTagName());
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

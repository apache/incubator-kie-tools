package org.uberfire.ext.plugin.client.perspective.editor.components.popup;

import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;


public class NameValidator {

    public static final String VALID_DIR_REGEX = "^([^*\"\\/><?\\\\\\!|;:]*)$";

    private String error;

    private NameValidator( String error ) {
        this.error = error;
    }

    public static NameValidator perspectiveNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidPerspectiveName() );
    }

    public static NameValidator tagNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidTagName() );
    }

    public static NameValidator parameterNameValidator() {
        return new NameValidator( CommonConstants.INSTANCE.InvalidParameterName() );
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

package org.uberfire.properties.editor.model.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LongValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        try {
            Long.parseLong( value.toString() );
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    @Override
    public String getValidatorErrorMessage() {
        return "Value must be a number.";
    }
}

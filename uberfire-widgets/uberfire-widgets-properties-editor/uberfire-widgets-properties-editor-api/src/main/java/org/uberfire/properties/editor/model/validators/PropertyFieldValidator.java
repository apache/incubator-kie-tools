package org.uberfire.properties.editor.model.validators;

public interface PropertyFieldValidator {

    public boolean validate( Object value );

    public String getValidatorErrorMessage();

}

package org.uberfire.properties.editor.model.validators;

/**
 * A validator of PropertyEditorFields. A field can contain multiples validators.
 */
public interface PropertyFieldValidator {

    /**
     * Validate a field new value
     * @param value
     * @return
     */
    public boolean validate( Object value );

    /**
     * Error message used in property editor.
     * @return
     */
    public String getValidatorErrorMessage();

}

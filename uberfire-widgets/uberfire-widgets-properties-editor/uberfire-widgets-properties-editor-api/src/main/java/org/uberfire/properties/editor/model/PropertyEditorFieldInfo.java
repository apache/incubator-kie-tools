package org.uberfire.properties.editor.model;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.properties.editor.model.validators.PropertyFieldValidator;

@Portable
/**
 * In Property Editor, PropertyEditorFieldInfo is a child of PropertyEditorCategory.
 * One PropertyEditorCategory contains multiple PropertyEditorFieldInfo.
 */
public class PropertyEditorFieldInfo {

    private String label;
    private String currentStringValue;
    private String originalValue;
    private PropertyEditorCategory category;
    private PropertyEditorType type;
    private List<String> comboValues;
    private int priority = Integer.MAX_VALUE;
    private List<PropertyFieldValidator> validators = new ArrayList<PropertyFieldValidator>();
    private String key;

    public PropertyEditorFieldInfo() {
    }

    /**
     * Create a PropertyEditorFieldInfo
     * @param label field descriptor
     * @param type Property Editor Type of this field
     */
    public PropertyEditorFieldInfo( String label,
                                    PropertyEditorType type ) {
        this.label = checkNotNull( "label", label );
        this.originalValue = currentStringValue;
        this.type = checkNotNull( "type", type );
        this.validators.addAll( type.getValidators() );
    }

    /**
     * Create a PropertyEditorFieldInfo
     * @param label field descriptor
     * @param type Property Editor Type of this field
     * @param currentStringValue Current value of this field
     */
    public PropertyEditorFieldInfo( String label,
                                    String currentStringValue,
                                    PropertyEditorType type ) {
        this.label = checkNotNull( "label", label );
        this.currentStringValue = checkNotNull( "currentStringValue", currentStringValue );
        this.originalValue = currentStringValue;
        this.type = checkNotNull( "type", type );
        this.validators.addAll( type.getValidators() );
    }

    /**
     * Key is a helper to identify a field. Sometimes labels can have complex descriptions.
     * This key is a nice way to identify a property in a PropertyEditorChangeEvent.
     * @param key
     */
    public PropertyEditorFieldInfo withKey( String key ) {
        this.key = checkNotNull( "key", key );
        return this;
    }

    /**
     * Combo values used in PropertyEditorType.COMBO fields.
     * @param comboValues a list of combo values
     */
    public PropertyEditorFieldInfo withComboValues( List<String> comboValues ) {
        this.comboValues = checkNotNull( "comboValues", comboValues );
        return this;
    }

    /**
     * The priority value is used to sort the categories.
     * @param priority  (zero means the the highest priority).
     * @return
     */
    public PropertyEditorFieldInfo withPriority( int priority ) {
        this.priority = checkNotNull( "priority", priority );
        return this;
    }

    /**
     * Add validators to a specific field. This validators are executed before the change event.
     * @param validators
     * @return
     */
    public PropertyEditorFieldInfo withValidators( PropertyFieldValidator... validators ) {
        checkNotNull( "validators", validators );
        for ( PropertyFieldValidator field : validators ) {
            this.validators.add( field );
        }
        return this;
    }

    public List<String> getComboValues() {
        return comboValues;
    }

    public PropertyEditorType getType() {
        return type;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getCurrentStringValue() {
        return currentStringValue;
    }

    public void setCurrentStringValue( String currentStringValue ) {
        this.currentStringValue = currentStringValue;
    }

    public void setPropertyEditorCategory( PropertyEditorCategory category ) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public List<PropertyFieldValidator> getValidators() {
        return validators;
    }

    public String getEventId() {
        return category.getIdEvent();
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "PropertyEditorFieldInfo{" +
                "label=" + label +
                ", currentStringValue=" + currentStringValue +
                ", originalValue=" + originalValue +
                ", category=" + category +
                ", type=" + type +
                ", comboValues=" + comboValues +
                ", priority=" + priority +
                ", validators=" + validators +
                ", key=" + key +
                '}';
    }
}


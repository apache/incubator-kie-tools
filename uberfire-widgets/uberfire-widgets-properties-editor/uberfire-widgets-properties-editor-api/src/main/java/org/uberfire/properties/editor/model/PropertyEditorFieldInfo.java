package org.uberfire.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.properties.editor.model.validators.PropertyFieldValidator;

@Portable
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

    public PropertyEditorFieldInfo( String label,
                                    PropertyEditorType type ) {
        this.label = label;
        this.originalValue = currentStringValue;
        this.type = type;
        this.validators.addAll( type.getValidators() );
    }

    public PropertyEditorFieldInfo( String label,
                                    String currentStringValue,
                                    PropertyEditorType type ) {
        this.label = label;
        this.currentStringValue = currentStringValue;
        this.originalValue = currentStringValue;
        this.type = type;
        this.validators.addAll( type.getValidators() );
    }

    public PropertyEditorFieldInfo withKey( String key ) {
        this.key = key;
        return this;
    }

    public PropertyEditorFieldInfo withComboValues( List<String> comboValues ) {
        this.comboValues = comboValues;
        return this;
    }

    public PropertyEditorFieldInfo withPriority( int priority ) {
        this.priority = priority;
        return this;
    }

    public PropertyEditorFieldInfo withValidators( PropertyFieldValidator... validators ) {

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

}


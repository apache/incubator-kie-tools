package org.uberfire.properties.editor.model;

public class PropertyEditorChangeEvent {

    private final PropertyEditorFieldInfo property;
    private final String newValue;
    private final String idEvent;

    public PropertyEditorChangeEvent( PropertyEditorFieldInfo property,
                                      String newValue ) {
        this.idEvent = property.getEventId();
        this.property = property;
        this.newValue = newValue;
    }

    public PropertyEditorFieldInfo getProperty() {
        return property;
    }

    public String getNewValue() {
        return newValue;
    }
}

package org.uberfire.properties.editor.model;

/**
 * A Property Editor CDI event.
 * Class that groups a PropertyEditorChangeEvent information.
 * This event is fired by property editor when a fields has its value changed.
 */
public class PropertyEditorChangeEvent {

    private final PropertyEditorFieldInfo property;
    private final String newValue;
    private final String idEvent;

    /**
     * Creates a new PropertyEditorChangeEvent
     * @param property the property changed
     * @param newValue the new  value of the field
     */
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

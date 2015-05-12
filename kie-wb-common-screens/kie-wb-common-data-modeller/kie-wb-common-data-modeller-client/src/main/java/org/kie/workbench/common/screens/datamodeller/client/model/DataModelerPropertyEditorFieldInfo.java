package org.kie.workbench.common.screens.datamodeller.client.model;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.ext.properties.editor.model.CustomPropertyEditorFieldInfo;

public class DataModelerPropertyEditorFieldInfo extends CustomPropertyEditorFieldInfo {

    protected DataObject currentDataObject;

    protected ObjectProperty currentObjectProperty;

    protected Annotation currentValue;

    protected Annotation newValue;

    protected Map<String, Object> currentValues = new HashMap<String, Object>( );


    public DataModelerPropertyEditorFieldInfo( String label, String currentStringValue, Class<?> customEditorClass,
            DataObject currentDataObject, ObjectProperty currentObjectProperty,
            Annotation currentValue, Annotation newValue ) {

        super( label, currentStringValue, customEditorClass );
        this.currentDataObject = currentDataObject;
        this.currentObjectProperty = currentObjectProperty;
        this.currentValue = currentValue;
        this.newValue = newValue;
    }

    public DataModelerPropertyEditorFieldInfo( String label, String currentStringValue, Class<?> customEditorClass ) {
        super( label, currentStringValue, customEditorClass );
    }

    public Annotation getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue( Annotation currentValue ) {
        this.currentValue = currentValue;
    }

    public Annotation getNewValue() {
        return newValue;
    }

    public void setNewValue( Annotation newValue ) {
        this.newValue = newValue;
    }

    public void removeCurrentValue( String name ) {
        currentValues.remove( name );
    }

    public void setCurrentValue(String name, Object value ) {
        currentValues.put( name, value );
    }

    public Object getCurrentValue( String name ) {
        return currentValues.get( name );
    }

    public void cleanCurrentValues() {
        currentValues.clear();
    }

}

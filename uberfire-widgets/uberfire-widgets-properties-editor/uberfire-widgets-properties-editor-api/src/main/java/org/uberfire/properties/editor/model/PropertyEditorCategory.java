package org.uberfire.properties.editor.model;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
/**
 * A grouping of PropertyEditorFieldInfo in a property editor.
 * The priority value is used to sort the categories. (lower values toward the beginning).
 */
public class PropertyEditorCategory {

    private String name;
    private int priority = Integer.MAX_VALUE;
    private List<PropertyEditorFieldInfo> fields = new ArrayList<PropertyEditorFieldInfo>();
    private String idEvent;

    public PropertyEditorCategory(){

    }
    public PropertyEditorCategory(String name) {
        this.name = checkNotNull( "name", name );
    }

    public PropertyEditorCategory( String name,
                                   int priority ) {
        this.name = checkNotNull( "name", name );
        this.priority = checkNotNull( "name", priority );
    }

    /**
     * Add a field to a PropertyEditorCategory
     */
    public PropertyEditorCategory withField( PropertyEditorFieldInfo field ) {
        checkNotNull( "field", field );
        field.setPropertyEditorCategory( this );
        fields.add( field );
        return this;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public List<PropertyEditorFieldInfo> getFields() {
        return fields;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + priority;
        result = 31 * result + ( fields != null ? fields.hashCode() : 0 );
        return result;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent( String idEvent ) {
        this.idEvent = idEvent;
    }

    @Override
    public String toString() {
        return "PropertyEditorCategory{" +
                "name=" + name +
                ", priority=" + priority +
                ", fields=" + fields +
                ", idEvent=" + idEvent +
                '}';
    }

}

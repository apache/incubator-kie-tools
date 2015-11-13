/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import static org.uberfire.commons.validation.PortablePreconditions.*;

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

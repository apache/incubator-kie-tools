/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.forms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Portable
public class FormDefinition {
    private String id;
    private String name;
    private FormModel model;

    private List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

    private LayoutTemplate layoutTemplate;

    public FormDefinition() {
    }

    public FormDefinition( @MapsTo( "model" ) FormModel model ) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate( LayoutTemplate layoutTemplate ) {
        this.layoutTemplate = layoutTemplate;
    }

    public FormModel getModel() {
        return model;
    }

    public void setModel( FormModel model ) {
        this.model = model;
    }

    public FieldDefinition getFieldByBinding( final String binding ) {
        return getFieldBy( field -> field.getBinding() != null && field.getBinding().equals( binding ) );
    }

    public FieldDefinition getFieldByName( final String name ) {
        return getFieldBy( field -> field.getName().equals( name ) );
    }

    public FieldDefinition getFieldById( final String fieldId ) {
        return getFieldBy( field -> field.getId().equals( fieldId ) );
    }

    protected FieldDefinition getFieldBy( Predicate<FieldDefinition> predicate ) {
        if ( predicate != null ) {
            Optional<FieldDefinition> result = fields.stream().filter( predicate ).findFirst();
            if ( result.isPresent() ) {
                return result.get();
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = ~~result;
        return result;
    }
}

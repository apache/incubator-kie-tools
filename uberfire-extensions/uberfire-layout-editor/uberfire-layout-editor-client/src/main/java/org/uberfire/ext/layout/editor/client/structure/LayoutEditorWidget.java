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

package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.LayoutTemplateAdapter;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LayoutEditorWidget implements EditorWidget {

    private ComplexPanel container;

    private List<EditorWidget> rowEditors = new ArrayList<EditorWidget>();

    public Map<String, LayoutComponent> componentMap = new HashMap<String, LayoutComponent>();

    public Map<String, String> layoutProperties = new HashMap<String, String>();

    private String name = "";

    public LayoutEditorWidget() {
    }

    public void setup( final ComplexPanel container,
                       final LayoutTemplate layoutTemplate ) {
        this.name = layoutTemplate.getName();
        this.container = container;
        this.rowEditors = new ArrayList<EditorWidget>();
        this.componentMap = new HashMap<String, LayoutComponent>();
        if ( layoutTemplate.getLayoutProperties() != null ) {
            this.layoutProperties = layoutTemplate.getLayoutProperties();
        }
    }

    @Override
    public EditorWidget getParent() {
        return null;
    }

    @Override
    public ComplexPanel getWidget() {
        return container;
    }

    @Override
    public List<EditorWidget> getChildren() {
        return rowEditors;
    }

    @Override
    public void addChild( EditorWidget child ) {
        rowEditors.add( child );
    }

    public ComplexPanel getContainer() {
        return container;
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        rowEditors.remove( editorWidget );
    }

    public LayoutTemplate toLayoutTemplate() {
        LayoutTemplateAdapter adapter = new LayoutTemplateAdapter( this );
        return adapter.convertToLayoutEditor();
    }

    public List<EditorWidget> getRowEditors() {
        return rowEditors;
    }

    public LayoutComponent getLayoutComponent( EditorWidget component ) {
        LayoutComponent layoutComponent = this.componentMap.get( String.valueOf( component.hashCode() ) );
        if ( layoutComponent == null ) {
            layoutComponent = new LayoutComponent( component.getType().getClass().getName() );
            this.componentMap.put( String.valueOf( component.hashCode() ), layoutComponent );
        }
        return layoutComponent;
    }

    public Map<String, String> getLayoutComponentProperties( EditorWidget component ) {
        LayoutComponent layoutComponent = getLayoutComponent( component );
        return layoutComponent.getProperties();
    }

    public void registerLayoutComponent( EditorWidget component,
                                         LayoutComponent editor ) {
        this.componentMap.put( String.valueOf( component.hashCode() ), editor );
    }

    public String getName() {
        return name;
    }

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

    public void addPropertyToLayoutComponent( EditorWidget component,
                                              String key,
                                              String value ) {
        final LayoutComponent layoutComponent = getLayoutComponent( component );
        layoutComponent.addProperty( key, value );
    }

    public void addLayoutProperty( String key,
                                   String value ) {
        layoutProperties.put( key, value );
    }

    public void addPropertyToLayoutComponentByKey( String componentKey,
                                                   String key,
                                                   String value ) {
        LayoutComponent layoutComponent = this.componentMap.get( componentKey );
        if ( layoutComponent != null ) {
            layoutComponent.addProperty( key, value );
        }
    }

    public void resetLayoutComponentProperties( EditorWidget component ) {
        LayoutComponent layoutComponent = new LayoutComponent( component.getType().getClass() );
        this.componentMap.put( String.valueOf( component.hashCode() ), layoutComponent );
    }

    public String getLayoutProperty( String key ) {
        return layoutProperties.get( key );
    }

    public Map<String, String> getLayoutProperties() {
        return layoutProperties;
    }

    public void removeLayoutComponentProperty( EditorWidget component,
                                               String key ) {
        LayoutComponent layoutComponent = getLayoutComponent( component );
        Map<String, String> properties = layoutComponent.getProperties();
        properties.remove( key );
    }
}

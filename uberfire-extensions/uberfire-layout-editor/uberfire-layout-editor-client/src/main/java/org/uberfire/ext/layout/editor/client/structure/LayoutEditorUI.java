package org.uberfire.ext.layout.editor.client.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.LayoutEditorEditorAdapter;

@ApplicationScoped
public class LayoutEditorUI implements EditorWidget {

    private FlowPanel container;

    private List<EditorWidget> rowEditors = new ArrayList<EditorWidget>();

    public Map<String, LayoutComponent> layoutComponentProperties = new HashMap<String, LayoutComponent>();

    public Map<String, String> layoutProperties = new HashMap<String, String>();

    private String name = "";

    public LayoutEditorUI() {

    }

    public void setup( FlowPanel container,
                       LayoutEditor layoutEditor ) {
        this.name = layoutEditor.getName();
        this.container = container;
        this.rowEditors = new ArrayList<EditorWidget>();
        this.layoutComponentProperties = new HashMap<String, LayoutComponent>();
        if ( layoutEditor.getLayoutProperties() != null ) {
            this.layoutProperties = layoutEditor.getLayoutProperties();
        }
    }

    public FlowPanel getWidget() {
        return container;
    }

    public void addChild( EditorWidget child ) {
        rowEditors.add( child );
    }

    public FlowPanel getContainer() {
        return container;
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        rowEditors.remove( editorWidget );
    }

    public LayoutEditor toLayoutEditor() {
        LayoutEditorEditorAdapter adapter = new LayoutEditorEditorAdapter( this );
        return adapter.convertToLayoutEditor();
    }

    public List<EditorWidget> getRowEditors() {
        return rowEditors;
    }

    public LayoutComponent getLayoutComponent( EditorWidget component ) {
        LayoutComponent layoutComponent = this.layoutComponentProperties.get( String.valueOf( component.hashCode() ) );
        if ( layoutComponent == null ) {
            layoutComponent = new LayoutComponent( component.getType().getClass().getName() );
            this.layoutComponentProperties.put( String.valueOf( component.hashCode() ), layoutComponent );
        }
        return layoutComponent;
    }

    public Map<String, String> getLayoutComponentProperties( EditorWidget component ) {
        LayoutComponent layoutComponent = getLayoutComponent( component );
        return layoutComponent.getProperties();
    }

    public void loadComponentProperties( EditorWidget component,
                                         LayoutComponent editor ) {
        this.layoutComponentProperties.put( String.valueOf( component.hashCode() ), editor );
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
        LayoutComponent layoutComponent = this.layoutComponentProperties.get( componentKey );
        if ( layoutComponent != null ) {
            layoutComponent.addProperty( key, value );
        }
    }

    public void resetLayoutComponentProperties( EditorWidget component ) {
        LayoutComponent layoutComponent = new LayoutComponent( component.getType().getClass());
        this.layoutComponentProperties.put( String.valueOf( component.hashCode() ), layoutComponent );
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

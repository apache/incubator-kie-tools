package org.uberfire.ext.plugin.client.perspective.editor.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.plugin.client.perspective.editor.util.PerspectiveEditorAdapter;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.editor.ScreenEditor;
import org.uberfire.ext.plugin.editor.ScreenParameter;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@ApplicationScoped
public class PerspectiveEditorUI implements EditorWidget {

    private FlowPanel container;

    private List<EditorWidget> rowEditors = new ArrayList<EditorWidget>();

    public static String PROPERTY_EDITOR_KEY = "PerspectiveEditor";

    public Map<String, ScreenEditor> screenProperties = new HashMap<String, ScreenEditor>();

    private List<String> tags;

    private String name = "";

    public PerspectiveEditorUI() {

    }

    public void setup( FlowPanel container, String name ) {
        this.name = name;
        this.container = container;
        this.rowEditors = new ArrayList<EditorWidget>();
        this.screenProperties = new HashMap<String, ScreenEditor>();
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

    public PerspectiveEditor toPerspectiveEditor() {
        PerspectiveEditorAdapter adapter = new PerspectiveEditorAdapter( this );
        return adapter.convertToPerspectiveEditor();
    }

    public List<EditorWidget> getRowEditors() {
        return rowEditors;
    }

    public void observeEditComponentEventFromPropertyEditor( @Observes PropertyEditorChangeEvent event ) {

        PropertyEditorFieldInfo property = event.getProperty();
        if ( property.getEventId().equalsIgnoreCase( PROPERTY_EDITOR_KEY ) ) {
            ScreenEditor screenEditor = screenProperties.get( property.getKey() );
            screenEditor.setParameterValue( property.getLabel(),
                                            property.getCurrentStringValue() );
            screenProperties.put( property.getKey(), screenEditor );
        }
    }

    public void loadEditExternalComponentEvent( String hashcode,
                                                String componentFQCN,
                                                String placeName,
                                                Map<String, String> properties ) {
        ScreenEditor screenEditor = getScreenProperties( hashcode );
        screenEditor.setPlaceName( placeName );
        screenEditor.setType( ScreenEditor.SCREEN_TYPE.EXTERNAL );
        screenEditor.setExternalComponentFQCN( componentFQCN );
        for ( String key : properties.keySet() ) {
            screenEditor.addParameters( new ScreenParameter( key, properties.get( key ) ) );
        }
        screenProperties.put( hashcode, screenEditor );
    }

    public ScreenEditor getScreenProperties( String hashcode ) {
        ScreenEditor screenEditor = this.screenProperties.get( hashcode );
        if ( screenEditor == null ) {
            screenEditor = new ScreenEditor();
        }
        this.screenProperties.put( hashcode, screenEditor );
        return screenEditor;
    }

    public void loadProperties( String hashcode,
                                ScreenEditor editor ) {
        this.screenProperties.put( hashcode, editor );
    }

    public void addParameter( String hashcode,
                              ScreenParameter parameter ) {
        ScreenEditor screenEditor = this.screenProperties.get( hashcode );
        if ( screenEditor == null ) {
            screenEditor = new ScreenEditor();
        }
        screenEditor.addParameters( parameter );

        this.screenProperties.put( hashcode, screenEditor );
    }

    public String getName() {
        return name;
    }

    public void setTags( List<String> tags ) {
        if ( tags == null ) {
            tags = new ArrayList<String>();
        }
        this.tags = tags;
    }

    public List<String> getTags() {
        if ( tags == null ) {
            return new ArrayList<String>();
        }
        return tags;
    }
}

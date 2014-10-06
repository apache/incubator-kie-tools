package org.kie.uberfire.perspective.editor.client.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorJSON;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.perspective.editor.client.util.PerspectiveEditorJSONAdapter;
import org.kie.uberfire.properties.editor.model.PropertyEditorChangeEvent;
import org.kie.uberfire.properties.editor.model.PropertyEditorFieldInfo;

@ApplicationScoped
public class PerspectiveEditor implements EditorWidget {

    private String name = "";

    private FlowPanel container;

    private List<EditorWidget> rowEditors = new ArrayList<EditorWidget>();

    public static String PROPERTY_EDITOR_KEY = "PerspectiveEditor";

    public Map<String, Map<String, String>> screenProperties = new HashMap<String, Map<String, String>>();

    public PerspectiveEditor() {

    }

    public void setup( FlowPanel container ) {
        this.container = container;
        this.rowEditors = new ArrayList<EditorWidget>();
        this.screenProperties = new HashMap<String, Map<String, String>>();
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

    public PerspectiveEditorJSON toJSONStructure() {
        PerspectiveEditorJSONAdapter adapter = new PerspectiveEditorJSONAdapter( this );
        return adapter.convertToJSON();
    }

    public List<EditorWidget> getRowEditors() {
        return rowEditors;
    }

    public void observeEditComponentEvent( @Observes PropertyEditorChangeEvent event ) {

        PropertyEditorFieldInfo property = event.getProperty();
        if ( property.getEventId().equalsIgnoreCase( PROPERTY_EDITOR_KEY ) ) {
            Map<String, String> screenMap = screenProperties.get( property.getKey() );
            screenMap.put( property.getLabel(), property.getCurrentStringValue() );
            screenProperties.put( property.getKey(), screenMap );
        }
    }

    public Map<String, String> getScreenProperties( String hashcode ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
            screenMap.put( "Screen Name", " " );
        }
        this.screenProperties.put( hashcode, screenMap );
        return screenMap;
    }

    public void loadProperties( String hashcode,
                                List<ScreenParameter> parameters ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
        }
        for ( ScreenParameter parameter : parameters ) {
            screenMap.put( parameter.getKey(), parameter.getValue() );
        }
        this.screenProperties.put( hashcode, screenMap );
    }

    public void addParameter( String hashcode,
                              ScreenParameter parameter ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
        }
        screenMap.put( parameter.getKey(), parameter.getValue() );

        this.screenProperties.put( hashcode, screenMap );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

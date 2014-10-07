package org.kie.uberfire.perspective.editor.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.uberfire.perspective.editor.model.ColumnEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;

public class PerspectiveEditorJSONAdapter {

    private final org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor perspectiveEditor;

    public PerspectiveEditorJSONAdapter( org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor perspectiveEditor ) {
        this.perspectiveEditor = perspectiveEditor;
    }

    public PerspectiveEditor convertToJSON() {
        PerspectiveEditor perspectiveJSON = new PerspectiveEditor( perspectiveEditor.getName());
        extractRows( perspectiveEditor, perspectiveJSON );
        return perspectiveJSON;
    }

    private void extractRows( org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor perspectiveEditor,
                              PerspectiveEditor perspectiveJSON ) {
        for ( EditorWidget genericEditor : perspectiveEditor.getRowEditors() ) {
            org.kie.uberfire.perspective.editor.client.structure.RowEditor rowEditor = (org.kie.uberfire.perspective.editor.client.structure.RowEditor) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            perspectiveJSON.addRowJSON( rowJSON );
        }
    }

    private void extractRows( org.kie.uberfire.perspective.editor.client.structure.ColumnEditor columnEditor,
                              ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            org.kie.uberfire.perspective.editor.client.structure.RowEditor rowEditor = (org.kie.uberfire.perspective.editor.client.structure.RowEditor) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            columnEditorJSON.addRowJSON( rowJSON );
        }
    }

    private void extractColumns( org.kie.uberfire.perspective.editor.client.structure.RowEditor rowEditor,
                                 RowEditor rowJSON ) {
        for ( EditorWidget genericEditor : rowEditor.getColumnEditors() ) {
            org.kie.uberfire.perspective.editor.client.structure.ColumnEditor columnEditor = (org.kie.uberfire.perspective.editor.client.structure.ColumnEditor) genericEditor;
            ColumnEditor columnEditorJSON = new ColumnEditor( columnEditor.getSpan() );
            if ( !columnEditor.getChilds().isEmpty() ) {
                extractChilds( columnEditor, columnEditorJSON );
            }
            rowJSON.add( columnEditorJSON );
        }
    }

    private void extractChilds( org.kie.uberfire.perspective.editor.client.structure.ColumnEditor columnEditor,
                                ColumnEditor columnEditorJSON ) {
        //ederign
        if ( columnEditor.getChilds().get( 0 ) instanceof org.kie.uberfire.perspective.editor.client.structure.RowEditor ) {
            extractRows( columnEditor, columnEditorJSON );
        }
        if ( columnEditor.getChilds().get( 0 ) instanceof org.kie.uberfire.perspective.editor.client.structure.ScreenEditor ) {
            extractScreens( columnEditor, columnEditorJSON );
        }
    }

    private void extractScreens( org.kie.uberfire.perspective.editor.client.structure.ColumnEditor columnEditor,
                                 ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            org.kie.uberfire.perspective.editor.client.structure.ScreenEditor screenEditor = (org.kie.uberfire.perspective.editor.client.structure.ScreenEditor) genericEditor;
            List<ScreenParameter> parameteres = getScreenParameters(screenEditor);
            ScreenEditor screenEditorJSON = new ScreenEditor( parameteres );
            columnEditorJSON.addScreenJSON( screenEditorJSON );
        }
    }

    public List<ScreenParameter> getScreenParameters( org.kie.uberfire.perspective.editor.client.structure.ScreenEditor screenEditor ) {

        List<ScreenParameter> screenParameters  = new ArrayList<ScreenParameter>(  );
        Map<String, String> screenProperties = perspectiveEditor.getScreenProperties( screenEditor.hashCode() + "" );
        for ( String key : screenProperties.keySet() ) {
            screenParameters.add(new ScreenParameter(key, screenProperties.get(key) ));
        }

        return screenParameters;
    }
}

package org.kie.uberfire.perspective.editor.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.uberfire.perspective.editor.model.ColumnEditorJSON;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorJSON;
import org.kie.uberfire.perspective.editor.model.RowEditorJSON;
import org.kie.uberfire.perspective.editor.model.ScreenEditorJSON;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.perspective.editor.client.structure.ColumnEditor;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.client.structure.RowEditor;
import org.kie.uberfire.perspective.editor.client.structure.ScreenEditor;

public class PerspectiveEditorJSONAdapter {

    private final PerspectiveEditor perspectiveEditor;

    public PerspectiveEditorJSONAdapter( PerspectiveEditor perspectiveEditor ) {
        this.perspectiveEditor = perspectiveEditor;
    }

    public PerspectiveEditorJSON convertToJSON() {
        PerspectiveEditorJSON perspectiveJSON = new PerspectiveEditorJSON( perspectiveEditor.getName());
        extractRows( perspectiveEditor, perspectiveJSON );
        return perspectiveJSON;
    }

    private void extractRows( PerspectiveEditor perspectiveEditor,
                              PerspectiveEditorJSON perspectiveJSON ) {
        for ( EditorWidget genericEditor : perspectiveEditor.getRowEditors() ) {
            RowEditor rowEditor = (RowEditor) genericEditor;
            RowEditorJSON rowJSON = new RowEditorJSON( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            perspectiveJSON.addRowJSON( rowJSON );
        }
    }

    private void extractRows( ColumnEditor columnEditor,
                              ColumnEditorJSON columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            RowEditor rowEditor = (RowEditor) genericEditor;
            RowEditorJSON rowJSON = new RowEditorJSON( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            columnEditorJSON.addRowJSON( rowJSON );
        }
    }

    private void extractColumns( RowEditor rowEditor,
                                 RowEditorJSON rowJSON ) {
        for ( EditorWidget genericEditor : rowEditor.getColumnEditors() ) {
            ColumnEditor columnEditor = (ColumnEditor) genericEditor;
            ColumnEditorJSON columnEditorJSON = new ColumnEditorJSON( columnEditor.getSpan() );
            if ( !columnEditor.getChilds().isEmpty() ) {
                extractChilds( columnEditor, columnEditorJSON );
            }
            rowJSON.add( columnEditorJSON );
        }
    }

    private void extractChilds( ColumnEditor columnEditor,
                                ColumnEditorJSON columnEditorJSON ) {
        //ederign
        if ( columnEditor.getChilds().get( 0 ) instanceof RowEditor ) {
            extractRows( columnEditor, columnEditorJSON );
        }
        if ( columnEditor.getChilds().get( 0 ) instanceof ScreenEditor ) {
            extractScreens( columnEditor, columnEditorJSON );
        }
    }

    private void extractScreens( ColumnEditor columnEditor,
                                 ColumnEditorJSON columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            ScreenEditor screenEditor = (ScreenEditor) genericEditor;
            List<ScreenParameter> parameteres = getScreenParameters(screenEditor);
            ScreenEditorJSON screenEditorJSON = new ScreenEditorJSON( parameteres );
            columnEditorJSON.addScreenJSON( screenEditorJSON );
        }
    }

    public List<ScreenParameter> getScreenParameters( ScreenEditor screenEditor ) {

        List<ScreenParameter> screenParameters  = new ArrayList<ScreenParameter>(  );
        Map<String, String> screenProperties = perspectiveEditor.getScreenProperties( screenEditor.hashCode() + "" );
        for ( String key : screenProperties.keySet() ) {
            screenParameters.add(new ScreenParameter(key, screenProperties.get(key) ));
        }

        return screenParameters;
    }
}

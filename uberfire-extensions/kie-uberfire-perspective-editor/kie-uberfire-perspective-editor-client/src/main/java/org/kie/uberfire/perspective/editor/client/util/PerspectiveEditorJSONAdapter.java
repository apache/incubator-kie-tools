package org.kie.uberfire.perspective.editor.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.uberfire.perspective.editor.client.structure.ColumnEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.RowEditorWidgetUI;
import org.kie.uberfire.perspective.editor.client.structure.ScreenEditorWidgetUI;
import org.kie.uberfire.perspective.editor.model.ColumnEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;

public class PerspectiveEditorJSONAdapter {

    private final PerspectiveEditorUI perspectiveEditor;

    public PerspectiveEditorJSONAdapter( PerspectiveEditorUI perspectiveEditor ) {
        this.perspectiveEditor = perspectiveEditor;
    }

    public PerspectiveEditor convertToJSON() {
        PerspectiveEditor perspectiveJSON = new PerspectiveEditor( perspectiveEditor.getName());
        extractRows( perspectiveEditor, perspectiveJSON );
        return perspectiveJSON;
    }

    private void extractRows( PerspectiveEditorUI perspectiveEditor,
                              PerspectiveEditor perspectiveJSON ) {
        for ( EditorWidget genericEditor : perspectiveEditor.getRowEditors() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            perspectiveJSON.addRowJSON( rowJSON );
        }
    }

    private void extractRows( ColumnEditorUI columnEditor,
                              ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            columnEditorJSON.addRowJSON( rowJSON );
        }
    }

    private void extractColumns( RowEditorWidgetUI rowEditor,
                                 RowEditor rowJSON ) {
        for ( EditorWidget genericEditor : rowEditor.getColumnEditors() ) {
            ColumnEditorUI columnEditor = (ColumnEditorUI) genericEditor;
            ColumnEditor columnEditorJSON = new ColumnEditor( columnEditor.getSpan() );
            if ( !columnEditor.getChilds().isEmpty() ) {
                extractChilds( columnEditor, columnEditorJSON );
            }
            rowJSON.add( columnEditorJSON );
        }
    }

    private void extractChilds( ColumnEditorUI columnEditor,
                                ColumnEditor columnEditorJSON ) {
        //ederign
        if ( columnEditor.getChilds().get( 0 ) instanceof RowEditorWidgetUI ) {
            extractRows( columnEditor, columnEditorJSON );
        }
        if ( columnEditor.getChilds().get( 0 ) instanceof ScreenEditorWidgetUI ) {
            extractScreens( columnEditor, columnEditorJSON );
        }
    }

    private void extractScreens( ColumnEditorUI columnEditor,
                                 ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            ScreenEditorWidgetUI screenEditor = (ScreenEditorWidgetUI) genericEditor;
            List<ScreenParameter> parameteres = getScreenParameters(screenEditor);
            ScreenEditor screenEditorJSON = new ScreenEditor( parameteres );
            columnEditorJSON.addScreenJSON( screenEditorJSON );
        }
    }

    public List<ScreenParameter> getScreenParameters( ScreenEditorWidgetUI screenEditor ) {

        List<ScreenParameter> screenParameters  = new ArrayList<ScreenParameter>(  );
        Map<String, String> screenProperties = perspectiveEditor.getScreenProperties( screenEditor.hashCode() + "" );
        for ( String key : screenProperties.keySet() ) {
            screenParameters.add(new ScreenParameter(key, screenProperties.get(key) ));
        }

        return screenParameters;
    }
}

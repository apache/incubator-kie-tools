package org.kie.uberfire.perspective.editor.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.uberfire.perspective.editor.client.structure.ColumnEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.HTMLEditorWidgetUI;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.client.structure.RowEditorWidgetUI;
import org.kie.uberfire.perspective.editor.client.structure.ScreenEditorWidgetUI;
import org.kie.uberfire.perspective.editor.model.ColumnEditor;
import org.kie.uberfire.perspective.editor.model.HTMLEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;

public class PerspectiveEditorJSONAdapter {

    private final PerspectiveEditorUI perspectiveEditor;

    public PerspectiveEditorJSONAdapter( PerspectiveEditorUI perspectiveEditor ) {
        this.perspectiveEditor = perspectiveEditor;
    }

    public PerspectiveEditor convertToJSON() {
        PerspectiveEditor perspectiveJSON = new PerspectiveEditor( perspectiveEditor.getName() );
        extractRows( perspectiveEditor, perspectiveJSON );
        return perspectiveJSON;
    }

    private void extractRows( PerspectiveEditorUI perspectiveEditor,
                              PerspectiveEditor perspectiveJSON ) {
        for ( EditorWidget genericEditor : perspectiveEditor.getRowEditors() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            perspectiveJSON.addRow( rowJSON );
        }
    }

    private void extractRows( ColumnEditorUI columnEditor,
                              ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            columnEditorJSON.addRow( rowJSON );
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
        //ederign -> types can be mixed? refactoring
        if ( columnEditor.getChilds().get( 0 ) instanceof RowEditorWidgetUI ) {
            extractRows( columnEditor, columnEditorJSON );
        }
        if ( columnEditor.getChilds().get( 0 ) instanceof ScreenEditorWidgetUI ) {
            extractScreens( columnEditor, columnEditorJSON );
        }
        if ( columnEditor.getChilds().get( 0 ) instanceof HTMLEditorWidgetUI ) {
            extractHTML( columnEditor, columnEditorJSON );
        }
    }

    private void extractHTML( ColumnEditorUI columnEditor,
                              ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            HTMLEditorWidgetUI htmlEditorUI = (HTMLEditorWidgetUI) genericEditor;
            HTMLEditor htmlEditor = new HTMLEditor( htmlEditorUI.getHtmlCode() );
            columnEditorJSON.addHTML( htmlEditor );
        }
    }

    private void extractScreens( ColumnEditorUI columnEditor,
                                 ColumnEditor columnEditorJSON ) {
        for ( EditorWidget genericEditor : columnEditor.getChilds() ) {
            ScreenEditorWidgetUI screenEditor = (ScreenEditorWidgetUI) genericEditor;
            ScreenEditor screenEditorJSON = new ScreenEditor();
            List<ScreenParameter> parameters = getScreenParameters( screenEditor.hashCode() + "", screenEditorJSON );
            columnEditorJSON.addScreen( screenEditorJSON );
        }
    }

    public List<ScreenParameter> getScreenParameters( String uiHashCode,
                                                      ScreenEditor screenEditor ) {

        List<ScreenParameter> screenParameters = new ArrayList<ScreenParameter>();
        Map<String, String> screenProperties = perspectiveEditor.getScreenProperties( uiHashCode );
        for ( String key : screenProperties.keySet() ) {
            if ( key.equals( ScreenEditor.SCREEN_NAME ) ) {
                screenEditor.setScreenName( screenProperties.get( key ) );
            } else {
                screenParameters.add( new ScreenParameter( key, screenProperties.get( key ) ) );
            }
        }

        return screenParameters;
    }
}

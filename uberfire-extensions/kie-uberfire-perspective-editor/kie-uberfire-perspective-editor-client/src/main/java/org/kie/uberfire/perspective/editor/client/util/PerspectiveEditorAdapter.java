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

public class PerspectiveEditorAdapter {

    private final PerspectiveEditorUI perspectiveEditor;

    public PerspectiveEditorAdapter( PerspectiveEditorUI perspectiveEditor ) {
        this.perspectiveEditor = perspectiveEditor;
    }

    public PerspectiveEditor convertToPerspectiveEditor() {
        PerspectiveEditor perspectiveEditor = new PerspectiveEditor( this.perspectiveEditor.getName(), this.perspectiveEditor.getTags() );
        extractRows( this.perspectiveEditor, perspectiveEditor );
        return perspectiveEditor;
    }

    private void extractRows( PerspectiveEditorUI perspectiveEditor,
                              PerspectiveEditor perspective ) {
        for ( EditorWidget genericEditor : perspectiveEditor.getRowEditors() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            perspective.addRow( rowJSON );
        }
    }

    private void extractRows( ColumnEditorUI columnEditorUI,
                              ColumnEditor columnEditor ) {
        for ( EditorWidget genericEditor : columnEditorUI.getChilds() ) {
            RowEditorWidgetUI rowUI = (RowEditorWidgetUI) genericEditor;
            RowEditor rowEditor = new RowEditor( rowUI.getRowSpans() );
            extractColumns( rowUI, rowEditor );
            columnEditor.addRow( rowEditor );
        }
    }

    private void extractColumns( RowEditorWidgetUI rowUI,
                                 RowEditor rowEditor ) {
        for ( EditorWidget genericEditor : rowUI.getColumnEditors() ) {
            ColumnEditorUI columnEditorUI = (ColumnEditorUI) genericEditor;
            ColumnEditor columnEditor = new ColumnEditor( columnEditorUI.getSpan() );
            if ( !columnEditorUI.getChilds().isEmpty() ) {
                extractChilds( columnEditorUI, columnEditor );
            }
            rowEditor.add( columnEditor );
        }
    }

    private void extractChilds( ColumnEditorUI columnEditorUI,
                                ColumnEditor columnEditor ) {
        //ederign -> types can be mixed? refactoring
        if ( columnEditorUI.getChilds().get( 0 ) instanceof RowEditorWidgetUI ) {
            extractRows( columnEditorUI, columnEditor );
        }
        if ( columnEditorUI.getChilds().get( 0 ) instanceof ScreenEditorWidgetUI ) {
            extractScreens( columnEditorUI, columnEditor );
        }
        if ( columnEditorUI.getChilds().get( 0 ) instanceof HTMLEditorWidgetUI ) {
            extractHTML( columnEditorUI, columnEditor );
        }
    }

    private void extractHTML( ColumnEditorUI columnEditorUI,
                              ColumnEditor columnEditor ) {
        for ( EditorWidget genericEditor : columnEditorUI.getChilds() ) {
            HTMLEditorWidgetUI htmlEditorUI = (HTMLEditorWidgetUI) genericEditor;
            HTMLEditor htmlEditor = new HTMLEditor( htmlEditorUI.getHtmlCode() );
            columnEditor.addHTML( htmlEditor );
        }
    }

    private void extractScreens( ColumnEditorUI columnEditorUI,
                                 ColumnEditor columnEditor ) {
        for ( EditorWidget genericEditor : columnEditorUI.getChilds() ) {
            ScreenEditorWidgetUI screenEditorUI = (ScreenEditorWidgetUI) genericEditor;
            ScreenEditor screenEditor = new ScreenEditor();
            screenEditor.setParameters( extractScreenParameters( screenEditorUI.hashCode() + "", screenEditor ) );
            columnEditor.addScreen( screenEditor );
        }
    }

    public List<ScreenParameter> extractScreenParameters( String uiHashCode,
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

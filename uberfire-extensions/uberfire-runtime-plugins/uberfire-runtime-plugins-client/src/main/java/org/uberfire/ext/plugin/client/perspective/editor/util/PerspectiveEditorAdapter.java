package org.uberfire.ext.plugin.client.perspective.editor.util;

import org.uberfire.ext.plugin.client.perspective.editor.structure.ColumnEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.EditorWidget;
import org.uberfire.ext.plugin.client.perspective.editor.structure.HTMLEditorWidgetUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.PerspectiveEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.RowEditorWidgetUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.ScreenEditorWidgetUI;
import org.uberfire.ext.plugin.editor.ColumnEditor;
import org.uberfire.ext.plugin.editor.HTMLEditor;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.editor.RowEditor;
import org.uberfire.ext.plugin.editor.ScreenEditor;

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
            ScreenEditor screenEditor = perspectiveEditor.getScreenProperties( screenEditorUI.hashCode() + "" );

            columnEditor.addScreen( screenEditor );
        }
    }

}

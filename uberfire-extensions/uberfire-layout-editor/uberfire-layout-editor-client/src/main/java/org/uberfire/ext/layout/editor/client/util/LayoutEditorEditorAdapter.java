package org.uberfire.ext.layout.editor.client.util;

import java.util.Map;

import org.uberfire.ext.layout.editor.api.editor.ColumnEditor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorUI;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutComponentWidgetUI;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorUI;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidgetUI;

public class LayoutEditorEditorAdapter {

    private final LayoutEditorUI layoutEditor;

    public LayoutEditorEditorAdapter( LayoutEditorUI layoutEditor ) {
        this.layoutEditor = layoutEditor;
    }

    public LayoutEditor convertToLayoutEditor() {
        LayoutEditor layoutEditor = new LayoutEditor( this.layoutEditor.getName(), this.layoutEditor.getLayoutProperties() );
        extractRows( this.layoutEditor, layoutEditor );
        return layoutEditor;
    }

    private void extractRows( LayoutEditorUI layoutEditorUI,
                              LayoutEditor layoutEditor ) {
        for ( EditorWidget genericEditor : layoutEditorUI.getRowEditors() ) {
            RowEditorWidgetUI rowEditor = (RowEditorWidgetUI) genericEditor;
            RowEditor rowJSON = new RowEditor( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            layoutEditor.addRow( rowJSON );
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
        if ( columnEditorUI.childsIsRowEditorWidgetUI() ) {
            extractRows( columnEditorUI, columnEditor );
        } else {
            extractLayoutEditorComponent( columnEditorUI, columnEditor );
        }
    }

    private void extractLayoutEditorComponent( ColumnEditorUI columnEditorUI,
                                               ColumnEditor columnEditor ) {
        for ( EditorWidget genericEditor : columnEditorUI.getChilds() ) {
            LayoutComponentWidgetUI layoutComponentWidgetUI = (LayoutComponentWidgetUI) genericEditor;
            LayoutComponent layoutComponent = new LayoutComponent( layoutComponentWidgetUI.getType().getClass() );
            final LayoutComponent layoutComponentProperties = layoutEditor.getLayoutComponent( layoutComponentWidgetUI );
            final Map<String, String> parameters = layoutComponentProperties.getProperties();

            layoutComponent.addProperties( parameters );
            columnEditor.addLayoutComponent( layoutComponent );
        }
    }

}

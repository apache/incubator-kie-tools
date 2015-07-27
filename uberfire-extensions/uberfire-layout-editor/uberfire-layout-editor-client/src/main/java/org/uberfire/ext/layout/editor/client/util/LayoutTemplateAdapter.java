package org.uberfire.ext.layout.editor.client.util;

import java.util.Map;

import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.ComponentEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;

public class LayoutTemplateAdapter {

    private final LayoutEditorWidget layoutEditorWidget;

    public LayoutTemplateAdapter(LayoutEditorWidget layoutEditorWidget) {
        this.layoutEditorWidget = layoutEditorWidget;
    }

    public LayoutTemplate convertToLayoutEditor() {
        LayoutTemplate layoutTemplate = new LayoutTemplate( this.layoutEditorWidget.getName(), this.layoutEditorWidget.getLayoutProperties() );
        extractRows( this.layoutEditorWidget, layoutTemplate);
        return layoutTemplate;
    }

    private void extractRows( LayoutEditorWidget layoutEditorWidget,
                              LayoutTemplate layoutTemplate) {
        for ( EditorWidget genericEditor : layoutEditorWidget.getRowEditors() ) {
            RowEditorWidget rowEditor = (RowEditorWidget) genericEditor;
            LayoutRow rowJSON = new LayoutRow( rowEditor.getRowSpans() );
            extractColumns( rowEditor, rowJSON );
            layoutTemplate.addRow( rowJSON );
        }
    }

    private void extractRows( ColumnEditorWidget columnEditorWidget,
                              LayoutColumn layoutColumn) {
        for ( EditorWidget genericEditor : columnEditorWidget.getChilds() ) {
            RowEditorWidget rowUI = (RowEditorWidget) genericEditor;
            LayoutRow layoutRow = new LayoutRow( rowUI.getRowSpans() );
            extractColumns( rowUI, layoutRow);
            layoutColumn.addRow(layoutRow);
        }
    }

    private void extractColumns( RowEditorWidget rowEditorWidget,
                                 LayoutRow layoutRow) {
        for ( EditorWidget genericEditor : rowEditorWidget.getColumnEditors() ) {
            ColumnEditorWidget columnEditorWidget = (ColumnEditorWidget) genericEditor;
            LayoutColumn layoutColumn = new LayoutColumn( columnEditorWidget.getSpan() );
            if ( !columnEditorWidget.getChilds().isEmpty() ) {
                extractChilds(columnEditorWidget, layoutColumn);
            }
            layoutRow.add(layoutColumn);
        }
    }

    private void extractChilds( ColumnEditorWidget columnEditorWidget,
                                LayoutColumn layoutColumn) {
        if ( columnEditorWidget.childsIsRowEditorWidgetUI() ) {
            extractRows(columnEditorWidget, layoutColumn);
        } else {
            extractLayoutEditorComponent(columnEditorWidget, layoutColumn);
        }
    }

    private void extractLayoutEditorComponent( ColumnEditorWidget columnEditorWidget,
                                               LayoutColumn layoutColumn) {
        for ( EditorWidget genericEditor : columnEditorWidget.getChilds() ) {
            ComponentEditorWidget componentEditorWidget = (ComponentEditorWidget) genericEditor;
            LayoutComponent layoutComponent = new LayoutComponent( componentEditorWidget.getType().getClass() );
            final LayoutComponent layoutComponentProperties = layoutEditorWidget.getLayoutComponent(componentEditorWidget);
            final Map<String, String> parameters = layoutComponentProperties.getProperties();

            layoutComponent.addProperties( parameters );
            layoutColumn.addLayoutComponent( layoutComponent );
        }
    }

}

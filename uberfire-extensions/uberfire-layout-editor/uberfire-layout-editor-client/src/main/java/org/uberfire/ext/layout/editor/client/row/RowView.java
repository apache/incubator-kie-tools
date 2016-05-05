/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.row;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.dnd.DropColumnPanel;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;

import java.util.List;

public class RowView extends Composite {

    private DropColumnPanel oldDropColumnPanel;
    private RowEditorWidget row;

    @UiField
    Container fluidContainer;

    private EditorWidget editorWidget;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, RowView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public RowView( LayoutEditorWidget parent,
                    String rowSpamString ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidget( parent, fluidContainer, rowSpamString );
        build();
    }

    public RowView( ColumnEditorWidget parent,
                    String rowSpamString,
                    DropColumnPanel oldDropColumnPanel ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidget( parent, fluidContainer, rowSpamString );
        build();

    }

    private RowView( ColumnEditorWidget parent,
                     List<String> rowSpans,
                     DropColumnPanel oldDropColumnPanel,
                     LayoutRow layoutRow ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidget( parent, fluidContainer, rowSpans );
        reload( layoutRow.getLayoutColumns() );
    }

    public RowView( LayoutEditorWidget parent,
                    LayoutRow layoutRow ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidget( parent, fluidContainer, layoutRow.getRowSpam() );
        reload( layoutRow.getLayoutColumns() );
    }

    private void generateColumns( Row rowWidget, List<LayoutColumn> layoutColumns ) {

        for (LayoutColumn layoutColumn : layoutColumns) {

            Column column = createColumn( layoutColumn );
            ColumnEditorWidget parent = new ColumnEditorWidget( row, column, layoutColumn.getSpan() );

            // Create the drop panel always, but don't add it to the column in case we're reloading an existing layout, and the column already contains elements
            DropColumnPanel dropColumnPanel = generateDropColumnPanel( column, parent, !layoutColumn.hasElements() );

            for ( LayoutRow editor : layoutColumn.getRows() ) {
                column.add( createRowView( parent, dropColumnPanel, editor ) );
            }

            for ( LayoutComponent layoutComponent : layoutColumn.getLayoutComponents() ) {
                final LayoutDragComponent layoutDragComponent = getLayoutDragComponent( layoutComponent );
                column.add( createLayoutComponentView( parent, layoutComponent, layoutDragComponent ) );
            }

            rowWidget.add( column );
        }
    }

    protected RowView createRowView( ColumnEditorWidget parent,
                                     DropColumnPanel dropColumnPanel,
                                     LayoutRow editor ) {
        return new RowView( parent, editor.getRowSpam(), dropColumnPanel, editor );
    }

    protected LayoutComponentView createLayoutComponentView( ColumnEditorWidget parent,
                                                             LayoutComponent layoutComponent,
                                                             LayoutDragComponent layoutDragComponent ) {
        return new LayoutComponentView( parent, layoutComponent, layoutDragComponent );
    }

    protected LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
        return new DragTypeBeanResolver().lookupDragTypeBean( layoutComponent.getDragTypeName() );
    }

    private void reload( List<LayoutColumn> layoutColumns ) {
        Row rowWidget = new Row();
        generateHeaderRow( rowWidget );
        generateColumns( rowWidget, layoutColumns );
        row.getWidget().add( rowWidget );
    }

    private void generateColumns( Row rowWidget ) {
        for (String span : row.getRowSpans()) {
            Column column = createColumn( span );
            rowWidget.add( column );
        }
    }

    private void build() {
        Row rowWidget = new Row();
        generateHeaderRow( rowWidget );
        generateColumns( rowWidget );
        row.getWidget().add( rowWidget );
    }

    private Column createColumn( LayoutColumn layoutColumn ) {
        Column column = new Column( buildColumnSize( Integer.valueOf( layoutColumn.getSpan() ) ) );
        return column;
    }

    private DropColumnPanel generateDropColumnPanel( Column column,
                                                     ColumnEditorWidget parent,
                                                     boolean addToColumn ) {
        final DropColumnPanel drop = new DropColumnPanel( parent );
        if ( addToColumn ) {
            column.add( drop );
        }
        return drop;
    }

    private Column createColumn( String span ) {
        Column column = new Column( buildColumnSize( Integer.valueOf( span ) ) );
        ColumnEditorWidget columnEditor = new ColumnEditorWidget( row, column, span );
        column.add( new DropColumnPanel( columnEditor ) );
        return column;
    }

    private void generateHeaderRow( final Row rowWidget ) {
        rowWidget.add( generateButtonColumn() );
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( buildColumnSize( 12 ) );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        buttonColumn.getElement().getStyle().setProperty( "paddingRight", "3px" );
        buttonColumn.getElement().getStyle().setProperty( "textTop", "2px" );

        buttonColumn.add( generateRemoveButton() );
        return buttonColumn;
    }

    private Button generateRemoveButton() {
        Button remove = GWT.create( Button.class );
        remove.setSize( ButtonSize.EXTRA_SMALL );
        remove.setType( ButtonType.DANGER );
        remove.setIcon( IconType.REMOVE );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                editorWidget.getWidget().remove( RowView.this );
                if ( parentIsAColumn() ) {
                    attachDropColumnPanel();
                }
                row.removeFromParent();
            }
        } );
        return remove;
    }

    private void attachDropColumnPanel() {
        editorWidget.getWidget().add( oldDropColumnPanel );
    }

    private boolean parentIsAColumn() {
        return oldDropColumnPanel != null;
    }

    public static ColumnSize buildColumnSize( final int value ) {
        switch ( value ) {
            case 1:
                return ColumnSize.MD_1;
            case 2:
                return ColumnSize.MD_2;
            case 3:
                return ColumnSize.MD_3;
            case 4:
                return ColumnSize.MD_4;
            case 5:
                return ColumnSize.MD_5;
            case 6:
                return ColumnSize.MD_6;
            case 7:
                return ColumnSize.MD_7;
            case 8:
                return ColumnSize.MD_8;
            case 9:
                return ColumnSize.MD_9;
            case 10:
                return ColumnSize.MD_10;
            case 11:
                return ColumnSize.MD_11;
            case 12:
                return ColumnSize.MD_12;
            default:
                return ColumnSize.MD_12;
        }
    }

}
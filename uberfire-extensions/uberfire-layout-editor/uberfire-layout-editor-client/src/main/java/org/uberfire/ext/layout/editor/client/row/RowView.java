package org.uberfire.ext.layout.editor.client.row;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.api.editor.ColumnEditor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.dnd.DropColumnPanel;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorUI;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorUI;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidgetUI;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

public class RowView extends Composite {

    private DropColumnPanel oldDropColumnPanel;
    private RowEditorWidgetUI row;

    @UiField
    FluidContainer fluidContainer;

    private EditorWidget editorWidget;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, RowView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public RowView( LayoutEditorUI parent,
                    String rowSpamString ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpamString );
        build();
    }

    public RowView( ColumnEditorUI parent,
                    String rowSpamString,
                    DropColumnPanel oldDropColumnPanel ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpamString );
        build();

    }

    private RowView( ColumnEditorUI parent,
                     List<String> rowSpans,
                     DropColumnPanel oldDropColumnPanel,
                     RowEditor rowEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpans );
        reload( rowEditor.getColumnEditors() );
    }

    public RowView( LayoutEditorUI parent,
                    RowEditor rowEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowEditor.getRowSpam() );
        reload( rowEditor.getColumnEditors() );
    }

    private FluidRow generateColumns( List<ColumnEditor> columnEditors ) {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );

        for ( ColumnEditor columnEditor : columnEditors ) {

            Column column = createColumn( columnEditor );
            ColumnEditorUI parent = new ColumnEditorUI( row, column, columnEditor.getSpan() );

            // Create the drop panel always, but don't add it to the column in case we're reloading an existing layout, and the column already contains elements
            DropColumnPanel dropColumnPanel = generateDropColumnPanel( column, parent, !columnEditor.hasElements() );

            for ( RowEditor editor : columnEditor.getRows() ) {
                column.add( createRowView( parent, dropColumnPanel, editor ) );
            }

            for ( LayoutComponent layoutComponent : columnEditor.getLayoutComponents() ) {
                final LayoutDragComponent layoutDragComponent = getLayoutDragComponent( layoutComponent );
                column.add( createLayoutComponentView( parent, layoutComponent, layoutDragComponent ) );
            }

            rowWidget.add( column );
        }
        return rowWidget;
    }

    protected RowView createRowView( ColumnEditorUI parent,
                                   DropColumnPanel dropColumnPanel,
                                   RowEditor editor ) {
        return new RowView( parent, editor.getRowSpam(), dropColumnPanel, editor );
    }

    protected LayoutComponentView createLayoutComponentView( ColumnEditorUI parent,
                                                   LayoutComponent layoutComponent,
                                                   LayoutDragComponent layoutDragComponent ) {
        return new LayoutComponentView( parent, layoutComponent, layoutDragComponent );
    }

    protected LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
        return new DragTypeBeanResolver().lookupDragTypeBean( layoutComponent.getDragTypeName() );
    }

    private void reload( List<ColumnEditor> columnEditors ) {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns( columnEditors ) );
    }

    private FluidRow generateColumns() {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );
        for ( String span : row.getRowSpans() ) {
            Column column = createColumn( span );
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void build() {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns() );
    }

    private Column createColumn( ColumnEditor columnEditor ) {
        Column column = new Column( Integer.valueOf( columnEditor.getSpan() ) );
        column.add( generateLabel( "Column" ) );
        setCSS( column );
        return column;
    }

    private DropColumnPanel generateDropColumnPanel( Column column,
                                                     ColumnEditorUI parent,
                                                     boolean addToColumn ) {
        final DropColumnPanel drop = new DropColumnPanel( parent );
        if ( addToColumn ) {
            column.add( drop );
        }
        return drop;
    }

    private Column createColumn( String span ) {
        Column column = new Column( Integer.valueOf( span ) );
        column.add( generateLabel( "Column" ) );
        ColumnEditorUI columnEditor = new ColumnEditorUI( row, column, span );
        column.add( new DropColumnPanel( columnEditor ) );
        setCSS( column );
        return column;
    }

    private void setCSS( Column column ) {
        column.getElement().getStyle().setProperty( "border", "1px solid #DDDDDD" );
        column.getElement().getStyle().setProperty( "backgroundColor", "White" );
    }

    private FluidRow generateHeaderRow() {
        FluidRow row = new FluidRow();
        row.add( generateRowLabelColumn() );
        row.add( generateButtonColumn() );
        return row;
    }

    private Column generateRowLabelColumn() {
        Column column = new Column( 6 );
        Label row1 = generateLabel( "Row" );
        column.add( row1 );
        return column;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( 6 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        Button remove = generateButton();
        buttonColumn.add( remove );
        return buttonColumn;
    }

    private Button generateButton() {
        Button remove = GWT.create( Button.class );
        remove.setText( "Remove" );
        remove.setSize( ButtonSize.MINI );
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

    private Label generateLabel( String row ) {
        Label label = GWT.create( Label.class );
        label.setText( row );
        label.getElement().getStyle().setProperty( "marginLeft", "3px" );
        return label;
    }

}

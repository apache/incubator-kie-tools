package org.uberfire.ext.layout.editor.client.row;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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

    private Row generateColumns( List<LayoutColumn> layoutColumns ) {
        Row rowWidget = new Row();

        for ( LayoutColumn layoutColumn : layoutColumns ) {

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
        return rowWidget;
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
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns( layoutColumns ) );
    }

    private Row generateColumns() {
        Row rowWidget = new Row();
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

    private FlowPanel generateHeaderRow() {
        final FlowPanel header = new FlowPanel();
        header.add( generateButtonColumn() );
        header.setVisible( false );

        row.getWidget().addDomHandler( new MouseOverHandler() {
            @Override
            public void onMouseOver( MouseOverEvent mouseOverEvent ) {
                header.setVisible( true );
                row.getWidget().getElement().removeClassName( WebAppResource.INSTANCE.CSS().rowDragOut() );
                row.getWidget().getElement().addClassName( WebAppResource.INSTANCE.CSS().rowDragOver() );
            }
        }, MouseOverEvent.getType() );

        row.getWidget().addDomHandler( new MouseOutHandler() {
            @Override
            public void onMouseOut( MouseOutEvent mouseOutEvent ) {
                header.setVisible( false );
                row.getWidget().getElement().removeClassName( WebAppResource.INSTANCE.CSS().rowDragOver() );
                row.getWidget().getElement().addClassName( WebAppResource.INSTANCE.CSS().rowDragOut() );
            }
        }, MouseOutEvent.getType() );

        return header;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( buildColumnSize( 12 ) );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        Button remove = generateRemoveButton();
        buttonColumn.add( remove );
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
package org.uberfire.ext.layout.editor.client.row;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
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
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.dnd.DropColumnPanel;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class RowView extends Composite {

    private DropColumnPanel oldDropColumnPanel;
    private RowEditorWidget row;

    @UiField
    FluidContainer fluidContainer;

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
                     LayoutRow layoutRow) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidget( parent, fluidContainer, rowSpans );
        reload( layoutRow.getLayoutColumns() );
    }

    public RowView( LayoutEditorWidget parent,
                    LayoutRow layoutRow) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidget( parent, fluidContainer, layoutRow.getRowSpam() );
        reload( layoutRow.getLayoutColumns() );
    }

    private FluidRow generateColumns( List<LayoutColumn> layoutColumns) {
        FluidRow rowWidget = new FluidRow();

        for ( LayoutColumn layoutColumn : layoutColumns) {

            Column column = createColumn(layoutColumn);
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

    private void reload( List<LayoutColumn> layoutColumns) {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add(generateColumns(layoutColumns));
    }

    private FluidRow generateColumns() {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty("marginBottom", "15px");
        for ( String span : row.getRowSpans() ) {
            Column column = createColumn( span );
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void build() {
        row.getWidget().add(generateHeaderRow());
        row.getWidget().add(generateColumns());
    }

    private Column createColumn( LayoutColumn layoutColumn) {
        Column column = new Column( Integer.valueOf( layoutColumn.getSpan() ) );
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
        Column column = new Column(Integer.valueOf( span ) );
        ColumnEditorWidget columnEditor = new ColumnEditorWidget(row, column, span);
        column.add(new DropColumnPanel(columnEditor));
        return column;
    }

    private FlowPanel generateHeaderRow() {
        final FlowPanel header = new FlowPanel();
        header.add(generateButtonColumn());
        header.setVisible(false);

        row.getWidget().addDomHandler(new MouseOverHandler() {
            @Override public void onMouseOver(MouseOverEvent mouseOverEvent) {
                header.setVisible(true);
                row.getWidget().getElement().removeClassName(WebAppResource.INSTANCE.CSS().rowDragOut());
                row.getWidget().getElement().addClassName(WebAppResource.INSTANCE.CSS().rowDragOver());
            }
        }, MouseOverEvent.getType());

        row.getWidget().addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent mouseOutEvent) {
                header.setVisible(false);
                row.getWidget().getElement().removeClassName(WebAppResource.INSTANCE.CSS().rowDragOver());
                row.getWidget().getElement().addClassName(WebAppResource.INSTANCE.CSS().rowDragOut());
            }
        }, MouseOutEvent.getType());

        return header;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column(12);
        buttonColumn.getElement().getStyle().setProperty("textAlign", "right");
        Button remove = generateRemoveButton();
        buttonColumn.add(remove);
        return buttonColumn;
    }

    private Button generateRemoveButton() {
        Button remove = GWT.create( Button.class );
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
}

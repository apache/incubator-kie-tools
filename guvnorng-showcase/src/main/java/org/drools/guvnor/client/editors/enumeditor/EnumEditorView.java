package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;

import org.drools.guvnor.shared.common.vo.assets.enums.EnumModel;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
public class EnumEditorView
        extends
        Composite
    implements
    EnumEditorPresenter.View {

    private EnumModel                       content;

    private final CellTable<EnumRow>        cellTable;

    private final VerticalPanel             panel;

    private final ListDataProvider<EnumRow> dataProvider;

    public EnumEditorView() {
        dataProvider = new ListDataProvider<EnumRow>();
        panel = new VerticalPanel();
        cellTable = new CellTable<EnumRow>();
        cellTable.setWidth( "100%" );

        //Add columns to table
        Column<EnumRow, String> columnFirst = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFactName();
            }
        };
        Column<EnumRow, String> columnSecond = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getFieldName();
            }
        };
        Column<EnumRow, String> columnThird = new Column<EnumRow, String>( new EditTextCell() ) {

            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getContext();
            }
        };
        Column<EnumRow, ImageResource> delete = new Column<EnumRow, ImageResource>( new DeleteButtonCell() ) {

            @Override
            public ImageResource getValue(EnumRow enumRow1) {
                return null;
            }
        };
        cellTable.addColumn( delete );
        cellTable.addColumn( columnFirst,
                             "Fact" );
        cellTable.addColumn( columnSecond,
                             "Field" );
        cellTable.addColumn( columnThird,
                             "Context" );

        //Add Field Updaters to columns
        columnFirst.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update(int index,
                               EnumRow object,
                               String value) {
                object.setFactName( value );

            }
        } );
        columnSecond.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update(int index,
                               EnumRow object,
                               String value) {

                object.setFieldName( value );

            }
        } );
        columnThird.setFieldUpdater( new FieldUpdater<EnumRow, String>() {

            public void update(int index,
                               EnumRow object,
                               String value) {

                object.setContext( value );
            }
        } );
        delete.setFieldUpdater( new FieldUpdater<EnumRow, ImageResource>() {

            public void update(int index,
                               EnumRow object,
                               ImageResource value) {
                dataProvider.getList().remove( object );
            }
        } );

        // Connect the table to the data provider.
        dataProvider.addDataDisplay( cellTable );

        Button addButton = new Button( "+",
                                       new ClickHandler() {
                                           public void onClick(ClickEvent clickEvent) {
                                               EnumRow enumRow = new EnumRow( "" );
                                               dataProvider.getList().add( enumRow );
                                           }
                                       } );

        panel.add( cellTable );
        panel.add( addButton );

        initWidget( panel );
    }

    @Override
    public void setContent(EnumModel content) {
        this.content = content;
        String[] array = content.getDefinitions().split( "\n" );

        for ( String line : array ) {
            EnumRow enumRow = new EnumRow( line );
            dataProvider.getList().add( enumRow );
        }

        dataProvider.refresh();
    }

    @Override
    public EnumModel getContent() {
        return this.content;
    }

    @Override
    public void setFocus() {
        cellTable.setFocus( true );
    }

    @Override
    public void setDirty(boolean dirty) {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

}

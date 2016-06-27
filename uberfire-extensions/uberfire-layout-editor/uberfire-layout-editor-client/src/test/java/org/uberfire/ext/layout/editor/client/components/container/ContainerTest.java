package org.uberfire.ext.layout.editor.client.components.container;

import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.plugin.type.TagsConverterUtil;
import org.uberfire.mvp.ParameterizedCommand;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;


public class ContainerTest extends AbstractLayoutEditorTest {

    @Test
    public void assertEmptyContainerHasEmptyDropRow() {
        assertTrue( container.getRows().isEmpty() );
        assertNotNull( container.getEmptyDropRow() );
        verify( view ).addEmptyRow( emptyDropRow.getView() );
    }

    @Test
    public void createFirstRow() {
        assertEquals( 0, getRowsSizeFromContainer() );
        assertNotNull( container.getEmptyDropRow() );
        verify( view ).addEmptyRow( emptyDropRow.getView() );

        container.createEmptyDropCommand()
                .execute( new RowDrop( new LayoutComponent( "dragType" ), emptyDropRow.hashCode(),
                                       RowDrop.Orientation.BEFORE ) );
        assertEquals( 1, getRowsSizeFromContainer() );
    }

    @Test
    public void loadAndExportLayout() throws Exception {

        LayoutTemplate expected = loadLayout( SAMPLE_FULL_LAYOUT );

        LayoutTemplate actual = container.toLayoutTemplate();

        assertEquals( expected, actual );
        assertEquals( convertLayoutToString( expected ), convertLayoutToString( actual ) );
    }



    @Test
    public void dropBeforeComponentShouldCreateANewRow() throws Exception {

        loadLayout( SINGLE_ROW_COMPONENT_LAYOUT );

        Row dropRow = getRowByIndex( FIRST_ROW );

        RowDrop dropNewComponentOnFirstRow = new RowDrop( new LayoutComponent( "dragType" ), dropRow.hashCode(),
                                                          RowDrop.Orientation.BEFORE );
        dropNewComponentOnFirstRow.newComponent();

        container.createRowDropCommand().execute( dropNewComponentOnFirstRow );

        assertEquals( 2, getRowsSizeFromContainer() );

        Column droppedColumn = getColumnByIndex( getRowByIndex( FIRST_ROW ), FIRST_COLUMN );
        assertEquals( "dragType", droppedColumn.getLayoutComponent()
                .getDragTypeName() );

        assertEquals( dropRow, getRowByIndex( SECOND_ROW ) );
    }

    @Test
    public void moveComponentShouldRemoveComponentFromCurrentRow() throws Exception {
        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row dropRow = getRowByIndex( FIRST_ROW );

        RowDrop moveComponentAndDropInFirstRow = new RowDrop( new LayoutComponent( "dragType" ), dropRow.hashCode(),
                                                              RowDrop.Orientation.BEFORE );
        moveComponentAndDropInFirstRow.fromMove( dropRow.hashCode(), getColumns( dropRow ).get( 0 ) );

        container.createRowDropCommand().execute( moveComponentAndDropInFirstRow );

        assertEquals( 2, getRowsSizeFromContainer() );
        assertEquals( 1, getColumns( getRowByIndex( FIRST_ROW ) ).size() );
        assertEquals( 1, getColumns( getRowByIndex( SECOND_ROW ) ).size() );
    }

    @Test
    public void swapRows() throws Exception {
        loadLayout( SAMPLE_FULL_LAYOUT );

        Row row1 = getRowByIndex( FIRST_ROW );
        Row row2 = getRowByIndex( SECOND_ROW );

        container.swapRows( new RowDnDEvent( row1.hashCode(), row2.hashCode(), RowDrop.Orientation.AFTER ) );

        assertEquals( row2, getRowByIndex( FIRST_ROW ) );
        assertEquals( row1, getRowByIndex( SECOND_ROW ) );
    }


    @Test
    public void dropAfterComponentShouldCreateANewRow() throws Exception {
        loadLayout( SINGLE_ROW_COMPONENT_LAYOUT );

        Row dropRow = getRowByIndex( 0 );

        ParameterizedCommand<RowDrop> rowDropCommand = container.createRowDropCommand();
        RowDrop drop = new RowDrop( new LayoutComponent( "dragType" ), dropRow.hashCode(),
                                    RowDrop.Orientation.AFTER );
        drop.newComponent();
        rowDropCommand.execute( drop );

        assertEquals( 2, getRowsSizeFromContainer() );
        assertEquals( dropRow, getRowByIndex( FIRST_ROW ) );
    }


    @Test
    public void removeSingleComponentFromRowShouldRemoveRow() throws Exception {

        loadLayout( SINGLE_ROW_COMPONENT_LAYOUT );

        assertFalse( container.getRows().isEmpty() );

        Row row = getRowByIndex( FIRST_ROW );
        ComponentColumn column = ( ComponentColumn ) getColumns( row ).get( 0 );

        column.remove();

        assertTrue( container.getRows().isEmpty() );

    }

    @Test
    public void addGetPropertyTest() throws Exception {
        assertNull( container.getProperty( "key" ) );
        container.addProperty( "key", "value" );
        assertNotNull( container.getProperty( "key" ) );
        assertTrue( container.getProperties().containsKey( "key" ) );
    }

}
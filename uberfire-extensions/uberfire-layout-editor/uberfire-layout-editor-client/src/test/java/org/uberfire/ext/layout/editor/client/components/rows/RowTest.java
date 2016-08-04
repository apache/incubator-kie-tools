package org.uberfire.ext.layout.editor.client.components.rows;

import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;

import java.util.List;

import static org.jgroups.util.Util.assertEquals;

public class RowTest extends AbstractLayoutEditorTest {


    @Test
    public void dropOnLeftColumn() throws Exception {

        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row row = getRowByIndex( FIRST_ROW );
        Column dropColumn = getColumnByIndex( row, FIRST_COLUMN );

        assertEquals( 2, row.getColumns().size() );

        row.dropCommand().execute( new ColumnDrop( new LayoutComponent( "dragType" ), dropColumn.getId(),
                                                   ColumnDrop.Orientation.LEFT ) );

        Column newColumn = getColumnByIndex( row, FIRST_COLUMN );

        assertEquals( 3, row.getColumns().size() );
        assertEquals( "dragType", newColumn.getLayoutComponent().getDragTypeName() );
        assertEquals( dropColumn, getColumnByIndex( row, SECOND_COLUMN ) );
    }

    @Test
    public void dropOnRightColumn() throws Exception {

        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row row = getRowByIndex( FIRST_ROW );
        Column dropColumn = getColumnByIndex( row, FIRST_COLUMN );

        assertEquals( 2, row.getColumns().size() );

        row.dropCommand().execute( new ColumnDrop( new LayoutComponent( "dragType" ), dropColumn.getId(),
                                                   ColumnDrop.Orientation.RIGHT ) );

        Column newColumn = getColumnByIndex( row, SECOND_COLUMN );

        assertEquals( 3, row.getColumns().size() );
        assertEquals( "dragType", newColumn.getLayoutComponent().getDragTypeName() );
        assertEquals( dropColumn, getColumnByIndex( row, FIRST_COLUMN ) );
    }

    @Test
    public void dropAboveColumnShouldCreateColumnWithComponents() throws Exception {

        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row row = getRowByIndex( FIRST_ROW );
        Column dropColumn = getColumnByIndex( row, FIRST_COLUMN );
        LayoutComponent originalColumnLayoutComponent = dropColumn.getLayoutComponent();


        assertEquals( 2, row.getColumns().size() );

        row.dropCommand().execute( new ColumnDrop( new LayoutComponent( "dragType" ), dropColumn.getId(),
                                                   ColumnDrop.Orientation.DOWN ) );

        assertEquals( 2, row.getColumns().size() );

        ColumnWithComponents columnWithComponents = ( ColumnWithComponents ) getColumnByIndex( row, FIRST_COLUMN );

        List<Column> childs = extractColumnsFrom( columnWithComponents );

        assertEquals( 2, childs.size() );
        assertEquals( originalColumnLayoutComponent, childs.get( FIRST_COLUMN ).getLayoutComponent() );
        LayoutComponent newColumnLayoutComponent = childs.get( SECOND_COLUMN ).getLayoutComponent();
        assertEquals( "dragType", newColumnLayoutComponent.getDragTypeName() );
    }

    @Test
    public void dropUpperColumnShouldCreateColumnWithComponents() throws Exception {

        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row row = getRowByIndex( FIRST_ROW );
        Column dropColumn = getColumnByIndex( row, SECOND_COLUMN );
        LayoutComponent originalColumnLayoutComponent = dropColumn.getLayoutComponent();


        assertEquals( 2, row.getColumns().size() );

        row.dropCommand().execute( new ColumnDrop( new LayoutComponent( "dragType" ), dropColumn.getId(),
                                                   ColumnDrop.Orientation.UP ) );

        assertEquals( 2, row.getColumns().size() );

        ColumnWithComponents columnWithComponents = ( ColumnWithComponents ) getColumnByIndex( row, SECOND_COLUMN );

        List<Column> childs = extractColumnsFrom( columnWithComponents );

        assertEquals( 2, childs.size() );
        LayoutComponent newColumnLayoutComponent = childs.get( FIRST_COLUMN ).getLayoutComponent();
        assertEquals( "dragType", newColumnLayoutComponent.getDragTypeName() );
        assertEquals( originalColumnLayoutComponent, childs.get( SECOND_COLUMN ).getLayoutComponent() );
    }

    @Test
    public void resizeEventTest() throws Exception {

        loadLayout( SINGLE_ROW_TWO_COMPONENTS_LAYOUT );

        Row row = getRowByIndex( FIRST_ROW );
        Column first = getColumnByIndex( row, FIRST_COLUMN );
        Column second = getColumnByIndex( getRowByIndex( FIRST_ROW ), SECOND_COLUMN );

        Integer originalFirstSize = first.getSize();
        Integer originalSecondSize = second.getSize();

        row.resizeColumns( new ColumnResizeEvent( second.getId(), row.getId() ).left() );

        assertEquals( originalFirstSize - 1 , first.getSize());
        assertEquals( originalSecondSize + 1 , second.getSize());

        row.resizeColumns( new ColumnResizeEvent( second.getId(), row.getId() ).left() );

        assertEquals( originalFirstSize - 2 , first.getSize());
        assertEquals( originalSecondSize + 2 , second.getSize());

        row.resizeColumns( new ColumnResizeEvent( first.getId(), row.getId() ).right() );

        assertEquals( originalFirstSize - 1 , first.getSize());
        assertEquals( originalSecondSize + 1 , second.getSize());

    }
}
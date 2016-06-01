/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.model.impl;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class GridRowsMergedTest extends BaseGridTest {

    @Test
    public void testRemoveRow() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex > 0 && rowIndex < 4 ? "b" : "a" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 3, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( false, 1, "a" ) }
                           } );

        data.deleteRow( 2 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, false },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( false, 1, "a" ) }
                           } );
    }

    @Test
    public void testAppendRow() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );

        data.appendRow( new BaseGridRow() );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( false, 1, null ) }
                           } );
    }

    @Test
    public void testInsertRowAtZeroIndex() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );

        data.insertRow( 0,
                        new BaseGridRow() );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, null ) },
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );
    }

    @Test
    public void testInsertRowAtStartEndBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );

        data.insertRow( 2,
                        new BaseGridRow() );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( false, 1, null ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );
    }

    @Test
    public void testInsertRowAtMidBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "a" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );

        data.insertRow( 2,
                        new BaseGridRow() );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( false, 1, null ) },
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );
    }

    @Test
    public void testDeleteRowAtZeroIndex() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );

        data.deleteRow( 0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );
    }

    @Test
    public void testDeleteRowAtStartEndBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) }
                           } );

        data.deleteRow( 2 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( false, 1, "b" ) }
                           } );
    }

    @Test
    public void testDeleteRowAtMidBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );

        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );
        data.appendRow( new BaseGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "a" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );

        data.deleteRow( 2 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index4to3() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 4 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 3,
                        row4 );

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 4
        // row4 = a, 3

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "4" ) },
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "3" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index3to2() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 3 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 2,
                        row3 );

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 3
        // row3 = a, 2
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index2to1() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 2 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 1,
                        row2 );

        // row0 = a, 0
        // row1 = b, 2
        // row2 = a, 1
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index1to0() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 0,
                        row1 );

        // row0 = b, 1
        // row1 = a, 0
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 4, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index2to1_NewMergedBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, a
        // row1 = b, a
        // row2 = a, b
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 2, "a" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( true, 3, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );

        //Move row
        data.moveRowTo( 1,
                        row2 );

        // row0 = a, a
        // row1 = a, b
        // row2 = b, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowUp_Index3to2_NewMergedBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : ( rowIndex == 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );

        //Move row
        data.moveRowTo( 2,
                        row3 );

        // row0 = a, b
        // row1 = b, b
        // row2 = a, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 3, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "b" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index0to1() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 4 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 1,
                        row0 );

        // row0 = a, 1
        // row1 = a, 0
        // row2 = a, 2
        // row3 = a, 3
        // row4 = b, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index1to2() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 3 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 2
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 2,
                        row1 );

        // row0 = a, 0
        // row1 = a, 2
        // row2 = a, 1
        // row3 = b, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index2to3() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 2 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = a, 1
        // row2 = b, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 3,
                        row2 );

        // row0 = a, 0
        // row1 = a, 1
        // row2 = a, 3
        // row3 = b, 2
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "4" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index3to4() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : Integer.toString( rowIndex );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 3
        // row4 = a, 4

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) }
                           } );

        //Move row
        data.moveRowTo( 4,
                        row3 );

        // row0 = a, 0
        // row1 = b, 1
        // row2 = a, 2
        // row3 = a, 4
        // row4 = a, 3

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( false, 1, "0" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "1" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "2" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "4" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "3" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index1to2_NewMergedBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : ( rowIndex < 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, a
        // row1 = b, a
        // row2 = a, b
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 2, "a" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( true, 3, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );

        //Move row
        data.moveRowTo( 2,
                        row1 );

        // row0 = a, a
        // row1 = a, b
        // row2 = b, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, false, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );
    }

    @Test
    public void testMergedBlock_MoveRowDown_Index2to3_NewMergedBlock() {
        final GridData data = new BaseGridData();
        final GridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        final GridColumn<String> gc2 = new MockMergableGridColumn<String>( "col1",
                                                                           100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        final GridRow row0 = new BaseGridRow();
        final GridRow row1 = new BaseGridRow();
        final GridRow row2 = new BaseGridRow();
        final GridRow row3 = new BaseGridRow();
        final GridRow row4 = new BaseGridRow();
        data.appendRow( row0 );
        data.appendRow( row1 );
        data.appendRow( row2 );
        data.appendRow( row3 );
        data.appendRow( row4 );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++ ) {
                final String value = columnIndex == 0 ? ( rowIndex == 1 ? "b" : "a" ) : ( rowIndex == 2 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        // row0 = a, b
        // row1 = b, b
        // row2 = a, a
        // row3 = a, b
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( true, 0, "b" ) }
                           } );

        //Move row
        data.moveRowTo( 3,
                        row2 );

        // row0 = a, b
        // row1 = b, b
        // row2 = a, b
        // row3 = a, a
        // row4 = a, b

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ), Expected.build( true, 3, "b" ) },
                                   { Expected.build( false, 1, "b" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 3, "a" ), Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 0, "a" ), Expected.build( false, 1, "b" ) }
                           } );
    }

}

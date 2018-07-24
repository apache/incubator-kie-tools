/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.data;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.RowMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for DynamicData
 */
public class DynamicDataRowMapperTest extends BaseDynamicDataTests {

    @Before
    public void setup() {
        super.setup();

        //Setup date to merge
        //[-][-][-]
        //[1][1][1]
        //[1][1][1]
        //[1][1][1]
        //[2][2][2]
        //[2][2][2]
        //[2][2][2]
        //[-][-][-]
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );
        data.addRow( makeRow() );

        data.get( 0 ).get( 0 ).setValue( "-" );
        data.get( 0 ).get( 1 ).setValue( "-" );
        data.get( 0 ).get( 2 ).setValue( "-" );

        data.get( 1 ).get( 0 ).setValue( "1" );
        data.get( 1 ).get( 1 ).setValue( "1" );
        data.get( 1 ).get( 2 ).setValue( "1" );

        data.get( 2 ).get( 0 ).setValue( "1" );
        data.get( 2 ).get( 1 ).setValue( "1" );
        data.get( 2 ).get( 2 ).setValue( "1" );

        data.get( 3 ).get( 0 ).setValue( "1" );
        data.get( 3 ).get( 1 ).setValue( "1" );
        data.get( 3 ).get( 2 ).setValue( "1" );

        data.get( 4 ).get( 0 ).setValue( "2" );
        data.get( 4 ).get( 1 ).setValue( "2" );
        data.get( 4 ).get( 2 ).setValue( "2" );

        data.get( 5 ).get( 0 ).setValue( "2" );
        data.get( 5 ).get( 1 ).setValue( "2" );
        data.get( 5 ).get( 2 ).setValue( "2" );

        data.get( 6 ).get( 0 ).setValue( "2" );
        data.get( 6 ).get( 1 ).setValue( "2" );
        data.get( 6 ).get( 2 ).setValue( "2" );

        data.get( 7 ).get( 0 ).setValue( "-" );
        data.get( 7 ).get( 1 ).setValue( "-" );
        data.get( 7 ).get( 2 ).setValue( "-" );
    }

    @Test
    public void testMapToMergedRow() {
        //0=[-][-][-] --> 0=[-][-][-]
        //1=[1][1][1] --> 1=[1][1][1]
        //2=[1][1][1] --> 2=[2][2][2]
        //3=[1][1][1] --> 3=[2][2][2]
        //4=[2][2][2] --> 4=[2][2][2]
        //5=[2][2][2] --> 5=[-][-][-]
        //6=[2][2][2]
        //7=[-][-][-]
        RowMapper rowMapper = new RowMapper( data );
        CellValue<? extends Comparable<?>> cv = data.get( 1 ).get( 0 );

        data.setMerged( true );
        data.applyModelGrouping( cv );

        assertEquals( 0,
                      rowMapper.mapToMergedRow( 0 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 1 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 2 ) );
        assertEquals( 1,
                      rowMapper.mapToMergedRow( 3 ) );
        assertEquals( 2,
                      rowMapper.mapToMergedRow( 4 ) );
        assertEquals( 3,
                      rowMapper.mapToMergedRow( 5 ) );
        assertEquals( 4,
                      rowMapper.mapToMergedRow( 6 ) );
        assertEquals( 5,
                      rowMapper.mapToMergedRow( 7 ) );

    }

    @Test
    public void testMapToAbsoluteRow() {
        //0=[-][-][-] --> 0=[-][-][-]
        //1=[1][1][1] --> 1=[1][1][1]
        //2=[1][1][1] --> 2=[2][2][2]
        //3=[1][1][1] --> 3=[2][2][2]
        //4=[2][2][2] --> 4=[2][2][2]
        //5=[2][2][2] --> 5=[-][-][-]
        //6=[2][2][2]
        //7=[-][-][-]
        RowMapper rowMapper = new RowMapper( data );
        CellValue<? extends Comparable<?>> cv = data.get( 1 ).get( 0 );

        data.setMerged( true );
        data.applyModelGrouping( cv );

        assertEquals( 0,
                      rowMapper.mapToAbsoluteRow( 0 ) );
        assertEquals( 1,
                      rowMapper.mapToAbsoluteRow( 1 ) );
        assertEquals( 4,
                      rowMapper.mapToAbsoluteRow( 2 ) );
        assertEquals( 5,
                      rowMapper.mapToAbsoluteRow( 3 ) );
        assertEquals( 6,
                      rowMapper.mapToAbsoluteRow( 4 ) );
        assertEquals( 7,
                      rowMapper.mapToAbsoluteRow( 5 ) );

    }

    @Test
    public void testMapToAllAbsoluteRows() {
        //0=[-][-][-] --> 0=[-][-][-]
        //1=[1][1][1] --> 1=[1][1][1]
        //2=[1][1][1] --> 2=[2][2][2]
        //3=[1][1][1] --> 3=[2][2][2]
        //4=[2][2][2] --> 4=[2][2][2]
        //5=[2][2][2] --> 5=[-][-][-]
        //6=[2][2][2]
        //7=[-][-][-]
        RowMapper rowMapper = new RowMapper( data );
        CellValue<? extends Comparable<?>> cv = data.get( 1 ).get( 0 );

        data.setMerged( true );
        data.applyModelGrouping( cv );

        Set<Integer> results;
        results = rowMapper.mapToAllAbsoluteRows( 0 );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( 0 ) );

        results = rowMapper.mapToAllAbsoluteRows( 1 );
        assertEquals( 3,
                      results.size() );
        assertTrue( results.contains( 1 ) );
        assertTrue( results.contains( 2 ) );
        assertTrue( results.contains( 3 ) );

        results = rowMapper.mapToAllAbsoluteRows( 2 );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( 4 ) );

        results = rowMapper.mapToAllAbsoluteRows( 3 );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( 5 ) );

        results = rowMapper.mapToAllAbsoluteRows( 4 );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( 6 ) );

        results = rowMapper.mapToAllAbsoluteRows( 5 );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( 7 ) );

    }

}

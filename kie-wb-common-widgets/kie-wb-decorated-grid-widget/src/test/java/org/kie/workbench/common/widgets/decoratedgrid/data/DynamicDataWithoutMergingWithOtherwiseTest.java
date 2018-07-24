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

import org.junit.Test;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue.CellState;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

import static junit.framework.Assert.assertEquals;

/**
 * Tests for DynamicData
 */
public class DynamicDataWithoutMergingWithOtherwiseTest extends BaseDynamicDataTests {

    @Override
    public void setup() {
        super.setup();

        //Setup date to merge
        //[1][-][3]
        //[1][2][*]
        //[-][2][3]
        data.get( 0 ).get( 0 ).setValue( "1" );
        data.get( 0 ).get( 1 ).setValue( "-" );
        data.get( 0 ).get( 2 ).setValue( "3" );

        data.get( 1 ).get( 0 ).setValue( "1" );
        data.get( 1 ).get( 1 ).setValue( "2" );
        data.get( 1 ).get( 2 ).addState( CellState.OTHERWISE );

        data.get( 2 ).get( 0 ).setValue( "-" );
        data.get( 2 ).get( 1 ).setValue( "2" );
        data.get( 2 ).get( 2 ).setValue( "3" );

    }

    @Test
    public void testIndexing_DataCoordinates() {

        Coordinate c;
        c = data.get( 0 ).get( 0 ).getCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 0 ).get( 1 ).getCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 0 ).get( 2 ).getCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 1 ).get( 0 ).getCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 1 ).get( 1 ).getCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 1 ).get( 2 ).getCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 2 ).get( 0 ).getCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 2 ).get( 1 ).getCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 2 ).get( 2 ).getCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      2 );
    }

    @Test
    public void testIndexing_HtmlCoordinates() {

        Coordinate c;
        c = data.get( 0 ).get( 0 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 0 ).get( 1 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 0 ).get( 2 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 1 ).get( 0 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 1 ).get( 1 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 1 ).get( 2 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 2 ).get( 0 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 2 ).get( 1 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 2 ).get( 2 ).getHtmlCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      2 );
    }

    @Test
    public void testIndexing_PhysicalCoordinates() {

        Coordinate c;
        c = data.get( 0 ).get( 0 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 0 ).get( 1 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 0 ).get( 2 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      0 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 1 ).get( 0 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 1 ).get( 1 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 1 ).get( 2 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      1 );
        assertEquals( c.getCol(),
                      2 );

        c = data.get( 2 ).get( 0 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      0 );
        c = data.get( 2 ).get( 1 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      1 );
        c = data.get( 2 ).get( 2 ).getPhysicalCoordinate();
        assertEquals( c.getRow(),
                      2 );
        assertEquals( c.getCol(),
                      2 );
    }

    @Test
    public void testIndexing_RowSpans() {

        CellValue<? extends Comparable<?>> cv;
        cv = data.get( 0 ).get( 0 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 0 ).get( 1 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 0 ).get( 2 );
        assertEquals( cv.getRowSpan(),
                      1 );

        cv = data.get( 1 ).get( 0 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 1 ).get( 1 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 1 ).get( 2 );
        assertEquals( cv.getRowSpan(),
                      1 );

        cv = data.get( 2 ).get( 0 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 2 ).get( 1 );
        assertEquals( cv.getRowSpan(),
                      1 );
        cv = data.get( 2 ).get( 2 );
        assertEquals( cv.getRowSpan(),
                      1 );
    }

}

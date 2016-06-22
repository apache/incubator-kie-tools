/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerBoundsHelper.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableModellerBoundsHelperTest {

    private GuidedDecisionTableModellerBoundsHelper helper = new GuidedDecisionTableModellerBoundsHelper();

    @Test(expected = NullPointerException.class)
    public void nullCollection() {
        helper.getBounds( null );
    }

    @Test
    public void emptyCollection() {
        final Bounds b = helper.getBounds( Collections.<GuidedDecisionTableView.Presenter>emptySet() );
        assertEquals( BOUNDS_MIN_X,
                      b.getX(),
                      0.0 );
        assertEquals( BOUNDS_MIN_Y,
                      b.getY(),
                      0.0 );
        assertEquals( BOUNDS_MAX_X - BOUNDS_MIN_X,
                      b.getWidth(),
                      0.0 );
        assertEquals( BOUNDS_MAX_Y - BOUNDS_MIN_Y,
                      b.getHeight(),
                      0.0 );
    }

    @Test
    public void emptyOneDecisionTableWithinMinimumBounds() {
        final Bounds b = helper.getBounds( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( makeTable( 0, 0, 400, 400 ) );
        }} );
        assertEquals( BOUNDS_MIN_X,
                      b.getX(),
                      0.0 );
        assertEquals( BOUNDS_MIN_Y,
                      b.getY(),
                      0.0 );
        assertEquals( BOUNDS_MAX_X - BOUNDS_MIN_X,
                      b.getWidth(),
                      0.0 );
        assertEquals( BOUNDS_MAX_Y - BOUNDS_MIN_Y,
                      b.getHeight(),
                      0.0 );
    }

    @Test
    public void multipleDecisionTablesCheckLeftBounds() {
        final Bounds b = helper.getBounds( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( makeTable( 0, 0, 400, 400 ) );
            add( makeTable( -2200, 0, 400, 400 ) );
        }} );

        final double expectedMinX = -2200 - BOUNDS_PADDING;

        assertEquals( expectedMinX,
                      b.getX(),
                      0.0 );
        assertEquals( BOUNDS_MIN_Y,
                      b.getY(),
                      0.0 );
        assertEquals( BOUNDS_MAX_X - expectedMinX,
                      b.getWidth(),
                      0.0 );
        assertEquals( BOUNDS_MAX_Y - BOUNDS_MIN_Y,
                      b.getHeight(),
                      0.0 );
    }

    @Test
    public void multipleDecisionTablesCheckRightBounds() {
        final Bounds b = helper.getBounds( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( makeTable( 0, 0, 400, 400 ) );
            add( makeTable( 1800, 0, 400, 400 ) );
        }} );

        final double expectedMaxX = 1800 + 400 + BOUNDS_PADDING;

        assertEquals( BOUNDS_MIN_X,
                      b.getX(),
                      0.0 );
        assertEquals( BOUNDS_MIN_Y,
                      b.getY(),
                      0.0 );
        assertEquals( expectedMaxX - BOUNDS_MIN_X,
                      b.getWidth(),
                      0.0 );
        assertEquals( BOUNDS_MAX_Y - BOUNDS_MIN_Y,
                      b.getHeight(),
                      0.0 );
    }

    @Test
    public void multipleDecisionTablesCheckTopBounds() {
        final Bounds b = helper.getBounds( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( makeTable( 0, 0, 400, 400 ) );
            add( makeTable( 0, -2200, 400, 400 ) );
        }} );

        final double expectedMinY = -2200 - BOUNDS_PADDING;

        assertEquals( BOUNDS_MIN_X,
                      b.getX(),
                      0.0 );
        assertEquals( expectedMinY,
                      b.getY(),
                      0.0 );
        assertEquals( BOUNDS_MAX_X - BOUNDS_MIN_X,
                      b.getWidth(),
                      0.0 );
        assertEquals( BOUNDS_MAX_Y - expectedMinY,
                      b.getHeight(),
                      0.0 );
    }

    @Test
    public void multipleDecisionTablesCheckBottomBounds() {
        final Bounds b = helper.getBounds( new HashSet<GuidedDecisionTableView.Presenter>() {{
            add( makeTable( 0, 0, 400, 400 ) );
            add( makeTable( 0, 1800, 400, 400 ) );
        }} );

        final double expectedMaxY = 1800 + 400 + BOUNDS_PADDING;

        assertEquals( BOUNDS_MIN_X,
                      b.getX(),
                      0.0 );
        assertEquals( BOUNDS_MIN_Y,
                      b.getY(),
                      0.0 );
        assertEquals( BOUNDS_MAX_X - BOUNDS_MIN_X,
                      b.getWidth(),
                      0.0 );
        assertEquals( expectedMaxY - BOUNDS_MIN_Y,
                      b.getHeight(),
                      0.0 );
    }

    private GuidedDecisionTableView.Presenter makeTable( final double x,
                                                         final double y,
                                                         final double width,
                                                         final double height ) {
        final GuidedDecisionTableView.Presenter dtPresenter = mock( GuidedDecisionTableView.Presenter.class );
        final GuidedDecisionTableView dtView = mock( GuidedDecisionTableView.class );
        when( dtPresenter.getView() ).thenReturn( dtView );
        when( dtView.getX() ).thenReturn( x );
        when( dtView.getY() ).thenReturn( y );
        when( dtView.getWidth() ).thenReturn( width );
        when( dtView.getHeight() ).thenReturn( height );
        return dtPresenter;
    }

}

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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class RadarMenuBuilderTest {

    @Mock
    private RadarMenuView view;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    private GuidedDecisionTableModellerView modellerView;

    @Mock
    private GridLayer modellerLayer;

    @Mock
    private Viewport modellerViewport;

    @Mock
    private Attributes attributes;

    @Mock
    private Bounds bounds;

    @Mock
    private Bounds visibleBounds;

    private Transform transform;
    private RadarMenuBuilder builder;
    private Set<GuidedDecisionTableView.Presenter> dtables = new HashSet<>();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.transform = new Transform();
        this.builder = new RadarMenuBuilder( view );
        this.builder.setup();
        this.builder.setModeller( modeller );

        when( modeller.getView() ).thenReturn( modellerView );
        when( modeller.getView().getGridLayerView() ).thenReturn( modellerLayer );
        when( modellerLayer.getViewport() ).thenReturn( modellerViewport );
        when( modellerViewport.getTransform() ).thenReturn( transform );
        when( modellerViewport.getAttributes() ).thenReturn( attributes );
        when( modeller.getView().getBounds() ).thenReturn( bounds );
        when( modeller.getView().getGridLayerView().getVisibleBounds() ).thenReturn( visibleBounds );
        when( modeller.getAvailableDecisionTables() ).thenReturn( dtables );
    }

    @Test
    public void testOnClick() {
        builder.onClick();

        verify( view,
                times( 1 ) ).reset();
        verify( view,
                times( 1 ) ).setModellerBounds( eq( bounds ) );
        verify( view,
                times( 1 ) ).setAvailableDecisionTables( eq( dtables ) );
        verify( view,
                times( 1 ) ).setVisibleBounds( eq( visibleBounds ) );
    }

    @Test
    public void testOnDragVisibleBounds() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass( Transform.class );

        builder.onDragVisibleBounds( 10, 10 );

        verify( modellerViewport,
                times( 1 ) ).setTransform( transformArgumentCaptor.capture() );
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull( result );
        assertEquals( -10.0,
                      result.getTranslateX(),
                      0.0 );
        assertEquals( -10.0,
                      result.getTranslateY(),
                      0.0 );

        verify( modellerLayer,
                times( 1 ) ).batch();
    }

    @Test
    public void testOnDragVisibleBoundsScaled50pct() {
        final ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass( Transform.class );

        transform.scale( 0.5, 0.5 );
        builder.onDragVisibleBounds( 10, 10 );

        verify( modellerViewport,
                times( 1 ) ).setTransform( transformArgumentCaptor.capture() );
        final Transform result = transformArgumentCaptor.getValue();
        assertNotNull( result );
        assertEquals( -5.0,
                      result.getTranslateX(),
                      0.0 );
        assertEquals( -5.0,
                      result.getTranslateY(),
                      0.0 );

        verify( modellerLayer,
                times( 1 ) ).batch();
    }

    @Test
    public void testOnUpdateRadarEventNullModeller() {
        builder.onUpdateRadarEvent( new RadarMenuBuilder.UpdateRadarEvent( null ) );

        verify( view,
                never() ).setVisibleBounds( any( Bounds.class ) );
    }

    @Test
    public void testOnUpdateRadarEventDifferentModeller() {
        builder.onUpdateRadarEvent( new RadarMenuBuilder.UpdateRadarEvent( mock( GuidedDecisionTableModellerView.Presenter.class ) ) );

        verify( view,
                never() ).setVisibleBounds( any( Bounds.class ) );
    }

    @Test
    public void testOnUpdateRadarEventAssociatedModeller() {
        builder.onUpdateRadarEvent( new RadarMenuBuilder.UpdateRadarEvent( modeller ) );

        verify( view,
                times( 1 ) ).setVisibleBounds( eq( visibleBounds ) );
    }

    @Test
    public void testEnableDrag_Pinned() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( modeller,
                                                                          true ) );

        verify( view,
                times( 1 ) ).enableDrag( eq( false ) );
    }

    @Test
    public void testEnableDrag_Pinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( mock( GuidedDecisionTableModellerView.Presenter.class ),
                                                                          true ) );

        verify( view,
                never() ).enableDrag( any( Boolean.class ) );
    }

    @Test
    public void testEnableDrag_Unpinned() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( modeller,
                                                                          false ) );

        verify( view,
                times( 1 ) ).enableDrag( eq( true ) );
    }

    @Test
    public void testEnableDrag_Unpinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( mock( GuidedDecisionTableModellerView.Presenter.class ),
                                                                          false ) );

        verify( view,
                never() ).enableDrag( any( Boolean.class ) );
    }

}

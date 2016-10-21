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
package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridLayerTest {

    @Mock
    private Viewport viewport;

    @Mock
    private GridRenderer renderer;

    @Mock
    private Mediators mediators;

    private GridWidget gridWidget;

    private GridData uiModel;

    private GridLayer gridLayer;

    private Transform transform;

    @Before
    public void setup() {
        this.transform = new Transform();
        this.uiModel = new BaseGridData();
        this.gridWidget = new BaseGridWidget( uiModel,
                                              gridLayer,
                                              gridLayer,
                                              renderer ) {
            @Override
            public void select() {
                //Don't render Selector for tests
            }
        };

        final LienzoPanel panel = new LienzoPanel( 500,
                                                   500 );
        final DefaultGridLayer wrapped = new DefaultGridLayer() {

            @Override
            public Layer batch() {
                //Don't render Layer for tests
                return this;
            }

            @Override
            public Layer batch( final GridLayerRedrawManager.PrioritizedCommand command ) {
                //Don't render Layer for tests
                return this;
            }
        };
        panel.add( wrapped );
        this.gridLayer = spy( wrapped );
        this.gridLayer.add( gridWidget );

        when( gridLayer.getViewport() ).thenReturn( viewport );
        when( gridWidget.getViewport() ).thenReturn( viewport );
        when( viewport.getTransform() ).thenReturn( transform );
        when( viewport.getMediators() ).thenReturn( mediators );
    }

    @Test
    public void checkFlipToGridWidgetWhenPinned() {
        gridLayer.enterPinnedMode( gridWidget,
                                   new GridLayerRedrawManager.PrioritizedCommand( 0 ) {
                                       @Override
                                       public void execute() {

                                       }
                                   } );

        gridLayer.flipToGridWidget( gridWidget );

        verify( gridLayer,
                times( 1 ) ).updatePinnedContext( eq( gridWidget ) );
        verify( gridLayer,
                times( 1 ) ).batch( any( GridLayerRedrawManager.PrioritizedCommand.class ) );
    }

    @Test
    public void checkFlipToGridWidgetWhenNotPinned() {
        gridLayer.flipToGridWidget( gridWidget );

        verify( gridLayer,
                never() ).updatePinnedContext( eq( gridWidget ) );
        verify( gridLayer,
                never() ).batch( any( GridLayerRedrawManager.PrioritizedCommand.class ) );
    }

    @Test
    public void checkScrollToGridWidgetWhenPinned() {
        gridLayer.enterPinnedMode( gridWidget,
                                   new GridLayerRedrawManager.PrioritizedCommand( 0 ) {
                                       @Override
                                       public void execute() {
                                           //Do nothing
                                       }
                                   } );

        gridLayer.scrollToGridWidget( gridWidget );

        verify( gridLayer,
                never() ).select( eq( gridWidget ) );
    }

    @Test
    public void checkScrollToGridWidgetWhenNotPinned() {
        gridLayer.scrollToGridWidget( gridWidget );

        verify( gridLayer,
                times( 1 ) ).select( eq( gridWidget ) );
    }

}

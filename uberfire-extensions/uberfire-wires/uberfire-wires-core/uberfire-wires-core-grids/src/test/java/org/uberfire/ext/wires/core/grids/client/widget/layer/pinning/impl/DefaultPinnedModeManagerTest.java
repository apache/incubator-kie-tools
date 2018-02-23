/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import java.util.Set;

import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultPinnedModeManagerTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private Layer layer;

    @Mock
    private TransformMediator defaultMediator;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private Command enterPinnedModeCommand;

    @Mock
    private Command exitPinnedModeCommand;

    private Mediators mediators;

    private GridPinnedModeManager manager;

    @Before
    public void setup() {
        this.manager = new DefaultPinnedModeManager(gridLayer) {
            @Override
            protected void doEnterPinnedMode(final Command onStartCommand,
                                             final GridWidget gridWidget,
                                             final Set<GridWidget> gridWidgetsToFadeFromView,
                                             final Set<IPrimitive<?>> gridWidgetConnectorsToFadeFromView) {
                assertEquals(DefaultPinnedModeManagerTest.this.gridWidget,
                             gridWidget);
                assertTrue(gridWidgetsToFadeFromView.isEmpty());
                assertTrue(gridWidgetConnectorsToFadeFromView.isEmpty());
                onStartCommand.execute();
            }

            @Override
            protected void doExitPinnedMode(final Command onCompleteCommand,
                                            final Set<GridWidget> gridWidgetsToFadeIntoView,
                                            final Set<IPrimitive<?>> gridWidgetConnectorsToFadeIntoView) {
                assertTrue(gridWidgetsToFadeIntoView.isEmpty());
                assertTrue(gridWidgetConnectorsToFadeIntoView.isEmpty());
                onCompleteCommand.execute();
            }
        };
        this.mediators = new Mediators(viewport);
        this.mediators.push(new RestrictedMousePanMediator(gridLayer));

        when(gridLayer.getViewport()).thenReturn(viewport);
        when(gridLayer.getDefaultTransformMediator()).thenReturn(defaultMediator);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getLayer()).thenReturn(layer);
        when(viewport.getMediators()).thenReturn(mediators);
        when(viewport.getTransform()).thenReturn(transform);
        when(transform.copy()).thenReturn(transform);
        when(transform.getInverse()).thenReturn(transform);
    }

    @Test
    public void enteringPinnedModeSetsMediatorToGridTransformMediator() {
        manager.enterPinnedMode(gridWidget,
                                enterPinnedModeCommand);

        verify(enterPinnedModeCommand,
               times(1)).execute();

        assertNotNull(manager.getPinnedContext());

        final IMediator mediator = mediators.pop();
        assertTrue(mediator instanceof RestrictedMousePanMediator);

        final RestrictedMousePanMediator rmpm = (RestrictedMousePanMediator) mediator;
        final TransformMediator tm = rmpm.getTransformMediator();

        assertTrue(tm instanceof GridTransformMediator);
    }

    @Test
    public void exitingPinnedModeSetsMediatorToDefaultTransformMediator() {
        manager.enterPinnedMode(gridWidget,
                                enterPinnedModeCommand);

        manager.exitPinnedMode(exitPinnedModeCommand);

        verify(exitPinnedModeCommand,
               times(1)).execute();

        assertNull(manager.getPinnedContext());

        final IMediator mediator = mediators.pop();
        assertTrue(mediator instanceof RestrictedMousePanMediator);

        final RestrictedMousePanMediator rmpm = (RestrictedMousePanMediator) mediator;
        final TransformMediator tm = rmpm.getTransformMediator();

        assertEquals(defaultMediator,
                     tm);
    }
}

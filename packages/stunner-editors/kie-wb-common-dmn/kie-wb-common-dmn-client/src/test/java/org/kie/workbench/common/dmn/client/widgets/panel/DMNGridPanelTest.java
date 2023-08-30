/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.panel;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridPanelTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DivElement gridLayerDivElement;

    @Mock
    private Style gridLayerDivElementStyle;

    @Mock
    private Node gridLayerNode;

    @Mock
    private Context2D context2D;

    @Mock
    private NativeContext2D nativeContext2D;

    @Mock
    private RestrictedMousePanMediator mousePanMediator;

    @Mock
    private ContextMenuHandler contextMenuHandler;

    @Mock
    private TransformMediator transformMediator;

    @Mock
    private Transform transform;

    @Mock
    private Transform newTransform;

    @Mock
    private Viewport viewport;

    private DMNGridPanel gridPanel;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(gridLayerDivElement.getStyle()).thenReturn(gridLayerDivElementStyle);
        when(gridLayer.getContext()).thenReturn(context2D);
        when(gridLayer.asNode()).thenReturn(gridLayerNode);
        when(context2D.getNativeContext()).thenReturn(nativeContext2D);

        this.gridPanel = spy(new DMNGridPanel(gridLayer,
                                              mousePanMediator,
                                              contextMenuHandler));
        doAnswer((o) -> {
            ((Scheduler.ScheduledCommand) o.getArguments()[0]).execute();
            return null;
        }).when(gridPanel).doResize(Mockito.<Scheduler.ScheduledCommand>any());

        doNothing().when(gridPanel).updatePanelSize();
        doNothing().when(gridPanel).refreshScrollPosition();

        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        when(mousePanMediator.getTransformMediator()).thenReturn(transformMediator);
        when(transformMediator.adjust(eq(transform), Mockito.<Bounds>any())).thenReturn(newTransform);
    }

    @Test
    public void testDefaultGridLayer() {
        assertThat(gridPanel.getDefaultGridLayer()).isEqualTo(gridLayer);
    }

    @Test
    public void testOnResize() {
        gridPanel.onResize();

        verify(gridPanel).updatePanelSize();
        verify(gridPanel).refreshScrollPosition();
        verify(viewport).setTransform(eq(newTransform));
        //GridLayer.batch() is called once during DMNGridPanel construction and once during resize.
        verify(gridLayer, times(2)).batch();
    }
}

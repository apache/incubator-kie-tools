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
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridPanelControlImplTest {

    @Mock
    private DMNSession session;

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
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private RestrictedMousePanMediator mousePanMediator;

    private DMNGridPanelControlImpl control;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.control = new DMNGridPanelControlImpl();

        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);
        when(session.getMousePanMediator()).thenReturn(mousePanMediator);

        when(gridLayerDivElement.getStyle()).thenReturn(gridLayerDivElementStyle);
        when(gridLayer.getContext()).thenReturn(context2D);
        when(gridLayer.asNode()).thenReturn(gridLayerNode);
        when(context2D.getNativeContext()).thenReturn(nativeContext2D);
    }

    @Test
    public void testBind() {
        control.bind(session);

        assertNotNull(control.getGridPanel());
    }

    @Test
    public void testDoInit() {
        assertNull(control.getGridPanel());

        control.doInit();

        assertNull(control.getGridPanel());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        control.doDestroy();

        assertNull(control.getGridPanel());
    }
}

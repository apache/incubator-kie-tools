/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ConnectionAcceptorControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.DockingAcceptorControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.PanControlImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ZoomWheelControlImpl;
import org.kie.workbench.common.stunner.cm.client.canvas.controls.builder.CaseManagementElementBuilderControl;
import org.kie.workbench.common.stunner.cm.client.canvas.controls.builder.CaseManagementNodeBuilderControl;
import org.kie.workbench.common.stunner.cm.client.canvas.controls.containment.CaseManagementContainmentAcceptorControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControlSingleLine;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.EdgeBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasFactoryTest {

    private ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls;
    private ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls;
    private ManagedInstance<DockingAcceptorControl> dockingAcceptorControls;
    private ManagedInstance<CanvasInPlaceTextEditorControl> nameEditionControls;
    private ManagedInstance<SelectionControl> selectionControls;
    private ManagedInstance<ElementBuilderControl> elementBuilderControls;
    private ManagedInstance<NodeBuilderControl> nodeBuilderControls;
    private ManagedInstance<EdgeBuilderControl> edgeBuilderControls;
    private ManagedInstance<ZoomControl> zoomControls;
    private ManagedInstance<PanControl> panControls;
    private ManagedInstance<KeyboardControl> keyboardControls;
    private ManagedInstance<ClipboardControl> clipboardControls;
    private ManagedInstance<AbstractCanvas> canvasInstances;
    private ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    private ManagedInstance<ControlPointControl> controlPointControls;

    @Mock
    private Layer layer;

    private WiresManager wiresManager;

    private CaseManagementCanvasFactory factory;

    @Before
    public void setup() {
        when(layer.uuid()).thenReturn("uuid");
        this.wiresManager = WiresManager.get(layer);

        this.connectionAcceptorControls = mockManagedInstance(ConnectionAcceptorControlImpl.class);
        this.containmentAcceptorControls = mockManagedInstance(CaseManagementContainmentAcceptorControlImpl.class);
        this.dockingAcceptorControls = mockManagedInstance(DockingAcceptorControlImpl.class);
        this.nameEditionControls = mockManagedInstance(CanvasInPlaceTextEditorControlSingleLine.class);
        this.selectionControls = mockManagedInstance(SelectionControl.class);
        this.elementBuilderControls = mockManagedInstance(CaseManagementElementBuilderControl.class);
        this.nodeBuilderControls = mockManagedInstance(CaseManagementNodeBuilderControl.class);
        this.edgeBuilderControls = mockManagedInstance(EdgeBuilderControlImpl.class);
        this.zoomControls = mockManagedInstance(ZoomWheelControlImpl.class);
        this.panControls = mockManagedInstance(PanControlImpl.class);
        this.keyboardControls = mockManagedInstance(KeyboardControlImpl.class);
        this.clipboardControls = mockManagedInstance(LocalClipboardControl.class);
        this.controlPointControls = mockManagedInstance(ControlPointControl.class);
        this.canvasInstances = mockManagedInstance(CaseManagementCanvasPresenter.class,
                                                   (c) -> when(c.getWiresManager()).thenReturn(wiresManager));
        this.canvasHandlerInstances = mockManagedInstance(CaseManagementCanvasHandler.class);

        this.factory = new CaseManagementCanvasFactory(connectionAcceptorControls,
                                                       containmentAcceptorControls,
                                                       dockingAcceptorControls,
                                                       nameEditionControls,
                                                       selectionControls,
                                                       elementBuilderControls,
                                                       nodeBuilderControls,
                                                       edgeBuilderControls,
                                                       zoomControls,
                                                       panControls,
                                                       keyboardControls,
                                                       clipboardControls,
                                                       canvasInstances,
                                                       canvasHandlerInstances,
                                                       controlPointControls);
        factory.init();
    }

    @Test
    public void testControls() {
        ConnectionAcceptorControl connectionAcceptorControl = factory.newControl(ConnectionAcceptorControl.class);
        assertNotNull(connectionAcceptorControl);
        ContainmentAcceptorControl containmentAcceptorControl = factory.newControl(ContainmentAcceptorControl.class);
        assertNotNull(containmentAcceptorControl);
        DockingAcceptorControl dockingAcceptorControl = factory.newControl(DockingAcceptorControl.class);
        assertNotNull(dockingAcceptorControl);
        CanvasInPlaceTextEditorControl canvasInPlaceTextEditorControl = factory.newControl(CanvasInPlaceTextEditorControl.class);
        assertNotNull(canvasInPlaceTextEditorControl);
        SelectionControl selectionControl = factory.newControl(SelectionControl.class);
        assertNotNull(selectionControl);
        ElementBuilderControl elementBuilderControl = factory.newControl(ElementBuilderControl.class);
        assertNotNull(elementBuilderControl);
        NodeBuilderControl nodeBuilderControl = factory.newControl(NodeBuilderControl.class);
        assertNotNull(nodeBuilderControl);
        EdgeBuilderControl edgeBuilderControl = factory.newControl(EdgeBuilderControl.class);
        assertNotNull(edgeBuilderControl);
        ZoomControl zoomControl = factory.newControl(ZoomControl.class);
        assertNotNull(zoomControl);
        PanControl panControl = factory.newControl(PanControl.class);
        assertNotNull(panControl);
        KeyboardControl keyboardControl = factory.newControl(KeyboardControl.class);
        assertNotNull(keyboardControl);
    }

    @Test
    public void testCanvases() {
        assertNotNull(factory.newCanvas());
        assertNotNull(factory.newCanvasHandler());
    }

    @SuppressWarnings("unchecked, unused")
    private <T, C extends T> ManagedInstance<T> mockManagedInstance(final Class<C> concrete) {
        return mockManagedInstance(concrete,
                                   (c) -> {/*Do nothing*/});
    }

    @SuppressWarnings("unchecked, unused")
    private <T, C extends T> ManagedInstance<T> mockManagedInstance(final Class<C> concrete,
                                                                    final Consumer<C> setup) {
        final C mock = mock(concrete);
        final ManagedInstance<T> managedInstance = mock(ManagedInstance.class);
        when(managedInstance.isAmbiguous()).thenReturn(false);
        when(managedInstance.isUnsatisfied()).thenReturn(false);
        when(managedInstance.get()).thenReturn(mock);
        setup.accept(mock);
        return managedInstance;
    }
}

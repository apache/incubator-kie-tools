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
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
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
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.EdgeBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
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
    private ManagedInstance<AbstractCanvas> canvasInstances;
    private ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;

    @Mock
    private WiresControlFactory caseManagementControlFactory;

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
        this.nameEditionControls = mockManagedInstance(CanvasInPlaceTextEditorControlImpl.class);
        this.selectionControls = mockManagedInstance(SelectionControl.class);
        this.elementBuilderControls = mockManagedInstance(CaseManagementElementBuilderControl.class);
        this.nodeBuilderControls = mockManagedInstance(CaseManagementNodeBuilderControl.class);
        this.edgeBuilderControls = mockManagedInstance(EdgeBuilderControlImpl.class);
        this.zoomControls = mockManagedInstance(ZoomWheelControlImpl.class);
        this.panControls = mockManagedInstance(PanControlImpl.class);
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
                                                       canvasInstances,
                                                       canvasHandlerInstances,
                                                       caseManagementControlFactory);
        factory.init();
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
        when(managedInstance.get()).thenReturn(mock);
        setup.accept(mock);
        return managedInstance;
    }

    @Test
    public void checkCanvasHasWiresControlFactorySet() {
        factory.newCanvas();

        assertEquals(wiresManager.getControlFactory(),
                     caseManagementControlFactory);
    }
}

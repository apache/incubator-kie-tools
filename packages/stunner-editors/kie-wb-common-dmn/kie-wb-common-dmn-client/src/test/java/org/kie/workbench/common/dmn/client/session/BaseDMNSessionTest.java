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

package org.kie.workbench.common.dmn.client.session;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorControl;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayerControl;
import org.kie.workbench.common.dmn.client.widgets.layer.MousePanMediatorControl;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionLoader;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class BaseDMNSessionTest<S extends AbstractSession<AbstractCanvas, AbstractCanvasHandler>> {

    @Mock
    protected CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    @Mock
    protected Metadata metadata;

    @Mock
    protected Command callback;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected SessionLoader sessionLoader;

    @Mock
    protected StunnerPreferences stunnerPreferences;

    @Mock
    protected AbstractCanvas canvas;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected MediatorsControl mediatorsControl;

    @Mock
    protected SelectionControl selectionControl;

    @Mock
    protected ExpressionGridCache expressionGridCache;

    @Mock
    protected DMNGridLayerControl gridLayerControl;

    @Mock
    protected DMNGridLayer gridLayer;

    @Mock
    protected DMNGridPanelControl gridPanelControl;

    @Mock
    protected DMNGridPanel gridPanel;

    @Mock
    protected CellEditorControl cellEditorControl;

    @Mock
    protected CellEditorControls cellEditorControls;

    @Mock
    protected MousePanMediatorControl mousePanMediatorControl;

    @Mock
    protected RestrictedMousePanMediator mousePanMediator;

    @Mock
    protected ExpressionEditorControl expressionEditorControl;

    @Mock
    protected ExpressionEditorView.Presenter expressionEditor;

    protected ManagedSession managedSession;

    protected ManagedInstance<AbstractCanvas> canvasInstances;

    protected ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;

    protected ManagedInstance<CanvasControl<AbstractCanvas>> canvasControlInstances;

    protected ManagedInstance<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlInstances;

    protected S session;

    protected Map<CanvasControl, Class> canvasControlRegistrations;

    protected Map<CanvasControl, Class> canvasHandlerControlRegistrations;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.canvasInstances = new ManagedInstanceStub<>(canvas);
        this.canvasHandlerInstances = new ManagedInstanceStub<>(canvasHandler);

        this.canvasControlRegistrations = new HashMap<>();
        this.canvasControlRegistrations.putAll(getCanvasControlRegistrations());
        this.canvasControlRegistrations.put(mediatorsControl, MediatorsControl.class);
        this.canvasControlRegistrations.put(expressionGridCache, ExpressionGridCache.class);
        this.canvasControlRegistrations.put(gridLayerControl, DMNGridLayerControl.class);
        this.canvasControlRegistrations.put(gridPanelControl, DMNGridPanelControl.class);
        this.canvasControlRegistrations.put(cellEditorControl, CellEditorControl.class);
        this.canvasControlRegistrations.put(mousePanMediatorControl, MousePanMediatorControl.class);
        this.canvasControlRegistrations.put(expressionEditorControl, ExpressionEditorControl.class);

        when(gridLayerControl.getGridLayer()).thenReturn(gridLayer);
        when(gridPanelControl.getGridPanel()).thenReturn(gridPanel);
        when(cellEditorControl.getCellEditorControls()).thenReturn(cellEditorControls);
        when(mousePanMediatorControl.getMousePanMediator()).thenReturn(mousePanMediator);
        when(expressionEditorControl.getExpressionEditor()).thenReturn(expressionEditor);

        final CanvasControl[] canvasControls = new CanvasControl[0];
        this.canvasControlInstances = spy(new ManagedInstanceStub<>(canvasControlRegistrations.keySet().toArray(canvasControls)));
        this.canvasControlRegistrations.entrySet().forEach(e -> when(canvasControlInstances.select(eq(e.getValue()), Mockito.<Annotation>anyVararg())).thenReturn(new ManagedInstanceStub<>(e.getKey())));

        this.canvasHandlerControlRegistrations = new HashMap<>();
        this.canvasHandlerControlRegistrations.putAll(getCanvasHandlerControlRegistrations());
        this.canvasHandlerControlRegistrations.put(selectionControl, SelectionControl.class);

        final CanvasControl[] canvasHandlerControls = new CanvasControl[0];
        this.canvasHandlerControlInstances = spy(new ManagedInstanceStub<>(canvasHandlerControlRegistrations.keySet().toArray(canvasHandlerControls)));
        this.canvasHandlerControlRegistrations.entrySet().forEach(e -> when(canvasHandlerControlInstances.select(eq(e.getValue()), Mockito.<Annotation>anyVararg())).thenReturn(new ManagedInstanceStub<>(e.getKey())));

        this.managedSession = spy(new ManagedSession(definitionUtils,
                                                     sessionLoader,
                                                     canvasInstances,
                                                     canvasHandlerInstances,
                                                     canvasControlInstances,
                                                     canvasHandlerControlInstances));

        this.session = getSession();

        doAnswer(i -> {
            final ParameterizedCommand<StunnerPreferences> callback = (ParameterizedCommand) i.getArguments()[1];
            callback.execute(stunnerPreferences);
            return null;
        }).when(sessionLoader).load(eq(metadata), any(ParameterizedCommand.class), any(ParameterizedCommand.class));
    }

    protected abstract S getSession();

    protected abstract Map<CanvasControl, Class> getCanvasControlRegistrations();

    protected abstract Map<CanvasControl, Class> getCanvasHandlerControlRegistrations();

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        session.init(metadata, callback);

        canvasControlRegistrations.values().forEach(c -> verify(managedSession).registerCanvasControl(eq(c)));
        canvasHandlerControlRegistrations.values().forEach(c -> verify(managedSession).registerCanvasHandlerControl(eq(c), Mockito.<Class>any()));
        assertInitQualifiers();

        verify(managedSession).init(eq(metadata), eq(callback));
    }

    protected void assertInitQualifiers() {
        verify(managedSession).registerCanvasHandlerControl(eq(SelectionControl.class), eq(MultipleSelection.class));
    }

    @Test
    public void testDestroy() {
        //Session must first have been initialised
        session.init(metadata, callback);

        session.destroy();

        canvasControlRegistrations.keySet().forEach(r -> verify(r).destroy());
        canvasHandlerControlRegistrations.keySet().forEach(r -> verify(r).destroy());
    }

    @Test
    public void testGetExpressionGridCache() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(expressionGridCache,
                     ((DMNSession) session).getExpressionGridCache());
    }

    @Test
    public void testGetGridPanel() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(gridPanel,
                     ((DMNSession) session).getGridPanel());
    }

    @Test
    public void testGetGridLayer() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(gridLayer,
                     ((DMNSession) session).getGridLayer());
    }

    @Test
    public void testGetCellEditorControls() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(cellEditorControls,
                     ((DMNSession) session).getCellEditorControls());
    }

    @Test
    public void testGetMousePanMediator() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(mousePanMediator,
                     ((DMNSession) session).getMousePanMediator());
    }

    @Test
    public void testGetExpressionEditor() {
        //Session must first have been initialised
        session.init(metadata, callback);

        assertEquals(expressionEditor,
                     ((DMNSession) session).getExpressionEditor());
    }
}

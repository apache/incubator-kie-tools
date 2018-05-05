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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.EmptyRuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SessionPreviewImplTest extends AbstractCanvasHandlerViewerTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";
    private static final RuleSet EMPTY_RULESET = new EmptyRuleSet();

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> canvasCommandManagers;
    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private ManagedInstance<CanvasCommandFactory> canvasCommandFactories;

    private ManagedInstance<AbstractCanvas> canvases;

    @Mock
    private ManagedInstance<BaseCanvasHandler> canvasHandlerFactories;

    @Mock
    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    private ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControls;

    @Mock
    private WidgetWrapperView view;

    @Mock
    private EditorSession session;

    @Mock
    private SessionViewer.SessionViewerCallback<Diagram> callback;

    @Mock
    private ZoomControl<AbstractCanvas> zoomControl;
    private ManagedInstance<ZoomControl<AbstractCanvas>> zoomControls;

    @Mock
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;

    @Mock
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;

    @Mock
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;

    @Mock
    private Metadata metaData;

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance customCanvasCommandFactoryInstance;

    @Mock
    private ManagedInstance defaultCanvasCommandFactoryInstance;

    @Mock
    private CanvasCommandFactory customCanvasCommandFactoryImplementation;

    @Mock
    private CanvasCommandFactory defaultCanvasCommandFactoryImplementation;

    @Mock
    private ManagedInstance customCanvasHandlerInstance;

    @Mock
    private ManagedInstance defaultCanvasHandlerInstance;

    @Mock
    private BaseCanvasHandler customCanvasHandlerImplementation;

    @Mock
    private BaseCanvasHandler defaultCanvasHandlerImplementation;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetRuleAdapter ruleAdapter;

    @Mock
    private TypeDefinitionSetRegistry definitionSetRegistry;

    @Mock
    private Object defSet;

    private SessionPreviewImpl preview;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        zoomControls = new ManagedInstanceStub<>(zoomControl);
        selectionControls = new ManagedInstanceStub<>(selectionControl);
        canvasCommandManagers = new ManagedInstanceStub<>(canvasCommandManager);
        canvases = new ManagedInstanceStub<>(canvas);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getCanvas()).thenReturn(canvas);
        when(session.getZoomControl()).thenReturn(zoomControl);
        when(session.getConnectionAcceptorControl()).thenReturn(connectionAcceptorControl);
        when(session.getContainmentAcceptorControl()).thenReturn(containmentAcceptorControl);
        when(session.getDockingAcceptorControl()).thenReturn(dockingAcceptorControl);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forRules()).thenReturn(ruleAdapter);
        when(ruleAdapter.getRuleSet(eq(defSet))).thenReturn(EMPTY_RULESET);
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(definitionSetRegistry.getDefinitionSetById(eq(DEFINITION_SET_ID))).thenReturn(defSet);
        when(diagram.getMetadata()).thenReturn(metaData);
        when(metaData.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);

        when(customCanvasCommandFactoryInstance.get()).thenReturn(customCanvasCommandFactoryImplementation);
        when(defaultCanvasCommandFactoryInstance.get()).thenReturn(defaultCanvasCommandFactoryImplementation);
        when(canvasCommandFactories.select(eq(qualifier))).thenReturn(customCanvasCommandFactoryInstance);
        when(canvasCommandFactories.select(eq(DefinitionManager.DEFAULT_QUALIFIER))).thenReturn(defaultCanvasCommandFactoryInstance);
        when(customCanvasCommandFactoryImplementation.draw()).thenReturn(mock(CanvasCommand.class));
        when(defaultCanvasCommandFactoryImplementation.draw()).thenReturn(mock(CanvasCommand.class));

        when(customCanvasHandlerInstance.get()).thenReturn(customCanvasHandlerImplementation);
        when(defaultCanvasHandlerInstance.get()).thenReturn(defaultCanvasHandlerImplementation);
        when(canvasHandlerFactories.select(eq(qualifier))).thenReturn(customCanvasHandlerInstance);
        when(canvasHandlerFactories.select(eq(DefinitionManager.DEFAULT_QUALIFIER))).thenReturn(defaultCanvasHandlerInstance);

        this.preview = new SessionPreviewImpl(definitionUtils,
                                              graphUtils,
                                              shapeManager,
                                              textPropertyProviderFactory,
                                              canvases,
                                              canvasHandlerFactories,
                                              zoomControls,
                                              selectionControls,
                                              canvasCommandFactories,
                                              canvasCommandManagers,
                                              view);
    }

    @Test
    public void checkGetCanvasHandlerWhenSatisfied() {
        checkCanvasHandler(false,
                           (c) -> {
                               assertTrue(c instanceof SessionPreviewCanvasHandlerProxy);
                               assertEquals(((SessionPreviewCanvasHandlerProxy) c).getWrapped(),
                                            customCanvasHandlerImplementation);
                           });
    }

    @Test
    public void checkGetCanvasHandlerWhenUnsatisfied() {
        checkCanvasHandler(true,
                           (c) -> {
                               assertTrue(c instanceof SessionPreviewCanvasHandlerProxy);
                               assertEquals(((SessionPreviewCanvasHandlerProxy) c).getWrapped(),
                                            defaultCanvasHandlerImplementation);
                           });
    }

    private void checkCanvasHandler(final boolean isQualifierUnsatisfied,
                                    final Consumer<AbstractCanvasHandler> assertion) {
        when(customCanvasHandlerInstance.isUnsatisfied()).thenReturn(isQualifierUnsatisfied);

        preview.open(session,
                     callback);

        final AbstractCanvasHandler handler = preview.getCanvasHandler();
        assertion.accept(handler);
    }

    @Test
    public void checkGetCanvasCommandFactoryWhenSatisfied() {
        checkCanvasFactory(false,
                           (c) -> assertEquals(customCanvasCommandFactoryImplementation,
                                               c));
    }

    @Test
    public void checkGetCanvasCommandFactoryWhenUnsatisfied() {
        checkCanvasFactory(true,
                           (c) -> assertEquals(defaultCanvasCommandFactoryImplementation,
                                               c));
    }

    private void checkCanvasFactory(final boolean isQualifierUnsatisfied,
                                    final Consumer<CanvasCommandFactory> assertion) {
        when(customCanvasCommandFactoryInstance.isUnsatisfied()).thenReturn(isQualifierUnsatisfied);

        preview.open(session,
                     callback);

        final CanvasCommandFactory factory = preview.getCommandFactory();
        assertion.accept(factory);
    }
}

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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.safehtml.shared.SafeUri;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementDiagramShapeView;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementStageShapeView;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.BasicConnectorShape;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeFactoryTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private ShapeViewFactory basicViewFactory;

    @Mock
    private CaseManagementCanvasHandler canvasHandler;

    @Mock
    private Glyph glyph;

    @Mock
    private AbstractConnectorView connectorShapeView;

    @Mock
    private SyncBeanManager beanManager;

    private SVGPrimitiveShape stageShape;
    private SVGPrimitiveShape taskShape;
    private SVGPrimitiveShape subprocessShape;
    private SVGPrimitiveShape subcaseShape;
    private SVGPrimitiveShape rectangleShape;

    private Consumer<Shape> stageAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof CaseManagementShape);
        assertTrue(shape.getShapeView() instanceof CaseManagementStageShapeView);
        assertSame(((CaseManagementShapeView) shape.getShapeView()).getPrimitive(),
                   stageShape);
    };

    private Consumer<Shape> taskAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof CaseManagementShape);
        assertTrue(shape.getShapeView() instanceof CaseManagementShapeView);
        assertSame(((CaseManagementShapeView) shape.getShapeView()).getPrimitive(),
                   taskShape);
    };

    private Consumer<Shape> caseReusableSubprocessAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof CaseManagementShape);
        assertTrue(shape.getShapeView() instanceof CaseManagementShapeView);
        assertSame(((CaseManagementShapeView) shape.getShapeView()).getPrimitive(),
                   subcaseShape);
    };

    private Consumer<Shape> processReusableSubprocessAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof CaseManagementShape);
        assertTrue(shape.getShapeView() instanceof CaseManagementShapeView);
        assertSame(((CaseManagementShapeView) shape.getShapeView()).getPrimitive(),
                   subprocessShape);
    };

    private Consumer<Shape> connectorAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof BasicConnectorShape);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof SequenceFlowConnectorDef);
    };

    private CaseManagementShapeFactory factory;
    private DelegateShapeFactory delegate;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final PictureShapeView pictureShapeView = new PictureShapeView(new MultiPath().rect(0,
                                                                                            0,
                                                                                            10,
                                                                                            10));

        this.stageShape = new SVGPrimitiveShape(new Rectangle(0.0d, 0.0d));
        this.taskShape = new SVGPrimitiveShape(new Rectangle(0.0d, 0.0d));
        this.subprocessShape = new SVGPrimitiveShape(new Rectangle(0.0d, 0.0d));
        this.subcaseShape = new SVGPrimitiveShape(new Rectangle(0.0d, 0.0d));
        this.rectangleShape = new SVGPrimitiveShape(new Rectangle(0.0d, 0.0d));

        final SyncBeanDef beanDef = mock(SyncBeanDef.class);
        when(beanDef.getInstance()).thenReturn(new CaseManagementSVGViewFactory() {

            @Override
            public SVGShapeViewResource stage() {
                return new SVGShapeViewResource(arg ->
                                                        new CaseManagementStageShapeView("stage",
                                                                                         stageShape,
                                                                                         0.0d,
                                                                                         0.0d,
                                                                                         false));
            }

            @Override
            public SVGShapeViewResource task() {
                return new SVGShapeViewResource(arg ->
                                                        new CaseManagementShapeView("task",
                                                                                    taskShape,
                                                                                    0.0d,
                                                                                    0.0d,
                                                                                    false));
            }

            @Override
            public SVGShapeViewResource subprocess() {
                return new SVGShapeViewResource(arg ->
                                                        new CaseManagementShapeView("subprocess",
                                                                                    subprocessShape,
                                                                                    0.0d,
                                                                                    0.0d,
                                                                                    false));
            }

            @Override
            public SVGShapeViewResource subcase() {
                return new SVGShapeViewResource(arg ->
                                                        new CaseManagementShapeView("subcase",
                                                                                    subcaseShape,
                                                                                    0.0d,
                                                                                    0.0d,
                                                                                    false));
            }

            @Override
            public SVGShapeViewResource rectangle() {
                return new SVGShapeViewResource(arg ->
                                                        new CaseManagementDiagramShapeView("rectangle",
                                                                                           rectangleShape,
                                                                                           0.0d,
                                                                                           0.0d,
                                                                                           false));
            }
        });

        when(beanManager.lookupBean(eq(CaseManagementSVGViewFactory.class))).thenReturn(beanDef);

        final ShapeDefFunctionalFactory functionalFactory = new ShapeDefFunctionalFactory();
        final SVGShapeFactory cmShapeViewFactory = new SVGShapeFactory(beanManager, functionalFactory);
        cmShapeViewFactory.init();

        final BasicShapesFactory basicShapesFactory = new BasicShapesFactory(new ShapeDefFunctionalFactory<>(),
                                                                             basicViewFactory);
        basicShapesFactory.init();
        final CaseManagementShapeDefFactory cmShapeDefFactory = new CaseManagementShapeDefFactory(cmShapeViewFactory,
                                                                                                  new CaseManagementShapeDefFunctionalFactory<>());
        cmShapeDefFactory.init();
        this.delegate = spy(new DelegateShapeFactory());
        doReturn(glyph).when(delegate).getGlyph(anyString());
        this.factory = new CaseManagementShapeFactory(basicShapesFactory,
                                                      cmShapeDefFactory,
                                                      delegate);
        this.factory.init();

        when(basicViewFactory.pictureFromUri(any(SafeUri.class),
                                             anyDouble(),
                                             anyDouble())).thenReturn(pictureShapeView);
        when(basicViewFactory.connector(anyDouble(),
                                        anyDouble(),
                                        anyDouble(),
                                        anyDouble())).thenReturn(connectorShapeView);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
    }

    @Test
    public void checkCMDiagram() {
        assertShapeConstruction(new CaseManagementDiagram(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof CaseManagementShape);
                                    assertTrue(shape.getShapeView() instanceof CaseManagementDiagramShapeView);
                                    assertSame(((CaseManagementShapeView) shape.getShapeView()).getPrimitive(),
                                               rectangleShape);
                                });
        assertShapeGlyph(new CaseManagementDiagram());
    }

    @Test
    public void checkLane() {
        assertShapeConstructionNotSupported(new Lane(),
                                            null);
        assertShapeGlyph(new Lane());
    }

    @Test
    public void checkNoneTask() {
        assertShapeConstructionNotSupported(new NoneTask(),
                                            null);
        assertShapeGlyph(new NoneTask());
    }

    @Test
    public void checkUserTask() {
        assertShapeConstruction(new UserTask(),
                                taskAssertions);
        assertShapeGlyph(new UserTask());
    }

    @Test
    public void checkBusinessRuleTask() {
        assertShapeConstructionNotSupported(new BusinessRuleTask(),
                                            null);
        assertShapeGlyph(new BusinessRuleTask());
    }

    @Test
    public void checkStartNoneEvent() {
        assertShapeConstructionNotSupported(new StartNoneEvent(),
                                            null);
        assertShapeGlyph(new StartNoneEvent());
    }

    @Test
    public void checkEndNoneEvent() {
        assertShapeConstructionNotSupported(new EndNoneEvent(),
                                            null);
        assertShapeGlyph(new EndNoneEvent());
    }

    @Test
    public void checkEndTerminateEvent() {
        assertShapeConstructionNotSupported(new EndTerminateEvent(),
                                            null);
        assertShapeGlyph(new EndTerminateEvent());
    }

    @Test
    public void checkParallelGateway() {
        assertShapeConstructionNotSupported(new ParallelGateway(),
                                            null);
        assertShapeGlyph(new ParallelGateway());
    }

    @Test
    public void checkExclusiveDatabasedGateway() {
        assertShapeConstructionNotSupported(new ExclusiveGateway(),
                                            null);
        assertShapeGlyph(new ExclusiveGateway());
    }

    @Test
    public void checkAdHocSubprocess() {
        assertShapeConstruction(new AdHocSubprocess(),
                                stageAssertions);
        assertShapeGlyph(new AdHocSubprocess());
    }

    @Test
    public void checkCaseReusableSubprocess() {
        assertShapeConstruction(new CaseReusableSubprocess(),
                                caseReusableSubprocessAssertions);
        assertShapeGlyph(new CaseReusableSubprocess());
    }

    @Test
    public void checkProcessReusableSubprocess() {
        assertShapeConstruction(new ProcessReusableSubprocess(),
                                processReusableSubprocessAssertions);
        assertShapeGlyph(new ProcessReusableSubprocess());
    }

    @Test
    public void checkSequenceFlow() {
        assertShapeConstruction(new SequenceFlow(),
                                connectorAssertions);
        assertShapeGlyph(new SequenceFlow());
    }

    @Test
    public void checkServiceTask() {
        assertShapeConstructionNotSupported(new ServiceTask(),
                                            connectorAssertions);
        assertShapeGlyph(new ServiceTask());
    }

    @Test
    public void checkStartSignalEvent() {
        assertShapeConstructionNotSupported(new StartSignalEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartSignalEvent());
    }

    @Test
    public void checkStartTimerEvent() {
        assertShapeConstructionNotSupported(new StartTimerEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartTimerEvent());
    }

    @Test
    public void checkStartMessageEvent() {
        assertShapeConstructionNotSupported(new StartMessageEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartMessageEvent());
    }

    @Test
    public void checkStartErrorEvent() {
        assertShapeConstructionNotSupported(new StartErrorEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartErrorEvent());
    }

    @Test
    public void checkStartConditionalEvent() {
        assertShapeConstructionNotSupported(new StartConditionalEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartConditionalEvent());
    }

    @Test
    public void checkStartEscalationEvent() {
        assertShapeConstructionNotSupported(new StartEscalationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartEscalationEvent());
    }

    @Test
    public void checkStartCompensationEvent() {
        assertShapeConstructionNotSupported(new StartCompensationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new StartCompensationEvent());
    }

    @Test
    public void checkInclusiveGateway() {
        assertShapeConstructionNotSupported(new InclusiveGateway(),
                                            connectorAssertions);
        assertShapeGlyph(new InclusiveGateway());
    }

    @Test
    public void checkEmbeddedSubprocess() {
        assertShapeConstructionNotSupported(new EmbeddedSubprocess(),
                                            connectorAssertions);
        assertShapeGlyph(new EmbeddedSubprocess());
    }

    @Test
    public void checkEventSubprocess() {
        assertShapeConstructionNotSupported(new EventSubprocess(),
                                            connectorAssertions);
        assertShapeGlyph(new EventSubprocess());
    }

    @Test
    public void checkMultipleInstanceSubprocess() {
        assertShapeConstructionNotSupported(new MultipleInstanceSubprocess(),
                                            connectorAssertions);
        assertShapeGlyph(new MultipleInstanceSubprocess());
    }

    @Test
    public void checkEndSignalEvent() {
        assertShapeConstructionNotSupported(new EndSignalEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new EndSignalEvent());
    }

    @Test
    public void checkEndMessageEvent() {
        assertShapeConstructionNotSupported(new EndMessageEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new EndMessageEvent());
    }

    @Test
    public void checkEndErrorEvent() {
        assertShapeConstructionNotSupported(new EndErrorEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new EndErrorEvent());
    }

    @Test
    public void checkEndEscalationEvent() {
        assertShapeConstructionNotSupported(new EndEscalationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new EndEscalationEvent());
    }

    @Test
    public void checkEndCompensationEvent() {
        assertShapeConstructionNotSupported(new EndCompensationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new EndCompensationEvent());
    }

    @Test
    public void checkIntermediateTimerEvent() {
        assertShapeConstructionNotSupported(new IntermediateTimerEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateTimerEvent());
    }

    @Test
    public void checkIntermediateConditionalEvent() {
        assertShapeConstructionNotSupported(new IntermediateConditionalEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateConditionalEvent());
    }

    @Test
    public void checkIntermediateSignalEventCatching() {
        assertShapeConstructionNotSupported(new IntermediateSignalEventCatching(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateSignalEventCatching());
    }

    @Test
    public void checkIntermediateErrorEventCatching() {
        assertShapeConstructionNotSupported(new IntermediateErrorEventCatching(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateErrorEventCatching());
    }

    @Test
    public void checkIntermediateMessageEventCatching() {
        assertShapeConstructionNotSupported(new IntermediateMessageEventCatching(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateMessageEventCatching());
    }

    @Test
    public void checkIntermediateEscalationEvent() {
        assertShapeConstructionNotSupported(new IntermediateEscalationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateEscalationEvent());
    }

    @Test
    public void checkIntermediateCompensationEvent() {
        assertShapeConstructionNotSupported(new IntermediateCompensationEvent(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateCompensationEvent());
    }

    @Test
    public void checkIntermediateSignalEventThrowing() {
        assertShapeConstructionNotSupported(new IntermediateSignalEventThrowing(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateSignalEventThrowing());
    }

    @Test
    public void checkIntermediateMessageEventThrowing() {
        assertShapeConstructionNotSupported(new IntermediateMessageEventThrowing(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateMessageEventThrowing());
    }

    @Test
    public void checkIntermediateEscalationEventThrowing() {
        assertShapeConstructionNotSupported(new IntermediateEscalationEventThrowing(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateEscalationEventThrowing());
    }

    @Test
    public void checkIntermediateCompensationEventThrowing() {
        assertShapeConstructionNotSupported(new IntermediateCompensationEventThrowing(),
                                            connectorAssertions);
        assertShapeGlyph(new IntermediateCompensationEventThrowing());
    }

    @Test
    public void checkAssociation() {
        assertShapeConstructionNotSupported(new Association(),
                                            connectorAssertions);
        assertShapeGlyph(new Association());
    }

    @SuppressWarnings("unchecked")
    private void assertShapeConstruction(final BPMNDefinition definition,
                                         final Consumer<Shape> o) {
        when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(definition.getClass().getName()));

        final Shape<? extends ShapeView> shape = factory.newShape(definition);
        assertNotNull(shape);
        assertNotNull(shape.getShapeView());

        o.accept(shape);
    }

    private void assertShapeConstructionNotSupported(final BPMNDefinition definition,
                                                     final Consumer<Shape> o) {
        when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(definition.getClass().getName()));

        final Shape<? extends ShapeView> shape = factory.newShape(definition);
        assertNull(shape);
    }

    @SuppressWarnings("unchecked")
    private void assertShapeGlyph(final BPMNDefinition definition) {
        final String id = BindableAdapterUtils.getDefinitionId(definition.getClass());
        final Glyph glyph = factory.getGlyph(id);
        verify(delegate,
               times(1)).getGlyph(eq(id));
        assertEquals(this.glyph,
                     glyph);
    }
}

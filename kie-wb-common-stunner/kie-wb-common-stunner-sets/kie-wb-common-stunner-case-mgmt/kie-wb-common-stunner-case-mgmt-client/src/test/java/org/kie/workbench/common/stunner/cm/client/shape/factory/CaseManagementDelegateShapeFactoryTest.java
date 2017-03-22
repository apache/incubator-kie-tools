/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.DiagramShape;
import org.kie.workbench.common.stunner.cm.client.shape.NullShape;
import org.kie.workbench.common.stunner.cm.client.shape.StageShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
import org.kie.workbench.common.stunner.cm.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilder;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactoryImpl;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementDelegateShapeFactoryTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private GlyphBuilderFactory glyphBuilderFactory;

    @Mock
    private GlyphBuilder glyphBuilder;

    @Mock
    private ShapeViewFactory shapeViewFactory;

    @Mock
    private CaseManagementCanvasHandler canvasHandler;

    private CaseManagementDelegateShapeFactory factory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final BasicShapesFactory basicShapesFactory = new BasicShapesFactoryImpl(factoryManager,
                                                                                 shapeViewFactory,
                                                                                 definitionManager,
                                                                                 glyphBuilderFactory);
        final CaseManagementShapesFactory caseManagementShapesFactory = new CaseManagementShapesFactoryImpl(factoryManager,
                                                                                                            definitionManager,
                                                                                                            glyphBuilderFactory);
        this.factory = new CaseManagementDelegateShapeFactory(definitionManager,
                                                              basicShapesFactory,
                                                              caseManagementShapesFactory);
        this.factory.init();

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(glyphBuilderFactory.getBuilder(any(GlyphDef.class))).thenReturn(glyphBuilder);
        when(glyphBuilder.glyphDef(any(GlyphDef.class))).thenReturn(glyphBuilder);
        when(glyphBuilder.factory(any(ShapeFactory.class))).thenReturn(glyphBuilder);
        when(glyphBuilder.height(anyDouble())).thenReturn(glyphBuilder);
        when(glyphBuilder.width(anyDouble())).thenReturn(glyphBuilder);
    }

    @Test
    public void checkBPMNDiagram() {
        assertShapeConstruction(new BPMNDiagram.BPMNDiagramBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof DiagramShape);
                                    assertTrue(shape.getShapeView() instanceof DiagramView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementDiagramShapeDef);
                                });
        assertShapeGlyph(new BPMNDiagram.BPMNDiagramBuilder().build());
    }

    @Test
    public void checkLane() {
        assertShapeConstruction(new Lane.LaneBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new Lane.LaneBuilder().build());
    }

    @Test
    public void checkNoneTask() {
        assertShapeConstruction(new NoneTask.NoneTaskBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof ActivityShape);
                                    assertTrue(shape.getShapeView() instanceof ActivityView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementTaskShapeDef);
                                });
        assertShapeGlyph(new NoneTask.NoneTaskBuilder().build());
    }

    @Test
    public void checkUserTask() {
        assertShapeConstruction(new UserTask.UserTaskBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof ActivityShape);
                                    assertTrue(shape.getShapeView() instanceof ActivityView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementTaskShapeDef);
                                });
        assertShapeGlyph(new UserTask.UserTaskBuilder().build());
    }

    @Test
    public void checkBusinessRuleTask() {
        assertShapeConstruction(new BusinessRuleTask.BusinessRuleTaskBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof ActivityShape);
                                    assertTrue(shape.getShapeView() instanceof ActivityView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementTaskShapeDef);
                                });
        assertShapeGlyph(new BusinessRuleTask.BusinessRuleTaskBuilder().build());
    }

    @Test
    public void checkStartNoneEvent() {
        assertShapeConstruction(new StartNoneEvent.StartNoneEventBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new StartNoneEvent.StartNoneEventBuilder().build());
    }

    @Test
    public void checkEndNoneEvent() {
        assertShapeConstruction(new EndNoneEvent.EndNoneEventBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new EndNoneEvent.EndNoneEventBuilder().build());
    }

    @Test
    public void checkEndTerminateEvent() {
        assertShapeConstruction(new EndTerminateEvent.EndTerminateEventBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new EndTerminateEvent.EndTerminateEventBuilder().build());
    }

    @Test
    public void checkParallelGateway() {
        assertShapeConstruction(new ParallelGateway.ParallelGatewayBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new ParallelGateway.ParallelGatewayBuilder().build());
    }

    @Test
    public void checkExclusiveDatabasedGateway() {
        assertShapeConstruction(new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof NullShape);
                                    assertTrue(shape.getShapeView() instanceof NullView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
                                });
        assertShapeGlyph(new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder().build());
    }

    @Test
    public void checkAdHocSubprocess() {
        assertShapeConstruction(new AdHocSubprocess.AdHocSubprocessBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof StageShape);
                                    assertTrue(shape.getShapeView() instanceof StageView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementSubprocessShapeDef);
                                });
        assertShapeGlyph(new AdHocSubprocess.AdHocSubprocessBuilder().build());
    }

    @Test
    public void checkReusableSubprocess() {
        assertShapeConstruction(new ReusableSubprocess.ReusableSubprocessBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof StageShape);
                                    assertTrue(shape.getShapeView() instanceof StageView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementSubprocessShapeDef);
                                });
        assertShapeGlyph(new ReusableSubprocess.ReusableSubprocessBuilder().build());
    }

    @Test
    public void checkSequenceFlow() {
        assertShapeConstruction(new SequenceFlow.SequenceFlowBuilder().build(),
                                (shape) -> {
                                    assertNull(shape.getShapeView());
                                    assertTrue(shape instanceof ConnectorShape);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof SequenceFlowConnectorDef);
                                });
        assertShapeGlyph(new SequenceFlow.SequenceFlowBuilder().build());
    }

    @SuppressWarnings("unchecked")
    private void assertShapeConstruction(final BPMNDefinition definition,
                                         final Consumer<Shape> o) {
        when(definitionAdapter.getId(eq(definition))).thenReturn(definition.getClass().getName());

        final Shape<? extends ShapeView> shape = factory.build(definition,
                                                               canvasHandler);
        assertNotNull(shape);
        assertNotNull(((AbstractElementShape) shape).getShapeDefinition());

        o.accept(shape);
    }

    @SuppressWarnings("unchecked")
    private void assertShapeGlyph(final BPMNDefinition definition) {
        when(glyphBuilder.definitionType(eq(definition.getClass()))).thenReturn(glyphBuilder);
        when(glyphBuilder.build()).thenReturn(mock(Glyph.class));

        factory.glyph(definition.getClass().getName(),
                      20.0,
                      40.0);

        verify(glyphBuilder).width(eq(20.0));
        verify(glyphBuilder).height(eq(40.0));
        verify(glyphBuilder).build();
    }
}

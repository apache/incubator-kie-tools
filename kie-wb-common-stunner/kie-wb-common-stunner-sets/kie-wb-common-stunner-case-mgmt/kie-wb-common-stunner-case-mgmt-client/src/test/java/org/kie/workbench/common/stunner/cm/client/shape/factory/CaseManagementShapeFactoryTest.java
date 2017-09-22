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
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.DiagramShape;
import org.kie.workbench.common.stunner.cm.client.shape.NullShape;
import org.kie.workbench.common.stunner.cm.client.shape.StageShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementReusableSubprocessTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
import org.kie.workbench.common.stunner.cm.client.wires.AbstractCaseManagementShape;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.ConnectorShape;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.client.view.ConnectorView;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
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
    private ConnectorView connectorShapeView;

    private Consumer<Shape> nullAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof NullShape);
        assertTrue(shape.getShapeView() instanceof NullView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
    };

    private Consumer<Shape> stageAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof StageShape);
        assertTrue(shape.getShapeView() instanceof StageView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementSubprocessShapeDef);
        assertShapeSize((StageView) shape.getShapeView());
    };

    private Consumer<Shape> activityAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof ActivityShape);
        assertTrue(shape.getShapeView() instanceof ActivityView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementTaskShapeDef);
        assertShapeSize((ActivityView) shape.getShapeView());
    };

    private Consumer<Shape> reusableSubprocessActivityAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof ActivityShape);
        assertTrue(shape.getShapeView() instanceof ActivityView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementReusableSubprocessTaskShapeDef);
        assertShapeSize((ActivityView) shape.getShapeView());
    };

    private Consumer<Shape> connectorAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof ConnectorShape);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof SequenceFlowConnectorDef);
    };

    private CaseManagementShapeFactory factory;
    private DelegateShapeFactory delegate;
    private CaseManagementShapeViewFactory cmShapeViewFactory;
    private PictureShapeView pictureShapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.pictureShapeView = new PictureShapeView(new MultiPath().rect(0,
                                                                          0,
                                                                          10,
                                                                          10));
        this.cmShapeViewFactory = new CaseManagementShapeViewFactory();
        final BasicShapesFactory basicShapesFactory = new BasicShapesFactory(new ShapeDefFunctionalFactory<>(),
                                                                             basicViewFactory);
        basicShapesFactory.init();
        final CaseManagementShapeDefFactory cmShapeDefFactory = new CaseManagementShapeDefFactory(cmShapeViewFactory,
                                                                                                  basicViewFactory,
                                                                                                  new ShapeDefFunctionalFactory<>());
        cmShapeDefFactory.init();
        this.delegate = spy(new DelegateShapeFactory());
        doReturn(glyph).when(delegate).getGlyph(anyString());
        this.factory = new CaseManagementShapeFactory(cmShapeDefFactory,
                                                      basicShapesFactory,
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
        assertShapeConstruction(new CaseManagementDiagram.CaseManagementDiagramBuilder().build(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof DiagramShape);
                                    assertTrue(shape.getShapeView() instanceof DiagramView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementDiagramShapeDef);
                                });
        assertShapeGlyph(new CaseManagementDiagram.CaseManagementDiagramBuilder().build());
    }

    @Test
    public void checkLane() {
        assertShapeConstruction(new Lane.LaneBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new Lane.LaneBuilder().build());
    }

    @Test
    public void checkNoneTask() {
        assertShapeConstruction(new NoneTask.NoneTaskBuilder().build(),
                                activityAssertions);
        assertShapeGlyph(new NoneTask.NoneTaskBuilder().build());
    }

    @Test
    public void checkUserTask() {
        assertShapeConstruction(new UserTask.UserTaskBuilder().build(),
                                activityAssertions);
        assertShapeGlyph(new UserTask.UserTaskBuilder().build());
    }

    @Test
    public void checkBusinessRuleTask() {
        assertShapeConstruction(new BusinessRuleTask.BusinessRuleTaskBuilder().build(),
                                activityAssertions);
        assertShapeGlyph(new BusinessRuleTask.BusinessRuleTaskBuilder().build());
    }

    @Test
    public void checkStartNoneEvent() {
        assertShapeConstruction(new StartNoneEvent.StartNoneEventBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new StartNoneEvent.StartNoneEventBuilder().build());
    }

    @Test
    public void checkEndNoneEvent() {
        assertShapeConstruction(new EndNoneEvent.EndNoneEventBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new EndNoneEvent.EndNoneEventBuilder().build());
    }

    @Test
    public void checkEndTerminateEvent() {
        assertShapeConstruction(new EndTerminateEvent.EndTerminateEventBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new EndTerminateEvent.EndTerminateEventBuilder().build());
    }

    @Test
    public void checkParallelGateway() {
        assertShapeConstruction(new ParallelGateway.ParallelGatewayBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new ParallelGateway.ParallelGatewayBuilder().build());
    }

    @Test
    public void checkExclusiveDatabasedGateway() {
        assertShapeConstruction(new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder().build(),
                                nullAssertions);
        assertShapeGlyph(new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder().build());
    }

    @Test
    public void checkAdHocSubprocess() {
        assertShapeConstruction(new AdHocSubprocess.AdHocSubprocessBuilder().build(),
                                stageAssertions);
        assertShapeGlyph(new AdHocSubprocess.AdHocSubprocessBuilder().build());
    }

    @Test
    public void checkReusableSubprocess() {
        assertShapeConstruction(new ReusableSubprocess.ReusableSubprocessBuilder().build(),
                                reusableSubprocessActivityAssertions);
        assertShapeGlyph(new ReusableSubprocess.ReusableSubprocessBuilder().build());
    }

    @Test
    public void checkSequenceFlow() {
        assertShapeConstruction(new SequenceFlow.SequenceFlowBuilder().build(),
                                connectorAssertions);
        assertShapeGlyph(new SequenceFlow.SequenceFlowBuilder().build());
    }

    @SuppressWarnings("unchecked")
    private void assertShapeConstruction(final BPMNDefinition definition,
                                         final Consumer<Shape> o) {
        when(definitionAdapter.getId(eq(definition))).thenReturn(definition.getClass().getName());

        final Shape<? extends ShapeView> shape = factory.newShape(definition);
        assertNotNull(shape);
        assertNotNull(((AbstractElementShape) shape).getShapeDefinition());

        o.accept(shape);
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

    private void assertShapeSize(final AbstractCaseManagementShape shapeView) {
        assertEquals(BaseTask.BaseTaskBuilder.WIDTH,
                     shapeView.getWidth(),
                     0.0);
        assertEquals(BaseTask.BaseTaskBuilder.HEIGHT,
                     shapeView.getHeight(),
                     0.0);
    }
}

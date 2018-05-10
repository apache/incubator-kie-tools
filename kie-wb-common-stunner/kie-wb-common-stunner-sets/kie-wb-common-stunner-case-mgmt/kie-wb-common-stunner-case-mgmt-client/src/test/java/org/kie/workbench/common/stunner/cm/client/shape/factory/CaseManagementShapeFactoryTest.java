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
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.CMContainerShape;
import org.kie.workbench.common.stunner.cm.client.shape.NullShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementReusableSubprocessTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
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
import org.kie.workbench.common.stunner.shapes.client.BasicConnectorShape;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
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
    private AbstractConnectorView connectorShapeView;

    private Consumer<Shape> nullAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof NullShape);
        assertTrue(shape.getShapeView() instanceof NullView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof NullShapeDef);
    };

    private Consumer<Shape> stageAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof CMContainerShape);
        assertTrue(shape.getShapeView() instanceof StageView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementSubprocessShapeDef);
    };

    private Consumer<Shape> activityAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof ActivityShape);
        assertTrue(shape.getShapeView() instanceof ActivityView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementTaskShapeDef);
    };

    private Consumer<Shape> reusableSubprocessActivityAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof ActivityShape);
        assertTrue(shape.getShapeView() instanceof ActivityView);
        assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementReusableSubprocessTaskShapeDef);
    };

    private Consumer<Shape> connectorAssertions = (shape) -> {
        assertNotNull(shape.getShapeView());
        assertTrue(shape instanceof BasicConnectorShape);
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
        assertShapeConstruction(new CaseManagementDiagram(),
                                (shape) -> {
                                    assertNotNull(shape.getShapeView());
                                    assertTrue(shape instanceof CMContainerShape);
                                    assertTrue(shape.getShapeView() instanceof DiagramView);
                                    assertTrue(((AbstractElementShape) shape).getShapeDefinition() instanceof CaseManagementDiagramShapeDef);
                                });
        assertShapeGlyph(new CaseManagementDiagram());
    }

    @Test
    public void checkLane() {
        assertShapeConstruction(new Lane(),
                                nullAssertions);
        assertShapeGlyph(new Lane());
    }

    @Test
    public void checkNoneTask() {
        assertShapeConstruction(new NoneTask(),
                                activityAssertions);
        assertShapeGlyph(new NoneTask());
    }

    @Test
    public void checkUserTask() {
        assertShapeConstruction(new UserTask(),
                                activityAssertions);
        assertShapeGlyph(new UserTask());
    }

    @Test
    public void checkBusinessRuleTask() {
        assertShapeConstruction(new BusinessRuleTask(),
                                activityAssertions);
        assertShapeGlyph(new BusinessRuleTask());
    }

    @Test
    public void checkStartNoneEvent() {
        assertShapeConstruction(new StartNoneEvent(),
                                nullAssertions);
        assertShapeGlyph(new StartNoneEvent());
    }

    @Test
    public void checkEndNoneEvent() {
        assertShapeConstruction(new EndNoneEvent(),
                                nullAssertions);
        assertShapeGlyph(new EndNoneEvent());
    }

    @Test
    public void checkEndTerminateEvent() {
        assertShapeConstruction(new EndTerminateEvent(),
                                nullAssertions);
        assertShapeGlyph(new EndTerminateEvent());
    }

    @Test
    public void checkParallelGateway() {
        assertShapeConstruction(new ParallelGateway(),
                                nullAssertions);
        assertShapeGlyph(new ParallelGateway());
    }

    @Test
    public void checkExclusiveDatabasedGateway() {
        assertShapeConstruction(new ExclusiveGateway(),
                                nullAssertions);
        assertShapeGlyph(new ExclusiveGateway());
    }

    @Test
    public void checkAdHocSubprocess() {
        assertShapeConstruction(new AdHocSubprocess(),
                                stageAssertions);
        assertShapeGlyph(new AdHocSubprocess());
    }

    @Test
    public void checkReusableSubprocess() {
        assertShapeConstruction(new ReusableSubprocess(),
                                reusableSubprocessActivityAssertions);
        assertShapeGlyph(new ReusableSubprocess());
    }

    @Test
    public void checkSequenceFlow() {
        assertShapeConstruction(new SequenceFlow(),
                                connectorAssertions);
        assertShapeGlyph(new SequenceFlow());
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
}

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

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
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
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeDefFactoryTest {

    @Mock
    private CaseManagementShapeViewFactory cmShapeViewFactory;

    @Mock
    private ShapeViewFactory basicShapeViewFactory;

    @Mock
    private NullView nullView;

    @Mock
    private DiagramView diagramView;

    @Mock
    private StageView stageView;

    @Mock
    private ActivityView activityView;

    @Mock
    private AbstractConnectorView connectorShapeView;

    private CaseManagementShapeDefFactory tested;
    private PictureShapeView pictureShapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.pictureShapeView = new PictureShapeView(new MultiPath().rect(0,
                                                                          0,
                                                                          10,
                                                                          10));
        when(stageView.setWidth(anyDouble())).thenReturn(stageView);
        when(stageView.setHeight(anyDouble())).thenReturn(stageView);
        when(basicShapeViewFactory.pictureFromUri(any(SafeUri.class),
                                                  anyDouble(),
                                                  anyDouble())).thenReturn(pictureShapeView);
        when(basicShapeViewFactory.connector(anyDouble(),
                                             anyDouble(),
                                             anyDouble(),
                                             anyDouble())).thenReturn(connectorShapeView);
        when(cmShapeViewFactory.newNullView()).thenReturn(nullView);
        when(cmShapeViewFactory.newStageView(anyDouble(),
                                             anyDouble(),
                                             anyDouble())).thenReturn(stageView);
        when(cmShapeViewFactory.newActivityView(anyDouble(),
                                                anyDouble())).thenReturn(activityView);
        when(cmShapeViewFactory.newDiagramView(anyDouble(),
                                               anyDouble())).thenReturn(diagramView);
        this.tested = new CaseManagementShapeDefFactory(cmShapeViewFactory,
                                                        basicShapeViewFactory,
                                                        new ShapeDefFunctionalFactory<>());
        this.tested.init();
    }

    @Test
    public void testBuilders() {
        final Shape nullShape = tested.newShape(mock(BPMNDefinition.class),
                                                new NullShapeDef());
        assertNotNull(nullShape);
        assertTrue(nullShape instanceof NullShape);

        final Shape diagramShape = tested.newShape(new CaseManagementDiagram(),
                                                   new CaseManagementDiagramShapeDef());
        assertNotNull(diagramShape);
        assertTrue(diagramShape instanceof CMContainerShape);

        final Shape subprocessShape = tested.newShape(new AdHocSubprocess(),
                                                      new CaseManagementSubprocessShapeDef());
        assertNotNull(subprocessShape);
        assertTrue(subprocessShape instanceof CMContainerShape);

        final Shape activityShape = tested.newShape(new UserTask(),
                                                    new CaseManagementTaskShapeDef());
        assertNotNull(activityShape);
        assertTrue(activityShape instanceof ActivityShape);

        final Shape activityShape2 = tested.newShape(new ReusableSubprocess(),
                                                     new CaseManagementReusableSubprocessTaskShapeDef());
        assertNotNull(activityShape2);
        assertTrue(activityShape2 instanceof ActivityShape);
    }
}

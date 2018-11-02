/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.associations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.AssociationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util.PropertyWriterUtilsTest.assertWaypoint;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util.PropertyWriterUtilsTest.mockConnector;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util.PropertyWriterUtilsTest.mockShape;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssociationPropertyWriterTest {

    private static final String SOURCE_SHAPE_ID = "SOURCE_SHAPE_ID";
    private static final String TARGET_SHAPE_ID = "TARGET_SHAPE_ID";

    @Mock
    private Association association;

    @Mock
    private VariableScope variableScope;

    @Mock
    private BasePropertyWriter sourceWriter;

    @Mock
    private BaseElement sourceElement;

    @Mock
    private BasePropertyWriter targetWriter;

    @Mock
    private BaseElement targetElement;

    private AssociationPropertyWriter associationWriter;

    @Before
    public void setUp() {
        when(sourceWriter.getElement()).thenReturn(sourceElement);
        when(targetWriter.getElement()).thenReturn(targetElement);
        associationWriter = new AssociationPropertyWriter(association, variableScope);
    }

    @Test
    public void testSetConnection() {
        BPMNShape sourceShape = mockShape(SOURCE_SHAPE_ID, 1, 1, 4, 4);
        BPMNShape targetShape = mockShape(TARGET_SHAPE_ID, 10, 10, 4, 4);
        when(sourceWriter.getShape()).thenReturn(sourceShape);
        when(targetWriter.getShape()).thenReturn(targetShape);

        List<ControlPoint> controlPoints = new ArrayList<>();
        controlPoints.add(new ControlPoint(Point2D.create(3,3),0));
        controlPoints.add(new ControlPoint(Point2D.create(4,4),0));
        controlPoints.add(new ControlPoint(Point2D.create(5,5),0));

        ViewConnector <? extends BPMNViewDefinition> connector = mockConnector(1, 1, 10, 10, controlPoints);

        associationWriter.setSource(sourceWriter);
        associationWriter.setTarget(targetWriter);
        associationWriter.setConnection(connector);

        assertNotNull(associationWriter.getEdge());

        assertEquals(association, associationWriter.getEdge().getBpmnElement());
        assertEquals("edge_SOURCE_SHAPE_ID_to_TARGET_SHAPE_ID", associationWriter.getEdge().getId());
        assertWaypoint(2, 2, 0, associationWriter.getEdge().getWaypoint());
        assertWaypoint(3, 3, 1, associationWriter.getEdge().getWaypoint());
        assertWaypoint(4, 4, 2, associationWriter.getEdge().getWaypoint());
        assertWaypoint(5, 5, 3, associationWriter.getEdge().getWaypoint());
        assertWaypoint(20, 20, 4, associationWriter.getEdge().getWaypoint());
    }

    @Test
    public void testSetSource() {
        associationWriter.setSource(sourceWriter);
        verify(association).setSourceRef(sourceElement);
        verify(sourceWriter).setTarget(associationWriter);
    }

    @Test
    public void testSetTarget() {
        associationWriter.setTarget(targetWriter);
        verify(association).setTargetRef(targetElement);
        verify(targetWriter).setSource(associationWriter);
    }

    public void testSetOneDirectionAssociation() {
        verify(association).setAssociationDirection(AssociationDirection.ONE);
    }
}

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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.util.PropertyReaderUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PropertyReaderUtils.class)
public class AssociationPropertyReaderTest {

    private static String SOURCE_ID = "SOURCE_ID";

    private static String TARGET_ID = "TARGET_ID";

    private static String ASSOCIATION_ID = "ASSOCIATION_ID";

    private static double X = 1;

    private static double Y = 2;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private Association association;

    @Mock
    private BaseElement sourceRef;

    @Mock
    private BaseElement targetRef;

    @Mock
    private BPMNPlane bpmnLane;

    private Point2D position;

    @Mock
    private AssociationPropertyReader propertyReader;

    @Before
    public void setUp() {
        position = Point2D.create(X, Y);
        when(association.getId()).thenReturn(ASSOCIATION_ID);
        when(sourceRef.getId()).thenReturn(SOURCE_ID);
        when(targetRef.getId()).thenReturn(TARGET_ID);
        when(association.getSourceRef()).thenReturn(sourceRef);
        when(association.getTargetRef()).thenReturn(targetRef);

        propertyReader = new AssociationPropertyReader(association, bpmnLane, definitionResolver);
    }

    @Test
    public void testGetSourceId() {
        assertEquals(SOURCE_ID, propertyReader.getSourceId());
    }

    @Test
    public void testGetTargetId() {
        assertEquals(TARGET_ID, propertyReader.getTargetId());
    }

    @Test
    public void testGetSourceConnection() {
        mockStatic(PropertyReaderUtils.class);
        PowerMockito.when(PropertyReaderUtils.getSourcePosition(definitionResolver, ASSOCIATION_ID, SOURCE_ID)).thenReturn(position);
        boolean arbitraryBoolean = true;
        PowerMockito.when(PropertyReaderUtils.isAutoConnectionSource(association)).thenReturn(arbitraryBoolean);

        Connection result = propertyReader.getSourceConnection();
        assertEquals(X, result.getLocation().getX(), 0);
        assertEquals(Y, result.getLocation().getY(), 0);
    }

    @Test
    public void testGetTargetConnection() {
        mockStatic(PropertyReaderUtils.class);
        PowerMockito.when(PropertyReaderUtils.getTargetPosition(definitionResolver, ASSOCIATION_ID, TARGET_ID)).thenReturn(position);
        boolean arbitraryBoolean = true;
        PowerMockito.when(PropertyReaderUtils.isAutoConnectionSource(association)).thenReturn(arbitraryBoolean);

        Connection result = propertyReader.getTargetConnection();
        assertEquals(X, result.getLocation().getX(), 0);
        assertEquals(Y, result.getLocation().getY(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetControlPoints() {
        List<Point2D> controlPoints = mock(List.class);
        mockStatic(PropertyReaderUtils.class);
        PowerMockito.when(PropertyReaderUtils.getControlPoints(definitionResolver, ASSOCIATION_ID)).thenReturn(controlPoints);
        assertEquals(controlPoints, propertyReader.getControlPoints());
    }
}

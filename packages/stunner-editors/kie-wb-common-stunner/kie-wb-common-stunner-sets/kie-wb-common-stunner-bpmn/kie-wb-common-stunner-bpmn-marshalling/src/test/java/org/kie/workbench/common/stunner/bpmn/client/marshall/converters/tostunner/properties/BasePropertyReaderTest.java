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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasePropertyReaderTest {

    private static final double RESOLUTION_FACTOR = 0.65d;

    @Mock
    private BaseElement element;

    @Mock
    private BPMNShape shape;

    @Mock
    private BPMNDiagram diagram;

    private BasePropertyReader tested;

    @Before
    public void setup() {
        FeatureMap featureMap = mock(FeatureMap.class);
        org.eclipse.dd.dc.Bounds shapeBounds = mock(org.eclipse.dd.dc.Bounds.class);
        when(shapeBounds.getX()).thenReturn(1.1f);
        when(shapeBounds.getY()).thenReturn(2.2f);
        when(shapeBounds.getWidth()).thenReturn(100f);
        when(shapeBounds.getHeight()).thenReturn(545.34f);
        when(shape.getBounds()).thenReturn(shapeBounds);
        tested = new BasePropertyReader(element, diagram, shape, RESOLUTION_FACTOR);
    }

    @Test
    public void testBounds() {
        Bounds bounds = tested.getBounds();
        assertTrue(bounds.hasLowerRight());
        assertTrue(bounds.hasUpperLeft());
        assertEquals(0.7150000154972077d, bounds.getUpperLeft().getX(), 0d);
        assertEquals(1.4300000309944154d, bounds.getUpperLeft().getY(), 0d);
        assertEquals(65.71500001549721d, bounds.getLowerRight().getX(), 0d);
        assertEquals(355.9010174870491d, bounds.getLowerRight().getY(), 0d);
    }

    @Test
    public void testGetCircleDimensionSet() {
        CircleDimensionSet circleDimensionSet = tested.getCircleDimensionSet();
        assertEquals(32.5d, circleDimensionSet.getRadius().getValue(), 0d);
    }

    @Test
    public void testGetRectangleDimensionsSet() {
        RectangleDimensionsSet rectangleDimensionsSet = tested.getRectangleDimensionsSet();
        assertEquals(65.0d, rectangleDimensionsSet.getWidth().getValue(), 0d);
        assertEquals(354.4710174560547d, rectangleDimensionsSet.getHeight().getValue(), 0d);
    }

    @Test
    public void testIsExpandedTrue() {
        when(shape.isIsExpanded()).thenReturn(true);
        assertTrue(tested.isExpanded());
    }

    @Test
    public void testIsExpandedFalse() {
        when(shape.isIsExpanded()).thenReturn(false);
        assertFalse(shape.isIsExpanded());
    }

    @Test
    public void testGetElement() {
        assertEquals(element, tested.getElement());
    }

    @Test
    public void testGetShape() {
        assertEquals(shape, tested.getShape());
    }
}

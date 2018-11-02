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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.util.PropertyReaderUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyReaderUtilsTest {

    private static final String EDGE_ID = "EDGE_ID";

    private static final String SHAPE_ID = "SHAPE_ID";

    private static final float BOUNDS_X = 1;

    private static final float BOUNDS_Y = 2;

    private static final float BOUNDS_WIDTH = 2;

    private static final float BOUNDS_HEIGHT = 4;

    private static final float WAY_POINT_X = 1;

    private static final float WAY_POINT_Y = 2;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private BPMNEdge edge;

    @Mock
    private BPMNShape shape;

    @Mock
    private Bounds bounds;

    @Mock
    private Point point;

    @Mock
    private BaseElement baseElement;

    @Before
    public void setUp() {
        when(bounds.getX()).thenReturn(BOUNDS_X);
        when(bounds.getY()).thenReturn(BOUNDS_Y);
        when(bounds.getWidth()).thenReturn(BOUNDS_WIDTH);
        when(bounds.getHeight()).thenReturn(BOUNDS_HEIGHT);
        when(point.getX()).thenReturn(WAY_POINT_X);
        when(point.getY()).thenReturn(WAY_POINT_Y);

        when(definitionResolver.getEdge(EDGE_ID)).thenReturn(edge);
        when(definitionResolver.getShape(SHAPE_ID)).thenReturn(shape);
        when(shape.getBounds()).thenReturn(bounds);
    }

    @Test
    public void testGetSourcePositionWithNoWaypoint() {
        when(edge.getWaypoint()).thenReturn(Collections.emptyList());
        Point2D point = PropertyReaderUtils.getSourcePosition(definitionResolver, EDGE_ID, SHAPE_ID);
        assertPoint(BOUNDS_WIDTH, BOUNDS_HEIGHT/2, point);
    }

    @Test
    public void testGetSourcePositionWithWaypoint() {
        when(edge.getWaypoint()).thenReturn(Collections.singletonList(point));
        Point2D point = PropertyReaderUtils.getSourcePosition(definitionResolver, EDGE_ID, SHAPE_ID);
        assertPoint(WAY_POINT_X - BOUNDS_X, WAY_POINT_Y - BOUNDS_Y, point);
    }

    @Test
    public void testGetTargetPositionWithNoWaypoint() {
        when(edge.getWaypoint()).thenReturn(Collections.emptyList());
        Point2D point = PropertyReaderUtils.getTargetPosition(definitionResolver, EDGE_ID, SHAPE_ID);
        assertPoint(0, BOUNDS_HEIGHT/2, point);
    }

    @Test
    public void testGetTargetPositionWithWaypoint() {
        when(edge.getWaypoint()).thenReturn(Collections.singletonList(point));
        Point2D point = PropertyReaderUtils.getTargetPosition(definitionResolver, EDGE_ID, SHAPE_ID);
        assertPoint(WAY_POINT_X - BOUNDS_X, WAY_POINT_Y - BOUNDS_Y, point);
    }

    @Test
    public void testGetControlPointsWhenZeroPoints() {
        when(edge.getWaypoint()).thenReturn(Collections.emptyList());
        List<Point2D> result = PropertyReaderUtils.getControlPoints(definitionResolver, EDGE_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetControlPointsWhenOnePoints() {
        List<Point> waypoints = mockPoints(1, 2, 1);
        when(edge.getWaypoint()).thenReturn(waypoints);
        List<Point2D> result = PropertyReaderUtils.getControlPoints(definitionResolver, EDGE_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetControlPointsWhenTwoPoints() {
        List<Point> waypoints = mockPoints(1, 2, 2);
        when(edge.getWaypoint()).thenReturn(waypoints);
        List<Point2D> result = PropertyReaderUtils.getControlPoints(definitionResolver, EDGE_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetControlPointsWhenThreePoints() {
        testGetControlPointsWhenMoreThanTwo(3);
    }

    @Test
    public void testGetControlPointsWhenNPoints() {
        testGetControlPointsWhenMoreThanTwo(10);
    }

    @Test
    public void testIsAutoConnectionSourceWithTrue() {
        testIsAutoConnectionSource("true", true);
    }

    @Test
    public void testIsAutoConnectionSourceWithFalse() {
        testIsAutoConnectionSource("false", false);
    }

    @Test
    public void testIsAutoConnectionSourceWithNull() {
        testIsAutoConnectionSource(null, false);
    }

    private void testIsAutoConnectionSource(String valueToSet, boolean expectedResult) {
        prepareExtensionElement(CustomElement.autoConnectionSource.name(), valueToSet);
        assertEquals(expectedResult, PropertyReaderUtils.isAutoConnectionSource(baseElement));
    }

    @Test
    public void testIsAutoConnectionTargetWithTrue() {
        testIsAutoConnectionTarget("true", true);
    }

    @Test
    public void testIsAutoConnectionTargetWithFalse() {
        testIsAutoConnectionTarget("false", false);
    }

    @Test
    public void testIsAutoConnectionTargetWithNull() {
        testIsAutoConnectionTarget(null, false);
    }

    private void testIsAutoConnectionTarget(String valueToSet, boolean expectedResult) {
        prepareExtensionElement(CustomElement.autoConnectionTarget.name(), valueToSet);
        assertEquals(expectedResult, PropertyReaderUtils.isAutoConnectionTarget(baseElement));
    }

    private void prepareExtensionElement(String extensionElementName, String value) {
        ExtensionAttributeValue extensionAttributeValue = mock(ExtensionAttributeValue.class);
        FeatureMap extensionElements = mock(FeatureMap.class);
        when(extensionAttributeValue.getValue()).thenReturn(extensionElements);
        List<MetaDataType> metadataExtensions = new ArrayList<>();
        MetaDataType metaDataType = mock(MetaDataType.class);
        when(metaDataType.getName()).thenReturn(extensionElementName);
        when(metaDataType.getMetaValue()).thenReturn(value);
        metadataExtensions.add(metaDataType);
        when(extensionElements.get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true)).thenReturn(metadataExtensions);

        List<ExtensionAttributeValue> extensionAttributes = new ArrayList<>();
        extensionAttributes.add(extensionAttributeValue);
        when(baseElement.getExtensionValues()).thenReturn(extensionAttributes);
    }

    private void testGetControlPointsWhenMoreThanTwo(int size) {
        List<Point> waypoints = mockPoints(1, 2, size);
        when(edge.getWaypoint()).thenReturn(waypoints);
        List<Point2D> result = PropertyReaderUtils.getControlPoints(definitionResolver, EDGE_ID);
        waypoints.remove(0);
        waypoints.remove(waypoints.size() -1);
        assertPoints(waypoints, result);
    }

    private void assertPoint(float x, float y, Point2D point2D) {
        assertNotNull(point2D);
        assertEquals(x, point2D.getX(), 0);
        assertEquals(y, point2D.getY(), 0);
    }

    private void assertPoints(List<Point> originalPoints, List<Point2D> points) {
        assertEquals(originalPoints.size(), points.size());
        for (int i = 0; i < points.size(); i++) {
            assertEquals(originalPoints.get(i).getX(), points.get(i).getX(), 0);
            assertEquals(originalPoints.get(i).getY(), points.get(i).getY(), 0);
        }
    }

    private List<Point> mockPoints(float mockXStart, float mockYStart, int size) {
        List<Point> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Point point = mock(Point.class);
            when(point.getX()).thenReturn(mockXStart+i);
            when(point.getY()).thenReturn(mockYStart+i);
            result.add(point);
        }
        return result;
    }
}

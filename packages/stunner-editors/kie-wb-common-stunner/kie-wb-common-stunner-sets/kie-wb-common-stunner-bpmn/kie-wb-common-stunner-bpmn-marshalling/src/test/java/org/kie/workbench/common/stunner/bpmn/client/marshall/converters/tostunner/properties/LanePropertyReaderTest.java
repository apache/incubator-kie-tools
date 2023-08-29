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

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.common.util.EList;
import org.jboss.drools.DroolsPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestDefinitionsWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.assertBounds;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockBounds;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockExtensionValues;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LanePropertyReaderTest {

    private static final String NAME = "NAME";
    private static final String METADATA_ELEMENT_NAME = "elementname";
    private static final float X = 5;
    private static final float Y = 6f;
    private static final float WIDTH = 7f;
    private static final float HEIGHT = 8f;

    private static final float PARENT_X = 1f;
    private static final float PARENT_Y = 2f;
    private static final float PARENT_WIDTH = 10f;
    private static final float PARENT_HEIGHT = 11f;

    private static double RESOLUTION_FACTOR = 1.234;

    @Mock
    private Lane lane;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNShape shape;

    private org.eclipse.dd.dc.Bounds bounds;

    @Mock
    private BPMNShape parentLaneShape;

    private org.eclipse.dd.dc.Bounds parentBounds;

    @Before
    public void setUp() {
        bounds = mockBounds(X, Y, WIDTH, HEIGHT);
        when(shape.getBounds()).thenReturn(bounds);
        parentBounds = mockBounds(PARENT_X, PARENT_Y, PARENT_WIDTH, PARENT_HEIGHT);
        when(parentLaneShape.getBounds()).thenReturn(parentBounds);
    }

    @Test
    public void JBPM_7523_shouldPreserveNameChars() {
        PropertyReaderFactory factory = new PropertyReaderFactory(
                new TestDefinitionsWriter().getDefinitionResolver());
        Lane lane = bpmn2.createLane();

        PropertyWriterFactory writerFactory = new PropertyWriterFactory();
        LanePropertyWriter w = writerFactory.of(lane);

        String aWeirdName = "   XXX  !!@@ <><> ";
        String aWeirdDoc = "   XXX  !!@@ <><> Docs ";
        w.setName(aWeirdName);
        w.setDocumentation(aWeirdDoc);

        LanePropertyReader r = factory.of(lane);
        assertThat(r.getName()).isEqualTo(asCData(aWeirdName));
        assertThat(r.getDocumentation()).isEqualTo(asCData(aWeirdDoc));
    }

    @Test
    public void testGetBounds() {
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, RESOLUTION_FACTOR);
        Bounds result = propertyReader.getBounds();
        assertBounds(X * RESOLUTION_FACTOR,
                     Y * RESOLUTION_FACTOR,
                     X * RESOLUTION_FACTOR + WIDTH * RESOLUTION_FACTOR,
                     Y * RESOLUTION_FACTOR + HEIGHT * RESOLUTION_FACTOR,
                     result);
    }

    @Test
    public void testGetBoundsWithParentLaneShape() {
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, parentLaneShape, RESOLUTION_FACTOR);
        Bounds result = propertyReader.getBounds();
        assertBounds(PARENT_X * RESOLUTION_FACTOR,
                     Y * RESOLUTION_FACTOR,
                     PARENT_X * RESOLUTION_FACTOR + PARENT_WIDTH * RESOLUTION_FACTOR,
                     Y * RESOLUTION_FACTOR + HEIGHT * RESOLUTION_FACTOR,
                     result);
    }

    @Test
    public void testGetRectangleDimensionsSet() {
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, RESOLUTION_FACTOR);
        RectangleDimensionsSet dimensionsSet = propertyReader.getRectangleDimensionsSet();
        assertRectangleDimensions(WIDTH * RESOLUTION_FACTOR, HEIGHT * RESOLUTION_FACTOR, dimensionsSet);
    }

    @Test
    public void testGetRectangleDimensionsSetWithParentShape() {
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, parentLaneShape, RESOLUTION_FACTOR);
        RectangleDimensionsSet dimensionsSet = propertyReader.getRectangleDimensionsSet();
        assertRectangleDimensions(PARENT_WIDTH * RESOLUTION_FACTOR, HEIGHT * RESOLUTION_FACTOR, dimensionsSet);
    }

    @Test
    public void testGetNameFromExtensionElement() {
        EList<ExtensionAttributeValue> extensionValues = mockExtensionValues(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, METADATA_ELEMENT_NAME, NAME);
        when(lane.getExtensionValues()).thenReturn(extensionValues);
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, RESOLUTION_FACTOR);
        assertEquals(NAME, propertyReader.getName());
    }

    @Test
    public void testGetNameFromNameValue() {
        LanePropertyReader propertyReader = new LanePropertyReader(lane, diagram, shape, RESOLUTION_FACTOR);
        when(lane.getExtensionValues()).thenReturn(null);
        when(lane.getName()).thenReturn(NAME);
        assertEquals(NAME, propertyReader.getName());
    }

    private void assertRectangleDimensions(double width, double height, RectangleDimensionsSet dimensionsSet) {
        assertEquals(width, dimensionsSet.getWidth().getValue(), 0);
        assertEquals(height, dimensionsSet.getHeight().getValue(), 0);
    }
}
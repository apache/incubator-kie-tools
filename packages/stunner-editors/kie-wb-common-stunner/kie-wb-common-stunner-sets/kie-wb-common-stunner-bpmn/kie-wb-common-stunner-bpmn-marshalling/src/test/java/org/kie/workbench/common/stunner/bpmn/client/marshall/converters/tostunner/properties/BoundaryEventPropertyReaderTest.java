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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockBounds;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockFeatureMapEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BoundaryEventPropertyReaderTest extends CatchEventPropertyReaderTest {

    private static final String DOCKER_INFO_METADATA_ELEMENT_NAME = "dockerinfo";
    /**
     * encoded value format -> "1.0^2.0|"
     */
    private static final String DOCKER_INFO_VALUE = X + "^" + Y + "|";

    private static final float ACTIVITY_X = 100;
    private static final float ACTIVITY_Y = 150;
    float ACTIVITY_WIDTH = 100;
    float ACTIVITY_HEIGHT = 200;

    @Mock
    private BoundaryEvent boundaryEvent;

    @Override
    public void setUp() {
        FeatureMap featureMap = mock(FeatureMap.class);
        FeatureMap.Entry entry = mockFeatureMapEntry(DOCKER_INFO_METADATA_ELEMENT_NAME, DOCKER_INFO_VALUE);
        Stream<FeatureMap.Entry> entries = Stream.of(entry);
        when(featureMap.stream()).thenReturn(entries);
        when(boundaryEvent.getAnyAttribute()).thenReturn(featureMap);
        super.setUp();
    }

    @Override
    protected EventPropertyReader newPropertyReader() {
        return new BoundaryEventPropertyReader(boundaryEvent, diagram, definitionResolver);
    }

    @Override
    protected void setSignalEventDefinitionOnCurrentMock(SignalEventDefinition eventDefinition) {
        EList<EventDefinition> eventDefinitions = ECollections.singletonEList(eventDefinition);
        when(boundaryEvent.getEventDefinitions()).thenReturn(eventDefinitions);
        when(boundaryEvent.getEventDefinitionRefs()).thenReturn(ECollections.emptyEList());
    }

    @Test
    public void testComputeBoundsWhenNoDockerInfoEventOnTop() {
        float eventX = ACTIVITY_X + 40;
        float eventY = ACTIVITY_Y - 28;
        testComputeBoundsWhenNoDockerInfo(eventX,
                                          eventY,
                                          Double.valueOf(eventX * RESOLUTION_FACTOR - ACTIVITY_X * RESOLUTION_FACTOR).floatValue(),
                                          Double.valueOf(-EventPropertyReader.HEIGHT / 2).floatValue());
    }

    @Test
    public void testComputeBoundsWhenNoDockerInfoEventOnRight() {
        float eventX = ACTIVITY_X + ACTIVITY_WIDTH - 28;
        float eventY = ACTIVITY_Y + 40;
        testComputeBoundsWhenNoDockerInfo(eventX,
                                          eventY,
                                          Double.valueOf(eventX * RESOLUTION_FACTOR - ACTIVITY_X * RESOLUTION_FACTOR).floatValue(),
                                          Double.valueOf(eventY * RESOLUTION_FACTOR - ACTIVITY_Y * RESOLUTION_FACTOR).floatValue());
    }

    @Test
    public void testComputeBoundsWhenNoDockerInfoEventOnBottom() {
        float eventX = ACTIVITY_X + 40;
        float eventY = ACTIVITY_Y + ACTIVITY_WIDTH - 28;
        testComputeBoundsWhenNoDockerInfo(eventX,
                                          eventY,
                                          Double.valueOf(eventX * RESOLUTION_FACTOR - ACTIVITY_X * RESOLUTION_FACTOR).floatValue(),
                                          Double.valueOf(eventY * RESOLUTION_FACTOR - ACTIVITY_Y * RESOLUTION_FACTOR).floatValue());
    }

    @Test
    public void testComputeBoundsWhenNoDockerInfoEventOnLeft() {
        float eventX = ACTIVITY_X + -28;
        float eventY = ACTIVITY_Y + 40;
        testComputeBoundsWhenNoDockerInfo(eventX,
                                          eventY,
                                          Double.valueOf(-EventPropertyReader.WIDTH / 2).floatValue(),
                                          Double.valueOf(eventY * RESOLUTION_FACTOR - ACTIVITY_Y * RESOLUTION_FACTOR).floatValue());
    }

    private void testComputeBoundsWhenNoDockerInfo(float eventX, float eventY, float expectedX, float expectedY) {
        FeatureMap featureMap = mock(FeatureMap.class);
        List<FeatureMap.Entry> entries = new ArrayList<>();
        when(featureMap.stream()).thenReturn(entries.stream());
        when(boundaryEvent.getAnyAttribute()).thenReturn(featureMap);
        String activityId = "activityId";
        Activity sourceActivity = mock(Activity.class);
        BPMNShape sourceActivityShape = mock(BPMNShape.class);
        org.eclipse.dd.dc.Bounds activityBounds = mockBounds(ACTIVITY_X, ACTIVITY_Y, ACTIVITY_WIDTH, ACTIVITY_HEIGHT);
        when(sourceActivity.getId()).thenReturn(activityId);
        when(sourceActivityShape.getBounds()).thenReturn(activityBounds);
        when(definitionResolver.getShape(activityId)).thenReturn(sourceActivityShape);
        when(boundaryEvent.getAttachedToRef()).thenReturn(sourceActivity);

        float eventWidth = 56;
        float eventHeight = 56;
        org.eclipse.dd.dc.Bounds eventBounds = mockBounds(eventX, eventY, eventWidth, eventHeight);
        Bounds result = propertyReader.computeBounds(eventBounds);
        assertEquals(expectedX, result.getX(), 0);
        assertEquals(expectedY, result.getY(), 0);
    }

    @Override
    protected Event getCurrentEventMock() {
        return boundaryEvent;
    }
}

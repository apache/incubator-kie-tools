/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.mockito.Mock;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.TestUtils.mockFeatureMapEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoundaryEventPropertyReaderTest extends CatchEventPropertyReaderTest {

    private static final String DOCKER_INFO_METADATA_ELEMENT_NAME = "dockerinfo";
    /**
     * encoded value format -> "1.0^2.0|"
     */
    private static final String DOCKER_INFO_VALUE = X + "^" + Y + "|";

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
        List<EventDefinition> eventDefinitions = Collections.singletonList(eventDefinition);
        when(boundaryEvent.getEventDefinitions()).thenReturn(eventDefinitions);
        when(boundaryEvent.getEventDefinitionRefs()).thenReturn(Collections.EMPTY_LIST);
    }

    @Override
    protected Event getCurrentEventMock() {
        return boundaryEvent;
    }
}

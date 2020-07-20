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

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThrowEventPropertyReaderTest extends BaseEventPropertyReaderTest {

    @Mock
    private ThrowEvent throwEvent;

    @Override
    protected EventPropertyReader newPropertyReader() {
        return new ThrowEventPropertyReader(throwEvent, diagram, definitionResolver);
    }

    @Override
    protected void setSignalEventDefinitionOnCurrentMock(SignalEventDefinition eventDefinition) {
        List<EventDefinition> eventDefinitions = Collections.singletonList(eventDefinition);
        when(throwEvent.getEventDefinitions()).thenReturn(eventDefinitions);
        when(throwEvent.getEventDefinitionRefs()).thenReturn(Collections.EMPTY_LIST);
    }

    @Override
    protected Event getCurrentEventMock() {
        return throwEvent;
    }
}

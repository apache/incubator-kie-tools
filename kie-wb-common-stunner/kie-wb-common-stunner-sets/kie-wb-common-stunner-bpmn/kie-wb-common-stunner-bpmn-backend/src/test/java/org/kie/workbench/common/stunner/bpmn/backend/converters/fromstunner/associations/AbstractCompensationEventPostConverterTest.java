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

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Process;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCompensationEventPostConverterTest<T extends BPMNViewDefinition, B extends Event> {

    @Mock
    protected ProcessPropertyWriter processWriter;

    @Mock
    protected BasePropertyWriter nodeWriter;

    @Mock
    protected Node<View<T>, ?> node;

    @Mock
    protected View<T> content;

    protected T event;

    @Mock
    protected Event bpmn2Event;

    @Mock
    protected CompensateEventDefinition compensateEvent;

    @Mock
    protected Process process;

    protected PostConverterProcessor converter;

    @Before
    public void setUp() {
        event = createEvent();
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(event);

        bpmn2Event = createBpmn2Event(compensateEvent);
        when(nodeWriter.getElement()).thenReturn(bpmn2Event);
        when(processWriter.getProcess()).thenReturn(process);

        converter = createConverter();
    }

    public abstract T createEvent();

    public abstract B createBpmn2Event(CompensateEventDefinition compensateEvent);

    public abstract PostConverterProcessor createConverter();
}
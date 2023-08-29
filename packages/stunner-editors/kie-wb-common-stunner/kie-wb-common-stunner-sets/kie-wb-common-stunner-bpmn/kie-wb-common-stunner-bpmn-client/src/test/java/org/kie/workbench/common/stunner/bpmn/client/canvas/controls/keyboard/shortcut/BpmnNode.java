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


package org.kie.workbench.common.stunner.bpmn.client.canvas.controls.keyboard.shortcut;

import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public enum BpmnNode {
    NONE_TASK(new NoneTask()),
    PARALLEL_GATEWAY(new ParallelGateway()),
    EMBEDDED_SUBPROCESS(new EmbeddedSubprocess()),
    NONE_START_EVENT(new StartNoneEvent()),
    NONE_END_EVENT(new EndNoneEvent());

    final Object definition;

    final Element element;

    BpmnNode(final Object definition) {
        this.definition = definition;
        this.element = mock(Element.class);

        final Definition elementDefinition = mock(Definition.class);
        doReturn(elementDefinition).when(element).getContent();
        doReturn(definition).when(elementDefinition).getDefinition();
    }

    public Object getDefinition() {
        return definition;
    }

    public Element getElement() {
        return element;
    }
}

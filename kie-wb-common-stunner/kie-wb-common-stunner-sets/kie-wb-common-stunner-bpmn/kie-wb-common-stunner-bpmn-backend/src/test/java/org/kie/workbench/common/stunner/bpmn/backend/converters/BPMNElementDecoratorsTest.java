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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNBaseInfo;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessageDecorator;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNElementDecoratorsTest {

    private static final String NAME = "NAME";

    @Test
    public void flowElementDecorator() {
        MarshallingMessageDecorator<FlowElement> decorator = BPMNElementDecorators.flowElementDecorator();
        FlowElement element = mock(FlowElement.class);
        when(element.getName()).thenReturn(NAME);
        assertEquals(NAME, decorator.getName(element));
        assertEquals(element.getClass().getSimpleName(), decorator.getType(element));
    }

    @Test
    public void baseElementDecorator() {
        MarshallingMessageDecorator<BaseElement> decorator = BPMNElementDecorators.baseElementDecorator();
        BaseElement element = mock(FlowElement.class);
        when(element.getId()).thenReturn(NAME);
        assertEquals(NAME, decorator.getName(element));
        assertEquals(element.getClass().getSimpleName(), decorator.getType(element));
    }

    @Test
    public void bpmnNodeDecorator() {
        MarshallingMessageDecorator<BpmnNode> decorator = BPMNElementDecorators.bpmnNodeDecorator();
        BpmnNode element = mockBpmnNode();

        assertEquals(NAME, decorator.getName(element));
        assertEquals(element.value().getContent().getDefinition().getClass().getSimpleName(), decorator.getType(element));
    }

    private BpmnNode mockBpmnNode() {
        BpmnNode element = mock(BpmnNode.class);
        Node node = mock(Node.class);
        when(element.value()).thenReturn(node);
        View content = mock(View.class);
        when(node.getContent()).thenReturn(content);
        BPMNViewDefinition viewDefinition = mock(BPMNViewDefinition.class);
        when(content.getDefinition()).thenReturn(viewDefinition);
        BPMNBaseInfo general = mock(BPMNBaseInfo.class);
        when(viewDefinition.getGeneral()).thenReturn(general);
        Name name = mock(Name.class);
        when(general.getName()).thenReturn(name);
        when(name.getValue()).thenReturn(NAME);
        return element;
    }

    @Test
    public void resultBpmnDecorator() {
        MarshallingMessageDecorator<Result> decorator = BPMNElementDecorators.resultBpmnDecorator();
        BpmnNode node = mockBpmnNode();
        Result result = Result.success(node);

        assertEquals(NAME, decorator.getName(result));
        assertEquals(node.value().getContent().getDefinition().getClass().getSimpleName(),
                     decorator.getType(result));
    }
}
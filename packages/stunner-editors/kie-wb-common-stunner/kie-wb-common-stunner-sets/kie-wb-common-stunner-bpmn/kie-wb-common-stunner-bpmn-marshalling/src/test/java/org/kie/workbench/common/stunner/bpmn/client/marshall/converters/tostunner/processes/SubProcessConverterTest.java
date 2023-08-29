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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes;

import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SubProcess;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AdHocSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: Kogito
@Ignore
public class SubProcessConverterTest {

    private final String SLA_DUE_DATE = "12/25/1983";

    private DefinitionResolver definitionResolver;

    private SubProcessConverter tested;

    /*@Before
    public void setUp() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        Node adHocNode = new NodeImpl("");
        View<AdHocSubprocess> adHocContent =
                new ViewImpl<>(new AdHocSubprocess(), Bounds.create());
        adHocNode.setContent(adHocContent);

        Node multipleInstanceNode = new NodeImpl("");
        View<MultipleInstanceSubprocess> miContent =
                new ViewImpl<>(new MultipleInstanceSubprocess(), Bounds.create());
        multipleInstanceNode.setContent(miContent);

        Node embeddedNode = new NodeImpl("");
        View<EmbeddedSubprocess> embeddedContent =
                new ViewImpl<>(new EmbeddedSubprocess(), Bounds.create());
        embeddedNode.setContent(embeddedContent);

        Node eventNode = new NodeImpl("");
        View<EventSubprocess> eventSubprocess =
                new ViewImpl<>(new EventSubprocess(), Bounds.create());
        eventNode.setContent(eventSubprocess);

        FactoryManager factoryManager = mock(FactoryManager.class);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(AdHocSubprocess.class))))
                .thenReturn(adHocNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(MultipleInstanceSubprocess.class))))
                .thenReturn(multipleInstanceNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(EmbeddedSubprocess.class))))
                .thenReturn(embeddedNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(EventSubprocess.class))))
                .thenReturn(eventNode);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        tested = new SubProcessConverter(typedFactoryManager,
                                         new PropertyReaderFactory(definitionResolver),
                                         definitionResolver,
                                         new ConverterFactory(definitionResolver, typedFactoryManager));
    }*/

    @Test
    public void createNode() {
        assertTrue(AdHocSubprocess.class.isInstance(tested.createNode("id").getContent().getDefinition()));
    }

    @Test
    public void createProcessData() {
        assertTrue(ProcessData.class.isInstance(tested.createProcessData("id")));
    }

    @Test
    public void testCreateAdHocSubprocessTaskExecutionSet() {
        AdHocSubProcess adHocSubProcess = mock(AdHocSubProcess.class);
        when(adHocSubProcess.getCompletionCondition()).thenReturn(mock(FormalExpression.class));
        when(adHocSubProcess.getOrdering()).thenReturn(AdHocOrdering.SEQUENTIAL);

        assertTrue(AdHocSubprocessTaskExecutionSet.class.isInstance(tested.createAdHocSubprocessTaskExecutionSet(
                new AdHocSubProcessPropertyReader(adHocSubProcess,
                                                  definitionResolver.getDiagram(),
                                                  definitionResolver))));
    }

    @Test
    public void testConvertAdHocSubprocessNode() {
        SubProcess subProcess = bpmn2.createAdHocSubProcess();
        CustomElement.async.setValue(subProcess, Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        AdHocSubprocess adHocSubprocess = (AdHocSubprocess) node.value().getContent().getDefinition();

        assertNotNull(adHocSubprocess);
        assertBaseSubprocessExecutionSet(adHocSubprocess.getExecutionSet());
    }

    @Test
    public void testConvertMultInstanceSubprocessNode() {
        SubProcess subProcess = bpmn2.createSubProcess();
        subProcess.setLoopCharacteristics(bpmn2.createMultiInstanceLoopCharacteristics());
        CustomElement.async.setValue(subProcess, Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        MultipleInstanceSubprocess miSubProcess = (MultipleInstanceSubprocess) node.value().getContent().getDefinition();

        assertNotNull(miSubProcess);
        assertBaseSubprocessExecutionSet(miSubProcess.getExecutionSet());
    }

    @Test
    public void testConvertEmbeddedSubprocessNode() {
        SubProcess subProcess = bpmn2.createSubProcess();
        CustomElement.async.setValue(subProcess, Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        EmbeddedSubprocess embeddedSubprocess = (EmbeddedSubprocess) node.value().getContent().getDefinition();
        assertNotNull(embeddedSubprocess);

        assertBaseSubprocessExecutionSet(embeddedSubprocess.getExecutionSet());
    }

    @Test
    public void testConvertEventSubprocessNode() {
        SubProcess subProcess = bpmn2.createSubProcess();
        subProcess.setTriggeredByEvent(Boolean.TRUE);
        CustomElement.async.setValue(subProcess, Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        EventSubprocess eventSubprocess = (EventSubprocess) node.value().getContent().getDefinition();

        assertNotNull(eventSubprocess);
        assertBaseSubprocessExecutionSet(eventSubprocess.getExecutionSet());
    }

    private void assertBaseSubprocessExecutionSet(BaseSubprocessTaskExecutionSet executionSet) {
        assertNotNull(executionSet);

        assertNotNull(executionSet.getIsAsync());
        assertTrue(executionSet.getIsAsync().getValue());

        assertNotNull(executionSet.getSlaDueDate());
        assertTrue(executionSet.getSlaDueDate().getValue().contains(SLA_DUE_DATE));
    }
}

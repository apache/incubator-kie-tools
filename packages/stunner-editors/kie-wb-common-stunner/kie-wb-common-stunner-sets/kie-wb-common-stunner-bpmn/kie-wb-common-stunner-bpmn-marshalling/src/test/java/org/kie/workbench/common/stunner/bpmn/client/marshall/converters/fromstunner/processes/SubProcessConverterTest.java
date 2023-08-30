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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.AdHocSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.SubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.DocumentationTextHandler;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.uberfire.commons.Pair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.mockito.Mockito.mock;

public class SubProcessConverterTest {

    private static final String ELEMENT_ID = "ELEMENT_ID";
    private static final String NAME = "NAME";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final String ACTIVATION_CONDITION = "ACTIVATION_CONDITION";
    private static final ScriptTypeValue COMPLETION_CONDITION = new ScriptTypeValue("drools", "the condition");
    private static final ScriptTypeValue ON_ENTRY_ACTION = new ScriptTypeValue("java", "on entry script");
    private static final ScriptTypeValue ON_EXIT_ACTION = new ScriptTypeValue("java", "on exit script");
    private static final String SLA_DUE_DATE = "12/25/1983";

    @Test
    public void JBPM_7525_eventSubProcessShouldStoreIsAsync() {
        ConverterFactory f = new ConverterFactory(new DefinitionsBuildingContext(
                new GraphImpl("x", new GraphNodeStoreImpl())),
                                                  new PropertyWriterFactory());
        SubProcessConverter c = f.subProcessConverter();

        NodeImpl<View<? extends BPMNViewDefinition>> n = new NodeImpl<>("n");
        EventSubprocess subProcessNode = new EventSubprocess();
        subProcessNode.getExecutionSet().setIsAsync(new IsAsync(true));
        n.setContent(new ViewImpl<>(subProcessNode, Bounds.create()));

        Activity activity = c.convertSubProcess(n).value().getFlowElement();
        Boolean value = CustomElement.async.of(activity).get();

        assertThat(value).isEqualTo(true);
    }

    private SubProcessConverter tested;

    @Before
    public void setUp() {
        PropertyWriterFactory factory = new PropertyWriterFactory();

        DefinitionsBuildingContext definitionsBuildingContext = new DefinitionsBuildingContext(new GraphImpl("x", new GraphNodeStoreImpl()));

        tested = new SubProcessConverter(definitionsBuildingContext,
                                         factory,
                                         new ConverterFactory(definitionsBuildingContext, factory));
    }

    @Test
    public void testConvertAdHocSubprocessNode_autostart() {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(AdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertTrue(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }

    @Test
    public void testConvertAdHocSubprocessNode_notautostart() {
        final AdHocSubprocess definition = new AdHocSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(false));
        final View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseAdHocSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertAdHocSubprocessNode(node);
        assertTrue(AdHocSubProcessPropertyWriter.class.isInstance(writer));
        assertFalse(CustomElement.autoStart.of(writer.getFlowElement()).get());
    }

    private static void setBaseSubprocessExecutionSetValues(BaseSubprocessTaskExecutionSet executionSet) {
        executionSet.setIsAsync(new IsAsync(Boolean.TRUE));
        executionSet.setSlaDueDate(new SLADueDate(SLA_DUE_DATE));
    }

    private static void assertBaseSubprocessExecutionSet(SubProcessPropertyWriter writer) {
        assertTrue(SubProcessPropertyWriter.class.isInstance(writer));
        assertTrue(CustomElement.async.of(writer.getElement()).get());
        assertTrue(CustomElement.slaDueDate.of(writer.getElement()).get().contains(SLA_DUE_DATE));
    }

    @Test
    public void testConvertAdhocSubprocess() {
        AdHocSubprocess definition = new AdHocSubprocess();
        String adHocOrdering = "Parallel";
        boolean adHocAutostart = true;
        String processVariables = "processVar1:Object:myTag,processVar2:Integer";
        definition.getGeneral().getName().setValue(NAME);
        definition.getGeneral().getDocumentation().setValue(DOCUMENTATION);
        definition.getProcessData().getProcessVariables().setValue(processVariables);
        definition.getExecutionSet().getAdHocOrdering().setValue(adHocOrdering);
        definition.getExecutionSet().getAdHocAutostart().setValue(adHocAutostart);
        definition.getExecutionSet().getAdHocActivationCondition().setValue(ACTIVATION_CONDITION);
        definition.getExecutionSet().getAdHocCompletionCondition().setValue(COMPLETION_CONDITION);
        definition.getExecutionSet().getOnEntryAction().getValue().addValue(ON_ENTRY_ACTION);
        definition.getExecutionSet().getOnExitAction().getValue().addValue(ON_EXIT_ACTION);
        setBaseSubprocessExecutionSetValues(definition.getExecutionSet());

        double nodeX1 = 10;
        double nodeY1 = 20;
        double nodeX2 = 40;
        double nodeY2 = 60;
        View<BaseAdHocSubprocess> view = new ViewImpl<>(definition, Bounds.create(nodeX1, nodeY1, nodeX2, nodeY2));
        Node<View<? extends BPMNViewDefinition>, Edge> node = new NodeImpl<>(ELEMENT_ID);
        node.setContent(view);
        double parentX1 = 30;
        double parentY1 = 40;
        double parentX2 = 60;
        double parentY2 = 100;
        Node<View<? extends BPMNViewDefinition>, ?> parent = new NodeImpl<>("parentId");
        View<? extends BPMNViewDefinition> parentView = new ViewImpl<>(null, Bounds.create(parentX1, parentY1, parentX2, parentY2));
        parent.setContent(parentView);
        Edge<Child, Node> edge = new EdgeImpl("edgeId");
        edge.setContent(mock(Child.class));
        node.getInEdges().add(edge);
        edge.setSourceNode(parent);
        edge.setTargetNode(node);

        Result<SubProcessPropertyWriter> result = tested.convertSubProcess(node);
        assertTrue(result.isSuccess());
        AdHocSubProcess adHocSubProcess = (AdHocSubProcess) result.value().getElement();
        assertEquals(ELEMENT_ID, adHocSubProcess.getId());
        assertEquals(NAME, adHocSubProcess.getName());
        assertEquals(asCData(NAME), CustomElement.name.of(adHocSubProcess).get());
        assertEquals(asCData(DOCUMENTATION), DocumentationTextHandler.of(adHocSubProcess.getDocumentation().get(0)).getText());
        assertEquals(adHocOrdering, adHocSubProcess.getOrdering().getName());
        assertEquals(adHocAutostart, CustomElement.autoStart.of(adHocSubProcess).get());
        // TODO: Kogito - assertEquals(asCData(ACTIVATION_CONDITION), CustomElement.customActivationCondition.of(adHocSubProcess).get());
        assertEquals(Scripts.LANGUAGE.valueOf(COMPLETION_CONDITION.getLanguage().toUpperCase()).format(), ((FormalExpression) adHocSubProcess.getCompletionCondition()).getLanguage());
        assertEquals(asCData(COMPLETION_CONDITION.getScript()), FormalExpressionBodyHandler.of((FormalExpression) adHocSubProcess.getCompletionCondition()).getBody());
        assertEquals(ON_ENTRY_ACTION.getLanguage(), Scripts.onEntry(adHocSubProcess.getExtensionValues()).getValues().get(0).getLanguage());
        assertEquals(asCData(ON_ENTRY_ACTION.getScript()), Scripts.onEntry(adHocSubProcess.getExtensionValues()).getValues().get(0).getScript());
        assertEquals(ON_EXIT_ACTION.getLanguage(), Scripts.onExit(adHocSubProcess.getExtensionValues()).getValues().get(0).getLanguage());
        assertEquals(asCData(ON_EXIT_ACTION.getScript()), Scripts.onExit(adHocSubProcess.getExtensionValues()).getValues().get(0).getScript());
        assertVariables(Arrays.asList(new Pair<>("processVar1", "Object"), new Pair<>("processVar2", "Integer")), adHocSubProcess.getProperties());
        BPMNShape shape = result.value().getShape();
        assertEquals(parentX1 + nodeX1, shape.getBounds().getX(), 0);
        assertEquals(parentY1 + nodeY1, shape.getBounds().getY(), 0);
        assertEquals(nodeX2 - nodeX1, shape.getBounds().getWidth(), 0);
        assertEquals(nodeY2 - nodeY1, shape.getBounds().getHeight(), 0);
        assertBaseSubprocessExecutionSet(result.value());
    }

    @Test
    public void testConvertMultipleIntanceSubprocess() {
        final MultipleInstanceSubprocess definition = new MultipleInstanceSubprocess();
        setBaseSubprocessExecutionSetValues(definition.getExecutionSet());
        final View<MultipleInstanceSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<MultipleInstanceSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertMultipleInstanceSubprocessNode(node);
        assertBaseSubprocessExecutionSet(writer);
    }

    private static void assertVariables(List<Pair<String, String>> expectedVariables, List<Property> properties) {
        assertEquals(expectedVariables.size(), properties.size());
        Pair<String, String> expectedVariable;
        Property property;
        for (int i = 0; i < expectedVariables.size(); i++) {
            expectedVariable = expectedVariables.get(i);
            property = properties.get(i);
            assertEquals(expectedVariable.getK1(), property.getId());
            assertEquals(expectedVariable.getK1(), property.getName());
            assertEquals(String.format("_%sItem", expectedVariable.getK1()), property.getItemSubjectRef().getId());
            assertEquals(expectedVariable.getK2(), property.getItemSubjectRef().getStructureRef());
        }
    }

    @Test
    public void testConvertEmbeddedSubprocess() {
        final EmbeddedSubprocess definition = new EmbeddedSubprocess();
        setBaseSubprocessExecutionSetValues(definition.getExecutionSet());
        final View<EmbeddedSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<EmbeddedSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertEmbeddedSubprocessNode(node);
        assertBaseSubprocessExecutionSet(writer);
    }

    @Test
    public void testConvertEventSubprocess() {
        final EventSubprocess definition = new EventSubprocess();
        setBaseSubprocessExecutionSetValues(definition.getExecutionSet());
        final View<EventSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<EventSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        SubProcessPropertyWriter writer = tested.convertEventSubprocessNode(node);
        assertBaseSubprocessExecutionSet(writer);
    }
}
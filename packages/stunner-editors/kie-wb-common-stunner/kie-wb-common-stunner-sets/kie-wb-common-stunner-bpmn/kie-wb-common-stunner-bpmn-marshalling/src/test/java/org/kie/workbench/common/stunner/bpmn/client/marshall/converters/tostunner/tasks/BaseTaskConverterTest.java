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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks;

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.BusinessRuleTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.GenericServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseTaskConverterTest {

    protected BaseTaskConverter tested;

    @Mock
    protected TypedFactoryManager factoryManager;

    @Mock
    protected PropertyReaderFactory propertyReaderFactory;

    @Mock
    protected View<BusinessRuleTask> businessRuleTaskContent;

    @Mock
    protected View<NoneTask> noneTaskContent;

    protected NoneTask noneTaskDefinition;

    @Mock
    protected TaskPropertyReader taskPropertyReader;

    @Mock
    private Node<View<NoneTask>, Edge> noneTaskNode;

    @Mock
    private Node<View<CustomTask>, Edge> serviceTaskNode;

    @Mock
    private View<CustomTask> serviceTaskContent;

    @Mock
    protected View<GenericServiceTask> genericServiceTaskContent;

    @Mock
    private Node<View<BusinessRuleTask>, Edge> businessRuleTaskNode;

    @Mock
    private Node<View<GenericServiceTask>, Edge> genericServiceTaskNode;

    @Mock
    private FeatureMap featureMap;

    @Mock
    private FeatureMap.Entry entry;

    @Before
    public void setUp() {
        noneTaskDefinition = new NoneTask();

        when(factoryManager.newNode(anyString(), eq(NoneTask.class))).thenReturn(noneTaskNode);
        when(noneTaskNode.getContent()).thenReturn(noneTaskContent);
        when(noneTaskContent.getDefinition()).thenReturn(noneTaskDefinition);
        when(entry.getEStructuralFeature()).thenReturn(mock(EStructuralFeature.class));
        when(featureMap.stream()).thenReturn(Arrays.asList(entry).stream());

        tested = createTaskConverter();
    }

    protected BaseTaskConverter createTaskConverter() {
        return spy(new BaseTaskConverter(factoryManager, propertyReaderFactory, MarshallingRequest.Mode.AUTO) {
            @Override
            protected Node<View, Edge> createNode(String id) {
                return null;
            }

            @Override
            protected BaseUserTaskExecutionSet createUserTaskExecutionSet(UserTaskPropertyReader p) {
                return null;
            }
        });
    }

    @Test
    public void convertBusinessRuleTask() {
        org.eclipse.bpmn2.BusinessRuleTask task = mock(org.eclipse.bpmn2.BusinessRuleTask.class);
        BusinessRuleTaskPropertyReader propertyReader = mock(BusinessRuleTaskPropertyReader.class);
        BusinessRuleTask businessRuleDefinition = new BusinessRuleTask();

        when(factoryManager.newNode(anyString(), eq(BusinessRuleTask.class))).thenReturn(businessRuleTaskNode);
        when(businessRuleTaskNode.getContent()).thenReturn(businessRuleTaskContent);
        when(businessRuleTaskContent.getDefinition()).thenReturn(businessRuleDefinition);
        when(propertyReaderFactory.of(task)).thenReturn(propertyReader);

        final BpmnNode converted = (BpmnNode) tested.convert(task).value();
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), businessRuleTaskNode);
    }

    @Test
    public void convertServiceTask() {
        org.eclipse.bpmn2.ServiceTask task = mock(org.eclipse.bpmn2.ServiceTask.class);
        ServiceTaskPropertyReader serviceTaskPropertyReader = mock(ServiceTaskPropertyReader.class);
        CustomTask definition = new CustomTask();
        FeatureMap attributes = mock(FeatureMap.class);
        FeatureMap.Entry ruleAttr = mock(FeatureMap.Entry.class);
        EStructuralFeature ruleFeature = mock(EStructuralFeature.class);

        when(factoryManager.newNode(anyString(), eq(CustomTask.class))).thenReturn(serviceTaskNode);
        when(serviceTaskNode.getContent()).thenReturn(serviceTaskContent);
        when(serviceTaskContent.getDefinition()).thenReturn(definition);
        when(propertyReaderFactory.ofCustom(task)).thenReturn(serviceTaskPropertyReader);

        when(task.getAnyAttribute()).thenReturn(attributes);
        when(attributes.stream()).thenReturn(Stream.of(ruleAttr));
        when(ruleAttr.getEStructuralFeature()).thenReturn(ruleFeature);
        when(ruleAttr.getValue()).thenReturn("");
        when(ruleFeature.getName()).thenReturn(CustomAttribute.serviceImplementation.name());

        final BpmnNode converted = (BpmnNode) tested.convert(task).value();
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), serviceTaskNode);
    }

    @Test
    public void convertGenericServiceTask() {
        org.eclipse.bpmn2.ServiceTask task = mock(org.eclipse.bpmn2.ServiceTask.class);
        GenericServiceTaskPropertyReader genericServiceTaskPropertyReader = mock(GenericServiceTaskPropertyReader.class);
        GenericServiceTask definition = new GenericServiceTask();

        FeatureMap attributes = mock(FeatureMap.class);
        FeatureMap.Entry ruleAttr = mock(FeatureMap.Entry.class);
        EStructuralFeature ruleFeature = mock(EStructuralFeature.class);

        when(factoryManager.newNode(anyString(), eq(GenericServiceTask.class))).thenReturn(genericServiceTaskNode);
        when(genericServiceTaskNode.getContent()).thenReturn(genericServiceTaskContent);
        when(genericServiceTaskContent.getDefinition()).thenReturn(definition);
        when(propertyReaderFactory.of(task)).thenReturn(genericServiceTaskPropertyReader);

        when(task.getAnyAttribute()).thenReturn(attributes);
        when(attributes.stream()).thenReturn(Stream.of(ruleAttr));
        when(ruleAttr.getEStructuralFeature()).thenReturn(ruleFeature);
        when(ruleAttr.getValue()).thenReturn("Java");
        when(ruleFeature.getName()).thenReturn(CustomAttribute.serviceImplementation.name());

        final BpmnNode converted = (BpmnNode) tested.convert(task).value();
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), genericServiceTaskNode);
    }

    // TODO: Kogito - @Test
    public void convertManualTask() {
        testTaskToNoneTask(ManualTask.class);
    }

    @Test
    public void convertReceiveTask() {
        testTaskToNoneTask(ReceiveTask.class);
    }

    @Test
    public void convertSendTask() {
        testTaskToNoneTask(SendTask.class);
    }

    @Test
    public void convertDefaultTask() {
        testTaskToNoneTask(Task.class);
    }

    private <T extends Task> void testTaskToNoneTask(Class<T> taskType) {
        final T task = mock(taskType);
        when(task.getAnyAttribute()).thenReturn(featureMap);
        when(propertyReaderFactory.of(task)).thenReturn(taskPropertyReader);

        final BpmnNode converted = (BpmnNode) tested.convert(task).value();
        assertEquals(converted.value(), noneTaskNode);
    }
}
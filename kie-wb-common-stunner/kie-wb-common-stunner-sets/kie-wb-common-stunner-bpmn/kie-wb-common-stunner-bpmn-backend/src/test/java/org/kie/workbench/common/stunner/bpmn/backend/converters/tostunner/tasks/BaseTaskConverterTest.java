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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BusinessRuleTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.GenericServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseTaskConverterTest {

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
    protected View<ServiceTask> serviceTaskContent;
    @Mock
    protected View<GenericServiceTask> genericServiceTaskContent;
    @Mock
    private Node<View<NoneTask>, Edge> noneTaskNode;
    @Mock
    private Node<View<BusinessRuleTask>, Edge> businessRuleTaskNode;
    @Mock
    private Node<View<ServiceTask>, Edge> serviceTaskNode;
    @Mock
    private Node<View<GenericServiceTask>, Edge> genericServiceTaskNode;

    @Before
    public void setUp() {
        noneTaskDefinition = new NoneTask();

        when(factoryManager.newNode(anyString(), eq(NoneTask.class))).thenReturn(noneTaskNode);
        when(noneTaskNode.getContent()).thenReturn(noneTaskContent);
        when(noneTaskContent.getDefinition()).thenReturn(noneTaskDefinition);

        tested = createTaskConverter();
    }

    protected BaseTaskConverter createTaskConverter() {
        return spy(new BaseTaskConverter(factoryManager, propertyReaderFactory) {
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

        final BpmnNode converted = tested.convert(task);
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), businessRuleTaskNode);
    }

    @Test
    public void convertServiceTask() {
        org.eclipse.bpmn2.ServiceTask task = mock(org.eclipse.bpmn2.ServiceTask.class);
        ServiceTaskPropertyReader serviceTaskPropertyReader = mock(ServiceTaskPropertyReader.class);
        ServiceTask definition = new ServiceTask();
        FeatureMap attributes = mock(FeatureMap.class);
        FeatureMap.Entry ruleAttr = mock(FeatureMap.Entry.class);
        EStructuralFeature ruleFeature = mock(EStructuralFeature.class);

        when(factoryManager.newNode(anyString(), eq(ServiceTask.class))).thenReturn(serviceTaskNode);
        when(serviceTaskNode.getContent()).thenReturn(serviceTaskContent);
        when(serviceTaskContent.getDefinition()).thenReturn(definition);
        when(propertyReaderFactory.ofCustom(task)).thenReturn(Optional.of(serviceTaskPropertyReader));

        when(task.getAnyAttribute()).thenReturn(attributes);
        when(attributes.stream()).thenReturn(Stream.of(ruleAttr));
        when(ruleAttr.getEStructuralFeature()).thenReturn(ruleFeature);
        when(ruleAttr.getValue()).thenReturn("");
        when(ruleFeature.getName()).thenReturn(CustomAttribute.serviceImplementation.name());

        final BpmnNode converted = tested.convert(task);
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

        final BpmnNode converted = tested.convert(task);
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), genericServiceTaskNode);
    }
}
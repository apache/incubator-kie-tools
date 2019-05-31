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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.bpmn2.ExtensionDefinition;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    protected Task serviceTask;

    @Mock
    protected ExtensionDefinition serviceTaskDef;

    @Mock
    protected FeatureMap attributes;

    @Mock
    protected FeatureMap.Entry businessRuleAttr;

    @Mock
    protected EStructuralFeature businessRuleFeature;

    @Mock
    protected View<NoneTask> noneTaskContent;

    protected NoneTask noneTaskDefinition;

    @Mock
    protected TaskPropertyReader taskPropertyReader;

    @Mock
    private Node<View<NoneTask>, Edge> noneTaskNode;

    @Mock
    private Node<View<org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask>, Edge> serviceTaskNode;

    @Mock
    private View<org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask> serviceTaskContent;

    private org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask serviceTaskDefinition;

    @Mock
    private ServiceTaskPropertyReader serviceTaskPropertyReader;

    @Before
    public void setUp() {
        noneTaskDefinition = new NoneTask();
        serviceTaskDefinition = new org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask();

        when(factoryManager.newNode(anyString(), eq(NoneTask.class))).thenReturn(noneTaskNode);
        when(noneTaskNode.getContent()).thenReturn(noneTaskContent);
        when(noneTaskContent.getDefinition()).thenReturn(noneTaskDefinition);

        when(factoryManager.newNode(anyString(), eq(org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask.class)))
                .thenReturn(serviceTaskNode);
        when(serviceTaskNode.getContent()).thenReturn(serviceTaskContent);
        when(serviceTaskContent.getDefinition()).thenReturn(serviceTaskDefinition);
        when(propertyReaderFactory.of(serviceTask)).thenReturn(taskPropertyReader);
        when(propertyReaderFactory.ofCustom(serviceTask)).thenReturn(Optional.of(serviceTaskPropertyReader));

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
    public void convertBusinessRuleServiceTask() {
        when(serviceTask.getExtensionDefinitions()).thenReturn(Arrays.asList(serviceTaskDef));
        when(serviceTaskDef.getName()).thenReturn(CustomAttribute.serviceTaskName.name());
        when(serviceTask.getAnyAttribute()).thenReturn(attributes);
        when(attributes.stream()).thenReturn(Stream.of(businessRuleAttr));
        when(businessRuleAttr.getEStructuralFeature()).thenReturn(businessRuleFeature);
        when(businessRuleFeature.getName()).thenReturn(CustomAttribute.serviceTaskName.name());
        when(serviceTask.getName()).thenReturn(CustomAttribute.serviceTaskName.name());
        when(businessRuleAttr.getValue()).thenReturn("BusinessRuleTask");

        final BpmnNode converted = tested.convert(serviceTask);
        assertNotEquals(converted.value(), noneTaskNode);
        assertEquals(converted.value(), serviceTaskNode);
    }
}
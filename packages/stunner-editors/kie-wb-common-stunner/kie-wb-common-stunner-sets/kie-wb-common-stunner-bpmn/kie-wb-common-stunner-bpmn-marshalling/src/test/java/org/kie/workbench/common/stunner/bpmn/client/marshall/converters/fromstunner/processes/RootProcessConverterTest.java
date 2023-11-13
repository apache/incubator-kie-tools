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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Process;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.FlowElementConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RootProcessConverterTest {

    private RootProcessConverter converter;
    private DefinitionsBuildingContext context;

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    @Mock
    private ConverterFactory converterFactory;

    @Mock
    private Node node;

    @Mock
    private Definition<BPMNDiagramImpl> content;

    private BPMNDiagramImpl diagram;

    @Mock
    private CaseManagementSet caseManagementSet;

    @Mock
    private CaseRoles caseRoles;

    @Mock
    private CaseFileVariables caseFileVariables;

    @Mock
    private ProcessPropertyWriter processPropertyWriter;

    @Mock
    private SubProcessConverter subProcessConverter;

    @Mock
    private FlowElementConverter viewDefinitionConverter;

    @Mock
    private LaneConverter laneConverter;

    @Mock
    private CaseIdPrefix caseIdPrefix;

    @Before
    public void setUp() throws Exception {
        diagram = new BPMNDiagramImpl();
        diagram.setDiagramSet(new DiagramSet());
        diagram.setCaseManagementSet(caseManagementSet);
        context = new DefinitionsBuildingContext(node, Stream.of(new AbstractMap.SimpleEntry("uuid", node)).collect(Collectors.toMap(Map.Entry<String, Node>::getKey, Map.Entry<String, Node>::getValue))).withRootNode(node);
        converter = new RootProcessConverter(context, propertyWriterFactory, converterFactory);

        when(propertyWriterFactory.of(any(Process.class))).thenReturn(processPropertyWriter);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(diagram);
        when(caseManagementSet.getCaseIdPrefix()).thenReturn(caseIdPrefix);
        when(caseManagementSet.getCaseRoles()).thenReturn(caseRoles);
        when(caseManagementSet.getCaseFileVariables()).thenReturn(caseFileVariables);
        when(converterFactory.subProcessConverter()).thenReturn(subProcessConverter);
        when(converterFactory.viewDefinitionConverter()).thenReturn(viewDefinitionConverter);
        when(converterFactory.laneConverter()).thenReturn(laneConverter);
    }

    @Test
    public void testFlowElementConverter() {
        final FlowElementConverter viewDefinitionConverter = new FlowElementConverter(converterFactory);
        NodeImpl<View<? extends BPMNViewDefinition>> n = new NodeImpl<>("n");
        EventSubprocess subProcessNode = new EventSubprocess();
        subProcessNode.getExecutionSet().setIsAsync(new IsAsync(true));
        n.setContent(new ViewImpl<>(subProcessNode, Bounds.create()));
        final Result<PropertyWriter> propertyWriterResult = viewDefinitionConverter.toFlowElement(n);
        assertTrue(propertyWriterResult.isIgnored());
    }

    @Test
    public void convertProcessWithCaseProperties() {
        final ProcessPropertyWriter propertyWriter = converter.convertProcess();
        verify(propertyWriter).setCaseIdPrefix(caseIdPrefix);
        verify(propertyWriter).setCaseRoles(caseRoles);
        verify(propertyWriter).setCaseFileVariables(caseFileVariables);
    }

    @Test
    public void convertProcessWithExecutable() {
        final ProcessPropertyWriter propertyWriter = converter.convertProcess();
        verify(propertyWriter).setExecutable(anyBoolean());
    }

    @Test
    public void convertProcessWithGlobalVariables() {
        final ProcessPropertyWriter propertyWriter = converter.convertProcess();
        verify(propertyWriter).setGlobalVariables(any(GlobalVariables.class));
    }

    @Test
    public void convertProcessWithSlaDueDate() {
        final ProcessPropertyWriter propertyWriter = converter.convertProcess();
        verify(propertyWriter).setSlaDueDate(any(SLADueDate.class));
    }
}
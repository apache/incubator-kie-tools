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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Process;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.FlowElementConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Before
    public void setUp() throws Exception {
        diagram = new BPMNDiagramImpl();
        diagram.setDiagramSet(new DiagramSet());
        diagram.setCaseManagementSet(caseManagementSet);
        context = new DefinitionsBuildingContext(node, Stream.of(new AbstractMap.SimpleEntry("uuid", node)).collect(Collectors.toMap(Map.Entry<String, Node>::getKey, Map.Entry<String, Node>::getValue))).withRootNode(node);
        converter = new RootProcessConverter(context, propertyWriterFactory, converterFactory);

        when(propertyWriterFactory.of(Matchers.any(Process.class))).thenReturn(processPropertyWriter);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(diagram);
        when(caseManagementSet.getCaseRoles()).thenReturn(caseRoles);
        when(caseManagementSet.getCaseFileVariables()).thenReturn(caseFileVariables);
        when(converterFactory.subProcessConverter()).thenReturn(subProcessConverter);
        when(converterFactory.viewDefinitionConverter()).thenReturn(viewDefinitionConverter);
        when(converterFactory.laneConverter()).thenReturn(laneConverter);
    }

    @Test
    public void convertProcessWithCaseRoles() {
        final ProcessPropertyWriter propertyWriter = converter.convertProcess();
        verify(propertyWriter).setCaseRoles(caseRoles);
        verify(propertyWriter).setCaseFileVariables(caseFileVariables);
    }
}
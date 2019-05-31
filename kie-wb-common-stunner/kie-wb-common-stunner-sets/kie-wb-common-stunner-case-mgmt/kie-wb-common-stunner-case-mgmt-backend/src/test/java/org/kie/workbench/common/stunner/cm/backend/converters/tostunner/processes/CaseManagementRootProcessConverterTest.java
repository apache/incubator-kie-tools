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

package org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes;

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.cm.backend.converters.tostunner.CaseManagementConverterFactory;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.cm.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaseManagementRootProcessConverterTest {

    private DefinitionResolver definitionResolver;

    private Process process;

    private CaseManagementRootProcessConverter tested;

    @Before
    public void setUp() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        process = bpmn2.createProcess();
        definitions.getRootElements().add(process);
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        Node node = new NodeImpl("");
        View<CaseManagementDiagram> content = new ViewImpl<>(new CaseManagementDiagram(), Bounds.create());
        node.setContent(content);

        FactoryManager factoryManager = mock(FactoryManager.class);
        when(factoryManager.newElement(anyString(), eq(CaseManagementDiagram.class))).thenReturn(node);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        tested = new CaseManagementRootProcessConverter(typedFactoryManager,
                                                        new PropertyReaderFactory(definitionResolver),
                                                        definitionResolver,
                                                        new CaseManagementConverterFactory(definitionResolver, typedFactoryManager));
    }

    @Test
    public void testCreateNode() throws Exception {
        assertTrue(CaseManagementDiagram.class.isInstance(tested.createNode("id").getContent().getDefinition()));
    }

    @Test
    public void testCreateDiagramSet() throws Exception {
        assertTrue(DiagramSet.class.isInstance(tested.createDiagramSet(process,
                                                                       new ProcessPropertyReader(process,
                                                                                                 definitionResolver.getDiagram(),
                                                                                                 definitionResolver.getShape(process.getId()),
                                                                                                 definitionResolver.getResolutionFactor()))));
    }

    @Test
    public void testCreateProcessData() throws Exception {
        assertTrue(ProcessData.class.isInstance(tested.createProcessData("id")));
    }
}
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

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.DefaultImportsElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.ElementDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.DefinitionsPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RootProcessConverterTest {

    private DefinitionResolver definitionResolver;

    private Process process;

    private RootProcessConverter tested;

    @Before
    @SuppressWarnings("all")
    public void setUp() {
        Definitions definitions = bpmn2.createDefinitions();
        process = bpmn2.createProcess();
        definitions.getRootElements().add(process);
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);
        ElementDefinition.getExtensionElements(process).add(DefaultImportsElement.extensionOf(new DefaultImport(getClass().getName())));

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        Node node = new NodeImpl("");
        View<BPMNDiagramImpl> content = new ViewImpl<>(new BPMNDiagramImpl(), Bounds.create());
        node.setContent(content);

        FactoryManager factoryManager = mock(FactoryManager.class);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(BPMNDiagramImpl.class)))).thenReturn(node);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        tested = new RootProcessConverter(typedFactoryManager,
                                          new PropertyReaderFactory(definitionResolver),
                                          definitionResolver,
                                          new ConverterFactory(definitionResolver, typedFactoryManager));
    }

    @Test
    public void testCreateNode() {
        assertTrue(tested.createNode("id").getContent().getDefinition() instanceof BPMNDiagramImpl);
    }

    @Test
    public void testCreateProcessData() {
        assertNotNull(tested.createProcessData("id"));
    }

    @Test
    public void testCreateDiagramSet() {
        DiagramSet diagramSet = createDiagramSet();
        assertNotNull(diagramSet);
    }

    @Test
    public void testImports() {
        DiagramSet diagramSet = createDiagramSet();
        Imports imports = diagramSet.getImports();
        assertNotNull(imports);
        ImportsValue importsValue = imports.getValue();
        assertNotNull(importsValue);
        List<DefaultImport> defaultImports = importsValue.getDefaultImports();
        assertNotNull(defaultImports);
        assertFalse(defaultImports.isEmpty());
        DefaultImport defaultImport = defaultImports.get(0);
        assertNotNull(defaultImport);
        assertEquals(getClass().getName(), defaultImport.getClassName());
    }

    private DiagramSet createDiagramSet() {
        return tested.createDiagramSet(process,
                                       new ProcessPropertyReader(process,
                                                                 definitionResolver.getDiagram(),
                                                                 definitionResolver.getShape(process.getId()),
                                                                 definitionResolver.getResolutionFactor()),
                                       new DefinitionsPropertyReader(definitionResolver.getDefinitions(),
                                                                     definitionResolver.getDiagram(),
                                                                     definitionResolver.getShape(process.getId()),
                                                                     definitionResolver.getResolutionFactor()));
    }
}
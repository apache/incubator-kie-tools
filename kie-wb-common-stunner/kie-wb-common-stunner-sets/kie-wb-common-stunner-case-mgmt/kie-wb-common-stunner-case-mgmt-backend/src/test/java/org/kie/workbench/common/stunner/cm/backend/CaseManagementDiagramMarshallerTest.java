/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2Marshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.marshall.json.CaseManagementMarshaller;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDiagramMarshallerTest {

    @Mock
    private XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;

    @Mock
    private GraphObjectBuilderFactory graphBuilderFactory;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private GraphIndexBuilder<?> indexBuilder;

    @Mock
    private OryxManager oryxManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private GraphCommandManager graphCommandManager;

    @Mock
    private GraphCommandFactory commandFactory;

    @Mock
    private RuleManager rulesManager;

    private CaseManagementDiagramMarshaller marshaller;

    @Before
    public void setup() {
        this.marshaller = new CaseManagementDiagramMarshaller(diagramMetadataMarshaller,
                                                              graphBuilderFactory,
                                                              definitionManager,
                                                              indexBuilder,
                                                              oryxManager,
                                                              factoryManager,
                                                              null, // TODO!
                                                              rulesManager,
                                                              graphCommandManager,
                                                              commandFactory);
    }

    @Test
    public void getDiagramDefinitionSetClass() {
        // It is important that CaseManagementDiagramMarshaller declares it relates to the CaseManagementDefinitionSet
        // otherwise all sorts of things break. This test attempts to drawer the importance of this to future changes
        // should someone decide to change the apparent innocuous method in CaseManagementDiagramMarshaller.
        assertEquals(CaseManagementDefinitionSet.class,
                     marshaller.getDiagramDefinitionSetClass());
    }

    @Test
    public void testUpdateTitle() throws Exception {
        final String name = "mockName";

        final Diagram mockDiagram = mockDiagram(name, null);

        final Metadata metadata = new MetadataImpl();
        marshaller.updateTitle(metadata, mockDiagram.getGraph());

        assertEquals(metadata.getTitle(), name);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testMarshall_validateName() throws Exception {
        final Diagram mockDiagram = mockDiagram(null, "mockId");

        exceptionRule.expect(RuntimeException.class);
        marshaller.marshall(mockDiagram);
    }

    @Test
    public void testMarshall_validateId() throws Exception {
        final Diagram mockDiagram = mockDiagram("mockName", null);

        exceptionRule.expect(RuntimeException.class);
        marshaller.marshall(mockDiagram);
    }

    private Diagram mockDiagram(final String name, final String id) {
        final DiagramSet diagramSet = mock(DiagramSet.class);
        when(diagramSet.getName()).thenReturn(new Name(name));
        when(diagramSet.getId()).thenReturn(new Id(id));

        final CaseManagementDiagram caseManagementDiagram = new CaseManagementDiagram();
        caseManagementDiagram.setDiagramSet(diagramSet);

        final Definition definition = mock(Definition.class);
        when(definition.getDefinition()).thenReturn(caseManagementDiagram);

        final Node node = mock(Node.class);
        when(node.getContent()).thenReturn(definition);

        final Graph mockGraph = mock(Graph.class);
        when(mockGraph.nodes()).thenReturn(Collections.singletonList(node));

        final Diagram mockDiagram = mock(Diagram.class);
        when(mockDiagram.getGraph()).thenReturn(mockGraph);

        return mockDiagram;
    }

    @Test
    public void testCreateBpmn2Marshaller() throws Exception {
        Bpmn2Marshaller bpmn2Marshaller = this.marshaller.createBpmn2Marshaller(mock(DefinitionManager.class),
                                                                                mock(OryxManager.class));
        assertTrue(bpmn2Marshaller instanceof CaseManagementMarshaller);
    }
}

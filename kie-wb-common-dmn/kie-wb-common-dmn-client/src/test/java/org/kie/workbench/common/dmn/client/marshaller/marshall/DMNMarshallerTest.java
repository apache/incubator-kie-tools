/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.marshall;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerTest {

    @Test
    public void testMergeOrAddNodeToDefinitions() {

        final JSITDecision existingNode1 = makeDecision("id1");
        final JSITDecision node1 = makeDecision("id1");
        final JSITBusinessKnowledgeModel node2 = makeBusinessKnowledgeModel("id2");
        final JSITKnowledgeSource node3 = makeKnowledgeSource("id3");
        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());

        final JSITDefinitions definitions = spy(new JSITDefinitions());
        final List<JSITDRGElement> definitionsDRGElements = new ArrayList<>(singletonList(existingNode1));

        doReturn(node1).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node1), any());
        doReturn(node2).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node2), any());
        doReturn(node3).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node3), any());
        doReturn(true).when(dmnMarshaller).instanceOfDecision(eq(node1));
        doReturn(true).when(dmnMarshaller).instanceOfBusinessKnowledgeModel(eq(node2));
        doReturn(true).when(dmnMarshaller).instanceOfKnowledgeSource(eq(node3));
        doReturn(definitionsDRGElements).when(definitions).getDrgElement();

        final List<JSITAuthorityRequirement> authorityRequirement = new ArrayList<>(singletonList(new JSITAuthorityRequirement()));
        final List<JSITInformationRequirement> informationRequirement = new ArrayList<>(singletonList(new JSITInformationRequirement()));
        final List<JSITKnowledgeRequirement> knowledgeRequirement = new ArrayList<>(singletonList(new JSITKnowledgeRequirement()));

        doReturn(authorityRequirement).when(node1).getAuthorityRequirement();
        doReturn(informationRequirement).when(node1).getInformationRequirement();
        doReturn(knowledgeRequirement).when(node1).getKnowledgeRequirement();

        dmnMarshaller.mergeOrAddNodeToDefinitions(node1, definitions);
        dmnMarshaller.mergeOrAddNodeToDefinitions(node2, definitions);
        dmnMarshaller.mergeOrAddNodeToDefinitions(node3, definitions);

        verify(definitions, never()).addDrgElement(node1);
        verify(definitions).addDrgElement(node2);
        verify(definitions).addDrgElement(node3);
        verify(existingNode1).addAllAuthorityRequirement(authorityRequirement.toArray(new JSITAuthorityRequirement[0]));
        verify(existingNode1).addAllInformationRequirement(informationRequirement.toArray(new JSITInformationRequirement[0]));
        verify(existingNode1).addAllKnowledgeRequirement(knowledgeRequirement.toArray(new JSITKnowledgeRequirement[0]));
    }

    @Test
    public void testGetExistingNode() {

        final JSITDecision nodeDRGElement = makeDecision("id1");
        final JSITDecision definitionsDRGElement1 = makeDecision("id1");
        final JSITDecision definitionsDRGElement2 = makeDecision("id2");
        final JSITDecision definitionsDRGElement3 = makeDecision("id3");
        final DMNMarshaller dmnMarshaller = new DMNMarshaller();

        final JSITDefinitions definitions = spy(new JSITDefinitions());
        final List<JSITDRGElement> definitionsDRGElements = new ArrayList<>(asList(definitionsDRGElement1, definitionsDRGElement2, definitionsDRGElement3));

        doReturn(definitionsDRGElements).when(definitions).getDrgElement();

        final Optional<JSITDRGElement> existingNode = dmnMarshaller.getExistingNode(definitions, nodeDRGElement);

        assertTrue(existingNode.isPresent());
        assertEquals(definitionsDRGElement1, existingNode.get());
    }

    @Test
    public void testWithIncludedModelsWhenNodeParentIsDMNDiagram() {

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final Node node = mock(Node.class);
        final Definition nodeDefinition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final Definitions definitionsStunnerPojo = mock(Definitions.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final List<Import> diagramImports = new ArrayList<>(asList(import1, import2));
        final DMNDiagram nodeDiagram = mock(DMNDiagram.class);
        final Definitions nodeDiagramDefinitions = mock(Definitions.class);
        final List<Import> nodeDiagramImports = new ArrayList<>();

        when(node.getContent()).thenReturn(nodeDefinition);
        when(nodeDefinition.getDefinition()).thenReturn(drgElement);
        when(definitionsStunnerPojo.getImport()).thenReturn(diagramImports);
        when(drgElement.getParent()).thenReturn(nodeDiagram);
        when(nodeDiagram.getDefinitions()).thenReturn(nodeDiagramDefinitions);
        when(nodeDiagramDefinitions.getImport()).thenReturn(nodeDiagramImports);

        dmnMarshaller.withIncludedModels(node, definitionsStunnerPojo);

        assertEquals(2, nodeDiagramImports.size());
        assertTrue(nodeDiagramImports.contains(import1));
        assertTrue(nodeDiagramImports.contains(import2));
    }

    @Test
    public void testWithIncludedModelsWhenNodeAlreadyHasImports() {

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final Node node = mock(Node.class);
        final Definition nodeDefinition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final Definitions definitionsStunnerPojo = mock(Definitions.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final List<Import> diagramImports = new ArrayList<>(asList(import1, import2));
        final DMNDiagram nodeDiagram = mock(DMNDiagram.class);
        final Definitions nodeDiagramDefinitions = mock(Definitions.class);
        final List<Import> nodeDiagramImports = new ArrayList<>(asList(import1, import2));

        when(node.getContent()).thenReturn(nodeDefinition);
        when(nodeDefinition.getDefinition()).thenReturn(drgElement);
        when(definitionsStunnerPojo.getImport()).thenReturn(diagramImports);
        when(drgElement.getParent()).thenReturn(nodeDiagram);
        when(nodeDiagram.getDefinitions()).thenReturn(nodeDiagramDefinitions);
        when(nodeDiagramDefinitions.getImport()).thenReturn(nodeDiagramImports);

        dmnMarshaller.withIncludedModels(node, definitionsStunnerPojo);

        assertEquals(2, nodeDiagramImports.size());
        assertTrue(nodeDiagramImports.contains(import1));
        assertTrue(nodeDiagramImports.contains(import2));
    }

    @Test
    public void testWithIncludedModelsWhenNodeParentIsDefinitions() {

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final Node node = mock(Node.class);
        final Definition nodeDefinition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final Definitions definitionsStunnerPojo = mock(Definitions.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final List<Import> diagramImports = new ArrayList<>(asList(import1, import2));
        final Definitions nodeDiagramDefinitions = mock(Definitions.class);
        final List<Import> nodeDiagramImports = new ArrayList<>();

        when(node.getContent()).thenReturn(nodeDefinition);
        when(nodeDefinition.getDefinition()).thenReturn(drgElement);
        when(definitionsStunnerPojo.getImport()).thenReturn(diagramImports);
        when(drgElement.getParent()).thenReturn(nodeDiagramDefinitions);
        when(nodeDiagramDefinitions.getImport()).thenReturn(nodeDiagramImports);

        dmnMarshaller.withIncludedModels(node, definitionsStunnerPojo);

        assertEquals(2, nodeDiagramImports.size());
        assertTrue(nodeDiagramImports.contains(import1));
        assertTrue(nodeDiagramImports.contains(import2));
    }

    private JSITDecision makeDecision(final String id) {
        final JSITDecision decision = spy(new JSITDecision());
        doReturn(id).when(decision).getId();
        return decision;
    }

    private JSITBusinessKnowledgeModel makeBusinessKnowledgeModel(final String id) {
        final JSITBusinessKnowledgeModel businessKnowledgeModel = spy(new JSITBusinessKnowledgeModel());
        doReturn(id).when(businessKnowledgeModel).getId();
        return businessKnowledgeModel;
    }

    private JSITKnowledgeSource makeKnowledgeSource(final String id) {
        final JSITKnowledgeSource knowledgeSource = spy(new JSITKnowledgeSource());
        doReturn(id).when(knowledgeSource).getId();
        return knowledgeSource;
    }
}
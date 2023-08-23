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
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.invocation.InvocationOnMock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerTest {

    @Test
    public void testMergeNodeRequirements() {

        final JSITDecision existingNode1 = makeDecision("id1");
        final JSITBusinessKnowledgeModel existingNode2 = makeBusinessKnowledgeModel("id2");
        final JSITKnowledgeSource existingNode3 = makeKnowledgeSource("id3");

        final JSITDecision node1 = makeDecision("id1");
        final JSITBusinessKnowledgeModel node2 = makeBusinessKnowledgeModel("id2");
        final JSITKnowledgeSource node3 = makeKnowledgeSource("id3");
        final JSITBusinessKnowledgeModel node4 = makeBusinessKnowledgeModel("id4");
        final JSITKnowledgeSource node5 = makeKnowledgeSource("id5");

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());

        final JSITAuthorityRequirement node1AuthorityRequirement = mock(JSITAuthorityRequirement.class);
        final JSITInformationRequirement node1InformationRequirement = mock(JSITInformationRequirement.class);
        final JSITKnowledgeRequirement node1KnowledgeRequirement = mock(JSITKnowledgeRequirement.class);
        final JSITAuthorityRequirement node2AuthorityRequirement = mock(JSITAuthorityRequirement.class);
        final JSITKnowledgeRequirement node2KnowledgeRequirement = mock(JSITKnowledgeRequirement.class);
        final JSITAuthorityRequirement node3AuthorityRequirement = mock(JSITAuthorityRequirement.class);

        final List<JSITAuthorityRequirement> node1ExistingAuthorityRequirement = new ArrayList<>();
        final List<JSITInformationRequirement> node1ExistingInformationRequirement = new ArrayList<>();
        final List<JSITKnowledgeRequirement> node1ExistingKnowledgeRequirement = new ArrayList<>();
        final List<JSITAuthorityRequirement> node2ExistingAuthorityRequirement = new ArrayList<>();
        final List<JSITKnowledgeRequirement> node2ExistingKnowledgeRequirement = new ArrayList<>();
        final List<JSITAuthorityRequirement> node3ExistingAuthorityRequirement = new ArrayList<>();

        doReturn(node1).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node1), any());
        doReturn(node2).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node2), any());
        doReturn(node3).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node3), any());
        doReturn(node4).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node4), any());
        doReturn(node5).when(dmnMarshaller).getWrappedJSITDRGElement(eq(node5), any());

        doReturn(true).when(dmnMarshaller).instanceOfDecision(eq(node1));
        doReturn(true).when(dmnMarshaller).instanceOfBusinessKnowledgeModel(eq(node2));
        doReturn(true).when(dmnMarshaller).instanceOfKnowledgeSource(eq(node3));
        doReturn(true).when(dmnMarshaller).instanceOfBusinessKnowledgeModel(eq(node4));
        doReturn(true).when(dmnMarshaller).instanceOfKnowledgeSource(eq(node5));

        doReturn(node1ExistingAuthorityRequirement).when(existingNode1).getAuthorityRequirement();
        doReturn(node1ExistingInformationRequirement).when(existingNode1).getInformationRequirement();
        doReturn(node1ExistingKnowledgeRequirement).when(existingNode1).getKnowledgeRequirement();
        doReturn(node2ExistingAuthorityRequirement).when(existingNode2).getAuthorityRequirement();
        doReturn(node2ExistingKnowledgeRequirement).when(existingNode2).getKnowledgeRequirement();
        doReturn(node3ExistingAuthorityRequirement).when(existingNode3).getAuthorityRequirement();

        doReturn(new ArrayList<>(singletonList(node1AuthorityRequirement))).when(node1).getAuthorityRequirement();
        doReturn(new ArrayList<>(singletonList(node1KnowledgeRequirement))).when(node1).getKnowledgeRequirement();
        doReturn(new ArrayList<>(singletonList(node1InformationRequirement))).when(node1).getInformationRequirement();
        doReturn(new ArrayList<>(singletonList(node2AuthorityRequirement))).when(node2).getAuthorityRequirement();
        doReturn(new ArrayList<>(singletonList(node2KnowledgeRequirement))).when(node2).getKnowledgeRequirement();
        doReturn(new ArrayList<>(singletonList(node3AuthorityRequirement))).when(node3).getAuthorityRequirement();

        doAnswer((e) -> setList(node1ExistingAuthorityRequirement, e)).when(existingNode1).setAuthorityRequirement(any());
        doAnswer((e) -> setList(node1ExistingInformationRequirement, e)).when(existingNode1).setInformationRequirement(any());
        doAnswer((e) -> setList(node1ExistingKnowledgeRequirement, e)).when(existingNode1).setKnowledgeRequirement(any());
        doAnswer((e) -> setList(node2ExistingAuthorityRequirement, e)).when(existingNode2).setAuthorityRequirement(any());
        doAnswer((e) -> setList(node2ExistingKnowledgeRequirement, e)).when(existingNode2).setKnowledgeRequirement(any());
        doAnswer((e) -> setList(node3ExistingAuthorityRequirement, e)).when(existingNode3).setAuthorityRequirement(any());

        doAnswer((e) -> addAll(node1ExistingAuthorityRequirement, e)).when(existingNode1).addAllAuthorityRequirement(any());
        doAnswer((e) -> addAll(node1ExistingInformationRequirement, e)).when(existingNode1).addAllInformationRequirement(any());
        doAnswer((e) -> addAll(node1ExistingKnowledgeRequirement, e)).when(existingNode1).addAllKnowledgeRequirement(any());
        doAnswer((e) -> addAll(node2ExistingAuthorityRequirement, e)).when(existingNode2).addAllAuthorityRequirement(any());
        doAnswer((e) -> addAll(node2ExistingKnowledgeRequirement, e)).when(existingNode2).addAllKnowledgeRequirement(any());
        doAnswer((e) -> addAll(node3ExistingAuthorityRequirement, e)).when(existingNode3).addAllAuthorityRequirement(any());

        dmnMarshaller.mergeNodeRequirements(node1, existingNode1);
        dmnMarshaller.mergeNodeRequirements(node2, existingNode2);
        dmnMarshaller.mergeNodeRequirements(node3, existingNode3);
        dmnMarshaller.mergeNodeRequirements(node4, existingNode2);
        dmnMarshaller.mergeNodeRequirements(node5, existingNode3);

        // Merge twice. But the values must be added once.
        dmnMarshaller.mergeNodeRequirements(node1, existingNode1);
        dmnMarshaller.mergeNodeRequirements(node2, existingNode2);
        dmnMarshaller.mergeNodeRequirements(node3, existingNode3);
        dmnMarshaller.mergeNodeRequirements(node4, existingNode2);
        dmnMarshaller.mergeNodeRequirements(node5, existingNode3);

        assertEquals(1, node1ExistingAuthorityRequirement.size());
        assertEquals(1, node1ExistingInformationRequirement.size());
        assertEquals(1, node1ExistingKnowledgeRequirement.size());
        assertEquals(1, node2ExistingAuthorityRequirement.size());
        assertEquals(1, node2ExistingKnowledgeRequirement.size());
        assertEquals(1, node3ExistingAuthorityRequirement.size());

        assertEquals(node1AuthorityRequirement, node1ExistingAuthorityRequirement.get(0));
        assertEquals(node1InformationRequirement, node1ExistingInformationRequirement.get(0));
        assertEquals(node1KnowledgeRequirement, node1ExistingKnowledgeRequirement.get(0));
        assertEquals(node2AuthorityRequirement, node2ExistingAuthorityRequirement.get(0));
        assertEquals(node2KnowledgeRequirement, node2ExistingKnowledgeRequirement.get(0));
        assertEquals(node3AuthorityRequirement, node3ExistingAuthorityRequirement.get(0));
    }

    @Test
    public void testGetExistingNode() {

        final JSITDecision nodeDRGElement = makeDecision("id1");
        final JSITDecision definitionsDRGElement1 = makeDecision("id1");
        final JSITDecision definitionsDRGElement2 = makeDecision("id2");
        final JSITDecision definitionsDRGElement3 = makeDecision("id3");
        final DMNMarshaller dmnMarshaller = new DMNMarshaller();

        final JSITDefinitions definitions = mock(JSITDefinitions.class);
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

    @Test
    public void testConnect() {
        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        final List<String> dmnDiagramElementIds = mock(List.class);
        final Definitions definitionsStunnerPojo = mock(Definitions.class);
        final List<JSIDMNEdge> dmnEdges = mock(List.class);
        final JSIDMNEdge jsiEdge = mock(JSIDMNEdge.class);

        final Node<?, ?> node = mock(Node.class);
        final List inEdges = new ArrayList<>();
        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final View sourceView = mock(View.class);
        final ViewConnector viewConnector = mock(ViewConnector.class);
        final DiscreteConnection sourceConnection = mock(DiscreteConnection.class);
        final DiscreteConnection targetConnection = mock(DiscreteConnection.class);
        final View<?> view = mock(View.class);

        inEdges.add(edge);
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(sourceNode.getContent()).thenReturn(sourceView);

        doReturn(jsiEdge).when(dmnMarshaller).newJSIDMNEdgeInstance();
        doReturn(mock(JSIPoint.class)).when(dmnMarshaller).point2dToDMNDIPoint(any(Point2D.class));
        doNothing().when(jsiEdge).addWaypoint(any());

        when(node.getInEdges()).thenReturn(inEdges);
        when(edge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getControlPoints()).thenReturn(new ControlPoint[]{});
        when(sourceConnection.isAuto()).thenReturn(true);
        when(targetConnection.isAuto()).thenReturn(true);
        when(diagram.getName()).thenReturn("dmnEdge");
        when(definitionsStunnerPojo.getDefaultNamespace()).thenReturn("org.edge");

        when(viewConnector.getSourceConnection()).thenReturn(Optional.of(sourceConnection));
        when(viewConnector.getTargetConnection()).thenReturn(Optional.of(targetConnection));
        dmnMarshaller.connect(diagram,
                              dmnDiagramElementIds,
                              definitionsStunnerPojo,
                              dmnEdges,
                              node,
                              view);

        when(viewConnector.getSourceConnection()).thenReturn(Optional.empty());
        when(viewConnector.getTargetConnection()).thenReturn(Optional.empty());
        dmnMarshaller.connect(diagram,
                              dmnDiagramElementIds,
                              definitionsStunnerPojo,
                              dmnEdges,
                              node,
                              view);

        when(viewConnector.getSourceConnection()).thenReturn(Optional.of(sourceConnection));
        when(viewConnector.getTargetConnection()).thenReturn(Optional.empty());
        dmnMarshaller.connect(diagram,
                              dmnDiagramElementIds,
                              definitionsStunnerPojo,
                              dmnEdges,
                              node,
                              view);

        verify(sourceConnection).isAuto();
        verify(targetConnection).isAuto();
    }

    @Test
    public void testAddNodeToDefinitionsIfNotPresentWhenNodeIsPresent() {

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final Optional<JSITDRGElement> existingNode = Optional.of(mock(JSITDRGElement.class));
        final JSITDefinitions definitions = mock(JSITDefinitions.class);
        final JSITDRGElement node = mock(JSITDRGElement.class);

        doReturn(existingNode).when(dmnMarshaller).getExistingNode(definitions, node);
        doNothing().when(dmnMarshaller).addNodeToDefinitions(node, definitions);

        dmnMarshaller.addNodeToDefinitionsIfNotPresent(node, definitions);

        verify(dmnMarshaller, never()).addNodeToDefinitions(node, definitions);
    }

    @Test
    public void testAddNodeToDefinitionsIfNotPresentWhenNodeIsNotPresent() {

        final DMNMarshaller dmnMarshaller = spy(new DMNMarshaller());
        final Optional<JSITDRGElement> existingNode = Optional.empty();
        final JSITDefinitions definitions = mock(JSITDefinitions.class);
        final JSITDRGElement node = mock(JSITDRGElement.class);

        doReturn(existingNode).when(dmnMarshaller).getExistingNode(definitions, node);
        doNothing().when(dmnMarshaller).addNodeToDefinitions(node, definitions);

        dmnMarshaller.addNodeToDefinitionsIfNotPresent(node, definitions);

        verify(dmnMarshaller).addNodeToDefinitions(node, definitions);
    }

    private JSITDecision makeDecision(final String id) {
        final JSITDecision decision = mock(JSITDecision.class);
        doReturn(id).when(decision).getId();
        return decision;
    }

    private JSITBusinessKnowledgeModel makeBusinessKnowledgeModel(final String id) {
        final JSITBusinessKnowledgeModel businessKnowledgeModel = mock(JSITBusinessKnowledgeModel.class);
        doReturn(id).when(businessKnowledgeModel).getId();
        return businessKnowledgeModel;
    }

    private JSITKnowledgeSource makeKnowledgeSource(final String id) {
        final JSITKnowledgeSource knowledgeSource = mock(JSITKnowledgeSource.class);
        doReturn(id).when(knowledgeSource).getId();
        return knowledgeSource;
    }

    @SuppressWarnings("unchecked")
    private <T> boolean setList(final List<T> list,
                                final InvocationOnMock e) {
        final List<T> argument = new ArrayList<>((ArrayList<T>) e.getArguments()[0]);
        list.clear();
        list.addAll(argument);
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T> boolean addAll(final List<T> list,
                               final InvocationOnMock e) {
        final Object argument = e.getArguments()[0];
        list.add((T) argument);
        return true;
    }
}

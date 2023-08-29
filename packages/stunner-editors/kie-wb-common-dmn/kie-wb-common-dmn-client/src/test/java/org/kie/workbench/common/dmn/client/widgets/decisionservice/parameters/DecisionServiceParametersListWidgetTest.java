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

package org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceParametersList;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.parametergroup.ParameterGroup;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_EncapsulatedDecisions;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_Inputs;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionServiceParameters_Outputs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionServiceParametersListWidgetTest {

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private Elemental2DomUtil util;

    @Mock
    private HTMLDivElement container;

    @Mock
    private ParameterGroup groupEncapsulated;

    @Mock
    private ParameterGroup groupOutputs;

    @Mock
    private ParameterGroup groupInputs;

    private DecisionServiceParametersListWidget widget;

    @Before
    public void setup() {

        widget = spy(new DecisionServiceParametersListWidget(translationService,
                                                             util,
                                                             dmnDiagramsSession,
                                                             groupEncapsulated,
                                                             groupOutputs,
                                                             groupInputs,
                                                             container));
    }

    @Test
    public void testSetup() {

        final String encapsulatedDecisions = "Encapsulated Decisions";
        final String outputs = "Outputs";
        final String inputs = "Inputs";

        when(translationService.getValue(DecisionServiceParameters_EncapsulatedDecisions)).thenReturn(encapsulatedDecisions);
        when(translationService.getValue(DecisionServiceParameters_Outputs)).thenReturn(outputs);
        when(translationService.getValue(DecisionServiceParameters_Inputs)).thenReturn(inputs);

        widget.setup();

        verify(groupEncapsulated).setHeader(encapsulatedDecisions);
        verify(groupInputs).setHeader(inputs);
        verify(groupOutputs).setHeader(outputs);
    }

    @Test
    public void testSetValue() {

        doNothing().when(widget).refresh();

        final DecisionServiceParametersList value = mock(DecisionServiceParametersList.class);

        widget.setValue(value, false);

        verify(widget).refresh();
    }

    @Test
    public void testRefresh() {

        final DecisionServiceParametersList value = mock(DecisionServiceParametersList.class);
        final DecisionService decisionService = mock(DecisionService.class);
        final String contentDefinitionId = "contentId";
        final Node node = mock(Node.class);
        final Edge edge1 = mock(Edge.class);
        final Child child1 = mock(Child.class);
        final Node targetNode1 = mock(Node.class);
        final Edge edge2 = mock(Edge.class);
        final Child child2 = mock(Child.class);
        final Node targetNode2 = mock(Node.class);
        final Edge edge3 = mock(Edge.class);
        final Child child3 = mock(Child.class);
        final Node targetNode3 = mock(Node.class);
        final Edge edgeNotChild = mock(Edge.class);
        final Object notChild = mock(Object.class);
        final List<Edge> edges = Arrays.asList(edge1, edge2, edge3, edgeNotChild);
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);
        final Optional<DRGElement> targetDrg1 = Optional.of(drgElement1);
        final Optional<DRGElement> targetDrg2 = Optional.of(drgElement2);
        final Optional<DRGElement> targetDrg3 = Optional.of(drgElement3);
        final List<InputData> sortedList = new ArrayList<>();

        when(decisionService.getContentDefinitionId()).thenReturn(contentDefinitionId);
        when(value.getDecisionService()).thenReturn(decisionService);
        when(edge1.getContent()).thenReturn(child1);
        when(edge1.getTargetNode()).thenReturn(targetNode1);
        when(edge2.getContent()).thenReturn(child2);
        when(edge2.getTargetNode()).thenReturn(targetNode2);
        when(edge3.getContent()).thenReturn(child3);
        when(edge3.getTargetNode()).thenReturn(targetNode3);
        when(edgeNotChild.getContent()).thenReturn(notChild);
        when(node.getOutEdges()).thenReturn(edges);

        doNothing().when(widget).clear();
        doNothing().when(widget).loadGroupsElements();
        doNothing().when(widget).loadInputsParameters(anyList());
        doNothing().when(widget).loadDecisionsFromNode(any(), any());
        doNothing().when(widget).loadInputsFromNode(any(), any());
        doNothing().when(widget).loadInputsFromOthersDiagrams(anyList(), any(Node.class));
        doReturn(value).when(widget).getValue();
        doReturn(node).when(widget).getNode(contentDefinitionId);
        doReturn(targetDrg1).when(widget).getTargetDRGElement(edge1);
        doReturn(targetDrg2).when(widget).getTargetDRGElement(edge2);
        doReturn(targetDrg3).when(widget).getTargetDRGElement(edge3);
        doReturn(Optional.empty()).when(widget).getTargetDRGElement(edgeNotChild);
        doReturn(sortedList).when(widget).getSortedInputs(anyList());

        widget.refresh();

        verify(widget).loadDecisionsFromNode(node, targetNode1);
        verify(widget).loadDecisionsFromNode(node, targetNode2);
        verify(widget).loadDecisionsFromNode(node, targetNode3);
        verify(widget).loadInputsFromNode(anyList(), eq(targetNode1));
        verify(widget).loadInputsFromNode(anyList(), eq(targetNode2));
        verify(widget).loadInputsFromNode(anyList(), eq(targetNode3));
        verify(widget).loadInputsFromOthersDiagrams(anyList(), eq(targetNode1));
        verify(widget).loadInputsFromOthersDiagrams(anyList(), eq(targetNode2));
        verify(widget).loadInputsFromOthersDiagrams(anyList(), eq(targetNode3));
        verify(widget).getSortedInputs(anyList());
        verify(widget).loadInputsParameters(sortedList);
        verify(widget).loadGroupsElements();
    }

    @Test
    public void testGetSortedInputs() {

        final DecisionServiceParametersList value = mock(DecisionServiceParametersList.class);
        final DecisionService decisionService = mock(DecisionService.class);
        final String id1 = "id1";
        final String id2 = "id2";
        final String id3 = "id3";
        final String newId1 = "newId1";
        final String newId2 = "newId2";

        final List<DMNElementReference> list = createListOfDMNElementReferenceWithIds(
                id1,
                id2,
                id3);
        final List<InputData> unsortedInputs = Arrays.asList(createInputDataWithId(id2),
                                                             createInputDataWithId(id3),
                                                             createInputDataWithId(newId2),
                                                             createInputDataWithId(id1),
                                                             createInputDataWithId(newId1));

        doReturn(value).when(widget).getValue();

        when(value.getDecisionService()).thenReturn(decisionService);
        when(decisionService.getInputData()).thenReturn(list);

        final List<InputData> sorted = widget.getSortedInputs(unsortedInputs);

        assertThat(sorted)
                .extracting(inputData -> inputData.getId().getValue())
                .containsExactly(id1, id2, id3, newId1, newId2);
    }

    @Test
    public void testGetCurrentItems() {

        final DecisionServiceParametersList value = mock(DecisionServiceParametersList.class);
        final DecisionService decisionService = mock(DecisionService.class);
        final String id1 = "id1";
        final String id2 = "id2";
        final String id3 = "id3";

        final List<DMNElementReference> list = createListOfDMNElementReferenceWithIds(
                id1,
                id2,
                id3);
        final List<InputData> unsortedInputs = Arrays.asList(createInputDataWithId(id2),
                                                             createInputDataWithId(id3),
                                                             createInputDataWithId(id1));

        doReturn(value).when(widget).getValue();

        when(value.getDecisionService()).thenReturn(decisionService);
        when(decisionService.getInputData()).thenReturn(list);

        final List<InputData> sorted = widget.getCurrentItems(unsortedInputs);

        assertThat(sorted)
                .extracting(inputData -> inputData.getId().getValue())
                .containsExactly(id1, id2, id3);
    }

    @Test
    public void testGetNewItems() {

        final DecisionServiceParametersList value = mock(DecisionServiceParametersList.class);
        final DecisionService decisionService = mock(DecisionService.class);
        final String id1 = "id1";
        final String id2 = "id2";
        final String id3 = "id3";
        final String newItem1 = "aaa";
        final String newItem2 = "bbb";
        final String newItem3 = "ccc";
        final String newItem4 = "ddd";

        final List<DMNElementReference> list = createListOfDMNElementReferenceWithIds(
                id1,
                id2,
                id3);
        final List<InputData> currentItems = Arrays.asList(createInputDataWithId(id2),
                                                           createInputDataWithId(id3),
                                                           createInputDataWithId(id1));

        final List<InputData> inputs = Arrays.asList(createInputDataWithId(newItem3),
                                                     createInputDataWithId(newItem2),
                                                     createInputDataWithId(newItem4),
                                                     createInputDataWithId(newItem1));

        doReturn(value).when(widget).getValue();

        when(value.getDecisionService()).thenReturn(decisionService);
        when(decisionService.getInputData()).thenReturn(list);

        final List<InputData> result = widget.getNewItems(inputs, currentItems);

        assertThat(result)
                .extracting(inputData -> inputData.getId().getValue())
                .containsExactly(newItem1, newItem2, newItem3, newItem4);
    }

    private InputData createInputDataWithId(final String id) {
        final InputData inputData = new InputData();
        inputData.getId().setValue(id);
        inputData.getName().setValue(id);
        return inputData;
    }

    private List<DMNElementReference> createListOfDMNElementReferenceWithIds(final String... ids) {
        final List<DMNElementReference> list = new ArrayList<>();
        for (final String id : ids) {
            final DMNElementReference reference = new DMNElementReference();
            reference.setHref("#" + id);
            list.add(reference);
        }

        return list;
    }

    @Test
    public void testLoadDecisionsFromNode_WhenIsOutputDecision() {

        final Node node = mock(Node.class);
        final Node targetNode = mock(Node.class);
        final View targetContent = mock(View.class);
        final View nodeContent = mock(View.class);
        when(node.getContent()).thenReturn(nodeContent);
        when(targetNode.getContent()).thenReturn(targetContent);
        doReturn(true).when(widget).isOutputDecision(targetContent, nodeContent);
        doNothing().when(widget).addDecisionNodeToGroup(groupOutputs, targetNode);
        doNothing().when(widget).addDecisionNodeToGroup(groupEncapsulated, targetNode);

        widget.loadDecisionsFromNode(node, targetNode);

        verify(widget).isOutputDecision(targetContent, nodeContent);

        verify(widget).addDecisionNodeToGroup(groupOutputs, targetNode);
        verify(widget, never()).addDecisionNodeToGroup(groupEncapsulated, targetNode);
    }

    @Test
    public void testLoadDecisionsFromNode_WhenIsEncapsulatedDecision() {

        final Node node = mock(Node.class);
        final Node targetNode = mock(Node.class);
        final View targetContent = mock(View.class);
        final View nodeContent = mock(View.class);
        when(node.getContent()).thenReturn(nodeContent);
        when(targetNode.getContent()).thenReturn(targetContent);
        doReturn(false).when(widget).isOutputDecision(targetContent, nodeContent);
        doNothing().when(widget).addDecisionNodeToGroup(groupOutputs, targetNode);
        doNothing().when(widget).addDecisionNodeToGroup(groupEncapsulated, targetNode);

        widget.loadDecisionsFromNode(node, targetNode);

        verify(widget).isOutputDecision(targetContent, nodeContent);

        verify(widget, never()).addDecisionNodeToGroup(groupOutputs, targetNode);
        verify(widget).addDecisionNodeToGroup(groupEncapsulated, targetNode);
    }

    @Test
    public void testLoadGroupElements() {

        final HTMLElement inputElement = mock(HTMLElement.class);
        final HTMLElement outputElement = mock(HTMLElement.class);
        final HTMLElement encapsulatedElement = mock(HTMLElement.class);

        when(groupInputs.getElement()).thenReturn(inputElement);
        when(groupOutputs.getElement()).thenReturn(outputElement);
        when(groupEncapsulated.getElement()).thenReturn(encapsulatedElement);

        final elemental2.dom.HTMLElement htmlInput = mock(elemental2.dom.HTMLElement.class);
        final elemental2.dom.HTMLElement htmlOutput = mock(elemental2.dom.HTMLElement.class);
        final elemental2.dom.HTMLElement htmlEncapsulated = mock(elemental2.dom.HTMLElement.class);

        when(util.asHTMLElement(inputElement)).thenReturn(htmlInput);
        when(util.asHTMLElement(outputElement)).thenReturn(htmlOutput);
        when(util.asHTMLElement(encapsulatedElement)).thenReturn(htmlEncapsulated);

        widget.loadGroupsElements();

        verify(container).appendChild(htmlInput);
        verify(container).appendChild(htmlOutput);
        verify(container).appendChild(htmlEncapsulated);
    }

    @Test
    public void testLoadInputParameters() {

        final String name1 = "name1";
        final String type1 = "type1";
        final String name2 = "name2";
        final String type2 = "type2";
        final String name3 = "name3";
        final String type3 = "type3";
        final InputData input1 = createInputData(name1, type1);
        final InputData input2 = createInputData(name2, type2);
        final InputData input3 = createInputData(name3, type3);
        final List<InputData> inputs = Arrays.asList(input1, input2, input3);

        widget.loadInputsParameters(inputs);

        verify(groupInputs).addParameter(name1, type1);
        verify(groupInputs).addParameter(name2, type2);
        verify(groupInputs).addParameter(name3, type3);
    }

    @Test
    public void testGetTargetDRGElement_WhenTargetIsDRGElement() {

        final Edge edge = mock(Edge.class);
        final Node targetNode = mock(Node.class);
        final View targetNodeView = mock(View.class);
        final DRGElement drgElement = mock(DRGElement.class);

        when(edge.getTargetNode()).thenReturn(targetNode);
        when(targetNode.getContent()).thenReturn(targetNodeView);
        when(targetNodeView.getDefinition()).thenReturn(drgElement);

        final Optional<DRGElement> actual = widget.getTargetDRGElement(edge);

        assertTrue(actual.isPresent());
        assertEquals(drgElement, actual.get());
    }

    @Test
    public void testGetTargetDRGElement_WhenTargetIsNotDRGElement() {

        final Edge edge = mock(Edge.class);
        final Node targetNode = mock(Node.class);
        final View targetNodeView = mock(View.class);
        final Object obj = mock(Object.class);

        when(edge.getTargetNode()).thenReturn(targetNode);
        when(targetNode.getContent()).thenReturn(targetNodeView);
        when(targetNodeView.getDefinition()).thenReturn(obj);

        final Optional<DRGElement> actual = widget.getTargetDRGElement(edge);

        assertFalse(actual.isPresent());
    }

    @Test
    public void testAddDecisionNodeToGroup() {

        final ParameterGroup group = mock(ParameterGroup.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Decision decision = mock(Decision.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final String decisionName = "decision name";
        final String type = "type";
        final QName typeRef = mock(QName.class);

        when(definition.getDefinition()).thenReturn(decision);
        when(node.getContent()).thenReturn(definition);
        when(decision.getVariable()).thenReturn(variable);
        when(decision.getName()).thenReturn(new Name(decisionName));
        when(typeRef.getLocalPart()).thenReturn(type);
        when(variable.getTypeRef()).thenReturn(typeRef);

        widget.addDecisionNodeToGroup(group, node);

        verify(group).addParameter(decisionName, type);
    }

    @Test
    public void testGetInputs() {

        final Node targetNode = mock(Node.class);
        final Edge e1 = mock(Edge.class);
        final Edge e2 = mock(Edge.class);
        final Edge e3 = mock(Edge.class);

        final List<Edge> inEdges = Arrays.asList(e1, e2, e3);

        when(targetNode.getInEdges()).thenReturn(inEdges);

        final InputData input1 = createInputData("1", "type1");
        final InputData input2 = createInputData("2", "type2");
        final InputData input3 = createInputData("3", "type3");

        final Optional<InputData> nodeInputData1 = Optional.of(input1);
        final Optional<InputData> nodeInputData2 = Optional.of(input2);
        final Optional<InputData> nodeInputData3 = Optional.of(input3);

        doReturn(nodeInputData1).when(widget).getSourceNodeInputData(e1);
        doReturn(nodeInputData2).when(widget).getSourceNodeInputData(e2);
        doReturn(nodeInputData3).when(widget).getSourceNodeInputData(e3);

        final List<InputData> inputData = widget.getInputs(targetNode);

        assertEquals(3, inputData.size());
        assertTrue(inputData.contains(input1));
        assertTrue(inputData.contains(input2));
        assertTrue(inputData.contains(input3));
    }

    @Test
    public void testGetSourceNodeInputData() {

        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final View view = mock(View.class);
        final InputData inputData = mock(InputData.class);

        when(view.getDefinition()).thenReturn(inputData);
        when(sourceNode.getContent()).thenReturn(view);
        when(edge.getSourceNode()).thenReturn(sourceNode);

        final Optional<InputData> actualInputData = widget.getSourceNodeInputData(edge);

        assertTrue(actualInputData.isPresent());
        assertEquals(inputData, actualInputData.get());
    }

    @Test
    public void testGetSourceNodeInputData_WhenContentIsNotView() {

        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final Object obj = mock(Object.class);

        when(sourceNode.getContent()).thenReturn(obj);
        when(edge.getSourceNode()).thenReturn(sourceNode);

        final Optional<InputData> actualInputData = widget.getSourceNodeInputData(edge);

        assertFalse(actualInputData.isPresent());
    }

    @Test
    public void testGetSourceNodeInputData_WhenDoesNotHaveInputData() {

        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final View view = mock(View.class);
        final DRGElement anotherDrgElement = mock(DRGElement.class);

        when(view.getDefinition()).thenReturn(anotherDrgElement);
        when(sourceNode.getContent()).thenReturn(view);
        when(edge.getSourceNode()).thenReturn(sourceNode);

        final Optional<InputData> actualInputData = widget.getSourceNodeInputData(edge);

        assertFalse(actualInputData.isPresent());
    }

    @Test
    public void testGetSourceNodeInputData_WhenDefinitionIsNotDRGElement() {

        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final View view = mock(View.class);
        final Object anotherObject = mock(Object.class);

        when(view.getDefinition()).thenReturn(anotherObject);
        when(sourceNode.getContent()).thenReturn(view);
        when(edge.getSourceNode()).thenReturn(sourceNode);

        final Optional<InputData> actualInputData = widget.getSourceNodeInputData(edge);

        assertFalse(actualInputData.isPresent());
    }

    @Test
    public void testIsOutputDecision_WhenIsOutput() {
        testOutputDecision(99, 100, true);
    }

    @Test
    public void testIsOutputDecision_WhenIsNotOutput() {
        testOutputDecision(101, 100, false);
    }

    @Test
    public void testGetElementWithContentId() {

        final String id1 = "id1";
        final String id2 = "id2";
        final Node node1 = createNodeWithContentDefinitionId(id1);
        final Node node2 = createNodeWithContentDefinitionId(id2);
        final Stream<Node> stream = Arrays.asList(node1, node2).stream();

        final Node actual = widget.getElementWithContentId(id1, stream);

        assertEquals(node1, actual);
    }

    @Test
    public void testLoadInputsFromOtherDiagrams() {

        final String id = "theId";
        final Node node1 = createNodeWithContentDefinitionId(id);
        final Node node2 = createNodeWithContentDefinitionId(id);
        final Node node3 = createNodeWithContentDefinitionId(id);
        final List<Node> list = Arrays.asList(node1, node2, node3);
        final List<InputData> inputs = new ArrayList<>();
        final Node targetNode = createNodeWithContentDefinitionId(id);

        doNothing().when(widget).loadInputsFromNode(eq(inputs), any(Node.class));

        when(dmnDiagramsSession.getNodesFromAllDiagramsWithContentId(id)).thenReturn(list);

        widget.loadInputsFromOthersDiagrams(inputs, targetNode);

        verify(widget).loadInputsFromNode(inputs, node1);
        verify(widget).loadInputsFromNode(inputs, node2);
        verify(widget).loadInputsFromNode(inputs, node3);
    }

    private Node createNodeWithContentDefinitionId(final String contentDefinitionId) {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        when(drgElement.getContentDefinitionId()).thenReturn(contentDefinitionId);
        when(definition.getDefinition()).thenReturn(drgElement);
        when(node.getContent()).thenReturn(definition);
        return node;
    }

    @Test(expected = IllegalStateException.class)
    public void testGetElementWithContentId_WhenContentIsNotFound() {

        final String id1 = "id1";
        final String id2 = "id2";
        final Node node1 = createNodeWithContentDefinitionId(id1);
        final Node node2 = createNodeWithContentDefinitionId(id2);
        final Stream<Node> stream = Arrays.asList(node1, node2).stream();
        widget.getElementWithContentId("not found id", stream);
    }

    private void testOutputDecision(final double y,
                                    final double dividerLineValue,
                                    final boolean expected) {

        final View<?> childView = mock(View.class);
        final View<DecisionService> decisionServiceView = mock(View.class);
        final Bounds bounds = mock(Bounds.class);
        final Bound upperLeft = mock(Bound.class);

        when(upperLeft.getY()).thenReturn(y);
        when(bounds.getUpperLeft()).thenReturn(upperLeft);
        when(childView.getBounds()).thenReturn(bounds);

        final DecisionService definition = mock(DecisionService.class);
        final DecisionServiceDividerLineY dividerLineY = mock(DecisionServiceDividerLineY.class);

        when(dividerLineY.getValue()).thenReturn(dividerLineValue);
        when(definition.getDividerLineY()).thenReturn(dividerLineY);
        when(decisionServiceView.getDefinition()).thenReturn(definition);

        final boolean actual = widget.isOutputDecision(childView, decisionServiceView);

        assertEquals(expected, actual);
    }

    private InputData createInputData(final String name,
                                      final String type) {

        final InputData input = mock(InputData.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName typeRef = mock(QName.class);

        when(input.getVariable()).thenReturn(variable);
        when(input.getName()).thenReturn(new Name(name));
        when(typeRef.getLocalPart()).thenReturn(type);
        when(variable.getTypeRef()).thenReturn(typeRef);

        return input;
    }
}


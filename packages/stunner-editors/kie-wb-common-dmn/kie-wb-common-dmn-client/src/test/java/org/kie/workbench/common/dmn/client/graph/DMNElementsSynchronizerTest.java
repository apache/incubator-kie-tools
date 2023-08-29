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

package org.kie.workbench.common.dmn.client.graph;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.session.NodeTextSetter;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNElementsSynchronizerTest {

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private EventSourceMock<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private DMNGraphUtils graphUtils;

    @Mock
    private NodeTextSetter nodeTextSetter;

    private DMNElementsSynchronizer synchronizer;

    @Before
    public void setup() {
        synchronizer = spy(new DMNElementsSynchronizer(dmnDiagramsSession,
                                                       refreshDecisionComponentsEvent,
                                                       graphUtils,
                                                       nodeTextSetter));
    }

    @Test
    public void testOnExpressionEditorChanged() {
        final ExpressionEditorChanged event = mock(ExpressionEditorChanged.class);
        final String id = "id";
        final Node node = mock(Node.class);
        final Optional<Node> nodeOptional = Optional.of(node);

        when(event.getNodeUUID()).thenReturn(id);
        doReturn(nodeOptional).when(synchronizer).getNode(id);
        doNothing().when(synchronizer).synchronizeFromNode(nodeOptional);

        synchronizer.onExpressionEditorChanged(event);

        verify(synchronizer).getNode(id);
        verify(synchronizer).synchronizeFromNode(nodeOptional);
    }

    @Test
    public void testOnPropertyChanged() {
        final FormFieldChanged event = mock(FormFieldChanged.class);
        final String id = "id";
        final Node node = mock(Node.class);
        final Optional<Node> nodeOptional = Optional.of(node);

        when(event.getUuid()).thenReturn(id);
        doReturn(nodeOptional).when(synchronizer).getNode(id);
        doNothing().when(synchronizer).synchronizeFromNode(nodeOptional);

        synchronizer.onPropertyChanged(event);

        verify(synchronizer).getNode(id);
        verify(synchronizer).synchronizeFromNode(nodeOptional);
    }

    @Test
    public void testSynchronizeElementsFrom() {

        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final List<Node> nodes = Arrays.asList(node1, node2, node3);
        final DRGElement drgElement = mock(DRGElement.class);
        final String contentDefinitionId = "id";
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);

        when(drgElement.getContentDefinitionId()).thenReturn(contentDefinitionId);
        doNothing().when(synchronizer).updateText(any(DRGElement.class), any(Node.class));
        doReturn(nodes).when(synchronizer).getElementsWithContentId(contentDefinitionId);
        doReturn(drgElement1).when(synchronizer).getDRGElementFromContentDefinition(node1);
        doReturn(drgElement2).when(synchronizer).getDRGElementFromContentDefinition(node2);
        doReturn(drgElement3).when(synchronizer).getDRGElementFromContentDefinition(node3);

        doNothing().when(synchronizer).synchronizeBaseDRGProperties(any(DRGElement.class), any(DRGElement.class));
        doNothing().when(synchronizer).synchronizeSpecializedProperties(any(DRGElement.class), any(DRGElement.class));

        synchronizer.synchronizeElementsFrom(drgElement);

        verify(synchronizer).getElementsWithContentId(contentDefinitionId);
        verify(synchronizer).updateText(drgElement, node1);
        verify(synchronizer).updateText(drgElement, node2);
        verify(synchronizer).updateText(drgElement, node3);

        verify(synchronizer).synchronizeBaseDRGProperties(drgElement, drgElement1);
        verify(synchronizer).synchronizeBaseDRGProperties(drgElement, drgElement2);
        verify(synchronizer).synchronizeBaseDRGProperties(drgElement, drgElement3);

        verify(synchronizer).synchronizeSpecializedProperties(drgElement, drgElement1);
        verify(synchronizer).synchronizeSpecializedProperties(drgElement, drgElement2);
        verify(synchronizer).synchronizeSpecializedProperties(drgElement, drgElement3);

        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }

    @Test
    public void testSynchronizeSpecializedProperties_DecisionNode() {

        doNothing().when(synchronizer).synchronizeDecisionNode(any(Decision.class), any(Decision.class));

        final Decision from = mock(Decision.class);
        final Decision to = mock(Decision.class);

        synchronizer.synchronizeSpecializedProperties(from, to);

        verify(synchronizer).synchronizeDecisionNode(from, to);
    }

    @Test
    public void testSynchronizeSpecializedProperties_BusinessKnowledgeModelNode() {

        doNothing().when(synchronizer).synchronizeBusinessKnowledgeModelNode(any(BusinessKnowledgeModel.class), any(BusinessKnowledgeModel.class));

        final BusinessKnowledgeModel from = mock(BusinessKnowledgeModel.class);
        final BusinessKnowledgeModel to = mock(BusinessKnowledgeModel.class);

        synchronizer.synchronizeSpecializedProperties(from, to);

        verify(synchronizer).synchronizeBusinessKnowledgeModelNode(from, to);
    }

    @Test
    public void testSynchronizeSpecializedProperties_DecisionServiceNode() {

        doNothing().when(synchronizer).synchronizeDecisionServiceNode(any(DecisionService.class), any(DecisionService.class));

        final DecisionService from = mock(DecisionService.class);
        final DecisionService to = mock(DecisionService.class);

        synchronizer.synchronizeSpecializedProperties(from, to);

        verify(synchronizer).synchronizeDecisionServiceNode(from, to);
    }

    @Test
    public void testSynchronizeSpecializedProperties_InputDataNode() {

        doNothing().when(synchronizer).synchronizeInputDataNode(any(InputData.class), any(InputData.class));

        final InputData from = mock(InputData.class);
        final InputData to = mock(InputData.class);

        synchronizer.synchronizeSpecializedProperties(from, to);

        verify(synchronizer).synchronizeInputDataNode(from, to);
    }

    @Test
    public void testSynchronizeSpecializedProperties_KnowledgeSourceNode() {

        doNothing().when(synchronizer).synchronizeKnowledgeSourceNode(any(KnowledgeSource.class), any(KnowledgeSource.class));

        final KnowledgeSource from = mock(KnowledgeSource.class);
        final KnowledgeSource to = mock(KnowledgeSource.class);

        synchronizer.synchronizeSpecializedProperties(from, to);

        verify(synchronizer).synchronizeKnowledgeSourceNode(from, to);
    }

    @Test
    public void testSynchronizeBaseDRGProperties() {

        final DRGElement from = mock(DRGElement.class);
        final DRGElement to = mock(DRGElement.class);
        final Description description = mock(Description.class);
        final DocumentationLinksHolder linksHolder = mock(DocumentationLinksHolder.class);
        final Name name = mock(Name.class);

        when(from.getDescription()).thenReturn(description);
        when(from.getLinksHolder()).thenReturn(linksHolder);
        when(from.getName()).thenReturn(name);

        synchronizer.synchronizeBaseDRGProperties(from, to);

        verify(to).setDescription(description);
        verify(to).setName(name);
        verify(to).setLinksHolder(linksHolder);
    }

    @Test
    public void testSynchronizeKnowledgeSourceNode() {

        final KnowledgeSource from = mock(KnowledgeSource.class);
        final KnowledgeSource to = mock(KnowledgeSource.class);
        final KnowledgeSourceType type = mock(KnowledgeSourceType.class);
        final LocationURI locationURI = mock(LocationURI.class);

        when(from.getType()).thenReturn(type);
        when(from.getLocationURI()).thenReturn(locationURI);

        synchronizer.synchronizeKnowledgeSourceNode(from, to);

        verify(to).setType(type);
        verify(to).setLocationURI(locationURI);
    }

    @Test
    public void testSynchronizeInputDataNode() {

        final InputData from = mock(InputData.class);
        final InputData to = mock(InputData.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);

        when(from.getVariable()).thenReturn(variable);

        synchronizer.synchronizeInputDataNode(from, to);

        verify(to).setVariable(variable);
    }

    @Test
    public void testSynchronizeDecisionServiceNode() {

        final DecisionService from = mock(DecisionService.class);
        final DecisionService to = mock(DecisionService.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);

        when(from.getVariable()).thenReturn(variable);

        synchronizer.synchronizeDecisionServiceNode(from, to);

        verify(to).setVariable(variable);
    }

    @Test
    public void testSynchronizeBusinessKnowledgeModelNode() {

        final BusinessKnowledgeModel from = mock(BusinessKnowledgeModel.class);
        final BusinessKnowledgeModel to = mock(BusinessKnowledgeModel.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);

        when(from.getVariable()).thenReturn(variable);

        synchronizer.synchronizeBusinessKnowledgeModelNode(from, to);

        verify(to).setVariable(variable);
    }

    @Test
    public void testSynchronizeDecisionNodeNode() {

        final Decision from = mock(Decision.class);
        final Decision to = mock(Decision.class);
        final Question question = mock(Question.class);
        final AllowedAnswers allowedAnswers = mock(AllowedAnswers.class);
        final Expression expression = mock(Expression.class);
        final InformationItemPrimary variable = mock(InformationItemPrimary.class);

        when(from.getQuestion()).thenReturn(question);
        when(from.getAllowedAnswers()).thenReturn(allowedAnswers);
        when(from.getExpression()).thenReturn(expression);
        when(from.getVariable()).thenReturn(variable);

        synchronizer.synchronizeDecisionNode(from, to);

        verify(to).setQuestion(question);
        verify(to).setAllowedAnswers(allowedAnswers);
        verify(to).setExpression(expression);
        verify(to).setVariable(variable);
    }

    @Test
    public void testUpdateText() {

        final Decision from = mock(Decision.class);
        final Node to = mock(Node.class);
        final Name name = mock(Name.class);
        final String nameValue = "the name";

        when(name.getValue()).thenReturn(nameValue);
        when(from.getName()).thenReturn(name);

        synchronizer.updateText(from, to);

        verify(nodeTextSetter).setText(nameValue, to);
    }

    @Test
    public void testGetElementsWithContentId() {

        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final DRGElement contentDefinition1 = mock(DRGElement.class);
        final DRGElement contentDefinition3 = mock(DRGElement.class);
        final String theId = "the id";
        final String anotherId = "another id";
        final List<Node> nodes = Arrays.asList(node1, node2, node3);

        when(dmnDiagramsSession.getAllNodes()).thenReturn(nodes);
        when(contentDefinition1.getContentDefinitionId()).thenReturn(theId);
        when(contentDefinition3.getContentDefinitionId()).thenReturn(anotherId);
        doReturn(contentDefinition1).when(synchronizer).getDRGElementFromContentDefinition(node1);
        doReturn(contentDefinition3).when(synchronizer).getDRGElementFromContentDefinition(node3);
        doReturn(true).when(synchronizer).definitionContainsDRGElement(node1);
        doReturn(false).when(synchronizer).definitionContainsDRGElement(node2);
        doReturn(true).when(synchronizer).definitionContainsDRGElement(node3);

        final List<Node> elements = synchronizer.getElementsWithContentId(theId);

        verify(synchronizer).definitionContainsDRGElement(node1);
        verify(synchronizer).definitionContainsDRGElement(node2);
        verify(synchronizer).definitionContainsDRGElement(node3);

        verify(synchronizer).getDRGElementFromContentDefinition(node1);
        verify(synchronizer, never()).getDRGElementFromContentDefinition(node2);
        verify(synchronizer).getDRGElementFromContentDefinition(node3);

        assertEquals(1, elements.size());
        assertTrue(elements.contains(node1));
    }

    @Test
    public void testDefinitionContainsDRGElement() {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        when(definition.getDefinition()).thenReturn(drgElement);
        when(node.getContent()).thenReturn(definition);

        final boolean containsDRGElement = synchronizer.definitionContainsDRGElement(node);

        assertTrue(containsDRGElement);
    }

    @Test
    public void testDefinitionContainsDRGElement_WhenDoesNotContains() {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Object obj = mock(Object.class);
        when(definition.getDefinition()).thenReturn(obj);
        when(node.getContent()).thenReturn(definition);

        final boolean containsDRGElement = synchronizer.definitionContainsDRGElement(node);

        assertFalse(containsDRGElement);
    }

    @Test
    public void testDefinitionContainsDRGElement_WhenContentIsNotDefinition() {

        final Node node = mock(Node.class);
        final Object obj = mock(Definition.class);

        when(node.getContent()).thenReturn(obj);

        final boolean containsDRGElement = synchronizer.definitionContainsDRGElement(node);

        assertFalse(containsDRGElement);
    }

    @Test
    public void testGetDRGElementFromContentDefinition() {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        when(definition.getDefinition()).thenReturn(drgElement);
        when(node.getContent()).thenReturn(definition);

        final DRGElement actual = synchronizer.getDRGElementFromContentDefinition(node);

        assertEquals(drgElement, actual);
    }

    @Test
    public void testGetNode() {

        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final String id = "id";

        when(node1.getUUID()).thenReturn(id);
        when(node2.getUUID()).thenReturn("id2");
        when(node3.getUUID()).thenReturn("id3");
        when(graphUtils.getNodeStream()).thenReturn(Stream.of(node1, node2, node3));

        final Optional<Node> node = synchronizer.getNode(id);

        assertTrue(node.isPresent());
        assertEquals(node1, node.get());
    }

    @Test
    public void testSynchronizeFromNode() {

        final Node node = mock(Node.class);
        final Optional<Node> optional = Optional.of(node);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);

        doNothing().when(synchronizer).synchronizeElementsFrom(drgElement);
        when(definition.getDefinition()).thenReturn(drgElement);
        when(node.getContent()).thenReturn(definition);

        synchronizer.synchronizeFromNode(optional);

        verify(synchronizer).synchronizeElementsFrom(drgElement);
    }
}
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


package org.kie.workbench.common.stunner.core.client.canvas;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.HasStringName;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.canvas.DeleteNodeConfirmationImpl.QUOTE;
import static org.kie.workbench.common.stunner.core.client.canvas.DeleteNodeConfirmationImpl.SEPARATOR;
import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_ConfirmationDescription;
import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_Question;
import static org.kie.workbench.common.stunner.core.client.canvas.resources.StunnerClientCommonConstants.DeleteNodeConfirmationImpl_Title;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteNodeConfirmationImplTest {

    @Mock
    private GraphsProvider graphsProvider;

    @Mock
    private ConfirmationDialog confirmationDialog;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ManagedInstance<GraphsProvider> graphsProviderInstances;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    private DeleteNodeConfirmationImpl confirmation;

    @Before
    public void setup() {
        confirmation = spy(new DeleteNodeConfirmationImpl(graphsProviderInstances,
                                                          confirmationDialog,
                                                          translationService,
                                                          definitionUtils,
                                                          sessionManager));
    }

    @Test
    public void testRequiresDeleteConfirmationWhenAllConditionsMatch() {

        final Collection<Element> elements = mock(Collection.class);
        final List<Graph> list = mock(List.class);
        when(list.isEmpty()).thenReturn(false);
        when(graphsProvider.isGlobalGraphSelected()).thenReturn(true);
        when(graphsProvider.getNonGlobalGraphs()).thenReturn(list);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(elements);
        doReturn(graphsProvider).when(confirmation).getGraphsProvider();

        assertTrue(confirmation.requiresDeletionConfirmation(elements));
    }

    @Test
    public void testRequiresDeleteConfirmationWhenGlobalGraphItIsNotSelected() {

        final Collection<Element> elements = mock(Collection.class);

        when(graphsProvider.isGlobalGraphSelected()).thenReturn(false);

        assertFalse(confirmation.requiresDeletionConfirmation(elements));
    }

    @Test
    public void testRequiresDeleteConfirmationWhenThereIsNoNonGlobalGraphs() {

        final Collection<Element> elements = mock(Collection.class);
        final List<Graph> list = Collections.emptyList();
        when(graphsProvider.isGlobalGraphSelected()).thenReturn(true);
        when(graphsProvider.getNonGlobalGraphs()).thenReturn(list);

        assertFalse(confirmation.requiresDeletionConfirmation(elements));
    }

    @Test
    public void testRequiresDeleteConfirmationWhenThereIsNoElementReferredInAnotherGraph() {

        final Collection<Element> elements = mock(Collection.class);
        final List<Graph> list = mock(List.class);
        when(list.isEmpty()).thenReturn(false);
        when(graphsProvider.isGlobalGraphSelected()).thenReturn(true);
        when(graphsProvider.getNonGlobalGraphs()).thenReturn(list);
        doReturn(false).when(confirmation).isReferredInAnotherGraph(elements);

        assertFalse(confirmation.requiresDeletionConfirmation(elements));
    }

    @Test
    public void testConfirmDeletion() {

        final Command onDeletionAccepted = mock(Command.class);
        final Command onDeletionRejected = mock(Command.class);
        final Collection<Element> elements = mock(Collection.class);
        final String referredElementsName = "element-1, element-2";
        final String confirmationDescription = "All " + referredElementsName;

        doReturn(referredElementsName).when(confirmation).getReferredElementsName(elements);

        when(translationService.getValue(DeleteNodeConfirmationImpl_ConfirmationDescription, referredElementsName))
                .thenReturn(confirmationDescription);

        final String title = "title";
        when(translationService.getValue(DeleteNodeConfirmationImpl_Title)).thenReturn(title);
        final String question = "question";
        when(translationService.getValue(DeleteNodeConfirmationImpl_Question)).thenReturn(question);

        confirmation.confirmDeletion(onDeletionAccepted, onDeletionRejected, elements);

        verify(confirmationDialog).show(title,
                                        confirmationDescription,
                                        question,
                                        onDeletionAccepted,
                                        onDeletionRejected);
    }

    @Test
    public void testGetReferredElementsName() {

        final String name1 = "name1";
        final String name2 = "name2";
        final String name3 = "name3";
        final String name7 = "name7";
        final String nonReferredName = "nonReferredName";

        final Element element1 = createElementMockWithName(name1);
        final Element element2 = createElementMockWithName(name2);
        final Element element3 = createElementMockWithName(name3);
        final Element element4WithoutName = mock(Element.class);
        final Element element5WithNullName = createElementMockWithName(null);
        final Element element6WithEmptyName = createElementMockWithName("");
        final Element element7 = createElementMockWithName(name7);
        final Element elementNonReferred = createElementMockWithName(nonReferredName);
        final String expected = buildExpected(name1, name2, name3, name7);

        doReturn(true).when(confirmation).isReferredInAnotherGraph(element1);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element2);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element3);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element4WithoutName);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element5WithNullName);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element6WithEmptyName);
        doReturn(true).when(confirmation).isReferredInAnotherGraph(element7);
        doReturn(false).when(confirmation).isReferredInAnotherGraph(elementNonReferred);

        final List<Element> list = Arrays.asList(element1,
                                                 element2,
                                                 element3,
                                                 element4WithoutName,
                                                 element5WithNullName,
                                                 element6WithEmptyName,
                                                 element7,
                                                 elementNonReferred);
        final String referredElements = confirmation.getReferredElementsName(list);

        assertEquals(expected, referredElements);
    }

    @Test
    public void testGetElementName() {

        final String elementName = "name";
        final Element element = createElementMockWithName(elementName);

        final Optional<String> actualName = confirmation.getElementName(element);

        assertTrue(actualName.isPresent());
        assertEquals(elementName, actualName.get());
    }

    @Test
    public void testGetElementNameWhenNameIsNull() {

        final Element element = createElementMockWithName(null);

        final Optional<String> actualName = confirmation.getElementName(element);

        assertFalse(actualName.isPresent());
    }

    @Test
    public void testIsReferredInAnotherGraph() {

        final String contentId = "content id";
        final Element element = createElementMockWithContentId(contentId);

        final Graph graph1 = mock(Graph.class);
        final Graph graph2 = mock(Graph.class);
        final Graph graph3 = mock(Graph.class);
        final List<Graph> graphs = Arrays.asList(graph1, graph2, graph3);
        when(graphsProvider.getNonGlobalGraphs()).thenReturn(graphs);
        doReturn(true).when(confirmation).containsNodeWithContentId(graph3, contentId);
        doReturn(false).when(confirmation).containsNodeWithContentId(graph1, contentId);
        doReturn(false).when(confirmation).containsNodeWithContentId(graph2, contentId);
        doReturn(graphsProvider).when(confirmation).getGraphsProvider();

        final boolean isReferred = confirmation.isReferredInAnotherGraph(element);

        verify(confirmation).containsNodeWithContentId(graph3, contentId);
        verify(confirmation).containsNodeWithContentId(graph2, contentId);
        verify(confirmation).containsNodeWithContentId(graph1, contentId);

        assertTrue(isReferred);
    }

    @Test
    public void testIsReferredInAnotherGraphWhenItIsNot() {

        final String contentId = "content id";
        final Element element = createElementMockWithContentId(contentId);

        final Graph graph1 = mock(Graph.class);
        final Graph graph2 = mock(Graph.class);
        final Graph graph3 = mock(Graph.class);
        final List<Graph> graphs = Arrays.asList(graph1, graph2, graph3);
        when(graphsProvider.getNonGlobalGraphs()).thenReturn(graphs);
        doReturn(graphsProvider).when(confirmation).getGraphsProvider();
        doReturn(false).when(confirmation).containsNodeWithContentId(graph3, contentId);
        doReturn(false).when(confirmation).containsNodeWithContentId(graph1, contentId);
        doReturn(false).when(confirmation).containsNodeWithContentId(graph2, contentId);

        final boolean isReferred = confirmation.isReferredInAnotherGraph(element);

        verify(confirmation).containsNodeWithContentId(graph3, contentId);
        verify(confirmation).containsNodeWithContentId(graph2, contentId);
        verify(confirmation).containsNodeWithContentId(graph1, contentId);

        assertFalse(isReferred);
    }

    @Test
    public void testIsReferredInAnotherGraphWhenElementContentIsNotDefinition() {

        final String contentId = "content id";
        final Element element = mock(Element.class);
        final Object someContent = mock(Object.class);
        when(element.getContent()).thenReturn(someContent);

        final boolean isReferred = confirmation.isReferredInAnotherGraph(element);

        assertFalse(isReferred);
    }

    @Test
    public void testAnyElementOfCollectionIsReferredInAnotherGraph() {

        final Element e1 = mock(Element.class);
        final Element e2 = mock(Element.class);
        final Element e3 = mock(Element.class);
        final List<Element> collection = Arrays.asList(e1, e2, e3);

        final boolean isReferred = confirmation.isReferredInAnotherGraph(collection);

        assertFalse(isReferred);
        verify(confirmation).isReferredInAnotherGraph(e1);
        verify(confirmation).isReferredInAnotherGraph(e2);
        verify(confirmation).isReferredInAnotherGraph(e3);
    }

    @Test
    public void testContainsNodeWithContentId() {

        final String contentId1 = "id1";
        final String contentId2 = "id2";
        final Node node1 = createNodeMockWithContentId(contentId1);
        final Node node2 = createNodeMockWithContentId(contentId2);
        final List<Node> nodes = Arrays.asList(node1, node2);
        final Graph graph = mock(Graph.class);
        final Iterable iterable = mock(Iterable.class);

        when(iterable.spliterator()).thenReturn(nodes.spliterator());
        when(graph.nodes()).thenReturn(iterable);

        assertTrue(confirmation.containsNodeWithContentId(graph, contentId1));
        assertTrue(confirmation.containsNodeWithContentId(graph, contentId2));
        assertFalse(confirmation.containsNodeWithContentId(graph, "some random id"));
    }

    @Test
    public void testAnyElementOfCollectionIsReferredInAnotherGraphWhenItIs() {

        final Element e1 = mock(Element.class);
        final Element e2 = mock(Element.class);
        final Element e3 = mock(Element.class);
        final List<Element> collection = Arrays.asList(e1, e2, e3);

        doReturn(true).when(confirmation).isReferredInAnotherGraph(e3);

        final boolean isReferred = confirmation.isReferredInAnotherGraph(collection);

        assertTrue(isReferred);
        verify(confirmation).isReferredInAnotherGraph(e1);
        verify(confirmation).isReferredInAnotherGraph(e2);
        verify(confirmation).isReferredInAnotherGraph(e3);
    }

    @Test
    public void testDestroy() {

        confirmation.destroy();

        verify(graphsProviderInstances).destroyAll();
    }

    @Test
    public void testInit() {

        final ClientSession session = mock(ClientSession.class);
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        final String definitionId = "definitionId";
        final Annotation qualifier = mock(Annotation.class);
        final ManagedInstance<GraphsProvider> foundInstances = mock(ManagedInstance.class);
        final GraphsProvider foundInstance = mock(GraphsProvider.class);

        when(foundInstances.isUnsatisfied()).thenReturn(false);
        when(foundInstances.get()).thenReturn(foundInstance);
        when(graphsProviderInstances.select(GraphsProvider.class, qualifier)).thenReturn(foundInstances);
        when(definitionUtils.getQualifier(definitionId)).thenReturn(qualifier);
        when(metadata.getDefinitionSetId()).thenReturn(definitionId);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        confirmation.init();

        final GraphsProvider actualProvider = confirmation.getGraphsProvider();

        assertEquals(actualProvider, foundInstance);
    }

    private String buildExpected(final String... names) {

        final StringBuilder builder = new StringBuilder();
        for (final String name : names) {
            if (builder.length() > 0) {
                builder.append(SEPARATOR);
            }
            builder.append(QUOTE);
            builder.append(name);
            builder.append(QUOTE);
        }

        return builder.toString();
    }

    private Element createElementMockWithName(final String name) {

        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        final HasStringName hasStringName = mock(HasStringName.class);

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(hasStringName);
        when(hasStringName.getStringName()).thenReturn(name);

        return element;
    }

    private Element createElementMockWithContentId(final String contentId) {

        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        final HasContentDefinitionId contentDefinitionId = mock(HasContentDefinitionId.class);

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(contentDefinitionId);
        when(contentDefinitionId.getContentDefinitionId()).thenReturn(contentId);

        return element;
    }

    private Node createNodeMockWithContentId(final String contentId) {

        final Node element = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final HasContentDefinitionId contentDefinitionId = mock(HasContentDefinitionId.class);

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(contentDefinitionId);
        when(contentDefinitionId.getContentDefinitionId()).thenReturn(contentId);

        return element;
    }
}

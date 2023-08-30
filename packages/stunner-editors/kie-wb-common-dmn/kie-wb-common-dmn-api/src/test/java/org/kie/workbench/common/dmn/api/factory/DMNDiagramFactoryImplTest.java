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

package org.kie.workbench.common.dmn.api.factory;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DMNDiagramFactoryImplTest {

    private static final String NAME = "name";

    private static final String EXISTING_NAME = "existing-name";

    @Mock
    private Metadata metadata;

    @Mock
    private Bounds bounds;

    private GraphImpl<DefinitionSet> graph;

    private DMNDiagramFactoryImpl factory;

    @Before
    public void setup() {
        this.factory = new DMNDiagramFactoryImpl();
        this.graph = new GraphImpl<>(UUID.uuid(), new GraphNodeStoreImpl());
        this.graph.addNode(newNode(new DMNDiagram()));
    }

    private Node<View, Edge> newNode(final Object definition) {
        final Node<View, Edge> node = new NodeImpl<>(UUID.uuid());
        final View<Object> content = new ViewImpl<>(definition, bounds);
        node.setContent(content);
        return node;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNameSpaces() {
        final Diagram diagram = factory.build(NAME, metadata, graph);

        //We can safely get the first object on the iterator as we know the graph only contains one node
        final Node<View, Edge> root = (Node<View, Edge>) diagram.getGraph().nodes().iterator().next();
        final DMNDiagram dmnDiagram = (DMNDiagram) DefinitionUtils.getElementDefinition(root);

        final Definitions dmnDefinitions = dmnDiagram.getDefinitions();
        final Map<String, String> dmnDefaultNameSpaces = dmnDefinitions.getNsContext();

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.FEEL.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.DMN.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.DMN.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.DMN.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.KIE.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.KIE.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix()));
        assertEquals(dmnDefinitions.getNamespace().getValue(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.DMNDI.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.DMNDI.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.DMNDI.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.DI.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.DI.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.DI.getPrefix()));

        assertTrue(dmnDefaultNameSpaces.containsKey(DMNModelInstrumentedBase.Namespace.DC.getPrefix()));
        assertEquals(DMNModelInstrumentedBase.Namespace.DC.getUri(),
                     dmnDefaultNameSpaces.get(DMNModelInstrumentedBase.Namespace.DC.getPrefix()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelName() {
        final Diagram diagram = factory.build(NAME, metadata, graph);

        //We can safely get the first object on the iterator as we know the graph only contains one node
        final Node<View, Edge> root = (Node<View, Edge>) diagram.getGraph().nodes().iterator().next();
        final DMNDiagram dmnDiagram = (DMNDiagram) DefinitionUtils.getElementDefinition(root);

        final Definitions dmnDefinitions = dmnDiagram.getDefinitions();

        assertEquals(NAME, dmnDefinitions.getName().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelNameWithNonEmptyExistingName() {
        final Node<View, Edge> existingRoot = (Node<View, Edge>) graph.nodes().iterator().next();
        final DMNDiagram existingDMNDiagram = (DMNDiagram) DefinitionUtils.getElementDefinition(existingRoot);
        final Definitions existingDMNDefinitions = existingDMNDiagram.getDefinitions();
        existingDMNDefinitions.getName().setValue(EXISTING_NAME);

        final Diagram newDiagram = factory.build(NAME, metadata, graph);
        final Node<View, Edge> newRoot = (Node<View, Edge>) newDiagram.getGraph().nodes().iterator().next();
        final DMNDiagram newDMNDiagram = (DMNDiagram) DefinitionUtils.getElementDefinition(newRoot);
        final Definitions newDMNDefinitions = newDMNDiagram.getDefinitions();

        assertEquals(EXISTING_NAME, newDMNDefinitions.getName().getValue());
    }
}

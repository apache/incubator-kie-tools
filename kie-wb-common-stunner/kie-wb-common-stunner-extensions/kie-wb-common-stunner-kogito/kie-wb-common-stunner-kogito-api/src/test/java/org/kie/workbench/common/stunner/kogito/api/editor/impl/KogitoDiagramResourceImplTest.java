/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.api.editor.impl;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class KogitoDiagramResourceImplTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferent() {
        final DiagramImpl projectDiagram1 = new DiagramImpl("Diagram", makeGraph(), makeMetadata());
        final DiagramImpl projectDiagram2 = new DiagramImpl("Diagram_", makeGraph(), makeMetadata());
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl(projectDiagram1);
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenXmlDiagramIsDifferent() {
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl("<xml>");
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl("<xml />");

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenTypeIsDifferent() {
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl(null, null, DiagramType.PROJECT_DIAGRAM);
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl(null, null, DiagramType.XML_DIAGRAM);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferentGraph() {
        final GraphImpl graphOne = makeGraph();
        final GraphImpl graphTwo = makeGraph();

        graphTwo.addNode(new NodeImpl("unique id"));

        final DiagramImpl projectDiagram1 = new DiagramImpl("Diagram", graphOne, makeMetadata());
        final DiagramImpl projectDiagram2 = new DiagramImpl("Diagram", graphTwo, makeMetadata());
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl(projectDiagram1);
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferentMetadata() {
        final Metadata metadataOne = makeMetadata();
        final Metadata metadataTwo = makeMetadata();

        doReturn("moduleOne").when(metadataOne).getTitle();
        doReturn("moduleTwo").when(metadataTwo).getTitle();

        final DiagramImpl projectDiagram1 = new DiagramImpl("Diagram", makeGraph(), metadataOne);
        final DiagramImpl projectDiagram2 = new DiagramImpl("Diagram", makeGraph(), metadataTwo);
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl(projectDiagram1);
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenObjectsAreEqual() {
        final GraphImpl graph = makeGraph();
        final Metadata metadata = makeMetadata();
        final String diagramName = "Diagram";
        final DiagramImpl projectDiagram1 = new DiagramImpl(diagramName, graph, metadata);
        final DiagramImpl projectDiagram2 = new DiagramImpl(diagramName, graph, metadata);
        final KogitoDiagramResourceImpl projectDiagramResource1 = new KogitoDiagramResourceImpl(projectDiagram1);
        final KogitoDiagramResourceImpl projectDiagramResource2 = new KogitoDiagramResourceImpl(projectDiagram2);

        assertEquals(projectDiagramResource1, projectDiagramResource2);
        assertEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstanceCreationWhenTypeIsNull() {
        new KogitoDiagramResourceImpl(null, null, null);
    }

    private GraphImpl makeGraph() {
        return new GraphImpl("Graph", new GraphNodeStoreImpl());
    }

    private Metadata makeMetadata() {
        return mock(Metadata.class);
    }
}

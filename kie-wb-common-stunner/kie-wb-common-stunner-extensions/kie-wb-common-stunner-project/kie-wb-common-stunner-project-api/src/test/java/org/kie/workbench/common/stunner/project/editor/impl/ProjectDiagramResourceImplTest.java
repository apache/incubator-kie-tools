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

package org.kie.workbench.common.stunner.project.editor.impl;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.PROJECT_DIAGRAM;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.XML_DIAGRAM;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class ProjectDiagramResourceImplTest {

    @Test
    public void testEqualsWhenProjectDiagramIsDifferent() {

        final ProjectDiagramImpl projectDiagram1 = new ProjectDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final ProjectDiagramImpl projectDiagram2 = new ProjectDiagramImpl("Diagram_", makeGraph(), makeMetadata());
        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl(projectDiagram1);
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenXmlDiagramIsDifferent() {

        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl("<xml>");
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl("<xml />");

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenTypeIsDifferent() {

        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl(null, null, PROJECT_DIAGRAM);
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl(null, null, XML_DIAGRAM);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenProjectDiagramIsDifferentGraph() {

        final GraphImpl graphOne = makeGraph();
        final GraphImpl graphTwo = makeGraph();

        graphTwo.addNode(new NodeImpl("unique id"));

        final ProjectDiagramImpl projectDiagram1 = new ProjectDiagramImpl("Diagram", graphOne, makeMetadata());
        final ProjectDiagramImpl projectDiagram2 = new ProjectDiagramImpl("Diagram", graphTwo, makeMetadata());
        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl(projectDiagram1);
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenProjectDiagramIsDifferentMetadata() {

        final ProjectMetadataImpl metadataOne = spy(makeMetadata());
        final ProjectMetadataImpl metadataTwo = spy(makeMetadata());

        doReturn("moduleOne").when(metadataOne).getModuleName();
        doReturn("moduleTwo").when(metadataTwo).getModuleName();

        final ProjectDiagramImpl projectDiagram1 = new ProjectDiagramImpl("Diagram", makeGraph(), metadataOne);
        final ProjectDiagramImpl projectDiagram2 = new ProjectDiagramImpl("Diagram", makeGraph(), metadataTwo);
        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl(projectDiagram1);
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenObjectsAreEqual() {

        final ProjectDiagramImpl projectDiagram1 = new ProjectDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final ProjectDiagramImpl projectDiagram2 = new ProjectDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final ProjectDiagramResourceImpl projectDiagramResource1 = new ProjectDiagramResourceImpl(projectDiagram1);
        final ProjectDiagramResourceImpl projectDiagramResource2 = new ProjectDiagramResourceImpl(projectDiagram2);

        assertEquals(projectDiagramResource1, projectDiagramResource2);
        assertEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstanceCreationWhenTypeIsNull() {
        new ProjectDiagramResourceImpl(null, null, null);
    }

    private GraphImpl makeGraph() {
        return new GraphImpl("Graph", new GraphNodeStoreImpl());
    }

    private ProjectMetadataImpl makeMetadata() {
        return new ProjectMetadataImpl();
    }
}

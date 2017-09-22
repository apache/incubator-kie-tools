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

package org.kie.workbench.common.stunner.project.diagram.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDiagramImplTest {

    @Test
    public void testProjectDiagramEquals() {
        ProjectDiagramImpl a = new ProjectDiagramImpl("Diagram",
                                                      new GraphImpl("Graph",
                                                                    new GraphNodeStoreImpl()),
                                                      new ProjectMetadataImpl());
        ProjectDiagramImpl b = new ProjectDiagramImpl("AnotherDiagram",
                                                      new GraphImpl("Graph",
                                                                    new GraphNodeStoreImpl()),
                                                      new ProjectMetadataImpl());
        assertNotEquals(a,
                        b);
        b = new ProjectDiagramImpl("Diagram",
                                   new GraphImpl("Graph",
                                                 new GraphNodeStoreImpl()),
                                   new ProjectMetadataImpl());
        assertEquals(a,
                     b);

        a.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        b.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        assertEquals(a,
                     b);

        a.getGraph().addNode(new NodeImpl("Node1"));
        b.getGraph().addNode(new NodeImpl("Node2"));
        assertNotEquals(a,
                        b);
        b.getGraph().removeNode("Node2");
        b.getGraph().addNode(new NodeImpl("Node1"));
        assertEquals(a,
                     b);
        assertEquals(a,
                     a);
    }

    @Test
    public void testProjectDiagramHashCode() {
        ProjectDiagramImpl a = new ProjectDiagramImpl("Diagram",
                                                      new GraphImpl("Graph",
                                                                    new GraphNodeStoreImpl()),
                                                      new ProjectMetadataImpl());
        ProjectDiagramImpl b = new ProjectDiagramImpl("AnotherDiagram",
                                                      new GraphImpl("Graph",
                                                                    new GraphNodeStoreImpl()),
                                                      new ProjectMetadataImpl());
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        b = new ProjectDiagramImpl("Diagram",
                                   new GraphImpl("Graph",
                                                 new GraphNodeStoreImpl()),
                                   new ProjectMetadataImpl());
        assertEquals(a.hashCode(),
                     b.hashCode());

        a.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        b.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        assertEquals(a.hashCode(),
                     b.hashCode());

        a.getGraph().addNode(new NodeImpl("Node1"));
        b.getGraph().addNode(new NodeImpl("Node2"));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        b.getGraph().removeNode("Node2");
        b.getGraph().addNode(new NodeImpl("Node1"));
        assertEquals(a.hashCode(),
                     b.hashCode());
        assertEquals(a.hashCode(),
                     a.hashCode());
    }
}

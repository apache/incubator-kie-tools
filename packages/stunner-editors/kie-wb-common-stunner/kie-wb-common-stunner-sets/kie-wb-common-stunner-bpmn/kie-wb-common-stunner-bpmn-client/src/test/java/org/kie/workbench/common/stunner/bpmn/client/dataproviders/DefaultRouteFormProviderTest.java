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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultRouteFormProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final String SELECTED_UUID = "UUID";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter<Object> definitionAdapter;

    @Mock
    private DefaultRouteFormProvider defaultRouteFormProvider;

    @Mock
    private Node selectedNode;

    private List<Edge> outEdges;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);

        when(session.getSelectionControl()).thenReturn(selectionControl);
        Collection<String> selectedItems = new ArrayList<>();
        when(selectedNode.getUUID()).thenReturn(SELECTED_UUID);
        selectedItems.add(selectedNode.getUUID());
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);

        when(graph.getNode(SELECTED_UUID)).thenReturn(selectedNode);

        outEdges = new ArrayList<>();

        UserTask userTask1 = new UserTask(new TaskGeneralSet(new Name("UserTask1"),
                                                             null),
                                          null,
                                          null,
                                          null,
                                          null,
                                          null,
                                          null,
                                          null);
        when(definitionAdapter.getTitle(eq(userTask1))).thenReturn("User Task");
        outEdges.add(mockEdge("Edge1",
                              userTask1));

        UserTask userTask2 = new UserTask(new TaskGeneralSet(new Name("UserTask2"),
                                                             null),
                                          null,
                                          null,
                                          null,
                                          null,
                                          null,
                                          null,
                                          null);
        when(definitionAdapter.getTitle(eq(userTask2))).thenReturn("User Task");
        outEdges.add(mockEdge("Edge2",
                              userTask2));

        ScriptTask scriptTask3 = new ScriptTask(new TaskGeneralSet(new Name("ScriptTask3"),
                                                                   null),
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null);
        when(definitionAdapter.getTitle(eq(scriptTask3))).thenReturn("Script Task");
        outEdges.add(mockEdge("Edge3",
                              scriptTask3));

        ExclusiveGateway gateway4 = new ExclusiveGateway(new BPMNGeneralSet("Gateway4"),
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         null);
        when(definitionAdapter.getTitle(eq(gateway4))).thenReturn("Exclusive Gateway");
        outEdges.add(mockEdge("Edge4",
                              gateway4));

        InclusiveGateway gateway5 = new InclusiveGateway(new BPMNGeneralSet(""),
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         null);
        when(definitionAdapter.getTitle(eq(gateway5))).thenReturn("Inclusive Gateway");
        outEdges.add(mockEdge("Edge5",
                              gateway5));
    }

    @Test
    public void testGetSelectorDataWithValues() {
        List<Element> nodes = mockModes();

        //add the desired edges to the selected node.
        when(selectedNode.getOutEdges()).thenReturn(outEdges);

        SelectorData selectorData = provider.getSelectorData(renderingContext);
        Map values = selectorData.getValues();
        verifyValues(values);
    }

    @Override
    protected SelectorDataProvider createProvider() {
        return new DefaultRouteFormProvider(sessionManager,
                                            definitionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();
        nodes.add(selectedNode);
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        assertEquals(5,
                     values.size());
        assertEquals("UserTask1",
                     values.get("Edge1"));
        assertEquals("UserTask2",
                     values.get("Edge2"));
        assertEquals("ScriptTask3",
                     values.get("Edge3"));
        assertEquals("Gateway4",
                     values.get("Edge4"));
        assertEquals("Inclusive Gateway",
                     values.get("Edge5"));
    }

    protected Edge mockEdge(String uuid,
                            BPMNDefinition targetNodeDefinition) {
        Edge edge = mock(Edge.class);
        Node node = mockNode(targetNodeDefinition);
        when(edge.getUUID()).thenReturn(uuid);
        when(edge.getTargetNode()).thenReturn(node);
        return edge;
    }
}

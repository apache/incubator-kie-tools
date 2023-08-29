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
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VariableProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final String ROOT_NODE_UUID = "UUID";

    private static final String PROCESS_VARIABLES = "var1:String,var2:String";

    private static final String CASE_FILE_VARIABLES = "var1:String,var2:String";

    @Mock
    private View view;

    @Mock
    private Metadata metadata;

    @Mock
    private Definition definition;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_NODE_UUID);
        when(diagram.getMetadata()).thenReturn(metadata);
    }

    @Mock
    private View<DataObject> dataObjectView;

    @Mock
    private View<Object> otherView;

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();
        nodes.add(mockRootNode(PROCESS_VARIABLES, CASE_FILE_VARIABLES));
        Node<View<DataObject>, ?> dataObjectNode;

        DataObject dataObject = new DataObject();
        dataObject.getGeneral().getDocumentation().setValue("doc");
        dataObject.setName(new Name("name"));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue("name")));

        dataObjectNode = new NodeImpl<>(UUID.uuid());
        dataObjectNode.setContent(dataObjectView);
        when(dataObjectView.getDefinition()).thenReturn(dataObject);

        nodes.add(dataObjectNode);
        return nodes;
    }

    @Test
    public void testViewAndContent() {
        Node<View<DataObject>, ?> dataObjectNode;

        DataObject dataObject = new DataObject();
        dataObject.getGeneral().getDocumentation().setValue("doc");
        dataObject.setName(new Name("name"));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue("name")));

        dataObjectNode = new NodeImpl<>(UUID.uuid());
        dataObjectNode.setContent(dataObjectView);
        when(dataObjectView.getDefinition()).thenReturn(dataObject);

        boolean isBPMNDefinition = ((VariablesProvider) provider).isBPMNDefinition(dataObjectNode);
        assertTrue(" Must be a BPMN Definition", isBPMNDefinition);

        Node<View<Object>, ?> objectNode = new NodeImpl<>(UUID.uuid());
        objectNode.setContent(otherView);
        when(otherView.getDefinition()).thenReturn(new Object());

        isBPMNDefinition = ((VariablesProvider) provider).isBPMNDefinition(objectNode);
        assertFalse(" Must not be a BPMN Definition", isBPMNDefinition);

        Node<Element, ?> objectNode2 = new NodeImpl<>(UUID.uuid());
        Element someElement = mock(Element.class);
        objectNode2.setContent(someElement);

        isBPMNDefinition = ((VariablesProvider) provider).isBPMNDefinition(objectNode2);
        assertFalse(" Must not be a BPMN Definition", isBPMNDefinition);
    }

    @Test
    public void testGetSelectorDataWithValues() {
        List<Element> nodes = mockModes();
        when(graph.nodes()).thenReturn(nodes);
        when(graph.getNode(eq(ROOT_NODE_UUID))).thenReturn((Node) nodes.get(0));
        SelectorData selectorData = provider.getSelectorData(renderingContext);
        Map values = selectorData.getValues();
        verifyValues(values);
    }

    @Override
    public void testGetSelectorDataWithNoValues() {
        List<Element> nodes = new ArrayList<>();
        nodes.add(mockRootNodeWithoutVariables());
        when(graph.nodes()).thenReturn(nodes);
        when(graph.getNode(eq(ROOT_NODE_UUID))).thenReturn((Node) nodes.get(0));
        SelectorData selectorData = provider.getSelectorData(renderingContext);
        Map values = selectorData.getValues();
        assertTrue(values.isEmpty());
    }

    @Override
    protected SelectorDataProvider createProvider() {
        return new VariablesProvider(sessionManager);
    }

    @Override
    protected void verifyValues(Map values) {
        assertEquals(5,
                     values.size());

        assertEquals("var1",
                     values.get("var1"));
        assertEquals("var2",
                     values.get("var2"));
        assertEquals(CaseFileVariables.CASE_FILE_PREFIX + "var1",
                     values.get(CaseFileVariables.CASE_FILE_PREFIX + "var1"));
        assertEquals(CaseFileVariables.CASE_FILE_PREFIX + "var2",
                     values.get(CaseFileVariables.CASE_FILE_PREFIX + "var2"));
    }

    private Element mockRootNode(String processVariables, String caseFileVariables) {
        BPMNDiagramImpl rootNode = new BPMNDiagramImpl();
        rootNode.setProcessData(new ProcessData(new ProcessVariables(processVariables)));
        rootNode.setCaseManagementSet((new CaseManagementSet(
                new CaseIdPrefix(""),
                new CaseRoles(""),
                new CaseFileVariables(caseFileVariables))));
        return mockNode(rootNode);
    }

    private Element mockRootNodeWithoutVariables() {
        BPMNDiagramImpl rootNode = new BPMNDiagramImpl();
        rootNode.setProcessData(new ProcessData(new ProcessVariables("")));
        return mockNode(rootNode);
    }
}

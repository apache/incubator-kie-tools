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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
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
        when(view.getDefinition()).thenReturn(definition);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();
        nodes.add(mockRootNode(PROCESS_VARIABLES, CASE_FILE_VARIABLES));
        return nodes;
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
        assertEquals(4,
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

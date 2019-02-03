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

package org.kie.workbench.common.stunner.cm.client.forms.fields.conditionEditor;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.VariableSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.VariableSearchServiceTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.cm.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaseManagementVariableSearchServiceTest extends VariableSearchServiceTest {

    @Override
    protected VariableSearchService newSearchService() {
        return new CaseManagementVariableSearchService(editorServiceCaller, translationService);
    }

    protected List<Node> mockNodes() {
        Node sourceNode = mockSourceNode(SOURCE_NODE, SELECTED_ITEM);

        Node parentNode1 = mockNode(PARENT_NODE1, mockMultipleInstanceSubprocess(mockVariables(MULTIPLE_INSTANCE_SUBPROCESS)));
        setParentNode(sourceNode, parentNode1);

        Node parentNode2 = mockNode(PARENT_NODE2, mockEmbeddedSubprocess(mockVariables(EMBEDDED_SUBPROCESS)));
        setParentNode(parentNode1, parentNode2);

        Node parentNode3 = mockNode(PARENT_NODE3, mockCMAdHocSubProcess(mockVariables(ADHOC_SUBPROCESS)));
        setParentNode(parentNode2, parentNode3);

        Node parentNode4 = mockNode(PARENT_NODE4, mockEventSubProcess(mockVariables(EVENT_SUBPROCESS)));
        setParentNode(parentNode3, parentNode4);

        Node canvasRoot = mockNode(CANVAS_ROOT_ID, mockCMDiagram(mockVariables(MAIN_PROCESS), mockVariables(CASE_FILE)));
        setParentNode(parentNode4, canvasRoot);

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(sourceNode);
        nodes.add(parentNode1);
        nodes.add(parentNode2);
        nodes.add(parentNode3);
        nodes.add(parentNode4);
        nodes.add(canvasRoot);
        return nodes;
    }

    private AdHocSubprocess mockCMAdHocSubProcess(String variables) {
        AdHocSubprocess adHocSubprocess = mock(AdHocSubprocess.class);
        ProcessData processData = mockCMProcessData(variables);
        when(adHocSubprocess.getProcessData()).thenReturn(processData);
        return adHocSubprocess;
    }

    private CaseManagementDiagram mockCMDiagram(String variables, String caseVariables) {
        CaseManagementDiagram cmDiagram = mock(CaseManagementDiagram.class);
        ProcessData processData = mockCMProcessData(variables);
        when(cmDiagram.getProcessData()).thenReturn(processData);
        CaseManagementSet caseManagementSet = mock(CaseManagementSet.class);
        CaseFileVariables caseFileVariables = mock(CaseFileVariables.class);
        when(caseManagementSet.getCaseFileVariables()).thenReturn(caseFileVariables);
        when(caseFileVariables.getValue()).thenReturn(caseVariables);
        when(cmDiagram.getCaseManagementSet()).thenReturn(caseManagementSet);
        return cmDiagram;
    }

    protected ProcessData mockCMProcessData(String variables) {
        final ProcessData processData = mock(ProcessData.class);
        final ProcessVariables processVariables = mock(ProcessVariables.class);
        when(processData.getProcessVariables()).thenReturn(processVariables);
        when(processVariables.getValue()).thenReturn(variables);
        return processData;
    }
}

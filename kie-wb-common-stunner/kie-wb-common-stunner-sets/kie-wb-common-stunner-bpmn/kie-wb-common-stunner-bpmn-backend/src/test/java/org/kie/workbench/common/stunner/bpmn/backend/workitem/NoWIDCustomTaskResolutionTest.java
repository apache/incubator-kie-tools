/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.StunnerTestingGraphAPI;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NoWIDCustomTaskResolutionTest {

    private StunnerTestingGraphAPI stunnerAPI;
    private XMLEncoderDiagramMetadataMarshaller xmlEncoder;
    private WorkItemDefinitionRegistry widRegistry;
    private WorkItemDefinitionLookupService widService;
    private BPMNDirectDiagramMarshaller tested;
    private Diagram<Graph, Metadata> diagram;

    private static final String PATH_DIAGRAM = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram";
    private static final String BPMN_CUSTOM_TASK = PATH_DIAGRAM + "/customTask.bpmn";
    private static final String CUSTOM_TASK_ID = "_50DA575F-2FD3-49D4-A301-52F107E30AC4";
    private static final String CUSTOM_TASK_NAME = "Email";
    private static final String CUSTOM_TASK_TYPE = "Email";
    private static final String ASSIGNMENTS = "|Body:String,From:String,Subject:String,To:String|" +
            "|Status:String|" +
            "[din]Body=This+is+an+e-mail+body.," +
            "[din]From=anyemail%40test.com," +
            "[din]Subject=Test,[din]To=someone%40test.com," +
            "[dout]Email+check=Status";
    private static final String ON_ENTRY_SCRIPT = "java : System.out.Println(\"On entry!\");";
    private static final String ON_EXIT_SCRIPT = "java : System.out.Println(\"On exit!\");";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        // Setup context.
        widRegistry = new WorkItemDefinitionRegistry() {
            @Override
            public Collection<WorkItemDefinition> items() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public WorkItemDefinition get(String name) {
                return null;
            }
        };

        widService = mock(WorkItemDefinitionLookupService.class);
        when(widService.execute(any(Metadata.class))).thenReturn(widRegistry.items());
        stunnerAPI = StunnerTestingGraphBackendAPI.build(BPMNDefinitionSet.class,
                                                         new BPMNTestDefinitionFactory(widRegistry));
        xmlEncoder = new XMLEncoderDiagramMetadataMarshaller();
        // Setup tested instance.
        tested = new BPMNDirectDiagramMarshaller(xmlEncoder,
                                                 stunnerAPI.getDefinitionManager(),
                                                 stunnerAPI.getRuleManager(),
                                                 widService,
                                                 stunnerAPI.getFactoryManager(),
                                                 stunnerAPI.commandFactory,
                                                 stunnerAPI.commandManager);

        diagram = Unmarshalling.unmarshall(tested, BPMN_CUSTOM_TASK);
    }

    @Test
    public void testNoWIDPreserveCustomTaskType() {
        final Node customTaskNode = diagram.getGraph().getNode(CUSTOM_TASK_ID);
        final View customTaskNodeContent = (View) customTaskNode.getContent();

        // Custom tasks shall remain as custom tasks after unmarshalling
        assertTrue(customTaskNodeContent.getDefinition() instanceof CustomTask);

        final CustomTask customTask = (CustomTask) customTaskNodeContent.getDefinition();

        assertEquals(CUSTOM_TASK_TYPE, customTask.getTaskType().getRawType());
    }

    @Test
    public void testNoWIDPreserveProperties() {
        final Node customTaskNode = diagram.getGraph().getNode(CUSTOM_TASK_ID);
        final View customTaskNodeContent = (View) customTaskNode.getContent();
        final CustomTask customTask = (CustomTask) customTaskNodeContent.getDefinition();

        assertTrue(customTask.getCategory().isEmpty());
        assertTrue(customTask.getDescription().isEmpty());
        assertEquals(CUSTOM_TASK_NAME, customTask.getName());
        assertEquals(ASSIGNMENTS, customTask.getDataIOSet().getAssignmentsinfo().getValue());
        assertTrue(customTask.getExecutionSet().getAdHocAutostart().getValue());
        assertTrue(customTask.getExecutionSet().getIsAsync().getValue());
        assertEquals(ON_ENTRY_SCRIPT, customTask.getExecutionSet().getOnEntryAction().getValue().toString());
        assertEquals(ON_EXIT_SCRIPT, customTask.getExecutionSet().getOnExitAction().getValue().toString());
    }
}

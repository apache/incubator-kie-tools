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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AdHocSubProcessTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_ADHOC_SUBPROCESS_AUTOSTART =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/adHocSubProcessAutostart.bpmn";
    private static final String BPMN_ADHOC_SUBPROCESS_SPECIAL_CHARACTERS =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/adHocSubProcessSpecialCharacters.bpmn";

    @Before
    public void setUp() {
        super.init();
    }

    @Test
    public void testOldMarshallerAdHocSpecificProperties() throws Exception {
        unmarshallAddHocSubprocess(oldMarshaller);
    }

    @Test
    public void testNewMarshallerAdHocSpecificProperties() throws Exception {
        unmarshallAddHocSubprocess(newMarshaller);
    }

    @Test
    public void testOldMarshallerSpecialCharacters() throws Exception {
        unmarshallAddHocSubprocessSpecialCharacters(oldMarshaller);
    }

    @Test
    public void testNewMarshallerSpecialCharacters() throws Exception {
        unmarshallAddHocSubprocessSpecialCharacters(newMarshaller);
    }

    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_ADHOC_SUBPROCESS_AUTOSTART);
    }

    @Test
    public void testMigrationSpecialCharacters() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_ADHOC_SUBPROCESS_SPECIAL_CHARACTERS);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_ADHOC_SUBPROCESS_SPECIAL_CHARACTERS);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_ADHOC_SUBPROCESS_SPECIAL_CHARACTERS);
    }

    @SuppressWarnings("unchecked")
    private void unmarshallAddHocSubprocess(final DiagramMarshaller marshaller) throws Exception {

        final String ADHOC_SUBPROCESS_ID = "_8D223345-9B6F-4AD3-997B-582C9222CC35";
        final String USER_TASK_ID = "_E386D64B-70FE-45E5-A190-C65EB8695480";
        final String BUSINESS_RULE_TASK_ID = "_5AF47879-8D23-4893-8668-ADF88C3EAD1B";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);

        Node<? extends Definition, ?> adHocSubProcessNode = diagram.getGraph().getNode(ADHOC_SUBPROCESS_ID);
        AdHocSubprocess adHocSubprocess = (AdHocSubprocess) adHocSubProcessNode.getContent().getDefinition();

        assertNotNull(adHocSubprocess);

        BPMNGeneralSet generalSet = adHocSubprocess.getGeneral();
        AdHocSubprocessTaskExecutionSet executionSet = adHocSubprocess.getExecutionSet();
        ProcessData processData = adHocSubprocess.getProcessData();

        assertEquals("AdHoc Sub-process",
                     generalSet.getName().getValue());
        assertEquals("for marshalling test",
                     generalSet.getDocumentation().getValue());

        assertEquals("",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getLanguage());

        assertEquals("",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getLanguage());

        assertEquals("varA == null",
                     executionSet.getAdHocCompletionCondition().getValue().getScript());
        assertEquals("mvel",
                     executionSet.getAdHocCompletionCondition().getValue().getLanguage());

        assertEquals("Parallel",
                     executionSet.getAdHocOrdering().getValue());

        assertEquals("adHocVariable:Object",
                     processData.getProcessVariables().getValue());

        Node<? extends Definition, ?> userTaskNode = diagram.getGraph().getNode(USER_TASK_ID);
        UserTask userTask = (UserTask) userTaskNode.getContent().getDefinition();
        assertTrue(userTask.getExecutionSet().getAdHocAutostart().getValue());

        Node<? extends Definition, ?> businessRuleTaskNode = diagram.getGraph().getNode(BUSINESS_RULE_TASK_ID);
        BusinessRuleTask businessRuleTask = (BusinessRuleTask) businessRuleTaskNode.getContent().getDefinition();
        assertTrue(businessRuleTask.getExecutionSet().getAdHocAutostart().getValue());
    }

    @SuppressWarnings("unchecked")
    private void unmarshallAddHocSubprocessSpecialCharacters(final DiagramMarshaller marshaller) throws Exception {
        final String ADHOC_SUBPROCESS_ID = "_56770326-5337-4F88-A502-1269AB8E4786";
        final String USER_TASK_ID = "_1AEB4B8A-4515-404D-A1F3-2FFCA864C4CF";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_ADHOC_SUBPROCESS_SPECIAL_CHARACTERS);

        Node<? extends Definition, ?> adHocSubProcessNode = diagram.getGraph().getNode(ADHOC_SUBPROCESS_ID);
        AdHocSubprocess adHocSubprocess = (AdHocSubprocess) adHocSubProcessNode.getContent().getDefinition();

        assertNotNull(adHocSubprocess);

        BPMNGeneralSet generalSet = adHocSubprocess.getGeneral();
        AdHocSubprocessTaskExecutionSet executionSet = adHocSubprocess.getExecutionSet();
        ProcessData processData = adHocSubprocess.getProcessData();

        assertEquals("~`!@#$%^&*()_+|}{[]\":;'<>?/.,",
                     generalSet.getName().getValue());
        assertEquals("式",
                     generalSet.getDocumentation().getValue());

        assertEquals("String message = \"entering!\";\n" +
                             "System.out.println(message);",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getLanguage());

        assertEquals("String message = \"leaving!\";\n" +
                             "System.out.println(message);",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getLanguage());

        assertEquals("Person(name == \"式\" && age > 18)",
                     executionSet.getAdHocCompletionCondition().getValue().getScript());
        assertEquals("drools",
                     executionSet.getAdHocCompletionCondition().getValue().getLanguage());

        assertEquals("Sequential",
                     executionSet.getAdHocOrdering().getValue());

        assertEquals("",
                     processData.getProcessVariables().getValue());

        Node<? extends Definition, ?> userTaskNode = diagram.getGraph().getNode(USER_TASK_ID);
        UserTask userTask = (UserTask) userTaskNode.getContent().getDefinition();
        assertFalse(userTask.getExecutionSet().getAdHocAutostart().getValue());
    }
}

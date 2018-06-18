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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks;

import java.util.List;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static junit.framework.TestCase.assertEquals;

public class AssociationsTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/associationsAndAssignments.bpmn";
    private static final String TASK_ID = "_5E1F51EC-B398-4BF7-B32D-45FB6CE4731A";

    public AssociationsTest() {
        init();
    }

    @Test
    public void parseAssociations() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(newMarshaller, BPMN_FILE_PATH);
        Node<View<UserTask>, ?> node = d.getGraph().getNode(TASK_ID);
        UserTask definition = node.getContent().getDefinition();
        AssignmentsInfo assignmentsinfo = definition.getExecutionSet().getAssignmentsinfo();
        assertEquals("|Body:String,From:String,Subject:String,To:String||CUSTOM:String,CUSTOM2:String|[din]Body=HELLO,[din]myprocvar->From,[dout]CUSTOM->myprocvar",
                     assignmentsinfo.getValue());
    }

    @Test
    public void marshallAssociations() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(newMarshaller, BPMN_FILE_PATH);
        Node<View<UserTask>, ?> node = d.getGraph().getNode(TASK_ID);
        UserTask definition = node.getContent().getDefinition();
        AssignmentsInfo assignmentsinfo = definition.getExecutionSet().getAssignmentsinfo();

        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(d.getGraph());

        Definitions definitions =
                definitionsConverter.toDefinitions();

        Process p = (Process) definitions.getRootElements().get(0);
        org.eclipse.bpmn2.UserTask flowElement = (org.eclipse.bpmn2.UserTask)
                p.getFlowElements().stream().filter(e -> e.getId().equals(TASK_ID)).findFirst().get();
        List<DataInputAssociation> associations = flowElement.getDataInputAssociations();
        assertEquals("myprocvar", findVar(associations, "From"));
        assertEquals("HELLO", findAssignment(associations, "Body"));
    }

    public String findVar(List<DataInputAssociation> associations, String varName) {
        return associations.stream()
                .filter(a -> {
                    DataInput in = (DataInput) a.getTargetRef();
                    return in.getName().equals(varName);
                })
                .map(a -> {
                    Property dataOutput = (Property) a.getSourceRef().get(0);
                    return dataOutput.getName();
                })
                .findFirst()
                .get();
    }

    public String findAssignment(List<DataInputAssociation> associations, String varName) {
        return associations.stream()
                .filter(a -> {
                    DataInput in = (DataInput) a.getTargetRef();
                    return in.getName().equals(varName);
                })
                .map(a -> {
                    Assignment assignment = a.getAssignment().get(0);
                    FormalExpression expr = (FormalExpression) assignment.getFrom();
                    return expr.getBody();
                })
                .findFirst()
                .get();
    }
}

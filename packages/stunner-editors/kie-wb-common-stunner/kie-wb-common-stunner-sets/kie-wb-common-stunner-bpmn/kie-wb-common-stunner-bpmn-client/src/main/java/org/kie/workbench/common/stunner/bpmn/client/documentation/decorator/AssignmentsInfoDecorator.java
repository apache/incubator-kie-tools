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


package org.kie.workbench.common.stunner.bpmn.client.documentation.decorator;

import java.util.Map;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Assignment;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor.AssignmentsEditorWidget.DEFAULT_IGNORED_PROPERTY_NAMES;

public class AssignmentsInfoDecorator implements PropertyDecorator {

    private AssignmentsInfo assignmentsInfo;
    private Diagram diagram;

    public AssignmentsInfoDecorator(final AssignmentsInfo assignmentsInfo, Diagram diagram) {
        this.assignmentsInfo = assignmentsInfo;
        this.diagram = diagram;
    }

    @Override
    public String getValue() {
        final Map<String, String> assignmentsMap = AssignmentParser.parseAssignmentsInfo(assignmentsInfo.getValue());
        return new AssignmentData(assignmentsMap.get(AssignmentParser.DATAINPUTSET),
                                  assignmentsMap.get(AssignmentParser.DATAOUTPUTSET),
                                  VariableUtils.encodeProcessVariables(diagram, null),
                                  assignmentsMap.get(AssignmentParser.ASSIGNMENTS),
                                  DEFAULT_IGNORED_PROPERTY_NAMES).getAssignments()
                .stream()
                .map(Assignment::toString)
                .collect(Collectors.joining("\n"));
    }
}

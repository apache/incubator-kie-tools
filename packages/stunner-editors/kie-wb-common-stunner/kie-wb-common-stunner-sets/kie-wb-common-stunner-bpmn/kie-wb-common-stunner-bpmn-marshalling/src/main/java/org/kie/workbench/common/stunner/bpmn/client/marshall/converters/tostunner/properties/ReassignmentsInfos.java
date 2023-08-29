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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedReassignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;

import static java.util.Arrays.asList;

public class ReassignmentsInfos {

    private static Set<String> RESERVED_ASSIGNMENTS = new HashSet<>(asList(
            AssociationType.NOT_COMPLETED_REASSIGN.getName(),
            AssociationType.NOT_STARTED_REASSIGN.getName()));

    public static ReassignmentsInfo of(List<DataInputAssociation> dataInputAssociations) {
        ReassignmentTypeListValue reassignments = new ReassignmentTypeListValue();
        dataInputAssociations.forEach(din -> {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (isReservedIdentifier(targetRef.getName())) {
                if (!din.getAssignment().isEmpty()) {
                    Assignment assignment = din.getAssignment().get(0);
                    if (assignment != null) {
                        FormalExpression expr = (FormalExpression) assignment.getFrom();
                        String body = FormalExpressionBodyHandler.of(expr).getBody();
                        if (body != null) {
                            Arrays.stream(replaceBracket(body).split("\\^")).forEach(b -> {
                                reassignments.addValue(ParsedReassignmentsInfos.of(targetRef.getName(), b));
                            });
                        }
                    }
                }
            }
        });

        return new ReassignmentsInfo(reassignments);
    }

    public static boolean isReservedIdentifier(String targetName) {
        return RESERVED_ASSIGNMENTS.contains(targetName);
    }

    private static String replaceBracket(String original) {
        return original.replaceFirst("\\[", "").replace("]", "");
    }
}

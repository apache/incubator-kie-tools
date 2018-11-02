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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Collection;

import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

public class VariableDeleteHandler {

    private final String PROPERTY_IN_PREFIX = "[din]";
    private final String PROPERTY_OUT_PREFIX = "[dout]";

    private SessionManager canvasSessionManager;

    public boolean isVariableBoundToNodes(Graph graph,
                                          String propertyId) {
        final Collection<String> result = new ArrayList();
        graph.nodes().forEach(node -> {
            Node nodeVar = (Node) node;
            boolean process = this.isVariableBoundToNode(nodeVar,
                                                         propertyId);
            if (process) {
                result.add(nodeVar.getUUID());
            }
        });
        return result.size() > 0;
    }

    private boolean isVariableBoundToNode(Node node,
                                          String propertyId) {
        Object content = node.getContent();

        if (content instanceof Definition) {
            Object nodeDefinition = ((Definition) content).getDefinition();
            Collection<String> variablesName = new ArrayList();
            if (nodeDefinition instanceof UserTask) {
                UserTask userTask = (UserTask) nodeDefinition;
                final String userTaskAssignmentsinfo = userTask.getExecutionSet().getAssignmentsinfo().getValue();
                variablesName.addAll(getVariablesName(userTaskAssignmentsinfo,
                                                      PROPERTY_IN_PREFIX,
                                                      PROPERTY_OUT_PREFIX));
                variablesName.addAll(getVariablesName(userTaskAssignmentsinfo,
                                                      PROPERTY_OUT_PREFIX));
            }
            if ((nodeDefinition instanceof BusinessRuleTask)) {
                BusinessRuleTask businessRuleTaskTask = (BusinessRuleTask) nodeDefinition;
                final String businessRuleTaskAssignmentsinfo = businessRuleTaskTask.getDataIOSet().getAssignmentsinfo().getValue();
                variablesName.addAll(getVariablesName(businessRuleTaskAssignmentsinfo,
                                                      PROPERTY_IN_PREFIX,
                                                      PROPERTY_OUT_PREFIX));
                variablesName.addAll(getVariablesName(businessRuleTaskAssignmentsinfo,
                                                      PROPERTY_OUT_PREFIX));
            }
            return variablesName.stream().filter(id -> propertyId.equals(id)).findAny().isPresent();
        }
        return false;
    }

    private Collection<String> getVariablesName(final String propertiesIdString,
                                                final String startString,
                                                final String stopString) {
        int dinStartIndex = propertiesIdString.indexOf(startString);
        int dinEndIndex = propertiesIdString.indexOf(stopString);
        if (dinEndIndex <= 0) {
            dinEndIndex = propertiesIdString.length();
        }
        return getVariablesName(propertiesIdString,
                                dinStartIndex,
                                dinEndIndex);
    }

    private Collection<String> getVariablesName(final String propertiesIdString,
                                                final String startString) {
        int dinStartIndex = propertiesIdString.indexOf(startString);
        int dinEndIndex = propertiesIdString.length();
        return getVariablesName(propertiesIdString,
                                dinStartIndex,
                                dinEndIndex);
    }

    private Collection<String> getVariablesName(final String variablesNameString,
                                                final int startIndex,
                                                int stopIndex) {
        Collection<String> properties = new ArrayList();
        if (startIndex >= 0) {
            if (stopIndex <= 0) {
                stopIndex = variablesNameString.length();
            }

            String name = variablesNameString.substring(startIndex,
                                                        stopIndex);
            String[] dinSplit = name.split(",");

            for (int i = 0; i < dinSplit.length; i++) {
                String propertyName = getPropertyString(dinSplit[i]);
                properties.add(propertyName);
            }
        }
        return properties;
    }

    private String getPropertyString(String propertyString) {
        int start = propertyString.indexOf("->") + 2;
        int end = propertyString.length();
        return propertyString.substring(start,
                                        end);
    }
}




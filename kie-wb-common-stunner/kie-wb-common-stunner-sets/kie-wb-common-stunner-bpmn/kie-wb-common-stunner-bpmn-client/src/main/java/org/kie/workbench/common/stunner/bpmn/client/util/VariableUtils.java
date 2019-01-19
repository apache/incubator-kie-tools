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

package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class VariableUtils {

    private static final String PROPERTY_IN_PREFIX = "[din]";
    private static final String PROPERTY_OUT_PREFIX = "[dout]";

    @SuppressWarnings("unchecked")
    public static Collection<VariableUsage> findVariableUsages(Graph graph, String variableName) {
        if (StringUtils.isEmpty(variableName)) {
            return Collections.EMPTY_LIST;
        }
        Iterable<Node> nodes = graph.nodes();
        return StreamSupport.stream(nodes.spliterator(), false)
                .filter(VariableUtils::isBPMNDefinition)
                .map(node -> (Node<View<BPMNDefinition>, Edge>) node)
                .map(node -> findVariableUsages(variableName, node))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static boolean isBPMNDefinition(Node node) {
        return node.getContent() instanceof View &&
                ((View) node.getContent()).getDefinition() instanceof BPMNDefinition;
    }

    private static List<VariableUsage> findVariableUsages(String variableName, Node<View<BPMNDefinition>, Edge> node) {
        final List<VariableUsage> result = new ArrayList<>();
        AssignmentsInfo assignmentsInfo = null;
        String variableInfo;
        final BPMNDefinition definition = node.getContent().getDefinition();
        final String displayName = definition.getGeneral() != null && definition.getGeneral().getName() != null ? definition.getGeneral().getName().getValue() : null;
        if (definition instanceof BusinessRuleTask) {
            assignmentsInfo = ((BusinessRuleTask) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof UserTask) {
            assignmentsInfo = ((UserTask) definition).getExecutionSet().getAssignmentsinfo();
        } else if (definition instanceof ServiceTask) {
            assignmentsInfo = ((ServiceTask) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof EndErrorEvent) {
            assignmentsInfo = ((EndErrorEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof EndEscalationEvent) {
            assignmentsInfo = ((EndEscalationEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof EndMessageEvent) {
            assignmentsInfo = ((EndMessageEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof EndSignalEvent) {
            assignmentsInfo = ((EndSignalEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateErrorEventCatching) {
            assignmentsInfo = ((IntermediateErrorEventCatching) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateMessageEventCatching) {
            assignmentsInfo = ((IntermediateMessageEventCatching) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateSignalEventCatching) {
            assignmentsInfo = ((IntermediateSignalEventCatching) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateEscalationEvent) {
            assignmentsInfo = ((IntermediateEscalationEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateEscalationEventThrowing) {
            assignmentsInfo = ((IntermediateEscalationEventThrowing) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateMessageEventThrowing) {
            assignmentsInfo = ((IntermediateMessageEventThrowing) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof IntermediateSignalEventThrowing) {
            assignmentsInfo = ((IntermediateSignalEventThrowing) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof StartErrorEvent) {
            assignmentsInfo = ((StartErrorEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof StartEscalationEvent) {
            assignmentsInfo = ((StartEscalationEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof StartMessageEvent) {
            assignmentsInfo = ((StartMessageEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof StartSignalEvent) {
            assignmentsInfo = ((StartSignalEvent) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof BaseReusableSubprocess) {
            assignmentsInfo = ((BaseReusableSubprocess) definition).getDataIOSet().getAssignmentsinfo();
        } else if (definition instanceof MultipleInstanceSubprocess) {
            MultipleInstanceSubprocess miSubprocess = (MultipleInstanceSubprocess) definition;
            variableInfo = ((MultipleInstanceSubprocess) definition).getExecutionSet().getMultipleInstanceCollectionInput().getValue();
            if (variableName.equals(variableInfo)) {
                result.add(new VariableUsage(variableName, VariableUsage.USAGE_TYPE.INPUT_COLLECTION, node, miSubprocess.getGeneral().getName().getValue()));
            }
            variableInfo = ((MultipleInstanceSubprocess) definition).getExecutionSet().getMultipleInstanceCollectionOutput().getValue();
            if (variableName.equals(variableInfo)) {
                result.add(new VariableUsage(variableName, VariableUsage.USAGE_TYPE.OUTPUT_COLLECTION, node, miSubprocess.getGeneral().getName().getValue()));
            }
        }
        if (assignmentsInfo != null) {
            Map<String, VariableUsage> decodedVariableUsages = decodeVariableUsages(assignmentsInfo.getValue(), node, displayName);
            if (decodedVariableUsages.containsKey(variableName)) {
                result.add(decodedVariableUsages.get(variableName));
            }
        }
        return result;
    }

    private static Map<String, VariableUsage> decodeVariableUsages(String encodedAssignments, Node node, String displayName) {
        Map<String, VariableUsage> variableUsages = new HashMap<>();
        if (isEmpty(encodedAssignments)) {
            return variableUsages;
        }
        String[] encodedParts = encodedAssignments.split("\\|");
        if (encodedParts.length != 5) {
            return variableUsages;
        }
        String encodedVariablesList = encodedParts[4];
        if (!isEmpty(encodedVariablesList)) {
            String[] variablesList = encodedVariablesList.split(",");
            Arrays.stream(variablesList)
                    .filter(variableDef -> !isEmpty(variableDef))
                    .forEach(variableDef -> {
                        String variableName = null;
                        VariableUsage.USAGE_TYPE usageType = null;
                        String unPrefixedVariableDef;
                        String[] variableDefParts;
                        if (variableDef.startsWith(PROPERTY_IN_PREFIX)) {
                            unPrefixedVariableDef = variableDef.substring(PROPERTY_IN_PREFIX.length());
                            if (!isEmpty(unPrefixedVariableDef)) {
                                variableDefParts = unPrefixedVariableDef.split("->");
                                variableName = variableDefParts[0];
                                usageType = VariableUsage.USAGE_TYPE.INPUT_VARIABLE;
                            }
                        } else if (variableDef.startsWith(PROPERTY_OUT_PREFIX)) {
                            unPrefixedVariableDef = variableDef.substring(PROPERTY_OUT_PREFIX.length());
                            if (!isEmpty(unPrefixedVariableDef)) {
                                variableDefParts = unPrefixedVariableDef.split("->");
                                variableName = variableDefParts[1];
                                usageType = VariableUsage.USAGE_TYPE.OUTPUT_VARIABLE;
                            }
                        }
                        if (!isEmpty(variableName)) {
                            VariableUsage variableUsage = variableUsages.get(variableName);
                            if (variableUsage == null) {
                                variableUsage = new VariableUsage(variableName, usageType, node, displayName);
                                variableUsages.put(variableUsage.getVariableName(), variableUsage);
                            }
                            if (variableUsage.getUsageType() != usageType) {
                                variableUsage.setUsageType(VariableUsage.USAGE_TYPE.INPUT_OUTPUT_VARIABLE);
                            }
                        }
                    });
        }
        return variableUsages;
    }
}
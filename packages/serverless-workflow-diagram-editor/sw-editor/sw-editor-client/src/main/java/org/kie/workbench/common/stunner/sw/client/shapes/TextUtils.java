/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.FunctionRef;
import org.kie.workbench.common.stunner.sw.definition.StateDataFilter;
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public interface TextUtils {

    String ACTION_IS_NULL = "Action is not defined";
    String STATE_DATA_FILTER_IS_NULL = "StateDataFilter is not defined";
    String ACTIONS_ARE_NULL = "No actions provided";
    String ACTION_NAME = "Action: ";
    String ACTION_IS_FUNC = "Action functionRef: ";
    String ACTION_IS_EVENT = "Action eventRef: ";
    String ACTION_IS_SUBFLOW = "Action subFlowRef: ";

    static String getActionStringFromArray(ActionNode[] actions) {
        if (actions == null || actions.length == 0) {
            return ACTIONS_ARE_NULL;
        }

        StringBuilder actionString = new StringBuilder();
        for (ActionNode action : actions) {
            actionString.append(getActionString(action));
            actionString.append("\r\n");
        }

        return actionString.toString();
    }

    static String getActionString(ActionNode action) {
        if (action == null) {
            return ACTION_IS_NULL;
        }

        if (StringUtils.nonEmpty(action.getName())) {
            return ACTION_NAME + action.getName();
        }

        if (action.getFunctionRef() != null) {
            if (action.getFunctionRef() instanceof String) {
                return ACTION_IS_FUNC + action.getFunctionRef();
            }

            FunctionRef functionRef = (FunctionRef) action.getFunctionRef();
            return ACTION_IS_FUNC + functionRef.getRefName();
        }

        if (action.getEventRef() != null) {
            return ACTION_IS_EVENT + action.getEventRef().getConsumeEventRef();
        }

        if (action.getSubFlowRef() != null) {
            if (action.getSubFlowRef() instanceof String) {
                return ACTION_IS_SUBFLOW + action.getSubFlowRef();
            }

            SubFlowRef subFlowRef = (SubFlowRef) action.getSubFlowRef();
            return ACTION_IS_SUBFLOW + subFlowRef.getWorkflowId();
        }

        return ACTION_IS_NULL;
    }

    static String getStateDataFilter(StateDataFilter filter) {
        if (filter == null) {
            return STATE_DATA_FILTER_IS_NULL;
        }

        return "stateDataFilter:\r\ninput: " + truncate(filter.getInput(), 30)
                + "\r\noutput: " + truncate(filter.getOutput(), 30);
    }

    static String truncate(String value, int size) {
        if (isEmpty(value) || value.length() <= size) {
            return value;
        }

        return value.substring(0, size) + "...";
    }
}

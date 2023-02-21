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
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTIONS_ARE_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTION_IS_EVENT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTION_IS_FUNC;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTION_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTION_IS_SUBFLOW;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ACTION_NAME;

public interface HasActions extends HasTranslation {

    default String getActionStringFromArray(ActionNode[] actions) {
        if (actions == null || actions.length == 0) {
            return getTranslation(ACTIONS_ARE_NULL);
        }

        StringBuilder actionString = new StringBuilder();
        for (ActionNode action : actions) {
            actionString.append(getActionString(action));
            actionString.append("\r\n");
        }

        return actionString.toString();
    }

    default String getActionString(ActionNode action) {
        if (action == null) {
            return getTranslation(ACTION_IS_NULL);
        }

        if (StringUtils.nonEmpty(action.getName())) {
            return getTranslation(ACTION_NAME) + ": " + action.getName();
        }

        if (action.getFunctionRef() != null) {
            if (action.getFunctionRef() instanceof String) {
                return getTranslation(ACTION_IS_FUNC) + ": " + action.getFunctionRef();
            }

            FunctionRef functionRef = (FunctionRef) action.getFunctionRef();
            return getTranslation(ACTION_IS_FUNC) + ": " + functionRef.getRefName();
        }

        if (action.getEventRef() != null) {
            return getTranslation(ACTION_IS_EVENT) + ": " + action.getEventRef().getConsumeEventRef();
        }

        if (action.getSubFlowRef() != null) {
            if (action.getSubFlowRef() instanceof String) {
                return getTranslation(ACTION_IS_SUBFLOW) + ": " + action.getSubFlowRef();
            }

            SubFlowRef subFlowRef = (SubFlowRef) action.getSubFlowRef();
            return getTranslation(ACTION_IS_SUBFLOW) + ": " + subFlowRef.getWorkflowId();
        }

        return getTranslation(ACTION_IS_NULL);
    }
}

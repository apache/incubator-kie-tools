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


package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.CONDITION_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION_NAME;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION_VALUE;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION_NAME;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION_VALUE;

public interface HasConditionsAndHasDataFilter extends HasDataFilter {

    default String getConditionsString(DataConditionTransition[] data, EventConditionTransition[] events) {
        if ((data == null || data.length == 0)
                && (events == null || events.length == 0)) {
            return getTranslation(CONDITION_IS_NULL);
        }

        StringBuilder conditionString = new StringBuilder();
        if (data != null && data.length != 0) {
            conditionString.append(getTranslation(DATA_CONDITION_TRANSITION) + ":\r\n");
            for (DataConditionTransition d : data) {
                if (d.getName() != null && !d.getName().isEmpty()) {
                    conditionString.append(getTranslation(DATA_CONDITION_TRANSITION_NAME) + ": " + truncate(d.getName()) + "\r\n");
                } else {
                    conditionString.append(getTranslation(DATA_CONDITION_TRANSITION_VALUE) + ": " + truncate(d.getCondition()) + "\r\n");
                }
            }
            return conditionString.toString();
        }

        if (events != null && events.length != 0) {
            conditionString.append(getTranslation(EVENT_CONDITION_TRANSITION) + ":\r\n");
            for (EventConditionTransition event : events) {
                if (event.getName() != null && !event.getName().isEmpty()) {
                    conditionString.append(getTranslation(EVENT_CONDITION_TRANSITION_NAME) + ": " + truncate(event.getName()) + "\r\n");
                } else {
                    conditionString.append(getTranslation(EVENT_CONDITION_TRANSITION_VALUE) + ": " + truncate(event.eventRef) + "\r\n");
                }
            }
            return conditionString.toString();
        }

        return conditionString.toString();
    }
}

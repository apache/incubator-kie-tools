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

package org.kie.workbench.common.stunner.sw.marshall;

import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

public class DefinitionTypeUtils {

    public static boolean toEnd(Object end) {
        if (end == null) {
            return false;
        }

        if (end instanceof Boolean) {
            return (boolean) end;
        }

        return true;
    }

    public static String getTransition(Object transition) {
        if (transition instanceof String) {
            return (String) transition;
        } else if (transition != null) {
            StateTransition stateTransition = (StateTransition) transition;
            return stateTransition.getNextState();
        }
        return null;
    }

    public static String getStart(Workflow workflow) {
        Object start = workflow.getStart();
        if (start == null) {
            return null;
        }

        if (start instanceof String) {
            return (String) start;
        }

        String stateName = ((StartDefinition) workflow.getStart()).getStateName();
        if (stateName != null) {
            return stateName;
        } else {
            State[] states = workflow.getStates();
            return states[0].getName();
        }
    }
}

package org.kie.workbench.common.stunner.sw.marshall;

import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

public class DefinitionTypeUtils {

    public static boolean toEnd(Object end) {
        if (end instanceof Boolean) {
            return (boolean) end;
        } else if (end instanceof StateEnd) {
            StateEnd stateEnd = (StateEnd) end;
            if (stateEnd.getTerminate() != null) {
                return stateEnd.getTerminate();
            }
        }
        return false;
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

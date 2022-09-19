package org.kie.workbench.common.stunner.sw.marshall;

import jsinterop.base.Js;
import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

public class DefinitionTypeUtils {

    public static final String NEXT_STATE = "nextState";
    public static final String STATE_NAME = "stateName";
    public static final String TERMINATE = "terminate";

    public static boolean getEnd(Object end) {
        if (end instanceof Boolean) {
            return (boolean) end;
        } else if (end != null) {
            StateEnd stateEnd = (StateEnd) end;
            if(stateEnd.getTerminate() != null)
            return stateEnd.getTerminate();
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
        if (start instanceof String) {
            return (String) start;
        } else if (start != null) {
            Object stateName =((StartDefinition)workflow.getStart()).getStateName();
            if (stateName != null) {
                return (String) stateName;
            } else {
                State[] states = workflow.getStates();
                return states[0].getName();
            }
        }

        return null;
    }

    public static Object getObjectProperty(Object object, String propertyName) {
        if (object != null) {
            return Js.asPropertyMap(object).get(propertyName);
        }
        return null;
    }

    public static void setObjectProperty(Object object, String propertyName, Object value) {
        if (object != null) {
            Js.asPropertyMap(object).set(propertyName, value);
        }
    }
}
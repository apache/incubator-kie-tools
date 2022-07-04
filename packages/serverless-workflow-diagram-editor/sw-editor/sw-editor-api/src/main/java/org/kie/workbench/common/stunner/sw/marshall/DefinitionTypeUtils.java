package org.kie.workbench.common.stunner.sw.marshall;

public class DefinitionTypeUtils {

    public static boolean getEnd(Object end) {
        if (end instanceof Boolean) {
            return (boolean) end;
        } else if (end != null) {
            throw new Error("DefinitionTypeUtils.getEnd " + end.getClass().getName());

            //return (boolean) Js.asPropertyMap(end).get("terminate");
        }
        return false;
    }

    public static String getTransition(Object transition) {
        if (transition instanceof String) {
            return (String) transition;
        } else if (transition != null) {
            throw new Error("DefinitionTypeUtils.getTransition " + transition.getClass().getName());

           // return (String) Js.asPropertyMap(transition).get("nextState");
        }
        return null;
    }

    public static String getStart(Object start) {
        if (start instanceof String) {
            return (String) start;
        } else if (start != null) {
            throw new Error("DefinitionTypeUtils.getStart " + start.getClass().getName());
            //return (String) Js.asPropertyMap(start).get("stateName");
        }

        return null;
    }
}

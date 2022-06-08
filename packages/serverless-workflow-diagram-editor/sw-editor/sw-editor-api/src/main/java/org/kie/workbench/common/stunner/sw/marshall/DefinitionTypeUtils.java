package org.kie.workbench.common.stunner.sw.marshall;

import jsinterop.base.Js;

public class DefinitionTypeUtils {

    public static boolean getEnd(Object end) {
        if (end instanceof Boolean) {
            return (boolean) end;
        } else if (end != null) {
            return (boolean) Js.asPropertyMap(end).get("terminate");
        }
        return false;
    }

    public static String getTransition(Object transition) {
        if (transition instanceof String) {
            return (String) transition;
        } else if (transition != null) {
            return (String) Js.asPropertyMap(transition).get("nextState");
        }
        return null;
    }

    public static String getStart(Object start) {
        if (start instanceof String) {
            return (String) start;
        } else if (start != null) {
            return (String) Js.asPropertyMap(start).get("stateName");
        }

        return null;
    }
}

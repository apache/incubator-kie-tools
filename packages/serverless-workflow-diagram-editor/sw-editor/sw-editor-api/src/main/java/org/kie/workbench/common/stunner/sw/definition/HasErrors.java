package org.kie.workbench.common.stunner.sw.definition;

public interface HasErrors<T> {

    ErrorTransition[] getOnErrors();

    T setOnErrors(ErrorTransition[] onErrors);
}

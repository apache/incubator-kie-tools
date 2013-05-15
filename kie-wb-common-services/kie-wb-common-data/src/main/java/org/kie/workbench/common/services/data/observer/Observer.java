package org.kie.workbench.common.services.data.observer;

public class Observer<T> {

    private int hashCode;

    public Observer(T observable) {
        hashCode = observable.hashCode();
    }

    public boolean isDirty(T observable) {
        return hashCode == observable.hashCode();
    }
}

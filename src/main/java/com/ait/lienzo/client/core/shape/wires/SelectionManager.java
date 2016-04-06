package com.ait.lienzo.client.core.shape.wires;

public class SelectionManager {

    private boolean changeInProgress;

    public boolean isChangeInProgress() {
        return changeInProgress;
    }

    public void setChangeInProgress(boolean changeInProgress) {
        this.changeInProgress = changeInProgress;
    }
}

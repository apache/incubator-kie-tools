package org.drools.guvnor.client.mvp;

public interface Saveable {
    /**
     * Run validation so that no broken assets get checked in
     */
    public boolean onBeforeSave(); 

    /* Do the actual saving */
    public void onSave();

    /*
     * If the asset name changed. Inform all the lists that are showing the asset
     */
    public void OnAfterSave();
}

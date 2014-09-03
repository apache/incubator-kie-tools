package org.uberfire.wbtest.client.panels.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class CustomPanelInstanceCounter {

    @Inject Event<CustomPanelInstanceCounter> event;

    private int liveInstances;
    private int creationCount;

    public void instanceCreated() {
        liveInstances++;
        creationCount++;
        event.fire( this );
    }

    public void instanceDestroyed() {
        liveInstances--;
        event.fire( this );
    }

    /**
     * Returns the number of instances that are still alive (created but not destroyed).
     */
    public int getLiveInstances() {
        return liveInstances;
    }

    /**
     * Returns the total number of instances ever created (not decremented when an instance is destroyed).
     */
    public int getCreationCount() {
        return creationCount;
    }

}

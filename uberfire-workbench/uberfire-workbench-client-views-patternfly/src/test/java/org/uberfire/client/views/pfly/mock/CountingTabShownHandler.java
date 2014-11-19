package org.uberfire.client.views.pfly.mock;

import org.gwtbootstrap3.client.shared.event.TabShownEvent;
import org.gwtbootstrap3.client.shared.event.TabShownHandler;


public class CountingTabShownHandler implements TabShownHandler {

    private int eventCount;

    @Override
    public void onShown( TabShownEvent event ) {
        this.eventCount++;
    }

    public int getEventCount() {
        return eventCount;
    }
}

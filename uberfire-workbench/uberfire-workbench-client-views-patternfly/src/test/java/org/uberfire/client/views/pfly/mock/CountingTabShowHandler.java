package org.uberfire.client.views.pfly.mock;

import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;


public class CountingTabShowHandler implements TabShowHandler {

    private int eventCount;

    @Override
    public void onShow( TabShowEvent event ) {
        this.eventCount++;
    }

    public int getEventCount() {
        return eventCount;
    }
}

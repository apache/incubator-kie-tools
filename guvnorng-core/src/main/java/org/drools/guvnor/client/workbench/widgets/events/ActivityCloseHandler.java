package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.shared.EventHandler;

public interface ActivityCloseHandler extends EventHandler {

    void onCloseActivity(ActivityCloseEvent event);
}

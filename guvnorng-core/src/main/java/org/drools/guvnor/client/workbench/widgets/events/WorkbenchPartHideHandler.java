package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.shared.EventHandler;

public interface WorkbenchPartHideHandler extends EventHandler {

    void onHide(WorkbenchPartHideEvent event);
}

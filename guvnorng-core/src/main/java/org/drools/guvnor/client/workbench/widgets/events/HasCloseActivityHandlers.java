package org.drools.guvnor.client.workbench.widgets.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCloseActivityHandlers extends HasHandlers {
    HandlerRegistration addActivityCloseHandler(ActivityCloseHandler activityCloseHandler);
}

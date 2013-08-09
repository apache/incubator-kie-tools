package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasRemoveItemHandlers
        extends HasHandlers {

    HandlerRegistration addRemoveItemHandler(RemoveItemHandler handler);

}

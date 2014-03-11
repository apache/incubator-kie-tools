package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.IsWidget;

public interface OrderableIsWidget extends IsWidget {

    /**
     * Returns the unique identifier of this widget. This ID is used when specifying which headers and footers to retain
     * when running the workbench in standalone (embedded) mode.
     * 
     * @return a unique identifier for this widget
     */
    String getId();

    /**
     * Returns the stacking order of this header.
     * 
     * @return the order this header should be stacked in (higher numbers closer to the top of the screen).
     */
    int getOrder();

}

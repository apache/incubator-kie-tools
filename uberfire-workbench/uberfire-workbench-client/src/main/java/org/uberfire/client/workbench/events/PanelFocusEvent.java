package org.uberfire.client.workbench.events;

import org.uberfire.workbench.events.UberFireEvent;
import org.uberfire.workbench.model.PanelDefinition;

public class PanelFocusEvent implements UberFireEvent {

    private final PanelDefinition panel;

    public PanelFocusEvent( PanelDefinition panel ) {
        this.panel = panel;
    }

    public PanelDefinition getPanel() {
        return panel;
    }

    @Override
    public String toString() {
      return "PanelFocusEvent [panel=" + panel + "]";
    }

}

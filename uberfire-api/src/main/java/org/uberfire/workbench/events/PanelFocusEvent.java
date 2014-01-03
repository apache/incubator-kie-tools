package org.uberfire.workbench.events;

import org.uberfire.workbench.model.PanelDefinition;

public class PanelFocusEvent extends UberFireEvent {

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

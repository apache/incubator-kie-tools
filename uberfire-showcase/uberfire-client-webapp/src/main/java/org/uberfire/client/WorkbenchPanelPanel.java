package org.uberfire.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

public class WorkbenchPanelPanel extends FlowPanel {

    /**
     * Creates a panel with relative positioning that fills its nearest relative or absolute positioned parent.
     */
    public WorkbenchPanelPanel() {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( 100, Unit.PCT );
        getElement().getStyle().setHeight( 100, Unit.PCT );
    }

    /**
     * Creates a panel with relative positioning that has the given fixed height in pixels, and fills the width of its
     * nearest relative or absolute positioned parent.
     */
    public WorkbenchPanelPanel(int height) {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( 100, Unit.PCT );
        getElement().getStyle().setHeight( height, Unit.PX );
    }

    /**
     * Creates a panel with relative positioning that has the given fixed width and height in pixels.
     */
    public WorkbenchPanelPanel(int width, int height) {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( width, Unit.PX );
        getElement().getStyle().setHeight( height, Unit.PX );
    }

}

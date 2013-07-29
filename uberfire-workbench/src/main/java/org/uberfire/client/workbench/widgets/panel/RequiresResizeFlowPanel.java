package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class RequiresResizeFlowPanel
        extends FlowPanel
        implements RequiresResize {

    @Override
    public void onResize() {
        for ( int i = 0; i < getWidgetCount(); i++ ) {
            final Widget activeWidget = getWidget( i );
            if ( activeWidget instanceof RequiresResize ) {
                ( (RequiresResize) activeWidget ).onResize();
            }
        }
    }
}

package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class RequiresResizeFocusPanel
        extends FocusPanel
        implements RequiresResize {

    @Override
    public void onResize() {
        if ( getWidget() instanceof RequiresResize ) {
            ( (RequiresResize) getWidget() ).onResize();
        }
    }
}

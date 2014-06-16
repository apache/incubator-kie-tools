package org.uberfire.client.workbench.widgets.listbar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A FlowPanel that can exist in a hierarchy of {@link LayoutPanel}s. Behaves exactly like FlowPanel, but also
 * propagates <tt>onResize</tt> events to the child widgets.
 */
public class ResizeFlowPanel extends FlowPanel implements RequiresResize, ProvidesResize {

    @Override
    public void onResize() {
        for ( Widget child : this ) {
            if ( child instanceof RequiresResize ) {
                ((RequiresResize) child).onResize();
            }
        }
    }
}
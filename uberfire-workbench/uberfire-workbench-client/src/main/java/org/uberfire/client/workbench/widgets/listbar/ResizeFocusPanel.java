package org.uberfire.client.workbench.widgets.listbar;

import org.uberfire.client.util.Layouts;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A FocusPanel that can exist in a hierarchy of {@link LayoutPanel}s. Behaves exactly like FocusPanel, but also
 * propagates <tt>onResize</tt> events to the child widget.
 */
public class ResizeFocusPanel extends FocusPanel implements RequiresResize, ProvidesResize {

    public ResizeFocusPanel() {
    }

    public ResizeFocusPanel( Widget child ) {
        super( child );
        Layouts.setToFillParent( this );
    }

    @Override
    public void onResize() {
        if ( getWidget() instanceof RequiresResize ) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}

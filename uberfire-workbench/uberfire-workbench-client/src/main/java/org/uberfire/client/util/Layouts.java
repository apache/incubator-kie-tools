package org.uberfire.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;


public class Layouts {

    /**
     * Sets the CSS on the given widget so it automatically fills the available space. This uses the same technique as
     * GWT's LayoutPanel system: absoulte positioning with top, left, right, and bottom set to 0px. Note that, like
     * LayoutPanels, this only works if the host page is in standards mode (has a {@code <!doctype html>} header).
     * 
     * @param w
     *            the widget that should always fill its available space, rather than being sized to fit its contents.
     */
    public static void setToFillParent( Widget w ) {
        Element e = w.getElement();
        Style s = e.getStyle();
        s.setPosition( Position.ABSOLUTE );
        s.setTop( 0.0, Unit.PX );
        s.setLeft( 0.0, Unit.PX );
        s.setRight( 0.0, Unit.PX );
        s.setBottom( 0.0, Unit.PX );
    }

    public static String getContainmentHierarchy( Widget w ) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        while (w != null) {
            sb.append("  " + depth + " - " + w.getOffsetWidth() + "x" + w.getOffsetHeight() + " - " +
                    w.getClass().getName() + "@" + System.identityHashCode( w ) +
                    (w instanceof RequiresResize ? " RequiresResize" : "") +
                    (w instanceof ProvidesResize ? " ProvidesResize" : "") +
                    " position: " + w.getElement().getStyle().getPosition());
            w.ensureDebugId("containment-parent-" + depth);
            w = w.getParent();
            depth++;
        }
        return sb.toString();
    }
}

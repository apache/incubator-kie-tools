package org.uberfire.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;


public class Layouts {

    /**
     * Sets the CSS on the given widget so it automatically fills the available space, rather than being sized based on
     * the amount of space required by its contents. This tends to be useful when building a UI that always fills the
     * available space on the screen, as most desktop application windows do.
     * <p>
     * To achieve this, the element is given relative positioning with top and left set to 0px and width and height set
     * to 100%. This makes the widget fill its nearest ancestor which has relative or absolute positioning. This
     * technique is compatible with GWT's LayoutPanel system. Note that, like LayoutPanels, this only works if the host
     * page is in standards mode (has a {@code <!DOCTYPE html>} header).
     *
     * @param w
     *            the widget that should always fill its available space, rather than being sized to fit its contents.
     */
    public static void setToFillParent( Widget w ) {
        Element e = w.getElement();
        Style s = e.getStyle();
        s.setPosition( Position.RELATIVE );
        s.setTop( 0.0, Unit.PX );
        s.setLeft( 0.0, Unit.PX );
        s.setWidth( 100.0, Unit.PCT );
        s.setHeight( 100.0, Unit.PCT );
    }

    /**
     * Returns a multi-line string detailing layout information about the given widget and each of its ancestors in the
     * widget tree.
     *
     * @param w the widget to start at. Null is permitted, and results in this method returning an empty string.
     * @return information about w and its ancestors, one widget per line.
     */
    public static String getContainmentHierarchy( Widget w ) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        while ( w != null ) {
            sb.append( "  " + depth + " - " + widgetInfo( w ) );
            w.ensureDebugId( "containment-parent-" + depth );
            w = w.getParent();
            depth++;
        }
        return sb.toString();
    }

    private static String widgetInfo( Widget w ) {
        String widgetInfo;
        try {
            widgetInfo = w.getOffsetWidth() + "x" + w.getOffsetHeight() + " - " +
                    w.getClass().getName() + "@" + System.identityHashCode( w ) +
                    (w instanceof RequiresResize ? " RequiresResize" : "") +
                    (w instanceof ProvidesResize ? " ProvidesResize" : "") +
                    " position: " + w.getElement().getStyle().getPosition() + "\n";
        } catch ( Throwable t ) {
            widgetInfo = "?x? - " +
                    w.getClass().getName() + "@" + System.identityHashCode( w ) +
                    ": " + t.toString() + "\n";
        }
        return widgetInfo;
    }

    /**
     * Returns a multi-line string detailing layout information about the given widget and each of its descendants in
     * the widget tree.
     *
     * @param w
     *            the widget to start at. Null is permitted.
     * @return information about w and its descendants, one widget per line. Each line is indented with leading spaces
     *         to illustrate the containment hierarchy.
     */
    public static String getContainedHierarchy( final Widget startAt ) {
        IndentedLineAccumulator result = new IndentedLineAccumulator();
        getContainedHierarchyRecursively( startAt, 0, result );
        return result.toString();
    }

    private static void getContainedHierarchyRecursively( final Widget startAt,
                                                          int depth,
                                                          IndentedLineAccumulator result ) {
        if ( startAt == null ) {
            result.append( depth,
                           "(null)" );
            return;
        }
        result.append( depth,
                       widgetInfo( startAt ) );
        if ( startAt instanceof HasWidgets ) {
            for ( Widget child : ((HasWidgets) startAt) ) {
                getContainedHierarchyRecursively( child,
                                                  depth + 1,
                                                  result );
            }
        } else if ( startAt instanceof Composite ) {
            getContainedHierarchyRecursively( extractWidget( ((Composite) startAt) ),
                                              depth + 1,
                                              result );
        }
    }

    private static class IndentedLineAccumulator {
        final StringBuilder sb = new StringBuilder();

        private void append( int depth, String s ) {
            for ( int i = 0; i < depth; i++ ) {
                sb.append(" ");
            }
            sb.append( s );
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    private static native Widget extractWidget( Composite composite ) /*-{
        return composite.@com.google.gwt.user.client.ui.Composite::widget;
    }-*/;

}

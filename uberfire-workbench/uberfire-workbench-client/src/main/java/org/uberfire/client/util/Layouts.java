package org.uberfire.client.util;

import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.debug.Debug;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class Layouts {

    public static final int DEFAULT_CHILD_SIZE = 100;

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
        return getContainmentHierarchy( w, false );
    }

    /**
     * Returns a multi-line string detailing layout information about the given widget and each of its ancestors in the
     * widget tree, optionally setting debug IDs on each widget to assist in locating them in browser DOM explorer
     * tools.
     *
     * @param w
     *            the widget to start at. Null is permitted, and results in this method returning an empty string.
     * @param setDebugIds
     *            if true, the element and each of its ancestors will have its ID set to
     *            <code>"containment-parent-<i>depth</i>"</code>, where depth is 0 for the given widget, 1 for
     *            its parent, 2 for its grandparent, and so on. This ID will replace any ID that was previously set on
     *            the element, so it may break some CSS and even javascript functionality. Use with caution.
     * @return information about w and its ancestors, one widget per line.
     */
    public static String getContainmentHierarchy( Widget w, boolean setDebugIds ) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        while ( w != null ) {
            if ( setDebugIds ) {
                w.getElement().setId( "containment-parent-" + depth );
            }
            sb.append( "  " + depth + " - " + widgetInfo( w ) );
            w = w.getParent();
            depth++;
        }
        return sb.toString();
    }

    private static String widgetInfo( Widget w ) {
        String widgetInfo;
        try {
            String id = w.getElement().getId();
            widgetInfo = w.getOffsetWidth() + "x" + w.getOffsetHeight() + " - " +
                    Debug.objectId( w ) +
                    (id != null && id.length() > 0 ? " id=" + id : "" ) +
                    (w instanceof SplitPanel ? " divider at " + ((SplitPanel) w).getFixedWidgetSize() : "") +
                    (w instanceof RequiresResize ? " RequiresResize" : "") +
                    (w instanceof ProvidesResize ? " ProvidesResize" : "") +
                    " position: " + w.getElement().getStyle().getPosition() + "\n";
        } catch ( Throwable t ) {
            widgetInfo = "?x? - " +
                    Debug.objectId( w ) +
                    ": " + t.toString() + "\n";
        }
        return widgetInfo;
    }

    /**
     * Returns a multi-line string detailing layout information about the given widget and each of its descendants in
     * the widget tree.
     *
     * @param startAt
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

    /**
     * Returns the current width or height of the given panel definition.
     *
     * @param position
     *            determines which dimension (width or height) to return.
     * @param definition
     *            the definition to get the size information from.
     * @return the with if position is EAST or WEST; the height if position is NORTH or SOUTH. If no size is provided by the PanelDefinition the DEFAULT_CHILD_SIZE is used.
     */
    public static int widthOrHeight( CompassPosition position,
                                     PanelDefinition definition ) {
        switch ( position ) {
            case NORTH:
            case SOUTH:
                return heightOrDefault( definition );
            case EAST:
            case WEST:
                return widthOrDefault( definition );
            default:
                throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
        }
    }

    public static int heightOrDefault( PanelDefinition def ) {
        return def.getHeight() == null ? DEFAULT_CHILD_SIZE : def.getHeight();
    }

    public static int widthOrDefault( PanelDefinition def ) {
        return def.getWidth() == null ? DEFAULT_CHILD_SIZE : def.getWidth();
    }

    /**
     * Disables the scrolling behaviour of the nearest scrollpanel found in the given widget's containment hierarchy.
     * <p>
     * FIXME this is a really horrible workaround! should instead modify UF API to allow PanelDefinition to opt out of having a scroll panel.
     * The better fix would require changes to:
     * <ul>
     *  <li>WorkbenchPartPresenter.View
     *  <li>WorkbenchPartView and its mock
     *  <li>The @WorkbenchPanel annotation
     *  <li>The annotation processor code generators and their tests
     * </ul>
     *
     * @return true if a scroll panel was found and disabled; false if no scroll panel was found.
     */
    public static boolean disableNearestScrollPanel( Widget w ) {
        while ( w != null ) {
            if ( w instanceof ScrollPanel ) {
                w.getElement().getStyle().clearOverflow();
                w.getElement().getParentElement().getStyle().clearOverflow();
                return true;
            }
            w = w.getParent();
        }
        return false;
    }
}

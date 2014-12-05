package org.uberfire.client.workbench;

import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used by the workbench to construct the outer most DOM structure (header, footer and perspective container).
 * Uberfire provides a default {@link org.uberfire.client.workbench.WorkbenchLayoutImpl} that can be replaced
 * through the mechanism described in {@link org.uberfire.client.workbench.LayoutSelection}.
 */
public interface WorkbenchLayout {

    /**
     * Sets the given widgets as the contents of the header area, replacing any existing widgets that were already in
     * the header area. Each widget is meant to fill the width of the page, and the widgets are meant to stack on top of
     * each other with the first one in the list being highest up on the page. Headers should remain in place even when
     * the perspective switches.
     *
     * @param headers the list of headers in top-to-bottom stacking order. Never null, but can be empty.
     */
    void setHeaderContents( List<Header> headers );

    /**
     * Sets the given widgets as the contents of the footer area, replacing any existing widgets that were already in
     * the footer area. Each widget is meant to fill the width of the page, and the widgets are meant to stack on top of
     * each other with the first one in the list being highest up on the page. Footers should remain in place even when
     * the perspective switches.
     *
     * @param footers the list of footer in top-to-bottom stacking order. Never null, but can be empty.
     */
    void setFooterContents( List<Footer> footers );

    /**
     * Gives access to the root container element that will be attached to the {@link com.google.gwt.user.client.ui.RootLayoutPanel}.
     * @return the outer most workbench widget
     */
    IsWidget getRoot();

    /**
     * Gives access to the element of the workbench that hosts perspective widgets.
     * @return the perspective container element
     */
    HasWidgets getPerspectiveContainer();

    /**
     * Will be invoked by the {@link org.uberfire.client.workbench.Workbench}
     * when the discovery of header and footer elements is completed.
     *
     * @see {@link #setHeaderContents(java.util.List)}
     * @see {@link #setFooterContents(java.util.List)}
     */
    public void onBootstrap();

    /**
     * The {@link org.uberfire.client.workbench.Workbench} listens for resize events and hands them off
     * to the layout. Not needed if your layout is based on {@link com.google.gwt.user.client.ui.LayoutPanel}'s.
     * Kept for backwards compatibility.
     */
    void onResize();

    /**
     * See {@link #onResize()}
     * @param width
     * @param height
     */
    void resizeTo(int width, int height);

    /**
     * Makes the given widget fill the entire space normally dedicated to the perspective container. Has no effect if
     * the given widget is already maximized.
     *
     * @param w the Widget to maximize.
     */
    void maximize( Widget w );

    /**
     * Restores a previously maximized widget to its original size and position. Has no effect if the given widget is
     * not currently in a maximized state set up by {@link #maximize(Widget)}.
     *
     * @param w the Widget to restore to its original size and location.
     */
    void unmaximize( Widget w );
}

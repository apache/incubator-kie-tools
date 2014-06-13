package org.uberfire.client.workbench;

import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Heiko Braun
 * @date 05/06/14
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
     * @param footer the list of footer in top-to-bottom stacking order. Never null, but can be empty.
     */
    void setFooterContents( List<Footer> footers );

    IsWidget getRoot();
    HasWidgets getPerspectiveContainer();

    public void onBootstrap();

    // resize handling is kept for backwards compatibility
    void onResize();
    void resizeTo(int width, int height);

}

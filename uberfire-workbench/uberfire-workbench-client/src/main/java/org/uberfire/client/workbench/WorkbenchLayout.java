package org.uberfire.client.workbench;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Heiko Braun
 * @date 05/06/14
 */
public interface WorkbenchLayout {


    <T> void addMargin(Class<T> marginType, IsWidget widget);
    IsWidget getRoot();
    HasWidgets getPerspectiveContainer();

    public void onBootstrap();

    // resize handling is kept for backwards compatibility
    void onResize();
    void resizeTo(int width, int height);

}

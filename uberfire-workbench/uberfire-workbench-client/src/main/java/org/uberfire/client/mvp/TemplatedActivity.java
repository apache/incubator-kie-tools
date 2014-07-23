package org.uberfire.client.mvp;

import org.uberfire.workbench.model.NamedPosition;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;


public interface TemplatedActivity {

    /**
     * Returns the widget that contains the child WorkbenchPanelView at the given position.
     * 
     * @return the widget that contains the child at the given position, or null if the given position does not exist
     *         within this activity's view.
     */
    HasWidgets resolvePosition( NamedPosition p );

    /**
     * Returns the widget that is the root panel of this activity.
     */
    IsWidget getRootWidget();

}

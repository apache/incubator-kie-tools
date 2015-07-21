package org.uberfire.client.workbench.docks;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Uberfire Dock Support
 */
public interface UberfireDocks {

    void register( UberfireDock... docks );

    void setup( DockLayoutPanel rootContainer );

    void disable(UberfireDockPosition position, String perspectiveName);

    void enable(UberfireDockPosition position, String perspectiveName);

}

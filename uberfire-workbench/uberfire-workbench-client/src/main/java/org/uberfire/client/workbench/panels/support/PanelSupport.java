package org.uberfire.client.workbench.panels.support;

import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.Widget;

public interface PanelSupport {

    void addPanel( final PanelDefinition panel,
                   final WorkbenchPanelView newView,
                   final WorkbenchPanelView targetView,
                   final Position position );

    boolean remove( final WorkbenchPanelView<?> view,
                    final Widget parent );
}

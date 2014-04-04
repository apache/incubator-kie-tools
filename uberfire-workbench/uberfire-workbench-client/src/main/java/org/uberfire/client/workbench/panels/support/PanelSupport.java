package org.uberfire.client.workbench.panels.support;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.BaseWorkbenchPanelView;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public interface PanelSupport {

    void addPanel( final PanelDefinition panel,
                   final WorkbenchPanelView newView,
                   final WorkbenchPanelView targetView,
                   final Position position );

    void remove( final BaseWorkbenchPanelView<?> view,
                 final Widget parent );
}

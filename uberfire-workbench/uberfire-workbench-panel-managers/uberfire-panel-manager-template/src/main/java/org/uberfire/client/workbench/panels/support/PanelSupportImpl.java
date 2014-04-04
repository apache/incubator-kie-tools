package org.uberfire.client.workbench.panels.support;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.BaseWorkbenchPanelView;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
public class PanelSupportImpl implements PanelSupport {

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView newView,
                          final WorkbenchPanelView targetView,
                          final Position position ) {
    }

    @Override
    public void remove( final BaseWorkbenchPanelView<?> view,
                        final Widget parent ) {

    }
}

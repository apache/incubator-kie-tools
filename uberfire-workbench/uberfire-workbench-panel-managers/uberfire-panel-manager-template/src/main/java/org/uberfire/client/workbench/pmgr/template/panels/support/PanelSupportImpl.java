package org.uberfire.client.workbench.pmgr.template.panels.support;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class PanelSupportImpl implements PanelSupport {

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView newView,
                          final WorkbenchPanelView targetView,
                          final Position position ) {
    }

    @Override
    public boolean remove( final WorkbenchPanelView<?> view,
                           final Widget parent ) {
        return false;
    }
}

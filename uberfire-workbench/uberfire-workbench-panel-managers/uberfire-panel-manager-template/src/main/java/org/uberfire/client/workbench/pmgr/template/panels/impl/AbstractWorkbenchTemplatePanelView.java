package org.uberfire.client.workbench.pmgr.template.panels.impl;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public abstract class AbstractWorkbenchTemplatePanelView<P extends WorkbenchPanelPresenter>
        extends Composite
        implements WorkbenchPanelView<P> {

    @Inject
    protected WorkbenchDragAndDropManager dndManager;

    @Inject
    protected PanelManager panelManager;

    @Inject
    protected BeanFactory factory;

    protected P presenter;

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {

    }

    @Override
    public void removePanel() {

    }

}

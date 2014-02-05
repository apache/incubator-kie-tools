package org.uberfire.client.workbench.panels.impl;

import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;

public class MultiListWorkbenchPanelViewUnitTestWrapper extends MultiListWorkbenchPanelView {

    public void setupMocks( ListBarWidget listBar,
                            MultiListWorkbenchPanelPresenter presenter ) {
        this.listBar = listBar;
        this.presenter = presenter;
    }

}

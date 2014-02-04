package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;

import static org.mockito.Mockito.*;

public class MultiListWorkbenchPanelViewUnitTestWrapper extends MultiListWorkbenchPanelView {

    public void setupMocks( ListBarWidget listBar,
                            MultiListWorkbenchPanelPresenter presenter ) {
        this.listBar = listBar;
        this.presenter = presenter;
    }

}

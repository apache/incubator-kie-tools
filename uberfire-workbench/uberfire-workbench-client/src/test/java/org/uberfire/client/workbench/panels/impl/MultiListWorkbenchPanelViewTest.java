package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiListWorkbenchPanelViewTest {

    private MultiListWorkbenchPanelViewUnitTestWrapper view;

    @GwtMock
    private ListBarWidget listBar;

    @GwtMock
    private MultiListWorkbenchPanelPresenter presenter;

    @GwtMock
    private UberTabPanel uberTabPanel;

    @Before
    public void setup() {
        view = new MultiListWorkbenchPanelViewUnitTestWrapper();
        view.setupMocks( listBar, presenter );
    }

    @Test
    public void setupWidget() {
        view.setupWidget();

        verify( listBar ).addSelectionHandler( any(SelectionHandler.class) );
        verify( listBar ).addOnFocusHandler( any(Command.class) );

    }

}

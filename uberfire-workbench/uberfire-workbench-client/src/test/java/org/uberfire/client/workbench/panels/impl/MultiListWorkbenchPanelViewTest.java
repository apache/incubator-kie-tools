package org.uberfire.client.workbench.panels.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.mvp.Command;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class MultiListWorkbenchPanelViewTest extends AbstractDockingWorkbenchPanelViewTest {

    @Mock ListBarWidget listBar;
    @Mock MaximizeToggleButton maximizeButton;
    @Mock MultiListWorkbenchPanelPresenter presenter;

    @InjectMocks MultiListWorkbenchPanelView view;

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Before
    public void setup() {
        when( listBar.getMaximizeButton() ).thenReturn( maximizeButton );
    }

    @Test
    public void setupWidget() {
        view.setupWidget();

        verify( listBar ).addSelectionHandler( any(SelectionHandler.class) );
        verify( listBar ).addOnFocusHandler( any(Command.class) );

        verify( maximizeButton ).setVisible( true );
        verify( maximizeButton ).setMaximizeCommand( any( Command.class ) );
        verify( maximizeButton ).setUnmaximizeCommand( any( Command.class ) );
    }

    @Test
    public void shouldPropagateOnResize() {
        view.onResize();
        RequiresResize viewChild = (RequiresResize) view.getWidget();
        verify( viewChild, times( 1 ) ).onResize();
    }
}

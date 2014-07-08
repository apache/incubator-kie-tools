package org.uberfire.client.workbench.panels.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.mvp.Command;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class MultiListWorkbenchPanelViewTest {

    @Mock ListBarWidget listBar;
    @Mock MultiListWorkbenchPanelPresenter presenter;

    @InjectMocks MultiListWorkbenchPanelView view;

    @Before
    public void setup() {
    }

    @Test
    public void setupWidget() {
        view.setupWidget();

        verify( listBar ).addSelectionHandler( any(SelectionHandler.class) );
        verify( listBar ).addOnFocusHandler( any(Command.class) );
    }

    @Test
    public void shouldPropagateOnResize() {
        view.onResize();
        verify( listBar, times( 1 ) ).onResize();
    }
}

package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.workbench.events.MaximizePlaceEvent;
import org.uberfire.workbench.events.MinimizePlaceEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelPresenterTest {

    private SimpleWorkbenchPanelView view;

    @GwtMock
    private PanelManager panelManager;

    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        view = new SimpleWorkbenchPanelView();
        presenter = new SimpleWorkbenchPanelPresenter( view, panelManager, null,null);
    }

    @Test
    public void init() {

        presenter.init();

        assertEquals( presenter, view.getPresenter() );

    }



}

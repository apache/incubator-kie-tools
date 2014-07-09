package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelPresenterTest {

    private SimpleWorkbenchPanelView view;

    @GwtMock
    private PanelManager panelManager;

    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        view = new SimpleWorkbenchPanelView();
        presenter = new SimpleWorkbenchPanelPresenter( view, panelManager, null );
    }

    @Test
    public void init() {

        presenter.init();

        assertEquals( presenter, view.getPresenter() );

    }



}

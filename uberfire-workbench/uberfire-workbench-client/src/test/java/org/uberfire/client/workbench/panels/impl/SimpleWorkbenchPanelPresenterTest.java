package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveManager;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelPresenterTest {

    private SimpleWorkbenchPanelView view;

    @Mock
    private PerspectiveManager perspectiveManager;

    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        view = new SimpleWorkbenchPanelView();
        presenter = new SimpleWorkbenchPanelPresenter( view, perspectiveManager, null );
    }

    @Test
    public void init() {

        presenter.init();

        assertEquals( presenter, view.getPresenter() );

    }



}

package org.uberfire.client.workbench.panels.impl;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

@RunWith(MockitoJUnitRunner.class)
public class SimpleWorkbenchPanelPresenterTest extends AbstractDockingWorkbenchPanelPresenterTest {

    @Mock(name="view")
    protected WorkbenchPanelView<SimpleWorkbenchPanelPresenter> view;

    @InjectMocks
    SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setUp2() {
        presenter.init();
        presenter.setDefinition( panelPresenterPanelDefinition );
    }

    @Override
    AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest() {
        return presenter;
    }

    @Test
    public void initShouldBindPresenterToView() {
        verify( view ).init( presenter );
    }
}

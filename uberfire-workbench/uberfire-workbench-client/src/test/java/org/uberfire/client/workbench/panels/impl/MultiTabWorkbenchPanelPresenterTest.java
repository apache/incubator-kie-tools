package org.uberfire.client.workbench.panels.impl;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MultiTabWorkbenchPanelPresenterTest extends AbstractMultiPartWorkbenchPanelPresenterTest {

    @InjectMocks
    private MultiTabWorkbenchPanelPresenter presenter;

    @Before
    public void setUp2() {
        presenter.setDefinition( panelPresenterPanelDefinition );
    }

    @Override
    AbstractMultiPartWorkbenchPanelPresenter<?> getPresenterToTest() {
        return presenter;
    }

    // tests are inherited from superclass
}

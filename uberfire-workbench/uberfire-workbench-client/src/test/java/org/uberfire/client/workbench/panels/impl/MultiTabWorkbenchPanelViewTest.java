package org.uberfire.client.workbench.panels.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class MultiTabWorkbenchPanelViewTest {

    @InjectMocks
    private MultiTabWorkbenchPanelView view;

    @Mock
    private UberTabPanel uberTabPanel;

    @Mock
    private MaximizeToggleButton maximizeButton;

    @Mock
    private ResizeFlowPanel partViewContainer;

    @Before
    public void setup() {
        Element uberTabPanelElement = mock( Element.class );
        Style uberTabPanelElementStyle = mock( Style.class );
        when( uberTabPanel.getElement() ).thenReturn( uberTabPanelElement );
        when( uberTabPanelElement.getStyle() ).thenReturn( uberTabPanelElementStyle );
    }

    @Test
    public void setupWidget() {
        view.setupWidget();
        //assert event handlers
        verify( uberTabPanel ).addSelectionHandler(any(SelectionHandler.class));
        verify( uberTabPanel ).addOnFocusHandler( any( Command.class ));
    }

}

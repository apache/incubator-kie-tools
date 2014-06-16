package org.uberfire.client.workbench.panels.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class MultiTabWorkbenchPanelViewTest {

    private MultiTabWorkbenchPanelView view;

    @GwtMock
    private UberTabPanel uberTabPanel;

    @Before
    public void setup() {
        view = new MultiTabWorkbenchPanelView(){
            @Override
            UberTabPanel getUberTabPanel() {
                return uberTabPanel;
            }
        };

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

package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiTabWorkbenchPanelViewTest {

    private MultiTabWorkbenchPanelView view;

    @GwtMock
    private UberTabPanel uberTabPanel;

    @Before
    public void setup() {
        view = new MultiTabWorkbenchPanelView(){
            UberTabPanel getUberTabPanel() {
                return uberTabPanel;
            }
        };
    }

    @Test
    public void setupWidget() {
        view.setupWidget();
        //assert event handlers
        verify( uberTabPanel ).addSelectionHandler(any(SelectionHandler.class));
        verify( uberTabPanel ).addOnFocusHandler( any( Command.class ));
    }

}

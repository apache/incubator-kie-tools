package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.workbench.model.Position;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class VerticalSplitterPanelTest {

    private VerticalSplitterPanelUnitTestWrapper panel;

    private SimpleWorkbenchPanelViewUnitTestWrapper northWidget;
    private SimpleWorkbenchPanelViewUnitTestWrapper southWidget;

    private SimpleWorkbenchPanelPresenter presenterNorth;
    private SimpleWorkbenchPanelPresenter presenterSouth;

    @GwtMock
    private SimpleLayoutPanel northWidgetContainer;

    private SimpleLayoutPanel southWidgetContainer;

    @GwtMock
    private PanelManager panelManager;

    @GwtMock
    private WorkbenchSplitLayoutPanel workbenchSplitLayoutPanel;

    @Before
    public void setup() {
        southWidgetContainer = GWT.create( SimpleLayoutPanel.class );
        panel = new VerticalSplitterPanelUnitTestWrapper();
        northWidget = new SimpleWorkbenchPanelViewUnitTestWrapper();
        presenterNorth = new SimpleWorkbenchPanelPresenter( northWidget, panelManager, null, null );
        northWidget.setupPresenterAndParentMock( presenterNorth );

        southWidget = new SimpleWorkbenchPanelViewUnitTestWrapper();
        presenterSouth = new SimpleWorkbenchPanelPresenter( southWidget, panelManager, null, null );
        southWidget.setupPresenterAndParentMock( presenterSouth );

        panel.setupMocks( workbenchSplitLayoutPanel, northWidgetContainer, southWidgetContainer );
    }

    @Test
    public void testSetupNorth() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( northWidget, southWidget, Position.NORTH, preferredSize, preferredMinSize );

        verify( workbenchSplitLayoutPanel ).addNorth( eq( northWidgetContainer ), anyDouble() );
        verify( workbenchSplitLayoutPanel ).add( eq( southWidgetContainer ) );
        verify( workbenchSplitLayoutPanel, never() ).addSouth( any( SimpleLayoutPanel.class ), anyDouble());

    }

    @Test
    public void testSetupSouth() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( northWidget, southWidget, Position.SOUTH, preferredSize, preferredMinSize );

        verify( workbenchSplitLayoutPanel ).addSouth( eq( southWidgetContainer ), anyDouble() );
        verify( workbenchSplitLayoutPanel ).add( eq( northWidgetContainer ) );
        verify( workbenchSplitLayoutPanel, never() ).addNorth( any( SimpleLayoutPanel.class ), anyDouble());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupOtherPosition() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( northWidget, southWidget, Position.EAST, preferredSize, preferredMinSize );

    }

}

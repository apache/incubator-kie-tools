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
public class HorizontalSplitterPanelTest {

    private HorizontalSplitterPanelUnitTestWrapper panel;

    private SimpleWorkbenchPanelViewUnitTestWrapper eastWidget;
    private SimpleWorkbenchPanelViewUnitTestWrapper westWidget;

    private SimpleWorkbenchPanelPresenter presenterEast;
    private SimpleWorkbenchPanelPresenter presenterWest;

    @GwtMock
    private SimpleLayoutPanel eastWidgetContainer;

    private SimpleLayoutPanel westWidgetContainer;

    @GwtMock
    private PanelManager panelManager;

    @GwtMock
    private WorkbenchSplitLayoutPanel workbenchSplitLayoutPanel;

    @Before
    public void setup() {
        westWidgetContainer = GWT.create( SimpleLayoutPanel.class );
        panel = new HorizontalSplitterPanelUnitTestWrapper();

        eastWidget = new SimpleWorkbenchPanelViewUnitTestWrapper();
        presenterEast = new SimpleWorkbenchPanelPresenter( eastWidget, panelManager, null, null );
        eastWidget.setupPresenterAndParentMock( presenterEast );

        westWidget = new SimpleWorkbenchPanelViewUnitTestWrapper();
        presenterWest = new SimpleWorkbenchPanelPresenter( westWidget, panelManager, null, null );
        westWidget.setupPresenterAndParentMock( presenterWest );

        panel.setupMocks( workbenchSplitLayoutPanel, eastWidgetContainer, westWidgetContainer );
    }

    @Test
    public void testSetupEast() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( eastWidget, westWidget, Position.EAST, preferredSize, preferredMinSize );

        verify( workbenchSplitLayoutPanel ).addEast( eq( eastWidgetContainer ), anyDouble() );
        verify( workbenchSplitLayoutPanel ).add( eq( westWidgetContainer ) );
        verify( workbenchSplitLayoutPanel, never() ).addWest( any( SimpleLayoutPanel.class ), anyDouble());

    }

    @Test
    public void testSetupWest() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( eastWidget, westWidget, Position.WEST, preferredSize, preferredMinSize );

        verify( workbenchSplitLayoutPanel ).addWest( eq( westWidgetContainer ), anyDouble() );
        verify( workbenchSplitLayoutPanel ).add( eq( eastWidgetContainer ) );
        verify( workbenchSplitLayoutPanel, never() ).addEast( any( SimpleLayoutPanel.class ), anyDouble() );

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetupOtherPosition() {

        //default size
        Integer preferredSize = null;
        Integer preferredMinSize = null;
        panel.setup( eastWidget, westWidget, Position.NORTH, preferredSize, preferredMinSize );

    }

}

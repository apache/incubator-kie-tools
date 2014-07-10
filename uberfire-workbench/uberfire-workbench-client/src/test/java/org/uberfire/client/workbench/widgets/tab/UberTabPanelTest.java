package org.uberfire.client.workbench.widgets.tab;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class UberTabPanelTest {

    private UberTabPanelUnitTestWrapper uberTabPanel;
    private WorkbenchPartPresenter.View presenter;
    @GwtMock
    private WorkbenchDragAndDropManager dndManager;
    @Mock private PanelManager panelManager;

    @Before
    public void setup() {
        uberTabPanel = new UberTabPanelUnitTestWrapper( panelManager );
        uberTabPanel.setupMocks( dndManager );
        presenter = GWT.create( WorkbenchPartPresenter.View.class );
    }

    @Test
    public void assertInstanciationSequence() {
        assertNotNull( uberTabPanel.tabPanel );
    }

    @Test
    public void createTabTest() {
        Tab tab = uberTabPanel.createTab( presenter, true, 1, 1 );

        verify(tab).addClickHandler( any( ClickHandler.class ) );

        verify(tab).add( any( Widget.class ) );

        assertEquals( tab.asTabLink(), uberTabPanel.tabIndex.get( presenter ) );
        assertEquals( presenter, uberTabPanel.tabInvertedIndex.get( tab.asTabLink() ));

        verify(dndManager).makeDraggable( any( WorkbenchPartPresenter.View.class ), any( Widget.class ) );

        Class<Widget> closeButtom = Widget.class;
        verify(tab).addDecorate( any( closeButtom ) );

    }

    @Test
    public void addPartTest() {
        uberTabPanel.addPart(presenter );
        verify(uberTabPanel.tabPanelSpy).add( any(Tab.class) );
    }

    @Test
    public void addPartLastTabisDrownTabTest() {
        uberTabPanel.setLastTabIsDropdownTab( true );
        uberTabPanel.addPart( presenter );
        verify(uberTabPanel.tabPanelSpy).add( any(DropdownTab.class) );
    }


}

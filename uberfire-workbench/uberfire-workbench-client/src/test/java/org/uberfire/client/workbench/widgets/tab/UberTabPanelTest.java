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
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import com.github.gwtbootstrap.client.ui.Tab;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class UberTabPanelTest {

    private UberTabPanelUnitTestWrapper uberTabPanel;
    private WorkbenchPartPresenter partPresenter;
    private WorkbenchPartPresenter.View partView;
    private PartDefinition partDef;

    @GwtMock
    private WorkbenchDragAndDropManager dndManager;
    @Mock private PanelManager panelManager;

    @Before
    public void setup() {
        uberTabPanel = new UberTabPanelUnitTestWrapper( panelManager );
        uberTabPanel.setupMocks( dndManager );
        partView = GWT.create( WorkbenchPartPresenter.View.class );
        partDef = new PartDefinitionImpl( new DefaultPlaceRequest( "SomeWorkbenchScreen" ) );
        partPresenter = mock( WorkbenchPartPresenter.class );
        when( partView.getPresenter() ).thenReturn( partPresenter );
        when( partPresenter.getDefinition() ).thenReturn( partDef );
    }

    @Test
    public void assertInstanciationSequence() {
        assertNotNull( uberTabPanel.tabPanel );
    }

    @Test
    public void createTabTest() {
        Tab tab = uberTabPanel.createTab( partView, true, 1, 1 );

        verify(tab).addClickHandler( any( ClickHandler.class ) );

        verify(tab).add( any( Widget.class ) );

        assertEquals( tab.asTabLink(), uberTabPanel.tabIndex.get( partView ) );
        assertEquals( partView, uberTabPanel.tabInvertedIndex.get( tab.asTabLink() ));

        verify(dndManager).makeDraggable( any( WorkbenchPartPresenter.View.class ), any( Widget.class ) );

        Class<Widget> closeButtom = Widget.class;
        verify(tab).addDecorate( any( closeButtom ) );

    }

//    @Test
//    public void addPartTest() {
//        uberTabPanel.addPart( partView );
//        verify(uberTabPanel.tabPanelSpy).add( any(Tab.class) );
//    }
//
//    @Test
//    public void addPartLastTabIsDropdownTabTest() {
//        uberTabPanel.addPart( partView );
//        verify(uberTabPanel.tabPanelSpy).add( any(DropdownTab.class) );
//    }

    @Test
    public void shouldFireFocusEventWhenClickedWhenUnfocused() throws Exception {
        uberTabPanel.setFocus( false );

        final int[] focusEventCount = new int[1];
        uberTabPanel.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                focusEventCount[0]++;
            }
        } );

        uberTabPanel.onClick( mock( ClickEvent.class ) );
        assertEquals( 1, focusEventCount[0] );
    }
}

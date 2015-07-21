package org.uberfire.client.docks;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireDocksImplTest {

    private UberfireDocksImpl docks;

    @Mock
    private DockLayoutPanel dockLayoutPanel;
    private UberfireDock dock1;
    private UberfireDock dock4;
    private UberfireDock dock3;
    private UberfireDock dock2;

    @Before
    public void setUp() throws Exception {
        docks = new UberfireDocksImpl();

        dock1 = new UberfireDock(UberfireDockPosition.SOUTH, new DefaultPlaceRequest("Dock1"), "Home");
        dock2 = new UberfireDock(UberfireDockPosition.SOUTH, new DefaultPlaceRequest("Dock2"), "Another");
        dock3 = new UberfireDock(UberfireDockPosition.EAST, new DefaultPlaceRequest("Dock3"), "Another");
        dock4 = new UberfireDock(UberfireDockPosition.SOUTH, new DefaultPlaceRequest("Dock4"), "Another");
    }

    @Test
    public void testInit() throws Exception {
        docks.init();

        assertEquals(UberfireDockPosition.EAST, docks.eastCollapsed.getPosition());
        assertEquals(UberfireDockPosition.EAST, docks.eastExpanded.getPosition());
        assertEquals(UberfireDockPosition.WEST, docks.westCollapsed.getPosition());
        assertEquals(UberfireDockPosition.WEST, docks.westExpanded.getPosition());
        assertEquals(UberfireDockPosition.SOUTH, docks.southCollapsed.getPosition());
        assertEquals(UberfireDockPosition.SOUTH, docks.southExpanded.getPosition());

    }

    @Test
    public void testSetup() throws Exception {
        docks.init();
        docks.setup(dockLayoutPanel);

        verify(dockLayoutPanel).addSouth(docks.southCollapsed, docks.southCollapsed.widgetSize());
        verify(dockLayoutPanel).addSouth(docks.southExpanded, docks.southExpanded.defaultWidgetSize());
        verify(dockLayoutPanel).addEast(docks.eastCollapsed, docks.eastCollapsed.widgetSize());
        verify(dockLayoutPanel).addEast(docks.eastExpanded, docks.eastExpanded.defaultWidgetSize());
        verify(dockLayoutPanel).addWest(docks.westCollapsed, docks.westCollapsed.widgetSize());
        verify(dockLayoutPanel).addWest(docks.westExpanded, docks.westExpanded.defaultWidgetSize());

        verifyCollapseAllDocks(dockLayoutPanel, 1);

    }

    @Test
    public void testRegisterWithoutAssociatedPerspective() throws Exception {
        docks.init();
        docks.setup(dockLayoutPanel);

        UberfireDock dockWithoutPerspective = new UberfireDock(UberfireDockPosition.SOUTH, new DefaultPlaceRequest("Dock4"));

        docks.register(dockWithoutPerspective);

        assertTrue(docks.avaliableDocks.contains(dockWithoutPerspective));
        assertTrue(docks.docksPerPerspective.isEmpty());

        AllDocksMenu allDocksMenu = docks.southCollapsed.getAllDocksMenu();
        assertEquals(1, allDocksMenu.getCurrentDocks().size());
    }

    @Test
    public void testRegister() throws Exception {
        docks.init();
        docks.setup(dockLayoutPanel);

        docks.register(dock1, dock2);

        assertTrue(docks.avaliableDocks.contains(dock1));
        assertTrue(docks.avaliableDocks.contains(dock2));
        Set<UberfireDock> home = docks.docksPerPerspective.get("Home");
        assertTrue(home.contains(dock1));

        AllDocksMenu allDocksMenu = docks.southCollapsed.getAllDocksMenu();
        assertEquals(2, allDocksMenu.getCurrentDocks().size());
    }

    @Test
    public void testRegisterTwoDocksWithSameNameDisplayOnceOnAvaliablePerspectives() throws Exception {
        docks.init();
        docks.setup(dockLayoutPanel);

        UberfireDock dock5 = new UberfireDock(UberfireDockPosition.SOUTH, new DefaultPlaceRequest("Dock4"), "Another");

        docks.register(dock1, dock2, dock3, dock4, dock5);

        AllDocksMenu allDocksMenu = docks.southCollapsed.getAllDocksMenu();
        assertEquals(4, allDocksMenu.getCurrentDocks().size());
    }

    @Test
    public void testPerspectiveChangeEvent() throws Exception {
        docks.init();
        docks.setup(dockLayoutPanel);

        docks.register(dock1, dock2, dock3, dock4);

        docks.perspectiveChangeEvent(new PerspectiveChange(null, null, null, "Another"));

        assertEquals(2, docks.southCollapsed.getDocksItems().size());
        assertEquals(1, docks.eastCollapsed.getDocksItems().size());
        assertEquals(0, docks.westCollapsed.getDocksItems().size());

        docks.perspectiveChangeEvent(new PerspectiveChange(null, null, null, "Home"));

        assertEquals(1, docks.southCollapsed.getDocksItems().size());
        assertEquals(0, docks.eastCollapsed.getDocksItems().size());
        assertEquals(0, docks.westCollapsed.getDocksItems().size());

        //one for setup another 2 for changeEvent
        verifyCollapseAllDocks(dockLayoutPanel, 3);

        //one for each changeEvent
        verifyExpandAllCollapsed(dockLayoutPanel, 2);
    }

    @Test
    public void testMoveDock() {

        docks.init();
        docks.setup(dockLayoutPanel);

        docks.register(dock1, dock2, dock3, dock4);
//ederign
        docks.perspectiveChangeEvent( new PerspectiveChange(null, null, null, "Another" ) );

        assertEquals(2, docks.southCollapsed.getDocksItems().size());
        assertEquals(1, docks.eastCollapsed.getDocksItems().size());
        assertEquals(0, docks.westCollapsed.getDocksItems().size());

        docks.moveDock(dock2, UberfireDockPosition.EAST);

        assertEquals(1, docks.southCollapsed.getDocksItems().size());
        assertEquals(2, docks.eastCollapsed.getDocksItems().size());
        assertEquals(0, docks.westCollapsed.getDocksItems().size());

    }

    private void verifyExpandAllCollapsed(DockLayoutPanel dockLayoutPanel,
                                          int wantedNumberOfInvocations) {
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.southCollapsed, false);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.eastCollapsed, false);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.westCollapsed, false);
    }

    private void verifyCollapseAllDocks(DockLayoutPanel dockLayoutPanel,
                                        int wantedNumberOfInvocations) {
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.southCollapsed, true);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.southExpanded, true);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.eastCollapsed, true);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.eastExpanded, true);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.westCollapsed, true);
        verify(dockLayoutPanel, times(wantedNumberOfInvocations)).setWidgetHidden(docks.westExpanded, true);
    }
}
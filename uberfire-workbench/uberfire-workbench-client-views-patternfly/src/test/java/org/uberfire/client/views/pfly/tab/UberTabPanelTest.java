package org.uberfire.client.views.pfly.tab;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.mock.MockPlaceManager;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class UberTabPanelTest {

    private UberTabPanel uberTabPanel;

    @GwtMock
    private ResizeTabPanel resizeTabPanel;

    private final MockPlaceManager mockPlaceManager = new MockPlaceManager();

    @Before
    public void gwtSetUp() throws Exception {
        uberTabPanel = new UberTabPanel( mockPlaceManager, resizeTabPanel );
    }

    @Test
    public void testFireFocusEventWhenClickedWhenUnfocused() throws Exception {
        uberTabPanel.setFocus( false );

        final int[] focusEventCount = new int[1];
        uberTabPanel.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                focusEventCount[0]++;
            }
        } );

        uberTabPanel.onClick( null );
        assertEquals( 1, focusEventCount[0] );
    }
}

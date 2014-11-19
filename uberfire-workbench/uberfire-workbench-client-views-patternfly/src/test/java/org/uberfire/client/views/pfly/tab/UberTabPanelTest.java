package org.uberfire.client.views.pfly.tab;

import org.uberfire.client.views.pfly.mock.MockPlaceManager;
import org.uberfire.mvp.Command;

import com.google.gwt.junit.client.GWTTestCase;

public class UberTabPanelTest extends GWTTestCase {

    private UberTabPanel uberTabPanel;

    private final MockPlaceManager mockPlaceManager = new MockPlaceManager();

    @Override
    public String getModuleName() {
        return "org.uberfire.client.views.pfly.PatternFlyTabTests";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        uberTabPanel = new UberTabPanel( mockPlaceManager );
    }

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

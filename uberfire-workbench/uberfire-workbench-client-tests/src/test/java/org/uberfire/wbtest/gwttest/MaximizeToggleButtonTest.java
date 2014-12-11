package org.uberfire.wbtest.gwttest;

import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.mvp.Command;
import org.uberfire.wbtest.testutil.CountingCommand;

import com.google.gwt.junit.client.GWTTestCase;

public class MaximizeToggleButtonTest extends GWTTestCase {

    MaximizeToggleButton maximizeButton;
    CountingCommand maximizeCommand = new CountingCommand();
    CountingCommand unmaximizeCommand = new CountingCommand();

    @Override
    public String getModuleName() {
        return "org.uberfire.wbtest.UberFireClientGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        maximizeButton = new MaximizeToggleButton();
        maximizeButton.setMaximizeCommand( maximizeCommand );
        maximizeButton.setUnmaximizeCommand( unmaximizeCommand );
    }

    public void testMaximizeWhenClicked() throws Exception {
        maximizeButton.click();
        assertTrue( maximizeButton.isMaximized() );
        assertEquals( 1, maximizeCommand.executeCount );
        assertEquals( 0, unmaximizeCommand.executeCount );
    }

    public void testUnaximizeWhenClickedAgain() throws Exception {
        maximizeButton.click();
        maximizeButton.click();
        assertFalse( maximizeButton.isMaximized() );
        assertEquals( 1, maximizeCommand.executeCount );
        assertEquals( 1, unmaximizeCommand.executeCount );
    }

    public void testSetMaximizedDoesNotInvokeCommands() throws Exception {
        maximizeButton.setMaximized( true );
        maximizeButton.setMaximized( false );

        assertFalse( maximizeButton.isMaximized() );
        assertEquals( 0, maximizeCommand.executeCount );
        assertEquals( 0, unmaximizeCommand.executeCount );
    }

    public void testSetMaximizedFromCallbackIsSafe() throws Exception {
        maximizeButton.setMaximizeCommand( new Command() {
            @Override
            public void execute() {
                maximizeButton.setMaximized( true );
            }
        } );

        maximizeButton.click();

        assertTrue( maximizeButton.isMaximized() );
        assertEquals( 0, maximizeCommand.executeCount );
        assertEquals( 0, unmaximizeCommand.executeCount );
    }

}

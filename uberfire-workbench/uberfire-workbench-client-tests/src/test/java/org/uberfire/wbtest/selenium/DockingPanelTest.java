package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;
import static org.uberfire.wbtest.selenium.UberAssertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.uberfire.wbtest.client.panels.docking.NestedDockingPanelPerspective;
import org.uberfire.workbench.model.CompassPosition;

/**
 * Integration tests for the addition and removal of child panels that "dock" on the north, south, east, or west edges
 * of their parents.
 * <p>
 * All test methods are parameterized, so they each try the same test in each of the 4 compass directions.
 */
@RunWith(Parameterized.class)
public class DockingPanelTest extends AbstractSeleniumTest {

    @Parameters
    public static List<Object[]> params() {
        return Arrays.asList( new Object[][]{ { CompassPosition.NORTH },
                                              { CompassPosition.SOUTH },
                                              { CompassPosition.EAST },
                                              { CompassPosition.WEST } } );
    }

    @Parameter(0)
    public CompassPosition direction;

    @Before
    public void goToDockingPerspective() {
        driver.get( baseUrl + "#" + NestedDockingPanelPerspective.class.getName() );
    }

    /**
     * Tests that panels appear in the correct place when added and get removed from the layout when their only
     * contained part is closed. This is really two tests combined into one in order to save time.
     */
    @Test
    public void testAddAndRemovePanelRelativeToRoot() {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );

        NestingScreenWrapper childScreen = root.addChild( direction );
        assertTrue( childScreen.isStillInDom() );
        assertTrue( childScreen.isPanelStillInDom() );
        assertRelativePosition( direction, root.getWebElement(), childScreen.getWebElement() );

        childScreen.close();
        assertFalse( childScreen.isStillInDom() );
        assertFalse( childScreen.isPanelStillInDom() );
    }

    @Test
    public void testAddTwoPanelsToRootRemovingOldestFirst() {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );

        NestingScreenWrapper childScreen1 = root.addChild( direction );
        NestingScreenWrapper childScreen2 = root.addChild( direction );

        assertTrue( childScreen1.isStillInDom() );
        assertTrue( childScreen1.isPanelStillInDom() );
        assertTrue( childScreen2.isStillInDom() );
        assertTrue( childScreen2.isPanelStillInDom() );

        // each subsequent panel should be added at the edge of what remains of the parent panel
        // so we want root < childScreen2 < childScreen1 < window edge
        assertRelativePosition( direction, root.getWebElement(), childScreen2.getWebElement() );
        //assertRelativePosition( direction, childScreen2, childScreen1 );

        childScreen1.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertTrue( childScreen2.isStillInDom() );
        assertTrue( childScreen2.isPanelStillInDom() );

        childScreen2.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
    }

    @Test
    public void testAddTwoPanelsToRootRemovingNewestFirst() {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );

        NestingScreenWrapper childScreen1 = root.addChild( direction );
        NestingScreenWrapper childScreen2 = root.addChild( direction );

        assertTrue( childScreen1.isStillInDom() );
        assertTrue( childScreen1.isPanelStillInDom() );
        assertTrue( childScreen2.isStillInDom() );
        assertTrue( childScreen2.isPanelStillInDom() );

        childScreen2.close();
        assertTrue( childScreen1.isStillInDom() );
        assertTrue( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );

        childScreen1.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
    }

    @Test
    public void testAddAndRemoveTwoPanelsAtOppositeEgdes() throws Exception {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );
        Dimension originalRootSize = root.getSize();
        Point originalRootLocation = root.getLocation();

        NestingScreenWrapper childScreen1 = root.addChild( direction );
        NestingScreenWrapper childScreen2 = root.addChild( direction );
        NestingScreenWrapper childScreen3 = root.addChild( opposite( direction ) );
        NestingScreenWrapper childScreen4 = root.addChild( opposite( direction ) );

        assertTrue( childScreen1.isStillInDom() );
        assertTrue( childScreen1.isPanelStillInDom() );
        assertTrue( childScreen2.isStillInDom() );
        assertTrue( childScreen2.isPanelStillInDom() );
        assertTrue( childScreen3.isStillInDom() );
        assertTrue( childScreen3.isPanelStillInDom() );
        assertTrue( childScreen4.isStillInDom() );
        assertTrue( childScreen4.isPanelStillInDom() );

        childScreen2.close();
        assertTrue( childScreen1.isStillInDom() );
        assertTrue( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
        assertTrue( childScreen3.isStillInDom() );
        assertTrue( childScreen3.isPanelStillInDom() );
        assertTrue( childScreen4.isStillInDom() );
        assertTrue( childScreen4.isPanelStillInDom() );

        childScreen1.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
        assertTrue( childScreen3.isStillInDom() );
        assertTrue( childScreen3.isPanelStillInDom() );
        assertTrue( childScreen4.isStillInDom() );
        assertTrue( childScreen4.isPanelStillInDom() );

        childScreen3.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
        assertFalse( childScreen3.isStillInDom() );
        assertFalse( childScreen3.isPanelStillInDom() );
        assertTrue( childScreen4.isStillInDom() );
        assertTrue( childScreen4.isPanelStillInDom() );

        childScreen4.close();
        assertFalse( childScreen1.isStillInDom() );
        assertFalse( childScreen1.isPanelStillInDom() );
        assertFalse( childScreen2.isStillInDom() );
        assertFalse( childScreen2.isPanelStillInDom() );
        assertFalse( childScreen3.isStillInDom() );
        assertFalse( childScreen3.isPanelStillInDom() );
        assertFalse( childScreen4.isStillInDom() );
        assertFalse( childScreen4.isPanelStillInDom() );

        // finally, the root screen should be back to its original size and position
        assertEquals( originalRootSize, root.getSize() );
        assertEquals( originalRootLocation, root.getLocation() );
    }

    static CompassPosition opposite( CompassPosition position ) {
        switch ( position ) {
            case NORTH: return CompassPosition.SOUTH;
            case SOUTH: return CompassPosition.NORTH;
            case EAST: return CompassPosition.WEST;
            case WEST: return CompassPosition.EAST;
            default: throw new IllegalArgumentException( "Position " + position + " has no opposite." );
        }
    }

}

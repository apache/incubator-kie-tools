package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
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
        assertRelativePosition( direction, root, childScreen );

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
        assertRelativePosition( direction, root, childScreen2 );
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

    /**
     * Checks that the given "checkMe" element is positioned correctly with respect to the given "anchor" element.
     *
     * @param expected
     *            the position on the page that the checkMe element should have relative to the anchor.
     * @param anchor
     *            the reference element
     * @param checkMe
     *            the element to check
     */
    static void assertRelativePosition( CompassPosition expected, NestingScreenWrapper anchor, NestingScreenWrapper checkMe ) {
        switch( expected ) {
            case NORTH:
                if ( checkMe.getLocation().y >= anchor.getLocation().y ) {
                    fail( "checkMe is not NORTH of anchor. anchor.y = " + anchor.getLocation().y + "; checkMe.y " + checkMe.getLocation().y );
                }
                break;
            case SOUTH:
                if ( checkMe.getLocation().y <= anchor.getLocation().y ) {
                    fail( "checkMe is not SOUTH of anchor. anchor.y = " + anchor.getLocation().y + "; checkMe.y " + checkMe.getLocation().y );
                }
                break;
            case EAST:
                if ( checkMe.getLocation().x <= anchor.getLocation().x ) {
                    fail( "checkMe is not EAST of anchor. anchor.x = " + anchor.getLocation().x + "; checkMe.x " + checkMe.getLocation().x );
                }
                break;
            case WEST:
                if ( checkMe.getLocation().x >= anchor.getLocation().x ) {
                    fail( "checkMe is not WEST of anchor. anchor.x = " + anchor.getLocation().x + "; checkMe.x " + checkMe.getLocation().x );
                }
                break;
            default:
                throw new IllegalArgumentException( "Not a valid relative position: " + expected );
        }
    }
}

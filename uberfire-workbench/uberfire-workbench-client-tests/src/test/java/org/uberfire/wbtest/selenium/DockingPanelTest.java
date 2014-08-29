package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.wbtest.client.docking.NestedDockingPanelPerspective;
import org.uberfire.workbench.model.CompassPosition;

/**
 * Integration tests for the addition and removal of child panels that "dock" on the north, south, east, or west edges
 * of their parents.
 */
public class DockingPanelTest extends AbstractSeleniumTest {

    @Before
    public void goToDockingPerspective() {
        driver.get( baseUrl );
        driver.get( baseUrl + "#" + NestedDockingPanelPerspective.class.getName() );
    }

    @Test
    public void testAddPanelNorthOfRoot() throws Exception {
        testAddPanelRelativeToRoot( CompassPosition.NORTH );
    }

    @Test
    public void testAddPanelSouthOfRoot() throws Exception {
        testAddPanelRelativeToRoot( CompassPosition.SOUTH );
    }

    @Test
    public void testAddPanelEastOfRoot() throws Exception {
        testAddPanelRelativeToRoot( CompassPosition.EAST );
    }

    @Test
    public void testAddPanelWestOfRoot() throws Exception {
        testAddPanelRelativeToRoot( CompassPosition.WEST );
    }

    @Test
    public void testRemovePanelNorthOfRootNoOrphans() throws Exception {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );

        NestingScreenWrapper westChild = root.addChild( CompassPosition.NORTH );
        assertTrue( westChild.isStillInDom() );
        assertTrue( westChild.isPanelStillInDom() );

        westChild.close();
        assertFalse( westChild.isStillInDom() );
        assertFalse( westChild.isPanelStillInDom() );
    }

    private void testAddPanelRelativeToRoot( CompassPosition position ) throws Exception {
        NestingScreenWrapper root = new NestingScreenWrapper( driver, "root" );

        NestingScreenWrapper westChild = root.addChild( position );
        assertTrue( westChild.isStillInDom() );
        assertTrue( westChild.isPanelStillInDom() );
        assertRelativePosition( position, root, westChild );
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

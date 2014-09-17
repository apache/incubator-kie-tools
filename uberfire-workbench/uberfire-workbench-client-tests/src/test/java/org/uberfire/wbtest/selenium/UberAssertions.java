package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.uberfire.workbench.model.CompassPosition;

/**
 * A collection of assert methods that are useful across UberFire integration tests.
 */
public class UberAssertions {

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
    public static void assertRelativePosition( CompassPosition expected, WebElement anchor, WebElement checkMe ) {
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

    /**
     * Checks that the checkMe element's center point coincides with the reference element's center point.
     */
    public static void assertCentered( WebElement reference,
                                       WebElement checkMe ) {
        Point referenceCenter = new Point( reference.getLocation().x + reference.getSize().width / 2,
                                           reference.getLocation().y + reference.getSize().height / 2 );
        Point checkMeCenter = new Point( checkMe.getLocation().x + checkMe.getSize().width / 2,
                                         checkMe.getLocation().y + checkMe.getSize().height / 2 );
        assertEquals( referenceCenter, checkMeCenter );
    }


}

package org.uberfire.client.workbench.docks;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertNotEquals;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireDockTest extends TestCase {

    private static final UberfireDockPosition DOCK_POSITION = UberfireDockPosition.EAST;
    private static String ICON_TYPE = "ICON_TYPE";
    private static final String SCREEN_ID = "SCREEN_ID";
    private static final String PERSPECTIVE_ID = "PERSPECTIVE_ID";

    private static final int SIZE = 450;
    private static final String LABEL = "DOCK TITLE";
    private static final String TOOLTIP = "DOCK TOOLTIP";

    @Mock
    private ImageResource imageIcon1;
    @Mock
    private ImageResource imageIcon2;

    @Mock
    private ImageResource imageIconFocused1;
    @Mock
    private ImageResource imageIconFocused2;

    private UberfireDock tested;
    private UberfireDock testedWithImages;

    @Before
    public void setUp() {
        tested = new UberfireDock(DOCK_POSITION,
                                  ICON_TYPE,
                                  new DefaultPlaceRequest(SCREEN_ID),
                                  PERSPECTIVE_ID)
                .withLabel(LABEL)
                .withTooltip(TOOLTIP)
                .withSize(SIZE);

        testedWithImages = new UberfireDock(DOCK_POSITION,
                                            imageIcon1,
                                            imageIconFocused1,
                                            new DefaultPlaceRequest(SCREEN_ID),
                                            PERSPECTIVE_ID);
    }

    @Test
    public void testWithLabel() {
        assertEquals(0, tested.getLabel().compareTo(LABEL));
    }

    @Test
    public void testWithTooltip() {
        assertEquals(0, tested.getTooltip().compareTo(TOOLTIP));

        final UberfireDock tested2 = new UberfireDock(UberfireDockPosition.EAST,
                                                      ICON_TYPE,
                                                      new DefaultPlaceRequest(SCREEN_ID),
                                                      PERSPECTIVE_ID);
        assertNull(tested2.getTooltip());
    }

    @Test
    public void testWithSize() {
        assertEquals(SIZE, tested.getSize(), 0);
    }

    @Test
    public void testSetUberfireDockPosition() {

        UberfireDock uberfireDock = tested = new UberfireDock(UberfireDockPosition.EAST,
                                                              ICON_TYPE,
                                                              new DefaultPlaceRequest(SCREEN_ID),
                                                              PERSPECTIVE_ID);
        uberfireDock.setUberfireDockPosition(UberfireDockPosition.WEST);
        assertEquals(UberfireDockPosition.WEST, tested.getDockPosition());
    }

    @Test
    public void testGetAssociatedPerspective() {
        assertEquals(PERSPECTIVE_ID, tested.getAssociatedPerspective());
    }

    @Test
    public void testGetIdentifier() {
        assertEquals(SCREEN_ID, tested.getIdentifier());
    }

    @Test
    public void testGetPlaceRequest() {
        assertEquals(SCREEN_ID, tested.getPlaceRequest().getIdentifier());
    }

    @Test
    public void testGetDockPosition() {
        assertEquals(DOCK_POSITION, tested.getDockPosition());
    }

    @Test
    public void testGetSize() {
        assertEquals(SIZE, tested.getSize(), 0);
    }

    @Test
    public void testGetLabel() {
        assertEquals(LABEL, tested.getLabel());
    }

    @Test
    public void testGetTooltip() {
        assertEquals(TOOLTIP, tested.getTooltip());
    }

    @Test
    public void testGetIconType() {
        assertEquals(ICON_TYPE, tested.getIconType());
    }

    @Test
    public void testGetImageIcon() {
        assertEquals(imageIcon1, testedWithImages.getImageIcon());
    }

    @Test
    public void testGetImageIconFocused() {
        assertEquals(imageIconFocused1, testedWithImages.getImageIconFocused());
    }

    @Test
    public void testEquals() {
        UberfireDock compareDock1 = new UberfireDock(DOCK_POSITION,
                                                     ICON_TYPE,
                                                     new DefaultPlaceRequest(SCREEN_ID),
                                                     PERSPECTIVE_ID)
                .withSize(SIZE)
                .withLabel(LABEL)
                .withTooltip(TOOLTIP);

        UberfireDock compareDock2 = new UberfireDock(DOCK_POSITION,
                                                     imageIcon1,
                                                     imageIconFocused1,
                                                     new DefaultPlaceRequest(SCREEN_ID),
                                                     PERSPECTIVE_ID)
                .withSize(SIZE)
                .withLabel(LABEL)
                .withTooltip(TOOLTIP);

        UberfireDock compareDock3 = new UberfireDock(null,
                                                     null,
                                                     new DefaultPlaceRequest(SCREEN_ID));

        UberfireDock compareDock4 = new UberfireDock(null,
                                                     null,
                                                     null,
                                                     new DefaultPlaceRequest(SCREEN_ID));

        UberfireDock compareDock5 = new UberfireDock(UberfireDockPosition.WEST,
                                                     ICON_TYPE + "EXTRA",
                                                     new DefaultPlaceRequest(SCREEN_ID + "EXTRA"),
                                                     PERSPECTIVE_ID + "EXTRA")
                .withSize(SIZE + 20)
                .withLabel(LABEL + "EXTRA")
                .withTooltip(TOOLTIP + "EXTRA");

        UberfireDock compareDock6 = new UberfireDock(UberfireDockPosition.WEST,
                                                     imageIcon2,
                                                     imageIconFocused2,
                                                     new DefaultPlaceRequest(SCREEN_ID + "EXTRA"),
                                                     PERSPECTIVE_ID + "EXTRA")
                .withSize(SIZE + 20)
                .withLabel(LABEL + "EXTRA")
                .withTooltip(TOOLTIP + "EXTRA");

        assertEquals(tested, tested);
        assertEquals(tested, compareDock1);
        assertNotEquals(null, tested);
        assertNotEquals(tested, new Object());
        assertNotEquals(tested, compareDock2);
        assertNotEquals(tested, compareDock3);
        assertNotEquals(tested, compareDock4);
        assertNotEquals(tested, compareDock5);
        assertNotEquals(tested, compareDock6);
        assertNotEquals(compareDock1, compareDock2);
        assertNotEquals(compareDock1, compareDock3);
        assertNotEquals(compareDock1, compareDock4);
        assertNotEquals(compareDock1, compareDock5);
        assertNotEquals(compareDock1, compareDock6);
        assertNotEquals(compareDock2, compareDock3);
        assertNotEquals(compareDock2, compareDock4);
        assertNotEquals(compareDock2, compareDock5);
        assertNotEquals(compareDock2, compareDock6);
        assertEquals(compareDock3, compareDock4);
        assertNotEquals(compareDock3, compareDock5);
        assertNotEquals(compareDock3, compareDock6);
        assertNotEquals(compareDock4, compareDock5);
        assertNotEquals(compareDock4, compareDock6);
        assertNotEquals(compareDock5, compareDock6);
    }

    @Test
    public void testTestHashCode() {
        UberfireDock compareDock1 = new UberfireDock(DOCK_POSITION,
                                                     ICON_TYPE,
                                                     new DefaultPlaceRequest(SCREEN_ID),
                                                     PERSPECTIVE_ID)
                .withSize(SIZE)
                .withLabel(LABEL)
                .withTooltip(TOOLTIP);

        UberfireDock compareDock2 = new UberfireDock(DOCK_POSITION,
                                                     imageIcon1,
                                                     imageIconFocused1,
                                                     new DefaultPlaceRequest(SCREEN_ID),
                                                     PERSPECTIVE_ID)
                .withSize(SIZE)
                .withLabel(LABEL)
                .withTooltip(TOOLTIP);

        UberfireDock compareDock3 = new UberfireDock(null,
                                                     null,
                                                     new DefaultPlaceRequest(SCREEN_ID));

        UberfireDock compareDock4 = new UberfireDock(null,
                                                     null,
                                                     null,
                                                     new DefaultPlaceRequest(SCREEN_ID));

        UberfireDock compareDock5 = new UberfireDock(DOCK_POSITION,
                                                     ICON_TYPE,
                                                     new DefaultPlaceRequest(SCREEN_ID),
                                                     PERSPECTIVE_ID)
                .withLabel(null);

        assertEquals(tested.hashCode(), compareDock1.hashCode());
        assertNotSame(tested.hashCode(), compareDock2.hashCode());
        assertNotSame(tested.hashCode(), compareDock3.hashCode());
        assertNotSame(tested.hashCode(), compareDock4.hashCode());
        assertNotSame(tested.hashCode(), compareDock5.hashCode());
        assertNotSame(compareDock1.hashCode(), compareDock2.hashCode());
        assertNotSame(compareDock1.hashCode(), compareDock3.hashCode());
        assertNotSame(compareDock1.hashCode(), compareDock4.hashCode());
        assertNotSame(compareDock1.hashCode(), compareDock5.hashCode());
        assertNotSame(compareDock2.hashCode(), compareDock3.hashCode());
        assertNotSame(compareDock2.hashCode(), compareDock4.hashCode());
        assertNotSame(compareDock2.hashCode(), compareDock5.hashCode());
        assertNotSame(compareDock3.hashCode(), compareDock4.hashCode());
        assertNotSame(compareDock3.hashCode(), compareDock5.hashCode());
        assertNotSame(compareDock4.hashCode(), compareDock5.hashCode());
    }
}
/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PlaceHistoryHandlerTest {

    private final WorkbenchScreenActivity screenActivity = mock(WorkbenchScreenActivity.class);
    private final WorkbenchScreenActivity perspectiveActivity = mock(WorkbenchScreenActivity.class);

    @Mock
    private PlaceRequestHistoryMapper mapper;

    @Spy
    PlaceHistoryHandler placeHistoryHandler;

    @Before
    public void setup() {

        when(screenActivity.isDynamic()).thenReturn(false);
        when(screenActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(screenActivity.onMayClose()).thenReturn(true);
        when(screenActivity.preferredWidth()).thenReturn(26);
        when(screenActivity.preferredHeight()).thenReturn(77);

        when(perspectiveActivity.isDynamic()).thenReturn(false);
        when(perspectiveActivity.isDefault()).thenReturn(true);
        when(perspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(perspectiveActivity.onMayClose()).thenReturn(true);
        when(perspectiveActivity.preferredWidth()).thenReturn(26);
        when(perspectiveActivity.preferredHeight()).thenReturn(77);
    }

    @Test
    public void testPerspectiveFromUrlSimple() {
        PlaceRequest req = new DefaultPlaceRequest("perspective");

        PlaceRequest place = BookmarkableUrlHelper.getPerspectiveFromPlace(req);
        assertNotNull(place);
        assertEquals("perspective",
                     place.getIdentifier());
        assertSame(place,
                   req);
    }

    @Test
    public void testPerspectiveFromUrlWithHistory() {
        final String REQUEST = "perspective|secreenOne,~screenTwo$screenThree";

        PlaceRequest req = new DefaultPlaceRequest(REQUEST);

        PlaceRequest place = BookmarkableUrlHelper.getPerspectiveFromPlace(req);
        assertNotNull(place);
        assertEquals("perspective",
                     place.getIdentifier());
        assertNotSame(place,
                      req);
    }

    @Test
    public void testRegisterExistingURL() {
        final String REQUEST = "perspective|secreenOne,~screenTwo$screenThree";
        final PlaceRequest req = new DefaultPlaceRequest(REQUEST);

        placeHistoryHandler.registerOpen(screenActivity,
                                         req);
        assertEquals(REQUEST,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void testRegisterBuild() {
        final String SCREEN1_ID = "screen1";
        final String SCREEN2_ID = "screen2";
        final String PERSPECTIVE_ID = "perspective";
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final PlaceRequest perspective = new DefaultPlaceRequest(PERSPECTIVE_ID);

        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        assertEquals(SCREEN1_ID,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        final String SCREENS_OPEN_LIST = SCREEN1_ID.concat(",").concat(SCREEN2_ID);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        assertEquals(SCREENS_OPEN_LIST,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        final String PERSPECTIVE_STRING = PERSPECTIVE_ID.concat("|").concat(SCREENS_OPEN_LIST);
        placeHistoryHandler.registerOpen(perspectiveActivity,
                                         perspective);
        assertEquals(PERSPECTIVE_STRING,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void TestScreenCloseSimple() {
        final String SCREEN1_ID = "screen1";
        final String SCREEN2_ID = "screen2";
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);

        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        assertEquals(SCREEN1_ID,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        final String CLOSED_SCREENS = "~".concat(SCREEN1_ID);
        placeHistoryHandler.registerClose(screenActivity,
                                          screen1);
        assertEquals(CLOSED_SCREENS,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        String URL = "~".concat(SCREEN1_ID).concat(",").concat(SCREEN2_ID);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        assertEquals(URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        URL = "~".concat(SCREEN1_ID).concat(",~").concat(SCREEN2_ID);
        placeHistoryHandler.registerClose(screenActivity,
                                          screen2);
        assertEquals(URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    @SuppressWarnings(value = "all")
    public void TestScreenCloseSimpleWithArgs() {
        final String SCREEN1_ID = "screen1";
        final String SCREEN2_ID = "screen2";
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final String PAR1_KEY = "y";
        final String PAR1_VALUE = "x";
        final String PAR2_KEY = "a";
        final String PAR2_VALUE = "b";
        final String PARAM_TAIL =
                getParamListForTest(PAR2_KEY,
                                    PAR2_VALUE,
                                    PAR1_KEY,
                                    PAR1_VALUE);

        screen1.addParameter(PAR1_KEY,
                             PAR1_VALUE);
        screen1.addParameter(PAR2_KEY,
                             PAR2_VALUE);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        assertEquals(SCREEN1_ID.concat(PARAM_TAIL),
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        final String CLOSED_SCREENS = "~".concat(SCREEN1_ID)
                .concat(PARAM_TAIL);
        placeHistoryHandler.registerClose(screenActivity,
                                          screen1);
        assertEquals(CLOSED_SCREENS,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        String URL = "~".concat(SCREEN1_ID)
                .concat(PARAM_TAIL)
                .concat(",").concat(SCREEN2_ID);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        assertEquals(URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        URL = "~".concat(SCREEN1_ID)
                .concat(PARAM_TAIL)
                .concat(",~").concat(SCREEN2_ID);
        placeHistoryHandler.registerClose(screenActivity,
                                          screen2);
        assertEquals(URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void testOtherScreen() {
        final String SCREEN1_ID = "screen1";
        final String SCREEN2_ID = "screen2";
        final String SCREEN3_ID = "screen3";
        final String PERSPECTIVE_ID = "perspective";
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final PlaceRequest screen3 = new DefaultPlaceRequest(SCREEN3_ID);
        final PlaceRequest perspective = new DefaultPlaceRequest(PERSPECTIVE_ID);

        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        assertEquals(SCREEN1_ID,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        final String PERSPECTIVE_STRING = PERSPECTIVE_ID.concat("|").concat(SCREEN1_ID);
        placeHistoryHandler.registerOpen(perspectiveActivity,
                                         perspective);
        assertEquals(PERSPECTIVE_STRING,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        String EXPECTED_URL = PERSPECTIVE_STRING.concat("$").concat(SCREEN2_ID);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        EXPECTED_URL = EXPECTED_URL.concat(",").concat(SCREEN3_ID);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen3);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
//    @SuppressWarnings(value = "all")
    public void testCloseAllScreens() {
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final PlaceRequest screen3 = new DefaultPlaceRequest(SCREEN3_ID);
        final PlaceRequest screen4 = new DefaultPlaceRequest(SCREEN4_ID);

        prepareCompleteUrlWithParamsForTests();

        String EXPECTED_URL = "perspective|screen1,~screen2$screen3?y=x,screen4?y=x";
        placeHistoryHandler.registerClose(screenActivity,
                                          screen2);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        EXPECTED_URL = "perspective|screen1,~screen2$screen4?y=x";
        placeHistoryHandler.registerClose(screenActivity,
                                          screen3);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        EXPECTED_URL = "perspective|screen1,~screen2";
        placeHistoryHandler.registerClose(screenActivity,
                                          screen4);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());

        EXPECTED_URL = "perspective|~screen1,~screen2";
        placeHistoryHandler.registerClose(screenActivity,
                                          screen1);
        assertEquals(EXPECTED_URL,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void TestFlush() {
        prepareCompleteUrlForTests();
        placeHistoryHandler.flush();
        assertNotNull(placeHistoryHandler.getCurrentBookmarkableURLStatus());
        assertEquals("",
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    /**
     * Open a dock and then close it
     */
    @Test
    public void testRegisterOpenDock() {
        final String dockName = "testDock";
        final String placeRequestName = "testPlacerequest";

        // simulate an opened screen first
        PlaceRequest place = new DefaultPlaceRequest(placeRequestName);
        placeHistoryHandler.registerOpen(screenActivity,
                                         place);
        // mock a dock
        PlaceRequest dockPlace = new DefaultPlaceRequest(dockName);
        UberfireDocksInteractionEvent openEvent = mock(UberfireDocksInteractionEvent.class);

        UberfireDock dock = mock(UberfireDock.class);
        // simulate a docked screen in west position
        when(dock.getDockPosition()).thenReturn(UberfireDockPosition.WEST);
        when(dock.getIdentifier()).thenReturn(dockName);
        when(dock.getIconType()).thenReturn("iconType");
        when(dock.getPlaceRequest()).thenReturn(dockPlace);

        when(openEvent.getType()).thenReturn(UberfireDocksInteractionEvent.InteractionType.OPENED);
        when(openEvent.getTargetDock()).thenReturn(dock);
        placeHistoryHandler.registerOpenDock(openEvent);

        // compose the expected URL
        StringBuilder expected = new StringBuilder(placeRequestName);
        expected.append(BookmarkableUrlHelper.DOCK_BEGIN_SEP);
        expected.append("W"); // dock was mocked in WEST position
        expected.append(dockName);
        expected.append(BookmarkableUrlHelper.SEPARATOR);
        expected.append(BookmarkableUrlHelper.DOCK_CLOSE_SEP);
        assertEquals(expected.toString(),
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void testRegisterCloseDock() {
        final String dockName = "testDock";
        final String placeRequestName = "testPlacerequest";

        // simulate an opened screen first
        PlaceRequest place = new DefaultPlaceRequest(placeRequestName);
        placeHistoryHandler.registerOpen(screenActivity,
                                         place);
        // mock a dock
        PlaceRequest dockPlace = new DefaultPlaceRequest(dockName);
        UberfireDocksInteractionEvent openEvent = mock(UberfireDocksInteractionEvent.class);
        UberfireDocksInteractionEvent closeEvent = mock(UberfireDocksInteractionEvent.class);

        UberfireDock dock = mock(UberfireDock.class);
        // simulate a docked screen in west position
        when(dock.getDockPosition()).thenReturn(UberfireDockPosition.WEST);
        when(dock.getIdentifier()).thenReturn(dockName);
        when(dock.getIconType()).thenReturn("iconType");
        when(dock.getPlaceRequest()).thenReturn(dockPlace);

        when(openEvent.getType()).thenReturn(UberfireDocksInteractionEvent.InteractionType.OPENED);
        when(openEvent.getTargetDock()).thenReturn(dock);
        when(closeEvent.getType()).thenReturn(UberfireDocksInteractionEvent.InteractionType.CLOSED);
        when(closeEvent.getTargetDock()).thenReturn(dock);
        // open...
        placeHistoryHandler.registerOpenDock(openEvent);
        // ...close dock
        placeHistoryHandler.registerCloseDock(closeEvent);

        // compose the expected URL
        StringBuilder expected = new StringBuilder(placeRequestName);
        expected.append(BookmarkableUrlHelper.DOCK_BEGIN_SEP);
        expected.append(BookmarkableUrlHelper.CLOSED_DOCK_PREFIX);
        expected.append("W"); // dock was mocked in WEST position
        expected.append(dockName);
        expected.append(BookmarkableUrlHelper.SEPARATOR);
        expected.append(BookmarkableUrlHelper.DOCK_CLOSE_SEP);
        assertEquals(expected.toString(),
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    @Test
    public void testUrlLimit() {
        int cnt = 0;
        int length = 0;

        do {
            final PlaceRequest screen = new DefaultPlaceRequest("screen".concat(String.valueOf(cnt++)));

            placeHistoryHandler.registerOpen(screenActivity,
                                             screen);

            if (length == placeHistoryHandler.getCurrentBookmarkableURLStatus().length()) {
                break;
            }
            length = placeHistoryHandler.getCurrentBookmarkableURLStatus().length();
        } while ((placeHistoryHandler.getCurrentBookmarkableURLStatus().length()
                < BookmarkableUrlHelper.MAX_NAV_URL_SIZE + 100));
        assertNotNull(placeHistoryHandler.getCurrentBookmarkableURLStatus());
        assertFalse(placeHistoryHandler.getCurrentBookmarkableURLStatus().length()
                            > BookmarkableUrlHelper.MAX_NAV_URL_SIZE);
    }

    @Test
    public void testGetOpenScreens() {
        final String url = "perspective|screen1,!screen2,~screen3$!screen4";
        final PlaceRequest req = new DefaultPlaceRequest(url);

        Set<String> opened = BookmarkableUrlHelper.getOpenedScreenFromPlace(req);
        assertNotNull(opened);
        assertEquals(3L,
                     opened.size());
        assertTrue(opened.contains(SCREEN1_ID));
        assertTrue(opened.contains("!" + SCREEN2_ID));
        assertTrue(opened.contains("!" + SCREEN4_ID));
    }

    @Test
    public void testGetClosedScreens() {
        final String url = "perspective|screen1,~!screen2,~screen3$~!screen4";
        final PlaceRequest req = new DefaultPlaceRequest(url);

        Set<String> closed = BookmarkableUrlHelper.getClosedScreenFromPlace(req);
        assertNotNull(closed);
        assertEquals(3L,
                     closed.size());
        assertTrue(closed.contains("~" + SCREEN3_ID));
        assertTrue(closed.contains("~!" + SCREEN2_ID));
        assertTrue(closed.contains("~!" + SCREEN4_ID));
    }

    /**
     * Prepare an URL -> perspective|screen1,screen2$screen3,screen4
     */
    private void prepareCompleteUrlForTests() {
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final PlaceRequest screen3 = new DefaultPlaceRequest(SCREEN3_ID);
        final PlaceRequest screen4 = new DefaultPlaceRequest(SCREEN4_ID);
        final PlaceRequest perspective = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final String expectedUrl = "perspective|screen1,screen2$screen3,screen4";

        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        placeHistoryHandler.registerOpen(perspectiveActivity,
                                         perspective);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen3);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen4);
        assertEquals(expectedUrl,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    /**
     * Prepare an URL -> perspective|screen1,screen2$screen3?y=x,screen4?y=x
     */
//    @SuppressWarnings(value = "all")
    private void prepareCompleteUrlWithParamsForTests() {
        final PlaceRequest screen1 = new DefaultPlaceRequest(SCREEN1_ID);
        final PlaceRequest screen2 = new DefaultPlaceRequest(SCREEN2_ID);
        final PlaceRequest screen3 = new DefaultPlaceRequest(SCREEN3_ID);
        final PlaceRequest screen4 = new DefaultPlaceRequest(SCREEN4_ID);
        final PlaceRequest perspective = new DefaultPlaceRequest(PERSPECTIVE_ID);
        final String PAR_KEY = "y";
        final String PAR_VALUE = "x";
        final String PARAM_TAIL =
                getParamListForTest(PAR_KEY,
                                    PAR_VALUE);
        final String expectedUrl = "perspective|screen1,screen2$screen3"
                + PARAM_TAIL + ",screen4" + PARAM_TAIL;

        screen3.addParameter(PAR_KEY,
                             PAR_VALUE);
        screen4.addParameter(PAR_KEY,
                             PAR_VALUE);

        placeHistoryHandler.registerOpen(screenActivity,
                                         screen1);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen2);
        placeHistoryHandler.registerOpen(perspectiveActivity,
                                         perspective);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen3);
        placeHistoryHandler.registerOpen(screenActivity,
                                         screen4);
        assertEquals(expectedUrl,
                     placeHistoryHandler.getCurrentBookmarkableURLStatus());
    }

    /**
     * Return the list of parameters needed for test
     * @param txt
     * @return
     */
    private String getParamListForTest(String... txt) {
        boolean isQM = true;
        StringBuilder param = new StringBuilder();

        for (int i = 0; i < txt.length; i++) {
            if ((i == 0)
                    || (i % 2 == 0)) {
                if (isQM) {
                    param.append("?");
                    isQM = false;
                } else {
                    param.append("&");
                }
                param.append(txt[i]);
            } else {
                param.append("=");
                param.append(txt[i]);
            }
        }
        return param.toString();
    }

    final static String SCREEN1_ID = "screen1";
    final static String SCREEN2_ID = "screen2";
    final static String SCREEN3_ID = "screen3";
    final static String SCREEN4_ID = "screen4";
    final static String PERSPECTIVE_ID = "perspective";
}

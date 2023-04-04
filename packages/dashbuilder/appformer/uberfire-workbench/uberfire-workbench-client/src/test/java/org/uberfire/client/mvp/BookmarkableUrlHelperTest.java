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
import junit.framework.TestCase;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@RunWith(GwtMockitoTestRunner.class)
public class BookmarkableUrlHelperTest extends TestCase {

    @BeforeClass
    public static void setupBeans() {
        ((SyncBeanManagerImpl) IOC.getBeanManager()).reset();
    }

    @Test
    public void testRegisterOpen() {
        PlaceRequest req1 = new DefaultPlaceRequest("screen1");
        PlaceRequest req2 = new DefaultPlaceRequest("screen2");
        PlaceRequest req3 = new DefaultPlaceRequest("screen3");
        PlaceRequest req4 = new DefaultPlaceRequest("screen4");
        final String perspective = "perspective";
        String url = "";

        url = BookmarkableUrlHelper.registerOpenedScreen(url,
                req1);
        assertEquals(req1.getFullIdentifier(),
                url);
        url = BookmarkableUrlHelper.registerOpenedScreen(url,
                req2);
        assertEquals("screen1,screen2",
                url);

        // add the perspective, want to test screen not belonging to the current perspective
        url = perspective.concat(BookmarkableUrlHelper.PERSPECTIVE_SEP)
                .concat(url);
        url = BookmarkableUrlHelper.registerOpenedScreen(url,
                req3);

        assertEquals("perspective|screen1,screen2$screen3",
                url);

        url = BookmarkableUrlHelper.registerOpenedScreen(url,
                req4);
        assertEquals("perspective|screen1,screen2$screen3,screen4",
                url);

        // test with screen closed (we compose the URL)
        final String closedScreen = "closedScreen";
        final PlaceRequest closed = new DefaultPlaceRequest(closedScreen);
        url = "perspective|"
                .concat(BookmarkableUrlHelper.CLOSED_PREFIX)
                .concat(closedScreen).concat(",openScreen$externalScreen");
        url = BookmarkableUrlHelper.registerOpenedScreen(url,
                closed);
        String expected = "perspective|"
                .concat(closedScreen).concat(",openScreen$externalScreen");
        assertEquals(expected,
                url);
        // compose a big URL
        StringBuilder bigUrl = new StringBuilder(perspective);
        while (bigUrl.length() < BookmarkableUrlHelper.MAX_NAV_URL_SIZE) {
            bigUrl.append(",screen");
        }
        url = BookmarkableUrlHelper.registerOpenedScreen(bigUrl.toString(),
                req1);
        assertNotNull(url);
        assertEquals(bigUrl.toString(),
                url);
    }

    @Test
    public void testRegisterClose() {
        String url = "perspective|screen1,screen2$screen3,screen4";

        // close screens not belonging to the current perspective
        url = BookmarkableUrlHelper.registerClose(url,
                "screen3");
        assertEquals("perspective|screen1,screen2$screen4",
                url);
        url = BookmarkableUrlHelper.registerClose(url,
                "screen4");
        assertEquals("perspective|screen1,screen2",
                url);

        // close screens belonging to the current perspective

        url = BookmarkableUrlHelper.registerClose(url,
                "screen1");
        assertEquals("perspective|~screen1,screen2",
                url);

        url = BookmarkableUrlHelper.registerClose(url,
                "screen2");
        assertEquals("perspective|~screen1,~screen2",
                url);

        // screen already closed
        url = "perspective|screen1,~screen2$screen3";
        url = BookmarkableUrlHelper.registerClose(url,
                "screen2");
        assertEquals("perspective|screen1,~screen2$screen3",
                url);

        url = "perspective|screen1$screen2";
        url = BookmarkableUrlHelper.registerClose(url,
                "screen2");
        assertEquals("perspective|screen1",
                url);

        url = "perspective|screen1$screen2,screen3";
        url = BookmarkableUrlHelper.registerClose(url,
                "screen2");
        assertEquals("perspective|screen1$screen3",
                url);

        url = "perspective|screen1$screen2,screen3,screen4";
        url = BookmarkableUrlHelper.registerClose(url,
                "screen3");
        assertEquals("perspective|screen1$screen2,screen4",
                url);
    }

    @Test
    public void testGetPerspectiveFromPlace() {
        final String perspectiveName = "eccePerspective";
        final String bookmarkableUrl = perspectiveName
                .concat("|~screen1,~screen2");
        final PlaceRequest req = new DefaultPlaceRequest(bookmarkableUrl);

        PlaceRequest place = BookmarkableUrlHelper.getPerspectiveFromPlace(req);

        assertNotNull(place);
        assertNotSame(req,
                place);
        assertEquals(perspectiveName,
                place.getFullIdentifier());

        // return the same object if no perspective in URL
        final PlaceRequest empty = new DefaultPlaceRequest("screenOpened,~screenClosed");
        empty.addParameter("param",
                "value");
        place = BookmarkableUrlHelper.getPerspectiveFromPlace(empty);
        assertNotNull(place);
        assertEquals(empty.getFullIdentifier(),
                place.getFullIdentifier());
    }

    @Test
    public void testGetPerspectiveFromPlaceWithParams() {
        final String perspectiveName = "eccePerspective";
        final String bookmarkableUrl = perspectiveName
                .concat("|~screen1,~screen2");
        final PlaceRequest req = new DefaultPlaceRequest(bookmarkableUrl);

        req.addParameter("param",
                "value");
        PlaceRequest place = BookmarkableUrlHelper.getPerspectiveFromPlace(req);

        assertNotNull(place);
        assertNotSame(req,
                place);
        StringBuilder expected = new StringBuilder(perspectiveName);
        expected.append("?param=value");
        assertEquals(expected.toString(),
                place.getFullIdentifier());
    }

    @Test
    public void testIsPerspectiveScreen() {
        final String url = "perspective|screen1,screen2$screen3,screen4";

        assertTrue(BookmarkableUrlHelper.isPerspectiveScreen(url,
                "screen1"));
        assertTrue(BookmarkableUrlHelper.isPerspectiveScreen(url,
                "screen2"));
        assertFalse(BookmarkableUrlHelper.isPerspectiveScreen(url,
                "screen3"));
        assertFalse(BookmarkableUrlHelper.isPerspectiveScreen(url,
                "screen4"));
        assertFalse(BookmarkableUrlHelper.isPerspectiveScreen(null,
                "screen2"));
        assertFalse(BookmarkableUrlHelper.isPerspectiveScreen("",
                "screen2"));
        assertFalse(BookmarkableUrlHelper.isPerspectiveScreen(url,
                null));
    }

    @Test
    public void testIsPerspectiveInUrl() {
        final String url1 = "perspective|screen1,screen2$screen3,screen4";
        final String url2 = "screen1,screen2";
        final String url3 = "perspective|screen1,screen2$screen3,screen4";

        assertTrue(BookmarkableUrlHelper.isPerspectiveInUrl(url1));
        assertFalse(BookmarkableUrlHelper.isPerspectiveInUrl(url2));
        assertTrue(BookmarkableUrlHelper.isPerspectiveInUrl(url3));
    }

    @Test
    public void testUrlContainsExtraPerspectiveScreen() {
        final String url1 = "perspective|screen1,screen2$screen3,screen4";
        final String url2 = "screen1,screen2";
        final String url3 = "perspective|screen1,screen2$screen3,screen4";

        assertTrue(BookmarkableUrlHelper.urlContainsExtraPerspectiveScreen(url1));
        assertFalse(BookmarkableUrlHelper.urlContainsExtraPerspectiveScreen(url2));
        assertTrue(BookmarkableUrlHelper.urlContainsExtraPerspectiveScreen(url3));
    }

    @Test
    public void testGetUrlInToken() {
        final String url1 = "perspective|#screen1,§screen2$#screen3,!screen4";
        final String url2 = "!screen1,#screen2";

        assertEquals("!screen1",
                BookmarkableUrlHelper.getUrlToken(url2,
                        "screen1"));
        assertEquals("#screen2",
                BookmarkableUrlHelper.getUrlToken(url2,
                        "screen2"));

        assertEquals("§screen2",
                BookmarkableUrlHelper.getUrlToken(url1,
                        "screen2"));
        assertEquals("#screen1",
                BookmarkableUrlHelper.getUrlToken(url1,
                        "screen1"));
        assertEquals("#screen3",
                BookmarkableUrlHelper.getUrlToken(url1,
                        "screen3"));
        assertEquals("!screen4",
                BookmarkableUrlHelper.getUrlToken(url1,
                        "screen4"));
    }

    @Test
    public void testGetScreensFromPlace() {
        final String url = "perspective|~screen1,screen2$!screen3,screen4";
        final String url2 = "UFWidgets|PagedTableScreen";
        final String url3 = "PagedTableScreen";
        final PlaceRequest place = new DefaultPlaceRequest(url);
        final PlaceRequest place2 = new DefaultPlaceRequest(url2);
        final PlaceRequest placeNoPerspective = new DefaultPlaceRequest(url3);
        final PlaceRequest placeNull = null;

        Set<String> set = BookmarkableUrlHelper.getScreensFromPlace(place);
        assertNotNull(set);
        assertFalse(set.isEmpty());

        assertEquals(4,
                set.size());
        assertTrue(set.contains("~screen1"));
        assertTrue(set.contains("screen2"));
        assertTrue(set.contains("!screen3"));
        assertTrue(set.contains("screen4"));

        set = BookmarkableUrlHelper.getScreensFromPlace(place2);
        assertNotNull(set);

        assertFalse(set.isEmpty());
        assertEquals(1,
                set.size());
        assertTrue(set.contains("PagedTableScreen"));

        set = BookmarkableUrlHelper.getScreensFromPlace(placeNull);
        assertNotNull(set);
        assertTrue(set.isEmpty());

        // test with bookmarkable URL with no perspective
        set = BookmarkableUrlHelper.getScreensFromPlace(placeNoPerspective);
        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertTrue(set.contains("PagedTableScreen"));
    }

    @Test
    public void testGetClosedScreenFromPlace() {
        final String url = "perspective|~screen1,screen2$~screen3,screen4";
        final String url2 = "UFWidgets|PagedTableScreen[ESimpleDockScreen,!WSimpleDockScreen,ESimpleDockScreen,]";
        final PlaceRequest place = new DefaultPlaceRequest(url);
        final PlaceRequest place2 = new DefaultPlaceRequest(url2);

        Set<String> set = BookmarkableUrlHelper.getClosedScreenFromPlace(place);
        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertEquals(2,
                set.size());
        assertTrue(set.contains("~screen1"));
        assertTrue(set.contains("~screen3"));

        set = BookmarkableUrlHelper.getClosedScreenFromPlace(place2);
        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    public void testGetOpenedScreenFromPlace() {
        final String url = "perspective|~screen1,screen2$~screen3,screen4";
        final String url2 = "UFWidgets|PagedTableScreen";
        final PlaceRequest place = new DefaultPlaceRequest(url);
        final PlaceRequest place2 = new DefaultPlaceRequest(url2);

        Set<String> set = BookmarkableUrlHelper.getOpenedScreenFromPlace(place);
        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertTrue(set.contains("screen2"));
        assertTrue(set.contains("screen4"));

        set = BookmarkableUrlHelper.getOpenedScreenFromPlace(place2);
        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertTrue(set.contains("PagedTableScreen"));
    }

    @Test
    public void testIsScreenClosed() {
        final String url = "perspective|~screen1,screen2$~screen3,screen4";
        final String url2 = "UFWidgets|PagedTableScreen[ESimpleDockScreen,!WSimpleDockScreen,ESimpleDockScreen,]";

        assertTrue(BookmarkableUrlHelper.isScreenClosed(
                url,
                "screen1"));
        assertTrue(BookmarkableUrlHelper.isScreenClosed(
                url,
                "screen3"));
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url,
                "screen2"));
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url,
                "screen4"));

        // docked screens are ignored
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url2,
                "PagedTableScreen"));
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url2,
                "ESimpleDockScreen"));
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url2,
                "ESimpleDockScreen"));
        assertFalse(BookmarkableUrlHelper.isScreenClosed(
                url2,
                "!WSimpleDockScreen"));
    }

    @Test
    public void testRegisterOpenedPerspective() {
        final String screens = "screen1,~screen2";
        final String perspective = "perspective";
        final PlaceRequest place = new DefaultPlaceRequest(perspective);
        String url = screens;

        url = BookmarkableUrlHelper.registerOpenedPerspective(url,
                place);

        assertEquals(perspective.concat(BookmarkableUrlHelper.PERSPECTIVE_SEP).concat(screens),
                url);
    }

}

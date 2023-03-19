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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

/**
 * A bookmarkable URL has the following form:
 * <p>
 * http://url/index.html#FWidgets|$PagedTableScreen[WSimpleDockScreen,],~WiresPropertiesScreen$PagedTableScreen
 * <p>
 * between the '#' and '|' there is the perspective name
 * between the '|' and '$' there is the CSV list of the screens opened when loading the perspective
 * between the '[' and ']'there is the CSV list of the docked Screens
 * after the '$' there is the CSV list of the screens not belonging to the current perspective
 * <p>
 * '~' denotes a closed screen
 * <p>
 * In this unit we have the basic methods used to compose such URLs
 */
public class BookmarkableUrlHelper {

    public final static String PERSPECTIVE_SEP = "|";
    public final static String DOCK_BEGIN_SEP = "[";
    public final static String DOCK_CLOSE_SEP = "]";
    public final static String SEPARATOR = ",";
    public final static String OTHER_SCREEN_SEP = "$";
    public final static String CLOSED_PREFIX = "~";
    public final static String CLOSED_DOCK_PREFIX = "!";
    public final static int MAX_NAV_URL_SIZE = 1900;

    private static boolean isNotBlank(final String str) {
        return (str != null
                && str.trim().length() > 0);
    }

    private static boolean isNotBlank(final PlaceRequest place) {
        return (null != place && isNotBlank(place.getFullIdentifier()));
    }

    /**
     * Add a screen to the bookmarkable URL. If the screen belongs to the currently opened
     * perspective we add it to the list between the '|' and '$', otherwise we add it
     * after the '$'.
     * <p>
     * We add the '|' or the '$' when needed
     * @param bookmarkableUrl
     * @param placeRequest
     * @return
     */
    public static String registerOpenedScreen(String bookmarkableUrl,
                                              final PlaceRequest placeRequest) {
        final String screenName = placeRequest.getFullIdentifier();
        final String closedScreen = CLOSED_PREFIX.concat(screenName);
        final String currentBookmarkableUrl = bookmarkableUrl;

        if (screenWasClosed(bookmarkableUrl,
                            closedScreen)) {
            bookmarkableUrl = bookmarkableUrl.replace(closedScreen,
                                                      screenName);
        } else if (!isPerspectiveInUrl(bookmarkableUrl)) {
            // must add the screen in the group of the current perspective (which is not yet loaded)
            if (isNotBlank(bookmarkableUrl)) {
                bookmarkableUrl = bookmarkableUrl.concat(SEPARATOR).concat(screenName);
            } else {
                bookmarkableUrl = screenName;
            }
        } else {
            // this is a screen outside the current perspective
            if (!urlContainsExtraPerspectiveScreen(bookmarkableUrl)) {
                bookmarkableUrl = bookmarkableUrl.concat(OTHER_SCREEN_SEP).concat(screenName);
            } else {
                bookmarkableUrl = bookmarkableUrl.concat(SEPARATOR).concat(screenName);
            }
        }
        if (isBiggerThenMaxURLSize(bookmarkableUrl)) {
            return currentBookmarkableUrl;
        }
        return bookmarkableUrl;
    }

    private static boolean screenWasClosed(String bookmarkableUrl,
                                           String closedScreen) {
        return bookmarkableUrl.indexOf(closedScreen) != -1;
    }

    private static boolean isBiggerThenMaxURLSize(String bookmarkableUrl) {
        return isNotBlank(bookmarkableUrl) &&
                bookmarkableUrl.length() >= MAX_NAV_URL_SIZE;
    }

    /**
     * Update the bookmarkable URL, marking a screen or editor closed. Basically if the screen belongs
     * to the currently opened perspective the we prefix the screen with a '~'; if the
     * screen doesn't belong to the current perspective, that is, after the '$', the it
     * is simply removed.
     * <p>
     * We remove the '$' when needed
     * @param screenName
     */
    public static String registerClose(String bookmarkableUrl,
                                       final String screenName) {
        final boolean isPerspective = isPerspectiveScreen(bookmarkableUrl,
                                                          screenName);
        final String separator = isPerspective ? PERSPECTIVE_SEP : OTHER_SCREEN_SEP;
        final String closedScreen = CLOSED_PREFIX.concat(screenName);
        final String uniqueScreenAfterDelimiter =
                separator.concat(screenName); // |screen or $screen
        final String firstScreenAfterDelimiter =
                uniqueScreenAfterDelimiter.concat(SEPARATOR); // |screen, or $screen,
        final String commaSeparatedScreen =
                screenName.concat(SEPARATOR); // screen,

        if (isScreenClosed(bookmarkableUrl,
                           closedScreen)) {
            return bookmarkableUrl;
        }
        if (isPerspective) {
            bookmarkableUrl = bookmarkableUrl.replace(screenName,
                                                      closedScreen);
        } else {
            if (bookmarkableUrl.contains(firstScreenAfterDelimiter)) {
                bookmarkableUrl = bookmarkableUrl.replace(firstScreenAfterDelimiter,
                                                          separator);
            } else if (bookmarkableUrl.contains(uniqueScreenAfterDelimiter)) {
                bookmarkableUrl = bookmarkableUrl.replace(uniqueScreenAfterDelimiter,
                                                          "");
            } else if (bookmarkableUrl.contains(commaSeparatedScreen)) {
                bookmarkableUrl = bookmarkableUrl.replace(commaSeparatedScreen,
                                                          "");
            }
        }
        return bookmarkableUrl;
    }

    /**
     * Given a bookmarkable URL this methods returns a PlaceRequest
     * with the perspective
     * @param place\
     * @return
     */
    public static PlaceRequest getPerspectiveFromPlace(PlaceRequest place) {
        String url = place.getFullIdentifier();

        if (isPerspectiveInUrl(url)) {
            String perspectiveName = url.substring(0,
                                                   url.indexOf(PERSPECTIVE_SEP));
            PlaceRequest copy = place.clone();
            copy.setIdentifier(perspectiveName);
            if (!place.getParameters().isEmpty()) {
                for (Map.Entry<String, String> elem : place.getParameters().entrySet()) {
                    copy.addParameter(elem.getKey(),
                                      elem.getValue());
                }
            }
            return copy;
        }
        return place;
    }

    /**
     * Check whether the screen belongs to the currently opened perspective
     * @param screen
     * @return
     */
    public static boolean isPerspectiveScreen(final String bookmarkableUrl,
                                              final String screen) {
        return (isNotBlank(screen)
                && isNotBlank(bookmarkableUrl)
                && (!urlContainsExtraPerspectiveScreen(bookmarkableUrl)
                || (bookmarkableUrl.indexOf(OTHER_SCREEN_SEP) > bookmarkableUrl.indexOf(screen))));
    }

    /**
     * Returns true if the perspective is present in the URL
     * @return
     */
    public static boolean isPerspectiveInUrl(final String url) {
        return (isNotBlank(url) && (url.indexOf(PERSPECTIVE_SEP) > 0));
    }

    /**
     * Check if the URL contains screens not belonging to the current perspective
     * @return
     */
    public static boolean urlContainsExtraPerspectiveScreen(final String bookmarkableUrl) {
        return (bookmarkableUrl.indexOf(OTHER_SCREEN_SEP) != -1);
    }

    /**
     * Given a screen name, this method extracts the corresponding token in the
     * URL, that is the screen name with optional parameters and markers
     * @param screen
     * @return
     */
    public static String getUrlToken(final String bookmarkableUrl,
                                     final String screen) {
        int st = isPerspectiveInUrl(bookmarkableUrl) ? (bookmarkableUrl.indexOf(PERSPECTIVE_SEP) + 1) : 0;
        String screensList = bookmarkableUrl.replace(OTHER_SCREEN_SEP,
                                                     SEPARATOR)
                .substring(st,
                           bookmarkableUrl.length());

        String tokens[] = screensList.split(SEPARATOR);
        Optional<String> token = Arrays.asList(tokens).stream()
                .filter(s -> s.contains(screen))
                .findFirst();

        return token.orElse(screen);
    }

    /**
     * Return the docked screens in the URL
     * @param url
     * @return
     */
    public static Set<String> getDockedScreensFromUrl(final String url) {
        int start;
        int end;
        String docks;

        if (!isNotBlank(url)) {
            return new HashSet<>();
        }
        start = url.indexOf(DOCK_BEGIN_SEP) + 1;
        end = url.indexOf(DOCK_CLOSE_SEP) - 1;

        if (start > 0) {
            docks = url.substring(start,
                                  end);
            String[] token = docks.split(SEPARATOR);
            return new HashSet<>(Arrays.asList(token));
        }
        return new HashSet<>();
    }

    /**
     * Return all the docked screens
     * @param place
     * @return
     * @note non-docked screens are not taken into consideration
     */
    public static Set<String> getDockedScreensFromPlace(final PlaceRequest place) {
        if (null != place) {
            return getDockedScreensFromUrl(place.getFullIdentifier());
        }
        return new HashSet<>();
    }

    /**
     * Return all the screens (opened or closed) that is, everything
     * after the perspective declaration
     * @param place
     * @return
     * @note docked screens are not taken into consideration
     */
    public static Set<String> getScreensFromPlace(final PlaceRequest place) {
        String url;
        int start;
        int end;
        String docks;

        if (!isNotBlank(place)) {
            return new HashSet<>();
        }
        // get everything after the perspective
        if (isPerspectiveInUrl(place.getFullIdentifier())) {
            String request = place.getFullIdentifier();

            url = request.substring(request.indexOf(PERSPECTIVE_SEP) + 1);
        } else {
            url = place.getFullIdentifier();
        }

        start = url.indexOf(DOCK_BEGIN_SEP);
        end = url.indexOf(DOCK_CLOSE_SEP) + 1;
        if (start > 0) {
            docks = url.substring(start,
                                  end);
            url = url.replace(docks,
                              "");
        }
        // replace the '$' with a comma ','
        url = url.replace(OTHER_SCREEN_SEP,
                          SEPARATOR);
        String[] token = url.split(SEPARATOR);
        return new HashSet<>(Arrays.asList(token));
    }

    /**
     * Get the opened screens in the given place request
     * @param place
     * @return
     */
    public static Set<String> getClosedScreenFromPlace(final PlaceRequest place) {
        Set<String> screens = getScreensFromPlace(place);
        Set<String> result = screens.stream()
                .filter(s -> s.startsWith(CLOSED_PREFIX))
                .collect(Collectors.toSet());
        return result;
    }

    /**
     * Get the opened screens in the given place request
     * @param place
     * @return
     */
    public static Set<String> getOpenedScreenFromPlace(final PlaceRequest place) {
        Set<String> screens = getScreensFromPlace(place);
        Set<String> result = screens.stream()
                .filter(s -> !s.startsWith(CLOSED_PREFIX))
                .collect(Collectors.toSet());
        return result;
    }

    /**
     * Return true if the given screen is already closed.
     * @param screen
     * @return
     * @note docked screens are ignored
     */
    public static boolean isScreenClosed(final String bookmarkableUrl,
                                         String screen) {
        if (!screen.startsWith(CLOSED_PREFIX)) {
            screen = CLOSED_PREFIX.concat(screen);
        }
        return (bookmarkableUrl.indexOf(screen) != -1);
    }

    public static String registerOpenedPerspective(String currentBookmarkableURLStatus,
                                                   PlaceRequest place) {
        return place.getFullIdentifier().concat(PERSPECTIVE_SEP).concat(currentBookmarkableURLStatus);
    }

    private static String getDockId(UberfireDock targetDock) {
        return targetDock.getDockPosition().getShortName()
                + targetDock.getPlaceRequest().getFullIdentifier() + SEPARATOR;
    }

    public static String registerOpenedDock(String currentBookmarkableURLStatus,
                                            UberfireDock targetDock) {
        if (targetDock == null) {
            return currentBookmarkableURLStatus;
        }
        final String id = getDockId(targetDock);
        final String closed = CLOSED_DOCK_PREFIX.concat(id);

        if (currentBookmarkableURLStatus.contains(DOCK_CLOSE_SEP)) {
            String result = null;

            if (!currentBookmarkableURLStatus.contains(id)) {
                // the screen is not in the URL, insert in last position
                result = currentBookmarkableURLStatus.replace(DOCK_CLOSE_SEP,
                                                              (id + DOCK_CLOSE_SEP));
            } else if (currentBookmarkableURLStatus.contains(closed)) {
                // the screen is closed
                result = currentBookmarkableURLStatus.replace(closed,
                                                              id);
            } else {
                // screen already in URL
                result = currentBookmarkableURLStatus;
            }
            return result;
        } else {
            return currentBookmarkableURLStatus + DOCK_BEGIN_SEP + (getDockId(targetDock) + DOCK_CLOSE_SEP);
        }
    }

    public static String registerClosedDock(String currentBookmarkableURLStatus,
                                            UberfireDock targetDock) {
        if (!isNotBlank(currentBookmarkableURLStatus)
                || null == targetDock) {
            return currentBookmarkableURLStatus;
        }
        final String id = getDockId(targetDock);
        final String closed = CLOSED_DOCK_PREFIX.concat(id);
        if (!currentBookmarkableURLStatus.contains(closed)) {
            return currentBookmarkableURLStatus.replace(id,
                                                        CLOSED_DOCK_PREFIX.concat(id));
        }
        return currentBookmarkableURLStatus;
    }

    /**
     * Remove the editor reference from the URL
     * @param currentBookmarkableURLStatus
     * @param place
     * @return
     */
    public static String registerCloseEditor(final String currentBookmarkableURLStatus,
                                             final PlaceRequest place) {
        if (place != null
                && place instanceof PathPlaceRequest) {
            final String path = place.getFullIdentifier();
            final String pathWithSep = path.concat(SEPARATOR);

            if (currentBookmarkableURLStatus.contains(pathWithSep)) {
                return currentBookmarkableURLStatus.replace(pathWithSep,
                                                            "");
            }
            return currentBookmarkableURLStatus.replace(path,
                                                        "");
        }
        return currentBookmarkableURLStatus;
    }
}

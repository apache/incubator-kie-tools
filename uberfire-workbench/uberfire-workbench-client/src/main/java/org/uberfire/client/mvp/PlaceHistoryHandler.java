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

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
public class PlaceHistoryHandler {

    private static final Logger log = Logger.getLogger(PlaceHistoryHandler.class.getName());
    private final Historian historian;
    @Inject
    private PlaceRequestHistoryMapper mapper;
    private PlaceManager placeManager;
    private PlaceRequest defaultPlaceRequest = PlaceRequest.NOWHERE;
    private String currentBookmarkableURLStatus = "";

    /**
     * Create a new PlaceHistoryHandler.
     */
    public PlaceHistoryHandler() {
        this.historian = GWT.create(DefaultHistorian.class);
    }

    /**
     * Handle the current history token. Typically called at application start,
     * to ensure bookmark launches work.
     */
    public void handleCurrentHistory() {
        handleHistoryToken(historian.getToken());
    }

    /**
     * Initialize this place history handler.
     * @return a registration object to de-register the handler
     */
    public HandlerRegistration initialize(final PlaceManager placeManager,
                                          final EventBus eventBus,
                                          final PlaceRequest defaultPlaceRequest) {
        this.placeManager = placeManager;
        this.defaultPlaceRequest = defaultPlaceRequest;

        final HandlerRegistration historyReg =
                historian.addValueChangeHandler(event -> {
                    //Temporarily disabled until https://issues.jboss.org/browse/AF-523 is ready
//                    handleHistoryToken(event.getValue());
                });

        return () -> {
            PlaceHistoryHandler.this.defaultPlaceRequest = DefaultPlaceRequest.NOWHERE;
            PlaceHistoryHandler.this.placeManager = null;
            historyReg.removeHandler();
        };
    }

    private void updateHistoryBar() {
        if (currentBookmarkableURLStatus.endsWith(BookmarkableUrlHelper.OTHER_SCREEN_SEP)) {

            currentBookmarkableURLStatus =
                    currentBookmarkableURLStatus.substring(0,
                                                           currentBookmarkableURLStatus.length() - 1);
        }
        if (BusToolsCli.isRemoteCommunicationEnabled()) {
            historian.newItem(currentBookmarkableURLStatus,
                    false);
        }

    }

    Logger log() {
        return log;
    }

    public String getCurrentBookmarkableURLStatus() {
        return currentBookmarkableURLStatus;
    }

    private void handleHistoryToken(String token) {

        PlaceRequest newPlaceRequest = null;

        if ("".equals(token)) {
            newPlaceRequest = defaultPlaceRequest;
        }

        if (newPlaceRequest == null) {
            newPlaceRequest = mapper.getPlaceRequest(token);
        }

        if (newPlaceRequest == null) {
            log().warning("Unrecognized history token: " + token);
            newPlaceRequest = defaultPlaceRequest;
        }

        placeManager.goTo(newPlaceRequest);
    }

    /**
     * currentBookmarkableURLStatus schema   perspective#screen-1,screen-2#editor-path1,editor-path2
     * @param newPlaceRequest
     * @return
     */
    private String tokenForPlace(final PlaceRequest newPlaceRequest) {
        if (defaultPlaceRequest.equals(newPlaceRequest)) {
            return "";
        }
        return currentBookmarkableURLStatus;
    }

    /**
     * Return true if the given screen is already closed.
     * @param screen
     * @return
     */
    private boolean isScreenClosed(String screen) {
        return BookmarkableUrlHelper.isScreenClosed(screen,
                                                    currentBookmarkableURLStatus);
    }

    /**
     * Extract a perspective from a place
     * @param place
     * @return
     */
    public PlaceRequest getPerspectiveFromPlace(final PlaceRequest place) {
        return BookmarkableUrlHelper.getPerspectiveFromPlace(place);
    }

    /**
     * register opened screen of perspective
     * @param activity
     * @param place
     */
    public void registerOpen(Activity activity,
                             PlaceRequest place) {
        if (place.isUpdateLocationBarAllowed()) {
            if (activity.isType(ActivityResourceType.PERSPECTIVE.name())) {
                currentBookmarkableURLStatus = BookmarkableUrlHelper.registerOpenedPerspective(currentBookmarkableURLStatus,
                                                                                               place);
            } else if (activity.isType(ActivityResourceType.SCREEN.name())) {
                currentBookmarkableURLStatus =
                        BookmarkableUrlHelper.registerOpenedScreen(currentBookmarkableURLStatus,
                                                                   place);
            } else if (activity.isType(ActivityResourceType.EDITOR.name())) {
                currentBookmarkableURLStatus =
                        BookmarkableUrlHelper.registerOpenedScreen(currentBookmarkableURLStatus,
                                                                   place);
            }
            updateHistoryBar();
        }
    }

    public void registerClose(Activity activity,
                              PlaceRequest place) {
        if (place.isUpdateLocationBarAllowed()) {
            if (place instanceof PathPlaceRequest) {
                // handle editors
                currentBookmarkableURLStatus =
                        BookmarkableUrlHelper.registerCloseEditor(currentBookmarkableURLStatus,
                                                                  place);
            } else {
                final String id = place.getIdentifier();
                if (activity.isType(ActivityResourceType.SCREEN.name())) {
                    final String token = BookmarkableUrlHelper.getUrlToken(currentBookmarkableURLStatus,
                                                                           id);

                    currentBookmarkableURLStatus =
                            BookmarkableUrlHelper.registerClose(currentBookmarkableURLStatus,
                                                                token);
                }
            }
            updateHistoryBar();
        }
    }

    public void flush() {
        currentBookmarkableURLStatus = "";
    }

    public String getToken() {
        return (historian.getToken());
    }

    public void registerOpenDock(@Observes UberfireDocksInteractionEvent event) {
        if (event.getType() == UberfireDocksInteractionEvent.InteractionType.OPENED) {
            currentBookmarkableURLStatus =
                    BookmarkableUrlHelper.registerOpenedDock(currentBookmarkableURLStatus,
                                                             event.getTargetDock());
            updateHistoryBar();
        }
    }

    public void registerCloseDock(@Observes UberfireDocksInteractionEvent event) {
        if (event.getType() == UberfireDocksInteractionEvent.InteractionType.CLOSED) {

            currentBookmarkableURLStatus =
                    BookmarkableUrlHelper.registerClosedDock(currentBookmarkableURLStatus,
                                                             event.getTargetDock());
            updateHistoryBar();
        }
    }

    /**
     * Optional delegate in charge of History related events. Provides nice
     * isolation for unit testing, and allows pre- or post-processing of tokens.
     * Methods correspond to the like named methods on {@link History}.
     */
    public interface Historian {

        /**
         * Adds a {@link com.google.gwt.event.logical.shared.ValueChangeEvent}
         * handler to be informed of changes to the browser's history stack.
         * @param valueChangeHandler the handler
         * @return the registration used to remove this value change handler
         */
        com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> valueChangeHandler);

        /**
         * @return the current history token.
         */
        String getToken();

        /**
         * Adds a new browser history entry. Calling this method will cause
         * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
         * to be called as well.
         */
        void newItem(final String token,
                     final boolean issueEvent);
    }

    /**
     * Default implementation of {@link Historian}, based on {@link History}.
     */
    public static class DefaultHistorian
            implements
            Historian {

        @Override
        public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> valueChangeHandler) {
            return History.addValueChangeHandler(valueChangeHandler);
        }

        @Override
        public String getToken() {
            return History.getToken();
        }

        @Override
        public void newItem(String token,
                            boolean issueEvent) {
            History.newItem(token,
                            issueEvent);
        }
    }
}

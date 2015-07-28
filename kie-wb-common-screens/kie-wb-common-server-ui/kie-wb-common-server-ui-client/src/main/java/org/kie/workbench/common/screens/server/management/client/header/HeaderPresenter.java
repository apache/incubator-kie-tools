/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.header;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.server.management.client.events.HeaderClearSelectionEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderDeleteEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderFilterEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderRefreshEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderSelectAllEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStartEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStopEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderServerStatusUpdateEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;

@Dependent
public class HeaderPresenter {

    enum State {
        DISPLAY, HIDE
    }

    public interface View extends UberView<HeaderPresenter> {

        void displayDeleteContainer();

        void displayStopContainer();

        void displayStartContainer();

        void displayUpdateStatus();

        void hideDeleteContainer();

        void hideStopContainer();

        void hideStartContainer();

        void hideUpdateStatus();
    }

    private final View view;

    private final Event<HeaderFilterEvent> filterEvent;

    private final Event<HeaderClearSelectionEvent> clearSelectionEvent;

    private final Event<HeaderSelectAllEvent> selectAllEvent;

    private final Event<HeaderDeleteEvent> headerDeleteEvent;

    private final Event<HeaderStopEvent> headerStopEvent;

    private final Event<HeaderStartEvent> headerStartEvent;

    private final Event<HeaderRefreshEvent> headerRefreshEvent;

    private final Event<HeaderServerStatusUpdateEvent> headerServerStatusUpdateEvent;

    private final PlaceManager placeManager;

    private State deleteContainerState = State.DISPLAY;
    private State stopContainerState = State.DISPLAY;
    private State startContainerState = State.DISPLAY;
    private State updateStatusState = State.DISPLAY;

    @Inject
    public HeaderPresenter( final View view,
                            final PlaceManager placeManager,
                            final Event<HeaderFilterEvent> filterEvent,
                            final Event<HeaderClearSelectionEvent> clearSelectionEvent,
                            final Event<HeaderSelectAllEvent> selectAllEvent,
                            final Event<HeaderDeleteEvent> headerDeleteEvent,
                            final Event<HeaderStopEvent> headerStopEvent,
                            final Event<HeaderStartEvent> headerStartEvent,
                            final Event<HeaderRefreshEvent> headerRefreshEvent,
                            final Event<HeaderServerStatusUpdateEvent> headerServerStatusUpdateEvent) {
        this.view = view;
        this.placeManager = placeManager;
        this.filterEvent = filterEvent;
        this.clearSelectionEvent = clearSelectionEvent;
        this.selectAllEvent = selectAllEvent;
        this.headerDeleteEvent = headerDeleteEvent;
        this.headerStopEvent = headerStopEvent;
        this.headerStartEvent = headerStartEvent;
        this.headerRefreshEvent = headerRefreshEvent;
        this.headerServerStatusUpdateEvent = headerServerStatusUpdateEvent;
        this.view.init( this );
    }

    public void displayDeleteContainer() {
        deleteContainerState = State.DISPLAY;
        view.displayDeleteContainer();
    }

    public void displayStopContainer() {
        stopContainerState = State.DISPLAY;
        view.displayStopContainer();
    }

    public void displayStartContainer() {
        startContainerState = State.DISPLAY;
        view.displayStartContainer();
    }

    public void displayUpdateStatus() {
        updateStatusState = State.DISPLAY;
        view.displayUpdateStatus();
    }

    public void hideStartContainer() {
        startContainerState = State.HIDE;
        view.hideStartContainer();
    }

    public void hideStopContainer() {
        stopContainerState = State.HIDE;
        view.hideStopContainer();
    }

    public void hideDeleteContainer() {
        deleteContainerState = State.HIDE;
        view.hideDeleteContainer();
    }

    public void hideUpdateStatus() {
        updateStatusState = State.HIDE;
        view.hideUpdateStatus();
    }

    public View getView() {
        return view;
    }

    public void filter( String value ) {
        filterEvent.fire( new HeaderFilterEvent( this, value ) );
    }

    public void registerServer() {
        placeManager.goTo( "ServerRegistryEndpoint" );
    }

    public void refresh() {
        headerRefreshEvent.fire( new HeaderRefreshEvent( this ) );
    }

    public void selectAll() {
        selectAllEvent.fire( new HeaderSelectAllEvent( this ) );
    }

    public void clearSelection() {
        clearSelectionEvent.fire( new HeaderClearSelectionEvent( this ) );
    }

    public void start() {
        if ( startContainerState.equals( State.DISPLAY ) ) {
            headerStartEvent.fire( new HeaderStartEvent( this ) );
        }
    }

    public void stopContainer() {
        if ( stopContainerState.equals( State.DISPLAY ) ) {
            headerStopEvent.fire( new HeaderStopEvent( this ) );
        }
    }

    public void delete() {
        if ( deleteContainerState.equals( State.DISPLAY ) ) {
            headerDeleteEvent.fire( new HeaderDeleteEvent( this ) );
        }
    }

    public void updateServerStatus() {
        headerServerStatusUpdateEvent.fire( new HeaderServerStatusUpdateEvent( this ) );
    }
}
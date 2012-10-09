/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.commons.util.Preconditions;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<PlaceRequest, WorkbenchActivity> existingWorkbenchActivities = new HashMap<PlaceRequest, WorkbenchActivity>();
    private final Map<PlaceRequest, PartDefinition>    existingWorkbenchParts      = new HashMap<PlaceRequest, PartDefinition>();
    private final Map<PlaceRequest, Command>           onRevealCallbacks           = new HashMap<PlaceRequest, Command>();

    private final ActivityManager                      activityManager;

    private EventBus                                   tempBus                     = null;

    private final PanelManager                         panelManager;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent>       workbenchPartBeforeCloseEvent;

    @Inject
    private Event<WorkbenchPartCloseEvent>             workbenchPartCloseEvent;

    @Inject
    private Event<WorkbenchPartLostFocusEvent>         workbenchPartLostFocusEvent;

    private final Event<SelectWorkbenchPartEvent>      selectWorkbenchPartEvent;

    private final PlaceHistoryHandler                  placeHistoryHandler;

    private PlaceRequest                               currentPlaceRequest;

    @Inject
    public PlaceManagerImpl(ActivityManager activityManager,
                            PlaceHistoryHandler placeHistoryHandler,
                            Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent,
                            PanelManager panelManager) {
        this.activityManager = activityManager;
        this.placeHistoryHandler = placeHistoryHandler;
        this.selectWorkbenchPartEvent = selectWorkbenchPartEvent;
        this.panelManager = panelManager;

        initPlaceHistoryHandler();
    }

    public void initPlaceHistoryHandler() {
        placeHistoryHandler.register( this,
                                      produceEventBus(),
                                      DefaultPlaceRequest.NOWHERE );
    }

    @Override
    public void goTo(final String identifier) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place,
              null );
    }

    @Override
    public void goTo(final String identifier,
                     final Command callback) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place,
              callback );
    }

    @Override
    public void goTo(PlaceRequest place) {
        goTo( place,
              null );
    }

    @Override
    public void goTo(final PlaceRequest place,
                     final Command callback) {
        if ( place == null || place.equals( DefaultPlaceRequest.NOWHERE ) ) {
            return;
        }
        final Activity activity = activityManager.getActivity( place );
        if ( activity == null ) {
            return;
        }

        if ( activity instanceof WorkbenchActivity ) {
            final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
            launchActivity( place,
                            workbenchActivity,
                            workbenchActivity.getDefaultPosition(),
                            callback );
        } else if ( activity instanceof PopupActivity ) {
            launchActivity( place,
                            (PopupActivity) activity,
                            callback );
        } else if ( activity instanceof PerspectiveActivity ) {
            launchActivity( place,
                            (PerspectiveActivity) activity,
                            callback );
        }
    }

    @Override
    public void goTo(final PartDefinition part,
                     final PanelDefinition panel) {
        final PlaceRequest place = part.getPlace();
        if ( place == null ) {
            return;
        }
        final Activity activity = activityManager.getActivity( place );
        if ( activity == null ) {
            return;
        }

        if ( activity instanceof WorkbenchActivity ) {
            final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
            launchActivity( workbenchActivity,
                            part,
                            panel,
                            null );
        } else {
            throw new IllegalArgumentException( "placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel." );
        }
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if ( currentPlaceRequest != null ) {
            return currentPlaceRequest;
        } else {
            return DefaultPlaceRequest.NOWHERE;
        }
    }

    /**
     * Lookup the WorkbenchActivity corresponding to the WorkbenchPart
     * 
     * @param part
     * @return
     */
    @Override
    public WorkbenchActivity getActivity(final PartDefinition part) {
        final PlaceRequest place = getPlaceForPart( part );
        if ( place == null ) {
            return null;
        }
        final WorkbenchActivity activity = existingWorkbenchActivities.get( place );
        return activity;
    }

    @Override
    public void closeCurrentPlace() {
        if ( DefaultPlaceRequest.NOWHERE.equals( currentPlaceRequest ) ) {
            return;
        }
        closePlace( currentPlaceRequest );
    }

    @Override
    public void closePlace(final PlaceRequest placeToClose) {
        if ( placeToClose == null ) {
            return;
        }
        final PartDefinition partToClose = existingWorkbenchParts.get( placeToClose );
        if ( partToClose != null ) {
            workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( partToClose ) );
            if ( currentPlaceRequest.equals( placeToClose ) ) {
                currentPlaceRequest = DefaultPlaceRequest.NOWHERE;
            }
        }
    }

    @Override
    public void closeAllPlaces() {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>( existingWorkbenchParts.keySet() );
        for ( PlaceRequest placeToClose : placesToClose ) {
            closePlace( placeToClose );
        }
    }

    @Override
    public void registerOnRevealCallback(final PlaceRequest place,
                                         final Command command) {
        Preconditions.checkNotNull( "place",
                                    place );
        Preconditions.checkNotNull( "command",
                                    command );
        this.onRevealCallbacks.put( place,
                                    command );
    }

    @Override
    public void unregisterOnRevealCallback(final PlaceRequest place) {
        Preconditions.checkNotNull( "place",
                                    place );
        this.onRevealCallbacks.remove( place );
    }

    @Override
    public void executeOnRevealCallback(final PlaceRequest place) {
        Preconditions.checkNotNull( "place",
                                    place );
        final Command callback = this.onRevealCallbacks.get( place );
        if ( callback != null ) {
            callback.execute();
        }
    }

    private void launchActivity(final PlaceRequest place,
                                final WorkbenchActivity activity,
                                final Position position,
                                final Command callback) {
        final PartDefinition part = new PartDefinitionImpl( place );
        final PanelDefinition panel = panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                                                      position );
        launchActivity( activity,
                        part,
                        panel,
                        callback );
    }

    private void launchActivity(final WorkbenchActivity activity,
                                final PartDefinition part,
                                final PanelDefinition panel,
                                final Command callback) {
        //If we're already showing this place exit.
        if ( existingWorkbenchParts.containsValue( part ) ) {
            selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( part ) );
            return;
        }

        //Record new place\part\activity
        final PlaceRequest place = part.getPlace();
        currentPlaceRequest = place;
        existingWorkbenchActivities.put( place,
                                         activity );
        existingWorkbenchParts.put( place,
                                    part );
        updateHistory( place );

        //Reveal activity with call-back to attach to Workbench
        activity.launch( new AcceptItem() {
                             public void add(String tabTitle,
                                             IsWidget widget) {
                                 panelManager.addWorkbenchPart( tabTitle,
                                                                part,
                                                                panel,
                                                                widget );
                             }
                         },
                         place,
                         callback );
    }

    private void launchActivity(final PlaceRequest place,
                                final PopupActivity activity,
                                final Command callback) {
        activity.launch( place,
                         callback );
    }

    private void launchActivity(final PlaceRequest place,
                                final PerspectiveActivity activity,
                                final Command callback) {
        activity.launch( place,
                         callback );
    }

    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosed(@Observes WorkbenchPartBeforeCloseEvent event) {
        final PartDefinition part = event.getPart();
        final PlaceRequest place = getPlaceForPart( part );
        if ( place == null ) {
            return;
        }
        final WorkbenchActivity activity = existingWorkbenchActivities.get( place );
        if ( activity == null ) {
            return;
        }
        if ( activity.onMayClose() ) {
            activity.onClose();
            existingWorkbenchActivities.remove( place );
            existingWorkbenchParts.remove( place );
            activityManager.removeActivity( place );
            if ( part.equals( currentPlaceRequest ) ) {
                workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( part ) );
            }
            workbenchPartCloseEvent.fire( new WorkbenchPartCloseEvent( part ) );
        }
    }

    private PlaceRequest getPlaceForPart(final PartDefinition part) {
        for ( Map.Entry<PlaceRequest, PartDefinition> e : existingWorkbenchParts.entrySet() ) {
            if ( e.getValue().equals( part ) ) {
                return e.getKey();
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final PartDefinition part = event.getPart();
        final WorkbenchActivity activity = getActivity( part );
        if ( activity == null ) {
            return;
        }
        currentPlaceRequest = this.getPlaceForPart( part );
        activity.onFocus();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        final PartDefinition part = event.getDeselectedPart();
        final WorkbenchActivity activity = getActivity( part );
        if ( activity == null ) {
            return;
        }
        currentPlaceRequest = DefaultPlaceRequest.NOWHERE;
        activity.onLostFocus();
    }

    @Produces
    @ApplicationScoped
    EventBus produceEventBus() {
        if ( tempBus == null ) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

}

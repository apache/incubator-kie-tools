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

import static org.uberfire.shared.mvp.PlaceRequest.NOWHERE;

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
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<PlaceRequest, WorkbenchActivity> existingWorkbenchActivities = new HashMap<PlaceRequest, WorkbenchActivity>();
    private final Map<PlaceRequest, PartDefinition>    existingWorkbenchParts      = new HashMap<PlaceRequest, PartDefinition>();

    private final ActivityManager                      activityManager;

    private EventBus                                   tempBus                     = null;

    private final PanelManager                         panelManager;

    @Inject
    private Event<WorkbenchPartCloseEvent>             workbenchPartCloseEvent;

    private final Event<SelectWorkbenchPartEvent>      selectWorkbenchPartEvent;

    private final PlaceHistoryHandler                  placeHistoryHandler;

    PlaceRequest                                       currentPlaceRequest;

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
                                      NOWHERE );
    }

    @Override
    public void goTo(final PlaceRequest placeRequest) {
        if ( placeRequest == null || placeRequest.equals( NOWHERE ) ) {
            return;
        }
        final Activity activity = activityManager.getActivity( placeRequest );
        if ( activity == null ) {
            return;
        }

        if ( activity instanceof WorkbenchActivity ) {
            final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
            launchActivity( placeRequest,
                            workbenchActivity,
                            workbenchActivity.getDefaultPosition() );
        } else if ( activity instanceof PopupActivity ) {
            launchActivity( placeRequest,
                            (PopupActivity) activity );
        } else if ( activity instanceof PerspectiveActivity ) {
            launchActivity( placeRequest,
                            (PerspectiveActivity) activity );
        }
    }

    @Override
    public void goTo(final PartDefinition part,
                     final PanelDefinition panel) {
        if ( part.getPlace() == null ) {
            return;
        }
        final Activity activity = activityManager.getActivity( part.getPlace() );
        if ( activity == null ) {
            return;
        }

        if ( activity instanceof WorkbenchActivity ) {
            final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
            launchActivity( workbenchActivity,
                            part,
                            panel );
        } else {
            throw new IllegalArgumentException( "placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel." );
        }
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if ( currentPlaceRequest != null ) {
            return currentPlaceRequest;
        } else {
            return NOWHERE;
        }
    }

    @Override
    public void closeAllPlaces() {
        final List<PartDefinition> partsToBeClosed = new ArrayList<PartDefinition>( existingWorkbenchParts.values() );
        for ( PartDefinition part : partsToBeClosed ) {
            final WorkbenchPartBeforeCloseEvent beforeCloseEvent = new WorkbenchPartBeforeCloseEvent( part );
            onWorkbenchPartClosed( beforeCloseEvent );
        }
    }

    private void launchActivity(final PlaceRequest newPlace,
                                final WorkbenchActivity activity,
                                final Position position) {
        final PartDefinition part = new PartDefinitionImpl( newPlace );
        final PanelDefinition panel = panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                                                      position );
        launchActivity( activity,
                        part,
                        panel );
    }

    private void launchActivity(final WorkbenchActivity activity,
                                final PartDefinition part,
                                final PanelDefinition panel) {
        //If we're already showing this place exit.
        if ( existingWorkbenchParts.containsValue( part ) ) {
            selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( part ) );
            return;
        }

        //Record new activity
        currentPlaceRequest = part.getPlace();
        existingWorkbenchActivities.put( part.getPlace(),
                                         activity );
        existingWorkbenchParts.put( part.getPlace(),
                                    part );

        //Reveal activity with call-back to attach to Workbench
        activity.launch(
                new AcceptItem() {
                    public void add(String tabTitle,
                                    IsWidget widget) {
                        panelManager.addWorkbenchPart( tabTitle,
                                                       part,
                                                       panel,
                                                       widget );
                    }
                } );

        updateHistory( part.getPlace() );
    }

    private void launchActivity(final PlaceRequest newPlace,
                                final PopupActivity activity) {
        activity.launch();
    }

    private void launchActivity(final PlaceRequest newPlace,
                                final PerspectiveActivity activity) {
        activity.launch();
        activity.onReveal();
    }

    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
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
        activity.onFocus();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        final PartDefinition part = event.getDeselectedPart();
        final WorkbenchActivity activity = getActivity( part );
        if ( activity == null ) {
            return;
        }
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

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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
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

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<PlaceRequest, Activity>();
    private final Map<PlaceRequest, PartDefinition> existingWorkbenchParts = new HashMap<PlaceRequest, PartDefinition>();
    private final Map<PlaceRequest, Command> onRevealCallbacks = new HashMap<PlaceRequest, Command>();

    private final ActivityManager activityManager;

    private EventBus tempBus = null;

    private final PanelManager panelManager;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<WorkbenchPartCloseEvent> workbenchPartCloseEvent;

    @Inject
    private Event<WorkbenchPartLostFocusEvent> workbenchPartLostFocusEvent;

    private final Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent;

    private final PlaceHistoryHandler placeHistoryHandler;

    private PlaceRequest currentPlaceRequest;

    @Inject
    public PlaceManagerImpl( ActivityManager activityManager,
                             PlaceHistoryHandler placeHistoryHandler,
                             Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent,
                             PanelManager panelManager ) {
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
    public void goTo( final String identifier ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place,
              null );
    }

    @Override
    public void goTo( final String identifier,
                      final Command callback ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place,
              callback );
    }

    @Override
    public void goTo( PlaceRequest place ) {
        goTo( place,
              null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final Command callback ) {
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
    public void goTo( final PartDefinition part,
                      final PanelDefinition panel ) {
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
            launchActivity( place,
                            workbenchActivity,
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
     * @param place
     * @return
     */
    @Override
    public Activity getActivity( final PlaceRequest place ) {
        if ( place == null ) {
            return null;
        }
        final Activity activity = existingWorkbenchActivities.get( place );
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
    public void closePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( placeToClose ) );
    }

    @Override
    public void closeAllPlaces() {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>( existingWorkbenchParts.keySet() );
        for ( PlaceRequest placeToClose : placesToClose ) {
            closePlace( placeToClose );
        }
    }

    @Override
    public void registerOnRevealCallback( final PlaceRequest place,
                                          final Command command ) {
        Preconditions.checkNotNull( "place",
                                    place );
        Preconditions.checkNotNull( "command",
                                    command );
        this.onRevealCallbacks.put( place,
                                    command );
    }

    @Override
    public void unregisterOnRevealCallback( final PlaceRequest place ) {
        Preconditions.checkNotNull( "place",
                                    place );
        this.onRevealCallbacks.remove( place );
    }

    @Override
    public void executeOnRevealCallback( final PlaceRequest place ) {
        Preconditions.checkNotNull( "place",
                                    place );
        final Command callback = this.onRevealCallbacks.get( place );
        if ( callback != null ) {
            callback.execute();
        }
    }

    private void launchActivity( final PlaceRequest place,
                                 final WorkbenchActivity activity,
                                 final Position position,
                                 final Command callback ) {

        //If we're already showing this place exit.
        if ( existingWorkbenchParts.containsKey( place ) ) {
            selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( place ) );
            return;
        }

        final PartDefinition part = new PartDefinitionImpl( place );
        final PanelDefinition panel = panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                                                      position );

        launchActivity( place,
                        activity,
                        part,
                        panel,
                        callback );
    }

    private void launchActivity( final PlaceRequest place,
                                 final WorkbenchActivity activity,
                                 final PartDefinition part,
                                 final PanelDefinition panel,
                                 final Command callback ) {

        //Record new place\part\activity
        currentPlaceRequest = place;
        existingWorkbenchActivities.put( place,
                                         activity );
        existingWorkbenchParts.put( place,
                                    part );
        updateHistory( place );

        //Reveal activity with call-back to attach to Workbench
        activity.launch( new AcceptItem() {
            public void add( final IsWidget tabTitle,
                             final IsWidget widget ) {
                panelManager.addWorkbenchPart( place,
                                               part,
                                               panel,
                                               tabTitle,
                                               widget );
            }
        },
                         place,
                         callback );
    }

    private void launchActivity( final PlaceRequest place,
                                 final PopupActivity activity,
                                 final Command callback ) {
        //Record new place\part\activity
        currentPlaceRequest = place;
        existingWorkbenchActivities.put( place,
                                         activity );
        updateHistory( place );

        activity.launch( place,
                         callback );
    }

    private void launchActivity( final PlaceRequest place,
                                 final PerspectiveActivity activity,
                                 final Command callback ) {
        activity.launch( place,
                         callback );
    }

    public void updateHistory( PlaceRequest request ) {
        placeHistoryHandler.onPlaceChange( request );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartBeforeClose( @Observes WorkbenchPartBeforeCloseEvent event ) {
        final PlaceRequest place = event.getPlace();
        if ( place == null ) {
            return;
        }
        final Activity activity = existingWorkbenchActivities.get( place );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            onWorkbenchPartBeforeClose( (WorkbenchActivity) activity, place );
        } else if ( activity instanceof PopupActivity ) {
            onWorkbenchPartBeforeClose( (PopupActivity) activity, place );
        }
    }

    private void onWorkbenchPartBeforeClose( final WorkbenchActivity activity,
                                             final PlaceRequest place ) {
        if ( activity.onMayClose() ) {
            activity.onClose();
            existingWorkbenchActivities.remove( place );
            existingWorkbenchParts.remove( place );
            activityManager.removeActivity( place );
            if ( place.equals( currentPlaceRequest ) ) {
                workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( place ) );
            }
            workbenchPartCloseEvent.fire( new WorkbenchPartCloseEvent( place ) );
            if ( currentPlaceRequest.equals( place ) ) {
                currentPlaceRequest = DefaultPlaceRequest.NOWHERE;
            }

        }
    }

    private void onWorkbenchPartBeforeClose( final PopupActivity activity,
                                             final PlaceRequest place ) {
        if ( activity.onMayClose() ) {
            activity.onClose();
            existingWorkbenchActivities.remove( place );
            activityManager.removeActivity( place );
            workbenchPartCloseEvent.fire( new WorkbenchPartCloseEvent( place ) );
            if ( currentPlaceRequest.equals( place ) ) {
                currentPlaceRequest = DefaultPlaceRequest.NOWHERE;
            }
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus( @Observes WorkbenchPartOnFocusEvent event ) {
        final PlaceRequest place = event.getPlace();
        final Activity activity = getActivity( place );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            currentPlaceRequest = place;
            ( (WorkbenchActivity) activity ).onFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus( @Observes WorkbenchPartLostFocusEvent event ) {
        final Activity activity = getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            currentPlaceRequest = DefaultPlaceRequest.NOWHERE;
            ( (WorkbenchActivity) activity ).onLostFocus();
        }
    }

    @Produces
    @ApplicationScoped EventBus produceEventBus() {
        if ( tempBus == null ) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

}

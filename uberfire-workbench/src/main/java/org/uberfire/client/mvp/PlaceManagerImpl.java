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
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.kie.commons.data.Pair;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.widgets.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.SavePlaceEvent;
import org.uberfire.client.workbench.widgets.events.SelectPlaceEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static org.kie.commons.validation.PortablePreconditions.*;

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
    private Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<ClosePlaceEvent> workbenchPartCloseEvent;

    @Inject
    private Event<PlaceLostFocusEvent> workbenchPartLostFocusEvent;

    @Inject
    private DefaultPlaceResolver defaultPlaceResolver;

    private final Event<SelectPlaceEvent> selectWorkbenchPartEvent;

    private final PlaceHistoryHandler placeHistoryHandler;

    @Inject
    public PlaceManagerImpl( final ActivityManager activityManager,
                             final PlaceHistoryHandler placeHistoryHandler,
                             final Event<SelectPlaceEvent> selectWorkbenchPartEvent,
                             final PanelManager panelManager ) {
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
        goTo( place, null );
    }

    @Override
    public void goTo( final String identifier,
                      final Command callback ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, callback );
    }

    @Override
    public void goTo( PlaceRequest place ) {
        goTo( place, null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final Command callback ) {
        if ( place == null || place.equals( DefaultPlaceRequest.NOWHERE ) ) {
            return;
        }
        final Pair<Activity, PlaceRequest> requestPair = resolveActivity( place );

        if ( requestPair.getK1() != null ) {
            final Activity activity = requestPair.getK1();
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
        } else {
            goTo( requestPair.getK2() );
        }
    }

    private Pair<Activity, PlaceRequest> resolveActivity( final PlaceRequest place ) {
        final Set<Activity> activities = activityManager.getActivities( place );

        if ( activities == null || activities.size() == 0 ) {
            final PlaceRequest notFoundPopup = new DefaultPlaceRequest( "workbench.activity.notfound" );
            notFoundPopup.addParameter( "requestedPlaceIdentifier", place.getIdentifier() );

            return Pair.newPair( null, notFoundPopup );
        } else if ( activities.size() > 1 ) {
//            final PlaceRequest multiplePlaces = new DefaultPlaceRequest( "workbench.activities.multiple" ).addParameter( "requestedPlaceIdentifier", identifier );
// Check if there is a default
//            final String editorId = defaultPlaceResolver.getEditorId( place.getIdentifier() );
//            if ( editorId == null ) {
//                goToMultipleActivitiesPlace( placeRequest.getIdentifier() );
//            } else {
//                for ( final Activity activity : getActivities( placeRequest ) ) {
//                    if ( activity.getSignatureId().equals( editorId ) ) {
//                        return activity;
//                    }
//                }
//                goToMultipleActivitiesPlace( placeRequest.getIdentifier() );
//        }
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest( "workbench.activities.multiple" ).addParameter( "requestedPlaceIdentifier", null );
            return Pair.newPair( null, multiplePlaces );
        }

        return Pair.newPair( activities.iterator().next(), null );
    }

    @Override
    public void goTo( final PartDefinition part,
                      final PanelDefinition panel ) {
        final PlaceRequest place = part.getPlace();
        if ( place == null ) {
            return;
        }
        final Pair<Activity, PlaceRequest> requestPair = resolveActivity( place );

        if ( requestPair.getK1() != null ) {
            final Activity activity = requestPair.getK1();

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
        } else {
            goTo( requestPair.getK2() );
        }
    }

    @Override
    public void goTo( final Path path ) {
        goTo( getPlace( path ) );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest ) {
        goTo( getPlace( path, placeRequest ) );
    }

    @Override
    public void goTo( Path path,
                      Command callback ) {
        goTo( getPlace( path ), callback );
    }

    private PlaceRequest getPlace( final Path path,
                                   final PlaceRequest placeRequest ) {
        final PlaceRequest request = getPlace( path );

        for ( final Map.Entry<String, String> entry : placeRequest.getParameters().entrySet() ) {
            request.addParameter( entry.getKey(), entry.getValue() );
        }

        return request;
    }

    private PlaceRequest getPlace( final Path path ) {
        return new PathPlaceRequest( path );
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
    public void closePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        workbenchPartBeforeCloseEvent.fire( new BeforeClosePlaceEvent( placeToClose ) );
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
        checkNotNull( "place", place );
        checkNotNull( "command", command );
        this.onRevealCallbacks.put( place,
                                    command );
    }

    @Override
    public void unregisterOnRevealCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
        this.onRevealCallbacks.remove( place );
    }

    @Override
    public void executeOnRevealCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
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
            selectWorkbenchPartEvent.fire( new SelectPlaceEvent( place ) );
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
        existingWorkbenchActivities.put( place,
                                         activity );
        existingWorkbenchParts.put( place,
                                    part );
        updateHistory( place );

        //Reveal activity with call-back to attach to Workbench
        activity.launch( new AcceptItem() {
            public void add( final String title,
                             final IsWidget titleDecoration,
                             final IsWidget widget ) {
                panelManager.addWorkbenchPart( place,
                                               part,
                                               panel,
                                               title,
                                               titleDecoration,
                                               widget );
            }
        }, place, callback );
    }

    private void launchActivity( final PlaceRequest place,
                                 final PopupActivity activity,
                                 final Command callback ) {
        //Record new place\part\activity
        existingWorkbenchActivities.put( place, activity );
        updateHistory( place );

        activity.launch( place, callback );
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
    private void onWorkbenchPartBeforeClose( @Observes BeforeClosePlaceEvent event ) {
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
            workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        }
    }

    private void onWorkbenchPartBeforeClose( final PopupActivity activity,
                                             final PlaceRequest place ) {
        if ( activity.onMayClose() ) {
            activity.onClose();
            workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        }
    }

    private void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        existingWorkbenchActivities.remove( place );
        existingWorkbenchParts.remove( place );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus( @Observes PlaceGainFocusEvent event ) {
        final PlaceRequest place = event.getPlace();
        final Activity activity = getActivity( place );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            ( (WorkbenchActivity) activity ).onFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus( @Observes PlaceLostFocusEvent event ) {
        final Activity activity = getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            ( (WorkbenchActivity) activity ).onLostFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onSavePlace( @Observes SavePlaceEvent event ) {
        final Activity activity = getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchEditorActivity ) {
            final WorkbenchEditorActivity editor = (WorkbenchEditorActivity) activity;
            editor.onSave();
        }
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

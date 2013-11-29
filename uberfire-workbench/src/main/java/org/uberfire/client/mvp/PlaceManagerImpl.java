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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.PerspectiveChange;
import org.uberfire.workbench.events.PlaceGainFocusEvent;
import org.uberfire.workbench.events.PlaceLostFocusEvent;
import org.uberfire.workbench.events.SavePlaceEvent;
import org.uberfire.workbench.events.SelectPlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static java.util.Collections.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class PlaceManagerImpl
        implements PlaceManager {

    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<PlaceRequest, Activity>();
    private final Map<PlaceRequest, PartDefinition> existingWorkbenchParts = new HashMap<PlaceRequest, PartDefinition>();
    private final Map<PlaceRequest, Command> onOpenCallbacks = new HashMap<PlaceRequest, Command>();

    private EventBus tempBus = null;

    @Inject
    private Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<ClosePlaceEvent> workbenchPartCloseEvent;

    @Inject
    private Event<PerspectiveChange> perspectiveChangeEvent;

    @Inject
    private Event<PlaceLostFocusEvent> workbenchPartLostFocusEvent;

    @Inject
    private Event<NewSplashScreenActiveEvent> newSplashScreenActiveEvent;

    @Inject
    private DefaultPlaceResolver defaultPlaceResolver;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private PlaceHistoryHandler placeHistoryHandler;

    @Inject
    private Event<SelectPlaceEvent> selectWorkbenchPartEvent;

    @Inject
    private PanelManager panelManager;

    private Map<String, SplashScreenActivity> activeSplashScreens = new HashMap<String, SplashScreenActivity>();

    @PostConstruct
    public void initPlaceHistoryHandler() {
        placeHistoryHandler.register( this,
                                      produceEventBus(),
                                      DefaultPlaceRequest.NOWHERE );
    }

    @Override
    public void goTo( final String identifier,
                      final PanelDefinition panel ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, null, panel );
    }

    @Override
    public void goTo( final String identifier ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, null, null );
    }

    @Override
    public void goTo( final String identifier,
                      final Command callback,
                      final PanelDefinition panel ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, callback, panel );
    }

    @Override
    public void goTo( final String identifier,
                      final Command callback ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, callback, null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final PanelDefinition panel ) {
        goTo( place, null, panel );
    }

    @Override
    public void goTo( PlaceRequest place ) {
        goTo( place, null, null );
    }

    @Override
    public void goTo( final Path path,
                      final PanelDefinition panel ) {
        goTo( getPlace( path ), null, panel );
    }

    @Override
    public void goTo( final Path path ) {
        goTo( getPlace( path ), null, null );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest,
                      final PanelDefinition panel ) {
        goTo( getPlace( path, placeRequest ), null, panel );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest ) {
        goTo( getPlace( path, placeRequest ), null, null );
    }

    @Override
    public void goTo( final Path path,
                      final Command callback,
                      final PanelDefinition panel ) {
        goTo( getPlace( path ), callback, panel );
    }

    @Override
    public void goTo( Path path,
                      Command callback ) {
        goTo( getPlace( path ), callback, null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final Command callback ) {
        goTo( place, callback, null );
    }

    @Override
    public void goTo( PlaceRequest place,
                      Command callback,
                      PanelDefinition panel ) {
        if ( place == null || place.equals( DefaultPlaceRequest.NOWHERE ) ) {
            return;
        }
        final Pair<Activity, PlaceRequest> requestPair = resolveActivity( place );

        if ( requestPair.getK1() != null ) {
            final Activity activity = requestPair.getK1();
            if ( activity instanceof WorkbenchActivity ) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
                launchActivity( requestPair.getK2(),
                                workbenchActivity,
                                workbenchActivity.getDefaultPosition(),
                                panel,
                                callback );
            } else if ( activity instanceof PopupActivity ) {
                launchActivity( requestPair.getK2(),
                                (PopupActivity) activity,
                                callback );
            } else if ( activity instanceof PerspectiveActivity ) {
                launchActivity( requestPair.getK2(),
                                (PerspectiveActivity) activity,
                                callback );
            }
        } else {
            goTo( requestPair.getK2(), panel );
        }
    }

    private Pair<Activity, PlaceRequest> resolveActivity( final PlaceRequest place ) {

        final Pair<Activity, PlaceRequest> existingPair = resolveExistingParts( place );

        if ( existingPair != null ) {
            return existingPair;
        }

        final Set<Activity> activities = activityManager.getActivities( place );

        if ( activities == null || activities.size() == 0 ) {
            final PlaceRequest notFoundPopup = new DefaultPlaceRequest( "workbench.activity.notfound" );
            notFoundPopup.addParameter( "requestedPlaceIdentifier", place.getIdentifier() );

            return Pair.newPair( null, notFoundPopup );
        } else if ( activities.size() > 1 ) {
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest( "workbench.activities.multiple" ).addParameter( "requestedPlaceIdentifier", null );

            return Pair.newPair( null, multiplePlaces );
        }

        return Pair.newPair( activities.iterator().next(), place );
    }

    private Pair<Activity, PlaceRequest> resolveExistingParts( final PlaceRequest place ) {
        final Activity activity = getActivity( place );

        if ( activity != null ) {
            return new Pair<Activity, PlaceRequest>( activity, place );
        }

        if ( place instanceof PathPlaceRequest ) {
            for ( final Map.Entry<PlaceRequest, PartDefinition> entry : existingWorkbenchParts.entrySet() ) {
                if ( entry.getKey() instanceof PathPlaceRequest &&
                        ( (PathPlaceRequest) entry.getKey() ).getPath().compareTo( ( (PathPlaceRequest) place ).getPath() ) == 0 ) {
                    return new Pair<Activity, PlaceRequest>( getActivity( entry.getKey() ), entry.getKey() );
                }
            }
        }

        return null;
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
    public PlaceStatus getStatus( String id ) {
        return getStatus( new DefaultPlaceRequest( id ) );
    }

    @Override
    public PlaceStatus getStatus( final PlaceRequest place ) {
        return resolveExistingParts( place ) != null ? PlaceStatus.OPEN : PlaceStatus.CLOSE;
    }

    @Override
    public void closePlace( final String id ) {
        closePlace( new DefaultPlaceRequest( id ) );
    }

    @Override
    public void closePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        workbenchPartBeforeCloseEvent.fire( new BeforeClosePlaceEvent( placeToClose, false ) );
    }

    @Override
    public void forceClosePlace( final String id ) {
        forceClosePlace( new DefaultPlaceRequest( id ) );
    }

    @Override
    public void forceClosePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        workbenchPartBeforeCloseEvent.fire( new BeforeClosePlaceEvent( placeToClose, true ) );
    }

    @Override
    public void closeAllPlaces() {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>( existingWorkbenchParts.keySet() );
        for ( PlaceRequest placeToClose : placesToClose ) {
            closePlace( placeToClose );
        }
    }

    @Override
    public void registerOnOpenCallback( final PlaceRequest place,
                                        final Command command ) {
        checkNotNull( "place", place );
        checkNotNull( "command", command );
        this.onOpenCallbacks.put( place,
                                  command );
    }

    @Override
    public void unregisterOnOpenCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
        this.onOpenCallbacks.remove( place );
    }

    @Override
    public void executeOnOpenCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
        final Command callback = this.onOpenCallbacks.get( place );
        if ( callback != null ) {
            callback.execute();
        }
    }

    @Override
    public Collection<SplashScreenActivity> getActiveSplashScreens() {
        return unmodifiableCollection( activeSplashScreens.values() );
    }

    private void launchActivity( final PlaceRequest place,
                                 final WorkbenchActivity activity,
                                 final Position position,
                                 final PanelDefinition _panel,
                                 final Command callback ) {

        //If we're already showing this place exit.
        if ( existingWorkbenchParts.containsKey( place ) ) {
            selectWorkbenchPartEvent.fire( new SelectPlaceEvent( place ) );
            return;
        }

        final PartDefinition part = new PartDefinitionImpl( place );
        final PanelDefinition panel;
        if ( _panel != null ) {
            panel = _panel;
        } else {
            panel = panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                                    position );
        }

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

        final SplashScreenActivity splashScreen = activityManager.getSplashScreenInterceptor( place );

        //Reveal activity with call-back to attach to Workbench
        activity.launch( new AcceptItem() {
            public void add( final UIPart uiPart ) {
                panelManager.addWorkbenchPart( place,
                                               part,
                                               panel,
                                               activity.getMenus(),
                                               uiPart,
                                               activity.contextId() );
                if ( splashScreen != null ) {
                    activeSplashScreens.put( place.getIdentifier(), splashScreen );
                    newSplashScreenActiveEvent.fire( new NewSplashScreenActiveEvent() );
                    splashScreen.launch( place, null );
                }
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
        activeSplashScreens.clear();
        perspectiveChangeEvent.fire( new PerspectiveChange( activity.getPerspective(), activity.getMenus(), activity.getIdentifier() ) );
        final SplashScreenActivity splashScreen = activityManager.getSplashScreenInterceptor( place );
        activity.launch( place, callback );
        if ( splashScreen != null ) {
            activeSplashScreens.put( place.getIdentifier(), splashScreen );
            splashScreen.launch( place, null );
        }
        newSplashScreenActiveEvent.fire( new NewSplashScreenActiveEvent() );
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

        activeSplashScreens.remove( place.getIdentifier() );

        if ( activity instanceof WorkbenchActivity ) {
            onWorkbenchPartBeforeClose( (WorkbenchActivity) activity, place, event.isForce() );
        } else if ( activity instanceof PopupActivity ) {
            onWorkbenchPartBeforeClose( (PopupActivity) activity, place, event.isForce() );
        }
    }

    private void onWorkbenchPartBeforeClose( final WorkbenchActivity activity,
                                             final PlaceRequest place,
                                             final boolean force ) {
        if ( force || activity.onMayClose() ) {
            activity.onClose();
            workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        }
    }

    private void onWorkbenchPartBeforeClose( final PopupActivity activity,
                                             final PlaceRequest place,
                                             final boolean force ) {
        if ( force || activity.onMayClose() ) {
            activity.onClose();
            workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        }
    }

    private void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        final Activity activity = existingWorkbenchActivities.remove( place );
        existingWorkbenchParts.remove( place );

        if ( activity instanceof PopupActivity ) {
            ( (PopupActivity) activity ).onShutdown();
        } else if ( activity instanceof WorkbenchActivity ) {
            ( (WorkbenchActivity) activity ).onShutdown();
        } else if ( activity instanceof PerspectiveActivity ) {
            ( (PerspectiveActivity) activity ).onShutdown();
        }

        Scheduler.get().scheduleFinally( new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if ( place instanceof PathPlaceRequest ) {
                    ( (PathPlaceRequest) place ).getPath().dispose();
                }

                activityManager.destroyActivity( activity );
            }
        } );
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

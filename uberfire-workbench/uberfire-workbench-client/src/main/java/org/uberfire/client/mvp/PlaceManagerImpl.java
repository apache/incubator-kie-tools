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

import static java.util.Collections.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
implements PlaceManager {

    /** Activities that are currently open in the current perspective. */
    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<PlaceRequest, Activity>();

    /** Places that are currently open in the current perspective. */
    private final Map<PlaceRequest, PartDefinition> visibleWorkbenchParts = new HashMap<PlaceRequest, PartDefinition>();

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
    private ActivityManager activityManager;

    @Inject
    private PlaceHistoryHandler placeHistoryHandler;

    @Inject
    private Event<SelectPlaceEvent> selectWorkbenchPartEvent;

    @Inject
    private PanelManager panelManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    private final Map<String, SplashScreenActivity> activeSplashScreens = new HashMap<String, SplashScreenActivity>();
    private final Map<PlaceRequest, Activity> onMayCloseList = new HashMap<PlaceRequest, Activity>();

    private final OnLoadingPerspective loadingPerspective = new OnLoadingPerspective();

    @PostConstruct
    public void initPlaceHistoryHandler() {
        getPlaceHistoryHandler().register( this,
                                           produceEventBus(),
                                           DefaultPlaceRequest.NOWHERE );
    }

    PlaceHistoryHandler getPlaceHistoryHandler() {
        return placeHistoryHandler;
    }

    @Override
    public void goTo( final String identifier,
                      final PanelDefinition panel ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, panel );
    }

    @Override
    public void goTo( final String identifier ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, null );
    }

    @Override
    public void goTo( PlaceRequest place ) {
        goTo( place, null );
    }

    @Override
    public void goTo( final Path path,
                      final PanelDefinition panel ) {
        goTo( new PathPlaceRequest( path ), panel );
    }

    @Override
    public void goTo( final Path path ) {
        goTo( new PathPlaceRequest( path ), null );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest,
                      final PanelDefinition panel ) {
        goTo( getPlace( path, placeRequest ), panel );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest ) {
        goTo( getPlace( path, placeRequest ), null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final PanelDefinition panel ) {
        if ( place == null || place.equals( DefaultPlaceRequest.NOWHERE ) ) {
            return;
        }
        final ResolvedRequest resolved = resolveActivity( place );

        if ( resolved.getActivity() != null ) {
            final Activity activity = resolved.getActivity();
            if ( activity instanceof WorkbenchActivity ) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
                launchWorkbenchActivityAtPosition( resolved.getPlaceRequest(),
                                                   workbenchActivity,
                                                   workbenchActivity.getDefaultPosition(),
                                                   panel );
            } else if ( activity instanceof PopupActivity ) {
                launchPopupActivity( resolved.getPlaceRequest(),
                                     (PopupActivity) activity );
            } else if ( activity instanceof PerspectiveActivity ) {
                final Command launchActivity = new Command() {
                    @Override
                    public void execute() {
                        if ( closeAllCurrentPanels() ) {
                            launchPerspectiveActivity( resolved.getPlaceRequest(),
                                                       (PerspectiveActivity) activity );
                        }

                    }
                };
                final Command loadPerspective = new Command() {
                    @Override
                    public void execute() {
                        final PerspectiveDefinition activePerspective = getPanelManager().getPerspective();
                        if ( activePerspective != null && !activePerspective.isTransient() ) {
                            wbServices.save( activePerspective, new Command() {
                                @Override
                                public void execute() {
                                    launchActivity.execute();
                                }
                            } );
                        } else {
                            launchActivity.execute();
                        }
                    }
                };
                if ( loadingPerspective.isLoading() ) {
                    loadingPerspective.executeOnLoad( loadPerspective );
                } else {
                    loadPerspective.execute();
                }
            }
        } else {
            goTo( resolved.getPlaceRequest(), panel );
        }
    }

    private boolean closeAllCurrentPanels() {
        return closePlaces( new ArrayList<PlaceRequest>( visibleWorkbenchParts.keySet() ) );
    }

    private boolean closePlaces( final Collection<PlaceRequest> placeRequests ) {
        boolean result = true;
        for ( final PlaceRequest placeRequest : placeRequests ) {
            final Activity activity = existingWorkbenchActivities.get( placeRequest );
            if ( activity instanceof AbstractWorkbenchActivity ) {
                if ( ( (AbstractWorkbenchActivity) activity ).onMayClose() ) {
                    onMayCloseList.put( placeRequest, activity );
                } else {
                    result = false;
                    break;
                }
            }
        }

        if ( !result ) {
            onMayCloseList.clear();
        } else {
            for ( final PlaceRequest placeRequest : placeRequests ) {
                closePlace( placeRequest );
            }
        }

        return result;
    }

    /**
     * Resolves the given place request into an Activity instance, if one can be found. This method is responsible
     * for substituting special "not found" or "too many" place requests when the resolution doesn't work.
     * <p>
     * The behaviour of this method is affected by the boolean-valued
     * {@code org.uberfire.client.mvp.PlaceManagerImpl.ignoreUnkownPlaces} property in {@link UberFirePreferences}.
     * 
     * @param place
     *            A non-null place request that could have originated from within application code, from within the
     *            framework, or by parsing a hash fragment from a browser history event.
     * @return a non-null ResolvedRequest, where:
     * <ul>
     *  <li>the Activity value is either the unambiguous resolved Activity instance, or null if the activity was not resolvable;
     *  <li>if there is an Activity value, the PlaceRequest represents that Activity; otherwise it is a substitute PlaceRequest
     *      that should be navigated to recursively (ultimately by another call to this method). The PlaceRequest is never null.
     *      TODO (UF-94) : make this simpler. with enough tests in place, we should experiment with doing the recursive lookup automatically.
     * </ul>
     */
    private ResolvedRequest resolveActivity( final PlaceRequest place ) {

        final ResolvedRequest existingDestination = resolveExistingParts( place );

        if ( existingDestination != null ) {
            return existingDestination;
        }

        final Set<Activity> activities = activityManager.getActivities( place );

        if ( activities == null || activities.size() == 0 ) {
            boolean ignoreUnknown = (Boolean) UberFirePreferences.getProperty("org.uberfire.client.mvp.PlaceManagerImpl.ignoreUnkownPlaces", false);
            if ( ignoreUnknown ) {
                return new ResolvedRequest( null, PlaceRequest.NOWHERE );
            }

            System.out.println("Launching notfound activity for placeRequest " + place);
            final PlaceRequest notFoundPopup = new DefaultPlaceRequest( "workbench.activity.notfound" );
            notFoundPopup.addParameter( "requestedPlaceIdentifier", place.getIdentifier() );

            return new ResolvedRequest( null, notFoundPopup );
        } else if ( activities.size() > 1 ) {
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest( "workbench.activities.multiple" ).addParameter( "requestedPlaceIdentifier", null );

            return new ResolvedRequest( null, multiplePlaces );
        }

        return new ResolvedRequest( activities.iterator().next(), place );
    }

    private ResolvedRequest resolveExistingParts( final PlaceRequest place ) {
        final Activity activity = getActivity( place );

        if ( activity != null ) {
            return new ResolvedRequest( activity, place );
        }

        if ( place instanceof PathPlaceRequest ) {
            for ( final Map.Entry<PlaceRequest, PartDefinition> entry : visibleWorkbenchParts.entrySet() ) {
                if ( entry.getKey() instanceof PathPlaceRequest &&
                        ( (PathPlaceRequest) entry.getKey() ).getPath().compareTo( ( (PathPlaceRequest) place ).getPath() ) == 0 ) {
                    return new ResolvedRequest( getActivity( entry.getKey() ), entry.getKey() );
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
        final ResolvedRequest resolved = resolveActivity( place );

        if ( resolved.getActivity() != null ) {
            final Activity activity = resolved.getActivity();

            if ( activity instanceof WorkbenchActivity ) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
                launchWorkbenchActivityInPanel( place,
                                                workbenchActivity,
                                                part,
                                                panel );
            } else {
                throw new IllegalArgumentException( "placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel." );
            }
        } else {
            goTo( resolved.getPlaceRequest() );
        }
    }

    private PlaceRequest getPlace( final Path path,
                                   final PlaceRequest placeRequest ) {
        final PlaceRequest request = new PathPlaceRequest( path );

        for ( final Map.Entry<String, String> entry : placeRequest.getParameters().entrySet() ) {
            request.addParameter( entry.getKey(), entry.getValue() );
        }

        return request;
    }

    /**
     * Finds the <i>currently open</i> activity that handles the given PlaceRequest by ID. No attempt is made to match
     * by path, but see {@link #resolveExistingParts(PlaceRequest)} for a variant that does.
     * 
     * @param place
     *            the PlaceRequest whose activity to search for
     * @return the activity that currently exists in service of the given PlaceRequest's ID. Null if no current activity
     *         handles the given PlaceRequest.
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
        closePlace( placeToClose, false );
    }

    @Override
    public void tryClosePlace( final PlaceRequest placeToClose,
                               final Command onAfterClose ) {
        boolean execute = false;
        if ( placeToClose == null ) {
            execute = true;
        } else {
            execute = closePlaces( Arrays.asList( placeToClose ) );
        }

        if ( execute ) {
            onAfterClose.execute();
        }
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
        closePlace( placeToClose, true );
    }

    @Override
    public void closeAllPlaces() {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>( visibleWorkbenchParts.keySet() );
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

    /**
     * Returns all the PlaceRequests that map to activies that are currently in the open state and accessible
     * somewhere in the current perspective.
     * 
     * @return an unmodifiable view of the current active place requests. This view may or may not update after
     * further calls into PlaceManager that modify the workbench state. It's best not to hold on to the returned
     * set; instead, call this method again for current information.
     */
    public Collection<PlaceRequest> getActivePlaceRequests() {
        return unmodifiableCollection( existingWorkbenchActivities.keySet() );
    }

    /**
     * Returns all the PathPlaceRequests that map to activies that are currently in the open state and accessible
     * somewhere in the current perspective.
     * 
     * @return an unmodifiable view of the current active place requests. This view may or may not update after
     * further calls into PlaceManager that modify the workbench state. It's best not to hold on to the returned
     * set; instead, call this method again for current information.
     */
    public Collection<PathPlaceRequest> getActivePlaceRequestsWithPath() {
        ArrayList<PathPlaceRequest> pprs = new ArrayList<PathPlaceRequest>();
        for ( final PlaceRequest placeRequest : existingWorkbenchActivities.keySet() ) {
            if ( placeRequest instanceof PathPlaceRequest ) {
                pprs.add( (PathPlaceRequest) placeRequest );
            }
        }
        return pprs;
    }

    private void launchWorkbenchActivityAtPosition( final PlaceRequest place,
                                                    final WorkbenchActivity activity,
                                                    final Position position,
                                                    final PanelDefinition _panel ) {

        if ( visibleWorkbenchParts.containsKey( place ) ) {
            getSelectWorkbenchPartEvent().fire( new SelectPlaceEvent( place ) );
            return;
        }

        final PartDefinition part = new PartDefinitionImpl( place );
        final PanelDefinition panel;
        if ( _panel != null ) {
            panel = _panel;
        } else {
            // TODO (hbraun): If no panel given (i.e. when using token driven nav), this falls back to the root panel definition
            panel = addWorkbenchPanelTo( position );
        }

        launchWorkbenchActivityInPanel( place, activity, part, panel );
    }

    Event<SelectPlaceEvent> getSelectWorkbenchPartEvent() {
        return selectWorkbenchPartEvent;
    }

    PanelDefinition addWorkbenchPanelTo( Position position ) {
        return getPanelManager().addWorkbenchPanel( getPanelManager().getRoot(),
                                                    position );
    }

    private void launchWorkbenchActivityInPanel( final PlaceRequest place,
                                                 final WorkbenchActivity activity,
                                                 final PartDefinition part,
                                                 final PanelDefinition panel ) {

        existingWorkbenchActivities.put( place, activity );
        visibleWorkbenchParts.put( place, part );
        updateHistory( place );
        checkPathDelete( place );

        final SplashScreenActivity splashScreen = getSplashScreenInterceptor( place );

        UIPart uiPart = new UIPart( activity.getTitle(), activity.getTitleDecoration(), activity.getWidget() );

        getPanelManager().addWorkbenchPart( place,
                                            part,
                                            panel,
                                            activity.getMenus(),
                                            uiPart,
                                            activity.contextId() );
        if ( splashScreen != null ) {
            activeSplashScreens.put( place.getIdentifier(), splashScreen );
            fireNewSplashScreenActiveEvent();
            splashScreen.onOpen();
        }

        activity.onOpen();
    }

    PanelManager getPanelManager() {
        return panelManager;
    }

    private void checkPathDelete( final PlaceRequest place ) {
        if ( place == null ) {
            return;
        }
        try {
            if ( (Boolean) UberFirePreferences.getProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", true ) &&
                    place instanceof PathPlaceRequest ) {
                ( (PathPlaceRequest) place ).getPath().onDelete( new Command() {
                    @Override
                    public void execute() {
                        forceClosePlace( place );
                    }
                } );
            }
        } catch ( final Exception ex ) {
        }
    }

    private void launchPopupActivity( final PlaceRequest place,
                                      final PopupActivity activity ) {
        //Record new place\part\activity
        existingWorkbenchActivities.put( place, activity );
        updateHistory( place );
        checkPathDelete( place );

        activity.onOpen();
    }

    private void launchPerspectiveActivity( final PlaceRequest place,
                                            final PerspectiveActivity activity ) {
        loadingPerspective.startLoading();
        activeSplashScreens.clear();
        firePerspectiveChangeEvent( activity );
        final SplashScreenActivity splashScreen = getSplashScreenInterceptor( place );
        if ( splashScreen != null ) {
            activeSplashScreens.put( place.getIdentifier(), splashScreen );
            splashScreen.onOpen();
        }
        fireNewSplashScreenActiveEvent();
        loadingPerspective.endLoading();
        activity.onOpen();
    }

    /**
     * Gets the splash screen for the given place from the ActivityManager.
     * TODO: inline this method once the UnitTestWrapper thing is gone.
     */
    SplashScreenActivity getSplashScreenInterceptor( PlaceRequest place ) {
        return activityManager.getSplashScreenInterceptor( place );
    }

    void fireNewSplashScreenActiveEvent() {
        newSplashScreenActiveEvent.fire( new NewSplashScreenActiveEvent() );
    }

    void firePerspectiveChangeEvent( PerspectiveActivity activity ) {
        perspectiveChangeEvent.fire( new PerspectiveChange( activity.getPerspective(), activity.getMenus(), activity.getIdentifier() ) );
    }

    public void updateHistory( PlaceRequest request ) {
        getPlaceHistoryHandler().onPlaceChange( request );
    }

    private void closePlace( final PlaceRequest place, final boolean force ) {

        final Activity activity = existingWorkbenchActivities.get( place );
        if ( activity == null ) {
            return;
        }

        workbenchPartBeforeCloseEvent.fire(new BeforeClosePlaceEvent( place, force, true ));

        activeSplashScreens.remove( place.getIdentifier() );

        if ( activity instanceof WorkbenchActivity ) {
            WorkbenchActivity activity1 = (WorkbenchActivity) activity;
            if ( force || onMayCloseList.containsKey( place ) || activity1.onMayClose() ) {
                onMayCloseList.remove( place );
                activity1.onClose();
            } else {
                return;
            }
        } else if ( activity instanceof PopupActivity ) {
            PopupActivity activity1 = (PopupActivity) activity;
            if ( force || activity1.onMayClose() ) {
                activity1.onClose();
            } else {
                return;
            }
        }

        workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        existingWorkbenchActivities.remove( place );
        visibleWorkbenchParts.remove( place );

        activityManager.destroyActivity( activity );

        if ( place instanceof PathPlaceRequest ) {
            ( (PathPlaceRequest) place ).getPath().dispose();
        }
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

    @Produces
    @ApplicationScoped
    EventBus produceEventBus() {
        if ( tempBus == null ) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

    private class OnLoadingPerspective {

        boolean loading = false;
        private Command command;

        public void executeOnLoad( final Command command ) {
            this.command = command;
        }

        public void startLoading() {
            this.loading = true;
        }

        public void endLoading() {
            this.loading = false;
            if ( command != null ) {
                command.execute();
                command = null;
            }
        }

        public boolean isLoading() {
            return loading;
        }
    }

    /**
     * The result of an attempt to resolve a PlaceRequest to an Activity.
     */
    private static class ResolvedRequest {
        private final Activity activity;
        private final PlaceRequest placeRequest;

        public ResolvedRequest(final Activity resolvedActivity, final PlaceRequest substitutePlace ) {
            this.activity = resolvedActivity;
            this.placeRequest = substitutePlace;
        }

        public Activity getActivity() {
            return activity;
        }

        public PlaceRequest getPlaceRequest() {
            return placeRequest;
        }

        @Override
        public boolean equals( final Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            final ResolvedRequest resolvedRequest = (ResolvedRequest) o;

            if ( activity != null ? !activity.equals( resolvedRequest.activity ) : resolvedRequest.activity != null ) {
                return false;
            }
            if ( placeRequest != null ? !placeRequest.equals( resolvedRequest.placeRequest ) : resolvedRequest.placeRequest != null ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = activity != null ? activity.hashCode() : 0;
            result = 31 * result + ( placeRequest != null ? placeRequest.hashCode() : 0 );
            return result;
        }

        @Override
        public String toString() {
            return "{activity=" + activity + ", placeRequest=" + placeRequest + "}";
        }
    }
}

package org.drools.guvnor.client.mvp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<IPlaceRequest, Activity>      activeActivities       = new HashMap<IPlaceRequest, Activity>();
    private final Map<IPlaceRequest, WorkbenchPart> existingWorkbenchParts = new HashMap<IPlaceRequest, WorkbenchPart>();

    @Inject
    private ActivityMapper                          activityMapper;

    @Inject
    private PlaceRequestHistoryMapper               historyMapper;

    @Inject
    private EventBus                                eventBus;

    @Inject
    private PanelManager                            panelManager;

    @Inject
    private Event<WorkbenchPartCloseEvent>          workbenchPartCloseEvent;

    @Inject
    private Event<SelectWorkbenchPartEvent>         selectWorkbenchPartEvent;

    private PlaceHistoryHandler                     placeHistoryHandler;

    IPlaceRequest                                   currentPlaceRequest;

    @PostConstruct
    public void init() {
        placeHistoryHandler = new PlaceHistoryHandler( historyMapper );
        placeHistoryHandler.register( this,
                                      eventBus,
                                      new PlaceRequest( "NOWHERE" ) );
    }

    @Override
    public void goTo(IPlaceRequest placeRequest) {
        currentPlaceRequest = placeRequest;
        revealPlace( placeRequest );
    }

    @Override
    public IPlaceRequest getCurrentPlaceRequest() {
        if ( currentPlaceRequest != null ) {
            return currentPlaceRequest;
        } else {
            return new PlaceRequest( "NOWHERE" );
        }
    }

    private void revealPlace(final IPlaceRequest newPlace) {

        //If we're already showing this place exit.
        if ( activeActivities.containsKey( newPlace ) ) {
            final WorkbenchPart part = existingWorkbenchParts.get( newPlace );
            selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( part ) );
            return;
        }

        final Activity activity = activityMapper.getActivity( newPlace );
        activeActivities.put( newPlace,
                              activity );

        activity.onRevealPlace(
                new AcceptItem() {
                    public void add(String tabTitle,
                                    IsWidget widget) {

                        WorkbenchPart part = new WorkbenchPart( widget.asWidget(),
                                                                tabTitle );
                        existingWorkbenchParts.put( newPlace,
                                                    part );

                        panelManager.addWorkbenchPanel( part,
                                                        activity.getDefaultPosition() );
                    }
                } );
        updateHistory( newPlace );
    }

    public void updateHistory(IPlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosed(@Observes WorkbenchPartBeforeCloseEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        if ( activity.mayClosePlace() ) {
            activity.onClosePlace();
            activeActivities.remove( place );
            existingWorkbenchParts.remove( place );
            workbenchPartCloseEvent.fire( new WorkbenchPartCloseEvent( part ) );
        }
    }

    private IPlaceRequest getPlaceForWorkbenchPart(final WorkbenchPart part) {
        for ( Map.Entry<IPlaceRequest, WorkbenchPart> e : existingWorkbenchParts.entrySet() ) {
            if ( e.getValue().equals( part ) ) {
                return e.getKey();
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        activity.onFocus();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        final WorkbenchPart part = event.getDeselectedWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        activity.onLostFocus();
    }

    @Produces
    @ApplicationScoped
    EventBus makeEventBus() {
        return new SimpleEventBus();
    }

}

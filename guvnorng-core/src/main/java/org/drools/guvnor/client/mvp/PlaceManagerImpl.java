package org.drools.guvnor.client.mvp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseHandler;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideHandler;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.ResettableEventBus;
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
        //TODO What should we do if the Activity is hidden? Hidden Activities do not appear to
        //be handled in PlaceManagerImpl. Are hidden Activities still stored here, or do we need
        //some other mechanism?!?!
        if ( activeActivities.containsKey( newPlace ) ) {
            return;
        }

        final Activity activity = activityMapper.getActivity( newPlace );
        activeActivities.put( newPlace,
                              activity );

        activity.revealPlace(
                new AcceptItem() {
                    public void add(String tabTitle,
                                    IsWidget widget) {

                        WorkbenchPart workbenchPart = new WorkbenchPart( widget.asWidget(),
                                                                         tabTitle );

                        existingWorkbenchParts.put( newPlace,
                                                    workbenchPart );

                        workbenchPart.addActivityCloseHandler(
                                new ActivityCloseHandler() {
                                    @Override
                                    public void onCloseActivity(ActivityCloseEvent event) {
                                        onClosePlace( new ClosePlaceEvent( newPlace ) );
                                    }
                                } );

                        workbenchPart.addWorkbenchPartHideHandler( new WorkbenchPartHideHandler() {
                            @Override
                            public void onHide(WorkbenchPartHideEvent event) {
                                activity.hide();
                            }
                        } );

                        workbenchPart.addSelectionHandler( new SelectionHandler<WorkbenchPart>() {
                            @Override
                            public void onSelection(SelectionEvent<WorkbenchPart> workbenchPartSelectionEvent) {
                                activity.show();
                            }
                        } );

                        PanelManager.getInstance().addWorkbenchPanel( workbenchPart,
                                                                      activity.getPreferredPosition() );
                    }
                } );
        updateHistory( newPlace );
    }

    public void updateHistory(IPlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    public void onClosePlace(@Observes ClosePlaceEvent closePlaceEvent) {
        final IPlaceRequest place = closePlaceEvent.getPlaceRequest();
        final Activity activity = activeActivities.get( place );
        if ( activity.mayClosePlace() ) {
            activity.closePlace();
            activeActivities.remove( place );
            existingWorkbenchParts.remove( place ).close();
        }
    }

    @Produces
    @ApplicationScoped
    EventBus makeEventBus() {
        return new ResettableEventBus( new SimpleEventBus() );
    }

}

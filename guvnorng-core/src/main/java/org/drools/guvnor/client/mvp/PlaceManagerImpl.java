package org.drools.guvnor.client.mvp;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseHandler;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideHandler;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<PlaceRequest, Activity>            activeActivities       = new HashMap<PlaceRequest, Activity>();
    private final Map<PlaceRequest, WorkbenchPart>       existingWorkbenchParts = new HashMap<PlaceRequest, WorkbenchPart>();

    @Inject
    private ActivityMapper                               activityMapper;

    @Inject
    private PlaceRequestHistoryMapper                    historyMapper;

    @Inject
    private com.google.web.bindery.event.shared.EventBus eventBus;

    private PlaceHistoryHandler                          placeHistoryHandler;

    PlaceRequest                                         currentPlaceRequest;

    @PostConstruct
    public void init() {
        placeHistoryHandler = new PlaceHistoryHandler( historyMapper );
        placeHistoryHandler.register( this,
                                      eventBus,
                                      new PlaceRequest( "NOWHERE" ) );
    }

    @Override
    public void goTo(PlaceRequest placeRequest) {
        currentPlaceRequest = placeRequest;
        revealPlace( placeRequest );
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if ( currentPlaceRequest != null ) {
            return currentPlaceRequest;
        } else {
            return new PlaceRequest( "NOWHERE" );
        }
    }

    private void revealPlace(final PlaceRequest newPlace) {
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

    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    public void onClosePlace(@Observes ClosePlaceEvent closePlaceEvent) {
        final Activity activity = activeActivities.get( closePlaceEvent.getPlaceRequest() );
        if ( activity.mayClosePlace() ) {
            activity.closePlace();
            activeActivities.remove( activity );
            existingWorkbenchParts.remove( closePlaceEvent.getPlaceRequest() ).close();
        }
    }

}

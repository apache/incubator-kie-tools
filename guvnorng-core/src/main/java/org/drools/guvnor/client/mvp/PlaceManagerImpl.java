package org.drools.guvnor.client.mvp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.BeanFactory;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartClosedEvent;
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
    private BeanFactory                             factory;

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

                        WorkbenchPart part = factory.newWorkbenchPart( widget.asWidget(),
                                                                       tabTitle );
                        existingWorkbenchParts.put( newPlace,
                                                    part );

                        panelManager.addWorkbenchPanel( part,
                                                        activity.getPreferredPosition() );
                    }
                } );
        updateHistory( newPlace );
    }

    public void updateHistory(IPlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    public void onWorkbenchPartClosed(@Observes WorkbenchPartClosedEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        if ( activity.mayClosePlace() ) {
            activity.closePlace();
            activeActivities.remove( place );
            existingWorkbenchParts.remove( place );
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

    public void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        activity.show();
    }

    public void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        final WorkbenchPart part = event.getDeselectedWorkbenchPart();
        final IPlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final Activity activity = activeActivities.get( place );
        activity.hide();
    }

    @Produces
    @ApplicationScoped
    EventBus makeEventBus() {
        return new SimpleEventBus();
    }

}

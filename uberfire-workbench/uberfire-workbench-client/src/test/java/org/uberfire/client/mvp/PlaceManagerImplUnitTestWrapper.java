package org.uberfire.client.mvp;

import javax.enterprise.event.Event;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.PerspectiveChange;
import org.uberfire.workbench.events.SelectPlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public class PlaceManagerImplUnitTestWrapper extends PlaceManagerImpl {

    private Event<SelectPlaceEvent> selectWorkbenchPartEvent;
    private PlaceHistoryHandler placeHistoryHandler;
    private Activity activity;
    private PanelManager panelManagerFake;
    private SplashScreenActivity splashScreenActivity;

    public PlaceManagerImplUnitTestWrapper( Activity activity,
                                            PanelManager panelManagerFake,
                                            Event<SelectPlaceEvent> selectWorkbenchPartEvent ) {
        this.activity = activity;
        this.panelManagerFake = panelManagerFake;
        this.selectWorkbenchPartEvent = selectWorkbenchPartEvent;
    }

    public PlaceManagerImplUnitTestWrapper( Activity activity,
                                                PanelManager panelManagerFake ) {
        this.activity = activity;
        this.panelManagerFake = panelManagerFake;
    }

    public PlaceManagerImplUnitTestWrapper( Activity activity,
                                            PanelManager panelManagerFake, SplashScreenActivity splashScreenActivity ) {
        this.activity = activity;
        this.panelManagerFake = panelManagerFake;
        this.splashScreenActivity = splashScreenActivity;
    }

    public PlaceManagerImplUnitTestWrapper( PlaceHistoryHandler placeHistoryHandler ) {
        this.placeHistoryHandler = placeHistoryHandler;
    }

    PanelManager getPanelManager() {
        return panelManagerFake;
    }

    PlaceHistoryHandler getPlaceHistoryHandler() {
        return placeHistoryHandler;
    }

    @Override
    public Activity getActivity( PlaceRequest place ) {
        return activity;
    }

    PanelDefinition addWorkbenchPanelTo( Position position ) {
        return null;
    }

    public void updateHistory( PlaceRequest request ) {

    }

    SplashScreenActivity getSplashScreenInterceptor( PlaceRequest place ) {
        return splashScreenActivity;
    }

    Event<SelectPlaceEvent> getSelectWorkbenchPartEvent() {
        return selectWorkbenchPartEvent;
    }


    void fireNewSplashScreenActiveEvent() {
    }

    void firePerspectiveChangeEvent( PerspectiveActivity activity ) {
    }



}

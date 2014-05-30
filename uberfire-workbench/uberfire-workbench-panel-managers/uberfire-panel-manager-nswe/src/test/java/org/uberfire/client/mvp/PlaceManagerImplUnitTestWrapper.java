package org.uberfire.client.mvp;

import javax.enterprise.event.Event;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.PlaceRequest;

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

    @Override
    PanelManager getPanelManager() {
        return panelManagerFake;
    }

    @Override
    PlaceHistoryHandler getPlaceHistoryHandler() {
        return placeHistoryHandler;
    }

    @Override
    public Activity getActivity( PlaceRequest place ) {
        return activity;
    }

    @Override
    public void updateHistory( PlaceRequest request ) {

    }

    @Override
    SplashScreenActivity getSplashScreenInterceptor( PlaceRequest place ) {
        return splashScreenActivity;
    }

    @Override
    Event<SelectPlaceEvent> getSelectWorkbenchPartEvent() {
        return selectWorkbenchPartEvent;
    }


    @Override
    void fireNewSplashScreenActiveEvent() {
    }

    @Override
    void firePerspectiveChangeEvent( PerspectiveActivity activity ) {
    }



}

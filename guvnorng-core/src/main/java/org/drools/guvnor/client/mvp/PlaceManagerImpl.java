package org.drools.guvnor.client.mvp;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.workbench.WorkbenchPart;
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

    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject
    private ActivityMapper activityMapper;

    @Inject
    private PlaceRequestHistoryMapper historyMapper;

    @Inject
    private com.google.web.bindery.event.shared.EventBus eventBus;

    private PlaceHistoryHandler placeHistoryHandler;

    PlaceRequest currentPlaceRequest;

    @PostConstruct
    public void init() {
        //PlaceRequestHistoryMapper historyMapper = new GuvnorNGPlaceRequestHistoryMapper();
        placeHistoryHandler = new PlaceHistoryHandler(historyMapper);
        placeHistoryHandler.register(this,
                eventBus,
                new PlaceRequest("NOWHERE"));
    }

    @Override
    public void goTo(PlaceRequest placeRequest) {
        currentPlaceRequest = placeRequest;
        revealPlace(placeRequest);
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if (currentPlaceRequest != null) {
            return currentPlaceRequest;
        } else {
            return new PlaceRequest("NOWHERE");
        }
    }

    /*
     * private void showExistingActivity(final PlaceRequest token) { //TODO: the
     * requested view might have be dragged to other containers. How do we know
     * where it is now? // preferredContainer.show(token); } private boolean
     * isActivityAlreadyActive(final PlaceRequest token) { return
     * activeActivities.keySet().contains( token ); } private void
     * startNewActivity(final PlaceRequest newPlace) { final Activity activity =
     * activityMapper.getActivity( newPlace ); activeActivities.put(newPlace,
     * activity); activity.start( new AcceptItem() { public void add(String
     * tabTitle, IsWidget widget) {
     * PanelManager.getInstance().addWorkbenchPanel( new WorkbenchPart(
     * widget.asWidget(), tabTitle), activity.getPreferredPosition()); } } );
     * updateHistory( newPlace ); }
     */

    private void revealPlace(final PlaceRequest newPlace) {
        final Activity activity = activityMapper.getActivity(newPlace);

        activity.revealPlace(
                new AcceptItem() {
                    public void add(String tabTitle,
                                    IsWidget widget) {

                        WorkbenchPart workbenchPart = new WorkbenchPart(widget.asWidget(),
                                tabTitle);

                        workbenchPart.addCloseHandler(new CloseHandler<WorkbenchPart>() {
                            @Override
                            public void onClose(CloseEvent<WorkbenchPart> workbenchPartCloseEvent) {
                                if (activity.mayClosePlace()) {
                                    activity.closePlace();
                                    PanelManager.getInstance().removeWorkbenchPart(workbenchPartCloseEvent.getTarget());
                                }
                            }

                        });

                        PanelManager.getInstance().addWorkbenchPanel(workbenchPart,
                                activity.getPreferredPosition());
                    }
                });
        updateHistory(newPlace);
    }

    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange(request);
    }

    public void onClosePlace(@Observes ClosePlaceEvent closePlaceEvent) {
        final Activity activity = activeActivities.get(closePlaceEvent.getPlaceRequest());
        if (activity.mayClosePlace()) {
            activity.closePlace();
        }
    }

}

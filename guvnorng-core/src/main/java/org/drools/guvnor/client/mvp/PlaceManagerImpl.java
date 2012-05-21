package org.drools.guvnor.client.mvp;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.IsWidget;


@ApplicationScoped
public class PlaceManagerImpl implements PlaceManager {

    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject private GuvnorNGActivityMapperImpl activityMapper;
    @Inject private com.google.web.bindery.event.shared.EventBus eventBus;
    private PlaceHistoryHandler placeHistoryHandler;

    ExplorerViewCenterPanel preferredContainer;
    PlaceRequest currentPlaceRequest;
    
    
    @PostConstruct
    public void init() {        
        PlaceRequestHistoryMapper historyMapper = new GuvnorNGPlaceRequestHistoryMapper();
        placeHistoryHandler = new PlaceHistoryHandler(historyMapper);
        placeHistoryHandler.register(this, eventBus, new PlaceRequest("NOWHERE"));
    }


    public void setTabbedPanel(ExplorerViewCenterPanel tabbedPanel) {
        if (this.preferredContainer == null) {
            this.preferredContainer = tabbedPanel;
        }
    }
    
    @Override
    public void goTo(PlaceRequest placeRequest) {
        currentPlaceRequest = placeRequest;
        
        if (isActivityAlreadyActive(placeRequest)) {
            showExistingActivity(placeRequest);
        } else {
            startNewActivity(placeRequest);
        }
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
      if (currentPlaceRequest != null) {
        return currentPlaceRequest;
      } else {
        return new PlaceRequest("NOWHERE");
      }
    }
    
    private void showExistingActivity(final PlaceRequest token) {
        //TODO: the requested view might have be dragged to other containers. How do we know where it is now?
        preferredContainer.show(token);
    }

    private boolean isActivityAlreadyActive(final PlaceRequest token) {
        return activeActivities.keySet().contains(token);
    }

    private void startNewActivity(final PlaceRequest newPlace) {
        final Activity activity = activityMapper.getActivity(newPlace);
        
        //TODO: find the appropriate container to display this view based on current layout and PlaceRequest's preferred location (E/S/W/N/C)
        //Panel preferredContainer = LayoutManager.getPreferredContainer(newPlace);
        //final ExplorerViewCenterPanel preferredContainer = new ExplorerViewCenterPanel();

        activeActivities.put(newPlace, activity);

        activity.start(
                new AcceptItem() {
                    public void add(String tabTitle, IsWidget widget) {
                        preferredContainer.addTab(
                                tabTitle,
                                widget,
                                newPlace);
                    }
                });
        updateHistory(newPlace);
    }
 
    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange(request);
    }
    
    public void onClosePlace(@Observes ClosePlaceEvent closePlaceEvent) {
        final Activity activity = activeActivities.get(closePlaceEvent.getPlaceRequest());
        if (activity.mayStop()) {
            activity.onStop();
            activeActivities.remove(closePlaceEvent.getPlaceRequest());
            preferredContainer.close(closePlaceEvent.getPlaceRequest());
        }
    }
    
}

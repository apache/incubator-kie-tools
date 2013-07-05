/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.uberfire.client.screens.stackablebar;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.screens.stackablebar.events.CloseBarEvent;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;

/**
 *
 * @author salaboy
 */
public class StackBarContainerImpl extends Composite implements StackBarContainer {
    @Inject
    private Event<ClosePlaceEvent> closeEvent;
    @Inject
    private ActivityManager activityManager;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<CloseBarEvent> closeBarEvent;
    
    private static Long idGenerator = 1L;
    
    private Map<Long, StackBarWidget> bars;
    
    private FlowPanel container = new FlowPanel();
    
    private int maxBars = 1;


    public StackBarContainerImpl() {
        initWidget(container);
        container.setStyleName("container-fluid");
        bars = new HashMap<Long, StackBarWidget>(maxBars);
    }

    @Override
    public Long addNewBar(String place, int maxPlaces, boolean alwaysOpened) {
        Long id = null;
        if (bars.size() >= maxBars) {
            notification.fire(new NotificationEvent("The bar container is full, please delete the unused bars to add new ones!"));
        } else {
            id = idGenerator++;
            StackBarWidgetImpl stackBarWidget = new StackBarWidgetImpl(id, place, maxPlaces, alwaysOpened);
            stackBarWidget.setActivityManager(activityManager);
            stackBarWidget.setCloseEvent(closeEvent);
            stackBarWidget.setNotification(notification);
            stackBarWidget.setCloseBarEvent(closeBarEvent);
            stackBarWidget.refresh();
            bars.put(id, stackBarWidget);
        }

        return id;
    }
    
    @Override
    public Long addNewBar(String place) {
        return addNewBar(place, 1, false);
    }

    @Override
    public void addPlaceToBar(Long id, String place) {
        if (id == null || place == null || place.equals("")) {
            notification.fire(new NotificationEvent("The bar ID and the Place must be provided"));
            return;
        }

        StackBarWidget widget = bars.get(id);
        if (widget == null) {
            notification.fire(new NotificationEvent("The is no Bar with the provided ID ( " + id + " )"));
            return;
        }
        widget.addPlace(place);
        widget.refresh();
    }

    @Override
    public Map<Long, StackBarWidget> getBars() {
        return bars;
    }

    @Override
    public void refresh() {
        if(bars.isEmpty()){
            container.add(new Label("There are no Bars in this container yet."));
            return;
        }else{
            container.clear();
        }
        for (Long key : bars.keySet()) {
            FlowPanel panel = new FlowPanel();
            panel.setStyleName("container-fluid");
            panel.add((IsWidget) bars.get(key));
            container.add(panel);
        }

    }
    
    public void onBarCloseEvent(@Observes CloseBarEvent event){
        bars.remove(event.getId());
        refresh();
    }

    @Override
    public void clear() {
        bars.clear();
        container.clear();
    }


    @Override
    public void setMaxBars(int maxBars) {
        this.maxBars = maxBars;
    }

    @Override
    public int getMaxBars() {
        return this.maxBars;
    }
}

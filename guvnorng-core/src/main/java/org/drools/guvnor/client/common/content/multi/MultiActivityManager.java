/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.common.content.multi;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.PlaceChangeEvent;

import static com.google.gwt.place.shared.Place.*;

@Dependent
public class MultiActivityManager implements PlaceChangeEvent.Handler {

    private MultiContentPanel tabbedPanel;
    private final Map<ContentPlace, Activity> activeActivities = new HashMap<ContentPlace, Activity>();

    @Inject private ContentActivityMapper activityMapper;
    @Inject private com.google.web.bindery.event.shared.EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.addHandler(PlaceChangeEvent.TYPE, this);
    }

    public void setTabbedPanel(MultiContentPanel tabbedPanel) {
        if (this.tabbedPanel == null) {
            this.tabbedPanel = tabbedPanel;
        }
    }

    public void onPlaceChange(PlaceChangeEvent event) {

        if (tabbedPanel == null) {
            return;
        }

        if (!(event.getNewPlace() instanceof ContentPlace)) {
            return;
        }

        final ContentPlace place = (ContentPlace) event.getNewPlace();

        if (isActivityAlreadyActive(place)) {
            showExistingActivity(place);
        } else if (ifPlaceExists(event)) {
            startNewActivity(place);
        }
    }

    private void showExistingActivity(final ContentPlace token) {
        tabbedPanel.show(token);
    }

    private boolean isActivityAlreadyActive(final ContentPlace token) {
        return activeActivities.keySet().contains(token);
    }

    private void startNewActivity(final ContentPlace newPlace) {
        final Activity activity = activityMapper.getActivity(newPlace);

        final ResettableEventBus resettableEventBus = new ResettableEventBus(eventBus);

        activeActivities.put(newPlace, activity);

        activity.start(tabbedPanel.addTab(newPlace), resettableEventBus);
    }

    private boolean ifPlaceExists(PlaceChangeEvent event) {
        return !event.getNewPlace().equals(NOWHERE);
    }

    public void onClosePlace(@Observes ClosePlaceEvent closePlaceEvent) {
        final Activity activity = activeActivities.get(closePlaceEvent.getPlace());
        if (activity.mayStop() == null) {
            activity.onStop();
            activeActivities.remove(closePlaceEvent.getPlace());
            tabbedPanel.close(closePlaceEvent.getPlace());
        }
    }
}

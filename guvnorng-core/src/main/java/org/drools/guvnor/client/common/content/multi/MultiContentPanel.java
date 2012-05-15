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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.ScrollTabLayoutPanel;

/**
 * This is the tab panel manager.
 */
@Dependent
public class MultiContentPanel extends Composite {

    private ScrollTabLayoutPanel tabLayoutPanel;

    @Inject private PlaceController placeController;
    @Inject private Event<ClosePlaceEvent> event;

    private PanelMap openedTabs = new PanelMap();

    @PostConstruct
    public void init() {
        tabLayoutPanel = new ScrollTabLayoutPanel(2, Unit.EM);
        initWidget(tabLayoutPanel);
    }

    public boolean contains(ContentPlace key) {
        return openedTabs.contains(key);
    }

    public void show(final ContentPlace key) {
        if (openedTabs.contains(key)) {
            LoadingPopup.close();
            tabLayoutPanel.selectTab(openedTabs.get(key));
        }
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid
     * dupes being opened.
     * @param place A place which is unique.
     */
    public AcceptsOneWidget addTab(final ContentPlace place) {

        final ScrollPanel localTP = new ScrollPanel();
        tabLayoutPanel.add(localTP, newClosableLabel(place));
        tabLayoutPanel.selectTab(localTP);

        openedTabs.put(place, localTP);

        return localTP;
    }

    private Widget newClosableLabel(final ContentPlace place) {
        final ClosableLabel closableLabel = new ClosableLabel(place.getTabName());

        closableLabel.addCloseHandler(new CloseHandler<ClosableLabel>() {
            public void onClose(CloseEvent<ClosableLabel> xevent) {
                event.fire(new ClosePlaceEvent(place));
            }
        });

        return closableLabel;
    }

    public void close(final ContentPlace key) {
        int widgetIndex = openedTabs.getIndex(key);

        final ContentPlace nextPlace = getPlace(widgetIndex);

        tabLayoutPanel.remove(openedTabs.get(key));
        openedTabs.remove(key);

        if (nextPlace != null) {
            goTo(nextPlace);
        } else {
            goTo(Place.NOWHERE);
        }
    }

    private ContentPlace getPlace(int widgetIndex) {
        if (isOnlyOneTabLeft()) {
            return null;
        } else if (isSelectedTabIndex(widgetIndex)) {
            return getNeighbour(widgetIndex);
        } else {
            return null;
        }
    }

    private void goTo(final Place place) {
        placeController.goTo(place);
    }

    private ContentPlace getNeighbour(int widgetIndex) {
        if (isLeftMost(widgetIndex)) {
            return getNextPlace();
        } else {
            return getPreviousPlace();
        }
    }

    private boolean isLeftMost(int widgetIndex) {
        return widgetIndex == 0;
    }

    private boolean isSelectedTabIndex(int widgetIndex) {
        return tabLayoutPanel.getSelectedIndex() == widgetIndex;
    }

    private ContentPlace getPreviousPlace() {
        if (tabLayoutPanel.getSelectedIndex() > 0) {
            return openedTabs.getKey(tabLayoutPanel.getSelectedIndex() - 1);
        }
        return null;
    }

    private ContentPlace getNextPlace() {
        return openedTabs.getKey(tabLayoutPanel.getSelectedIndex() + 1);
    }

    private boolean isOnlyOneTabLeft() {
        return tabLayoutPanel.getWidgetCount() == 1;
    }

    class PanelMap {

        private final Map<ContentPlace, Panel> keysToPanel = new HashMap<ContentPlace, Panel>();
        private final List<ContentPlace> keys = new ArrayList<ContentPlace>();

        Panel get(ContentPlace key) {
            return keysToPanel.get(key);
        }

        ContentPlace getKey(int index) {
            return keys.get(index);
        }

        void remove(final ContentPlace key) {
            keys.remove(key);
            keysToPanel.remove(key);
        }

        public boolean contains(final ContentPlace key) {
            return keysToPanel.containsKey(key);
        }

        public void put(final ContentPlace key, final Panel panel) {
            keys.add(key);
            keysToPanel.put(key, panel);
        }

        public int getIndex(final ContentPlace key) {
            return keys.indexOf(key);
        }
    }
}

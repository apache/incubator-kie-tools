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

package org.drools.guvnor.client;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
/*import org.drools.guvnor.client.perspective.workspace.WorkspacePerspectivePlace;
import org.drools.guvnor.client.perspective.workspace.WorskpaceActivityMapper;
import org.drools.guvnor.client.place.PlaceBuilderUtil;*/
/*import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.RoundedCornersResource;*/
import org.jboss.errai.ioc.client.api.EntryPoint;

@EntryPoint
public class HelloWorld {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    
    @PostConstruct
    public void init() {
/*        RestClient.setApplicationRoot("/");
        final Place defaultPlace;
        final ActivityMapper activityMapper;

        if (Window.Location.getPath().contains("Standalone.html")) {
            activityMapper = manager.lookupBean(WorskpaceActivityMapper.class).getInstance();
            defaultPlace = PlaceBuilderUtil.buildPlaceFromWindow();
        } else {
            activityMapper = manager.lookupBean(PerspectiveActivityMapper.class).getInstance();
            defaultPlace = new WorkspacePerspectivePlace();
        }

        final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);
*/    }

    @Produces
    public PlaceController placeController() {
        return placeController;
    }

    @Produces
    public EventBus eventBus() {
        return eventBus;
    }


}

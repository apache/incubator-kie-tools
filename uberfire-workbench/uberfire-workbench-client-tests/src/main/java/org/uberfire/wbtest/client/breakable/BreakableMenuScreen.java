/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.breakable;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;
import org.uberfire.wbtest.client.api.PlaceButton;
import org.uberfire.wbtest.client.main.DefaultPerspectiveActivity;

import static java.util.Arrays.*;
import static org.uberfire.debug.Debug.*;

@Dependent
@Named("org.uberfire.wbtest.client.breakable.BreakableMenuScreen")
public class BreakableMenuScreen extends AbstractTestScreenActivity {

    private final VerticalPanel panel = new VerticalPanel();

    @Inject
    public BreakableMenuScreen(PlaceManager placeManager) {
        super(placeManager);
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void setup() {

        // makes this screen detectable to automated tests
        panel.getElement().setId(shortName(getClass()));

        Map<String, String> params = new HashMap<String, String>();
        for (Class<?> activityClass : asList(BreakableScreen.class,
                                             BreakablePerspective.class)) {
            for (LifecyclePhase phase : LifecyclePhase.values()) {
                params.put("broken",
                           phase.toString());
                panel.add(new PlaceButton(placeManager,
                                          new DefaultPlaceRequest(activityClass.getName(),
                                                                  params)));
            }
        }

        panel.add(new PlaceButton(placeManager,
                                  new DefaultPlaceRequest(DefaultPerspectiveActivity.class.getName())));
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}

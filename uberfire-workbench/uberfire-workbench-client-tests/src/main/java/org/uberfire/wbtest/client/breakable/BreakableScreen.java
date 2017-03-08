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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import static org.uberfire.debug.Debug.*;

@Dependent
@Named("org.uberfire.wbtest.client.breakable.BreakableScreen")
public class BreakableScreen extends AbstractTestScreenActivity {

    private final Panel panel = new VerticalPanel();
    private final Label label = new Label("Not started");
    private final Button closeButton = new Button("Close this screen");
    private LifecyclePhase brokenLifecycle;

    @Inject
    public BreakableScreen(PlaceManager placeManager) {
        super(placeManager);
    }

    @Override
    public void onStartup(PlaceRequest place) {
        super.onStartup(place);
        panel.getElement().setId(shortName(getClass()));
        String brokenParam = place.getParameter("broken",
                                                null);
        if (brokenParam != null && brokenParam.length() > 0) {
            brokenLifecycle = LifecyclePhase.valueOf(brokenParam);
        }

        if (brokenParam == null) {
            label.setText("Screen with no broken methods");
        } else {
            label.setText("Screen with broken " + brokenLifecycle + " method");
        }

        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                placeManager.closePlace(getPlace());
            }
        });

        panel.add(label);
        panel.add(closeButton);

        if (brokenLifecycle == LifecyclePhase.STARTUP) {
            throw new RuntimeException("This screen has a broken startup callback");
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if (brokenLifecycle == LifecyclePhase.OPEN) {
            throw new RuntimeException("This screen has a broken open callback");
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (brokenLifecycle == LifecyclePhase.CLOSE) {
            throw new RuntimeException("This screen has a broken close callback");
        }
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        if (brokenLifecycle == LifecyclePhase.SHUTDOWN) {
            throw new RuntimeException("This screen has a broken shutdown callback");
        }
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}

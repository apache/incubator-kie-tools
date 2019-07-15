/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.workbench.client.test;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = TestRunnerReportingScreen.IDENTIFIER, preferredWidth = 437)
public class TestRunnerReportingScreen {

    public static final String IDENTIFIER = "org.kie.guvnor.TestResults";

    @Inject
    private TestRunnerReportingPanel panel;

    @Inject
    private PlaceManager placeManager;

    public TestRunnerReportingScreen() {
        //Zero argument constructor for CDI
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return panel.asWidget();
    }

    public void onPlaceGainFocusEvent(final @Observes PlaceGainFocusEvent placeGainFocusEvent) {
        if (!Objects.equals(placeGainFocusEvent.getPlace().getIdentifier(), IDENTIFIER)) {
            placeManager.closePlace(IDENTIFIER);
        }
    }

    public void onTestRun(final @Observes TestResultMessage testResultMessage) {
        panel.onTestRun(testResultMessage);
    }
}

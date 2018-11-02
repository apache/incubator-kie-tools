/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.workbench.client.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.kie.workbench.common.workbench.client.docks.impl.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class TestReportingDocksHandler
        extends AbstractWorkbenchDocksHandler {

    @Inject
    private AuthoringWorkbenchDocks authoringWorkbenchDocks;

    private UberfireDock testReportDock;

    @Override
    public Collection<UberfireDock> provideDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        testReportDock = new UberfireDock(UberfireDockPosition.EAST,
                                          "PLAY_CIRCLE",
                                          new DefaultPlaceRequest("org.kie.guvnor.TestResults"),
                                          perspectiveIdentifier);
        result.add(testReportDock.withSize(450).withLabel(DefaultWorkbenchConstants.INSTANCE.TestReport()));

        return result;
    }

    public void onDiagramFocusEvent(@Observes OnShowTestPanelEvent event) {
        refreshDocks(true,
                     false);
    }

    public void onDiagramLoseFocusEvent(@Observes OnHideTestPanelEvent event) {
        refreshDocks(true,
                     true);
    }

    public void expandTestResultsDock() {
        authoringWorkbenchDocks.expandAuthoringDock(testReportDock);
    }
}

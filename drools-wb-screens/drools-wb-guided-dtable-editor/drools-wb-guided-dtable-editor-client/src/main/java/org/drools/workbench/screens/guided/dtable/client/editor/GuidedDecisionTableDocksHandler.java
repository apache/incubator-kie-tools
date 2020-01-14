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
package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisDockPlaceHolder;
import org.kie.workbench.common.services.verifier.reporting.client.resources.i18n.AnalysisConstants;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderPlace;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

@ApplicationScoped
public class GuidedDecisionTableDocksHandler
        extends AbstractWorkbenchDocksHandler {

    public static final String VERIFIER_DOCK = "VERIFIER_DOCK";

    private UberfireDock verifierReportDock;

    @Override
    public Collection<UberfireDock> provideDocks(final String perspectiveIdentifier) {

        final List<UberfireDock> result = new ArrayList<>();

        verifierReportDock = new UberfireDock(UberfireDockPosition.EAST,
                                              "PLAY_CIRCLE",
                                              new DockPlaceHolderPlace(AnalysisDockPlaceHolder.IDENTIFIER,
                                                                       VERIFIER_DOCK),
                                              perspectiveIdentifier);
        result.add(verifierReportDock.withSize(450).withLabel(AnalysisConstants.INSTANCE.Analysis()));

        return result;
    }

    public void addDocks() {
        refreshDocks(true,
                     false);
    }

    public void removeDocks() {
        refreshDocks(true,
                     true);
    }
}

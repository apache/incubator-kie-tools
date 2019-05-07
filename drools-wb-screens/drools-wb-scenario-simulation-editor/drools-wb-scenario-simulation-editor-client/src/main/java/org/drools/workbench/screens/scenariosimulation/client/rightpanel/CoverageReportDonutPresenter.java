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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;

@Dependent
public class CoverageReportDonutPresenter {

    protected Elemental2DomUtil elemental2DomUtil;
    protected DisplayerLocator displayerLocator;
    protected DisplayerCoordinator displayerCoordinator;
    protected Displayer displayer;
    protected HTMLDivElement container;

    public CoverageReportDonutPresenter() {
        //CDI
    }

    @Inject
    public CoverageReportDonutPresenter(final DisplayerLocator displayerLocator,
                                        final Elemental2DomUtil elemental2DomUtil,
                                        final DisplayerCoordinator displayerCoordinator) {
        this.displayerLocator = displayerLocator;
        this.elemental2DomUtil = elemental2DomUtil;
        this.displayerCoordinator = displayerCoordinator;
    }

    public void init(final HTMLDivElement container) {
        this.container = container;
    }

    public void showCoverageReport(final int executed,
                                   final int notCovered) {

        if (displayer != null) {
            elemental2DomUtil.removeAllElementChildren(container);
            displayerCoordinator.removeDisplayer(displayer);
        }

        displayer = makeDisplayer(executed,
                                  notCovered);

        displayerCoordinator.addDisplayer(displayer);
        displayerCoordinator.drawAll();

        elemental2DomUtil.appendWidgetToElement(container,
                                                displayer.asWidget());
    }

    protected Displayer makeDisplayer(final int executed,
                                      final int notCovered) {
        return displayerLocator.lookupDisplayer(DisplayerSettingsFactory.newPieChartSettings()
                                                        .height(100)
                                                        .width(80)
                                                        .titleVisible(true)
                                                        .subType_Donut()
                                                        .margins(1, 1, 1, 1)
                                                        .legendOff()
                                                        .column("coverage").format("coverage", "#")
                                                        .dataset(DataSetFactory.newDataSetBuilder()
                                                                         .label("STATUS")
                                                                         .number("coverage")
                                                                         .row(ScenarioSimulationEditorConstants.INSTANCE.executed(),
                                                                              executed)
                                                                         .row(ScenarioSimulationEditorConstants.INSTANCE.notCovered(),
                                                                              notCovered)
                                                                         .buildDataSet())
                                                        .buildSettings());
    }
}

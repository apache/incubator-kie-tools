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

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.NodeList;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;

@Dependent
public class CoverageReportDonutPresenter {

    private static final String COVERAGE = "coverage";

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
                                   final int notCovered,
                                   final String coveragePercentage) {

        if (displayer != null) {
            elemental2DomUtil.removeAllElementChildren(container);
            displayerCoordinator.removeDisplayer(displayer);
        }

        displayer = makeDisplayer(executed,
                                  notCovered,
                                  coveragePercentage);

        displayerCoordinator.addDisplayer(displayer);
        displayerCoordinator.drawAll();

        elemental2DomUtil.appendWidgetToElement(container,
                                                displayer.asWidget());
    }

    /**
     * Scope of this method is to manage the labels inside the Donut chart. The requirements is to:
     * - Remove all labels inside any arc of the chart. This is required because the chart current dimension is very
     *   small and leading to draw these labels in wrongly way.
     * To achieve these requirements without a native support of the component, it navigates the <code>container</code>
     * DOM to retrieve manually the text tags elements which handle the labels.
     */
    public void manageChartLabels() {
        NodeList<Element> listE = container.getElementsByTagName("text");
        for (int i = 0; i < listE.getLength(); i++) {
            Element element = listE.getAt(i);
            String className = element.getAttribute("class");
             if (element.innerHTML != null && element.innerHTML.endsWith("%") && className.isEmpty()) {
                String style = element.getAttribute("style");
                element.setAttribute("style", style.concat("display:none;"));
            }
        }
    }

    protected Displayer makeDisplayer(final int executed,
                                      final int notCovered,
                                      final String coveragePercentage) {
        return displayerLocator.lookupDisplayer(DisplayerSettingsFactory.newPieChartSettings()
                                                        .height(120)
                                                        .width(120)
                                                        .subType_Donut(coveragePercentage)
                                                        .margins(1, 1, 1, 1)
                                                        .legendOff()
                                                        .column(COVERAGE).format(COVERAGE, "#")
                                                        .dataset(DataSetFactory.newDataSetBuilder()
                                                                         .label("STATUS")
                                                                         .number(COVERAGE)
                                                                         .row(ScenarioSimulationEditorConstants.INSTANCE.executed(),
                                                                              executed)
                                                                         .row(ScenarioSimulationEditorConstants.INSTANCE.notCovered(),
                                                                              notCovered)
                                                                         .buildDataSet())
                                                        .buildSettings());
    }
}

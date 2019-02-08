/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.workbench.client.resources.i18n.WorkbenchConstants;

public class TestResultDonutPresenter {

    private TranslationService translationService;

    private Elemental2DomUtil elemental2DomUtil;
    private DisplayerLocator displayerLocator;
    private DisplayerCoordinator displayerCoordinator;
    private Displayer displayer;
    private HTMLDivElement container;

    public TestResultDonutPresenter() {
        //CDI
    }

    @Inject
    public TestResultDonutPresenter(final DisplayerLocator displayerLocator,
                                    final Elemental2DomUtil elemental2DomUtil,
                                    final DisplayerCoordinator displayerCoordinator,
                                    final TranslationService translationService) {
        this.displayerLocator = displayerLocator;
        this.elemental2DomUtil = elemental2DomUtil;
        this.displayerCoordinator = displayerCoordinator;
        this.translationService = translationService;
    }

    public void init(final HTMLDivElement container) {
        this.container = container;
    }

    public void showSuccessFailureDiagram(final int passed,
                                          final int failed) {

        if (displayer != null) {
            elemental2DomUtil.removeAllElementChildren(container);
            displayerCoordinator.removeDisplayer(displayer);
        }

        displayer = makeDisplayer(passed,
                                  failed);

        displayerCoordinator.addDisplayer(displayer);
        displayerCoordinator.drawAll();

        elemental2DomUtil.appendWidgetToElement(container,
                                                displayer.asWidget());
    }

    private Displayer makeDisplayer(final int passed,
                                    final int failed) {
        return displayerLocator.lookupDisplayer(DisplayerSettingsFactory.newPieChartSettings()
                                                        .height(350)
                                                        .width(200)
                                                        .titleVisible(true)
                                                        .subType_Donut()
                                                        .margins(1, 100, 1, 1)
                                                        .legendOn(Position.BOTTOM)
                                                        .column("testCount").format("testCount", "#")
                                                        .dataset(DataSetFactory.newDataSetBuilder()
                                                                         .label("STATUS")
                                                                         .number("testCount")
                                                                         .row(translationService.format(WorkbenchConstants.Passed),
                                                                              passed)
                                                                         .row(translationService.format(WorkbenchConstants.Failed),
                                                                              failed)
                                                                         .buildDataSet())
                                                        .buildSettings());
    }
}

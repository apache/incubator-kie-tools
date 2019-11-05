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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.i18n.client.NumberFormat;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.DMN;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageReportPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageReportPresenter.IDENTIFIER;

@ApplicationScoped
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class CoverageReportPresenter extends AbstractSubDockPresenter<CoverageReportView> implements CoverageReportView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    private static final NumberFormat numberFormat = NumberFormat.getFormat("00.00");

    public static final String IDENTIFIER = "org.drools.scenariosimulation.CoverageReport";

    protected CoverageReportDonutPresenter coverageReportDonutPresenter;

    protected CoverageElementPresenter coverageElementPresenter;

    protected CoverageScenarioListPresenter coverageScenarioListPresenter;

    /**
     * Command to invoke when the user click the <b>download</b> button
     */
    protected Command downloadReportCommand;

    public CoverageReportPresenter() {
        //Zero argument constructor for CDI
        title = ScenarioSimulationEditorConstants.INSTANCE.coverageReport();
    }

    @Inject
    public CoverageReportPresenter(CoverageReportView view,
                                   CoverageReportDonutPresenter coverageReportDonutPresenter,
                                   CoverageElementPresenter coverageElementPresenter,
                                   CoverageScenarioListPresenter coverageScenarioListPresenter) {
        super(view);
        title = ScenarioSimulationEditorConstants.INSTANCE.coverageReport();
        this.coverageReportDonutPresenter = coverageReportDonutPresenter;
        this.coverageElementPresenter = coverageElementPresenter;
        this.coverageScenarioListPresenter = coverageScenarioListPresenter;
    }

    @PostConstruct
    public void init() {
        coverageReportDonutPresenter.init(view.getDonutChart());
        coverageElementPresenter.initElementList(view.getList());
        coverageScenarioListPresenter.initScenarioList(view.getScenarioList());
        resetDownload();
    }

    @Override
    public void reset() {
        view.reset();
        resetDownload();
    }

    @Override
    public void populateCoverageReport(Type type, SimulationRunMetadata simulationRunMetadata) {
        if (simulationRunMetadata != null) {
            setSimulationRunMetadata(simulationRunMetadata, type);
        } else {
            showEmptyStateMessage();
        }
    }

    @Override
    public void onDownloadReportButtonClicked() {
        if (downloadReportCommand != null) {
            downloadReportCommand.execute();
        }
    }

    @Override
    public void setDownloadReportCommand(Command downloadReportCommand) {
        this.downloadReportCommand = downloadReportCommand;
        view.getDownloadReportButton().disabled = this.downloadReportCommand == null;
    }

    protected void setSimulationRunMetadata(SimulationRunMetadata simulationRunMetadata, Type type) {
        // Coverage report should not be shown if there are no rules/decisions.
        if (simulationRunMetadata.getAvailable() == 0) {
            String messageToShow = DMN.equals(type) ?
                    ScenarioSimulationEditorConstants.INSTANCE.noDecisionsAvailable() :
                    ScenarioSimulationEditorConstants.INSTANCE.noRulesAvailable();
            view.setEmptyStatusText(messageToShow);
            view.hide();
            return;
        }
        view.initText(type);

        populateSummary(simulationRunMetadata.getAvailable(),
                        simulationRunMetadata.getExecuted(),
                        simulationRunMetadata.getCoveragePercentage());

        populateList(simulationRunMetadata.getOutputCounter());

        populateScenarioList(simulationRunMetadata.getScenarioCounter(), type);
        view.show();
    }

    protected void resetDownload() {
        downloadReportCommand = null;
        view.getDownloadReportButton().disabled = true;
    }

    protected void showEmptyStateMessage() {
        view.setEmptyStatusText(ScenarioSimulationEditorConstants.INSTANCE.runATestToSeeCoverageReport());
        view.hide();
    }

    protected void populateSummary(int available, int executed, double coveragePercentage) {
        view.setReportAvailable(available + "");
        view.setReportExecuted(executed + "");
        String coveragePercentageFormatted = numberFormat.format(coveragePercentage);
        view.setReportCoverage(coveragePercentageFormatted + "%");

        // donut chart
        coverageReportDonutPresenter
                .showCoverageReport(executed,
                                    available - executed);
    }

    protected void populateList(Map<String, Integer> outputCounter) {
        coverageElementPresenter.clear();
        List<String> elements = new ArrayList<>(outputCounter.keySet());
        elements.sort(Comparator.naturalOrder());
        for (String element : elements) {
            coverageElementPresenter.addElementView(element, outputCounter.get(element) + "");
        }
    }

    protected void populateScenarioList(Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounter, Type type) {
        coverageScenarioListPresenter.clear();
        List<ScenarioWithIndex> scenarioIndexes = new ArrayList<>(scenarioCounter.keySet());
        scenarioIndexes.sort(Comparator.comparingInt(ScenarioWithIndex::getIndex));
        for (ScenarioWithIndex scenarioWithIndex : scenarioIndexes) {
            coverageScenarioListPresenter.addScesimDataGroup(scenarioWithIndex, scenarioCounter.get(scenarioWithIndex), type);
        }
    }
}

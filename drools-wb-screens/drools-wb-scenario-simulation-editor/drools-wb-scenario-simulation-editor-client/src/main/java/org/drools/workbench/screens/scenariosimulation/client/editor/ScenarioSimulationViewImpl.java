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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuItem;

/**
 * Implementation of the main view for the ScenarioSimulation editor.
 * <p>
 * This view contains a <code>ScenarioGridPanel</code>.
 */
@Dependent
public class ScenarioSimulationViewImpl
        extends KieEditorViewImpl
        implements ScenarioSimulationView {

    private ScenarioGridPanel scenarioGridPanel;

    private ScenarioSimulationEditorPresenter presenter;

    private ScenarioGridLayer scenarioGridLayer;

    private ScenarioMenuItem runMenuItem;

    private ScenarioMenuItem undoMenuItem;

    private ScenarioMenuItem redoMenuItem;

    private ScenarioMenuItem downloadMenuItem;

    private ScenarioMenuItem importMenuItem;

    private ScenarioMenuItem exportToCsvMenuItem;

    /**
     * This method also set <code>ScenarioGridLayer</code> taken the instance from given <code>ScenarioGridPanel</code>
     * @param scenarioGridPanel
     */
    @Override
    public void setScenarioGridPanel(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridPanel.getScenarioGridLayer();
    }

    @Override
    public void init(final ScenarioSimulationEditorPresenter presenter) {
        this.presenter = presenter;
        scenarioGridLayer.enterPinnedMode(scenarioGridLayer.getScenarioGrid(), () -> {
        });  // Hack to overcome default implementation
        initWidget(scenarioGridPanel);
    }

    @Override
    public void setContent(Simulation simulation) {
        scenarioGridPanel.getScenarioGrid().setContent(simulation);

        // prepare grid for keyboard navigation
        scenarioGridPanel.setFocus(true);
    }

    @Override
    public void refreshContent(Simulation simulation) {
        scenarioGridPanel.getScenarioGrid().getModel().bindContent(simulation);
        refreshErrors();
    }

    @Override
    public MenuItem getRunScenarioMenuItem() {
        if (runMenuItem == null) {
            runMenuItem = new ScenarioMenuItem(IconType.PLAY,
                                               () -> presenter.onRunScenario());
        }
        return runMenuItem;
    }

    @Override
    public MenuItem getUndoMenuItem() {
        if (undoMenuItem == null) {
            undoMenuItem = new ScenarioMenuItem(IconType.UNDO,
                                                () -> presenter.onUndo());
        }
        return undoMenuItem;
    }

    @Override
    public MenuItem getRedoMenuItem() {
        if (redoMenuItem == null) {
            redoMenuItem = new ScenarioMenuItem(IconType.REPEAT,
                                                () -> presenter.onRedo());
        }
        return redoMenuItem;
    }

    @Override
    public MenuItem getDownloadMenuItem(final Supplier<Path> pathSupplier) {
        if (downloadMenuItem == null) {
            downloadMenuItem = new ScenarioMenuItem(IconType.DOWNLOAD,
                                                    () -> presenter.onDownload(pathSupplier));
        }
        return downloadMenuItem;
    }

    @Override
    public MenuItem getExportToCsvMenuItem() {
        if (exportToCsvMenuItem == null) {
            exportToCsvMenuItem = new ScenarioMenuItem("Export",
                                                       () -> presenter.onExportToCsv());
        }
        return exportToCsvMenuItem;
    }

    @Override
    public MenuItem getImportMenuItem() {
        if (importMenuItem == null) {
            importMenuItem = new ScenarioMenuItem(ScenarioSimulationEditorConstants.INSTANCE.importLabel(),
                                                  () -> presenter.showImportDialog());
        }
        return importMenuItem;
    }

    @Override
    public ScenarioGridPanel getScenarioGridPanel() {
        return scenarioGridPanel;
    }

    @Override
    public ScenarioGridLayer getScenarioGridLayer() {
        return scenarioGridLayer;
    }

    private void refreshErrors() {
        scenarioGridPanel.getScenarioGrid().getModel().refreshErrors();
        onResize();
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if (parent != null) {
            final double w = parent.getOffsetWidth();
            final double h = parent.getOffsetHeight();
            setPixelSize((int) w, (int) h);
        }
        scenarioGridPanel.onResize();
    }
}
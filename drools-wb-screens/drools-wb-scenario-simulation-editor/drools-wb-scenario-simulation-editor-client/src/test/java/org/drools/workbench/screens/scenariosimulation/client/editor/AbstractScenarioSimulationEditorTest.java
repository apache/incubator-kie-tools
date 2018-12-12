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

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.BaseMenuView;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationEditorTest extends AbstractScenarioSimulationTest {

    protected static final String SCENARIO_PACKAGE = "scenario.package";

    @Mock
    protected VersionRecordManager versionRecordManagerMock;
    @Mock
    protected FileMenuBuilder fileMenuBuilderMock;
    @Mock
    protected ScenarioSimulationService scenarioSimulationServiceMock;
    @Mock
    protected ObservablePath observablePathMock;
    @Mock
    protected Overview overviewMock;
    @Mock
    protected GridContextMenu gridContextMenuMock;
    @Mock
    protected HeaderGivenContextMenu headerGivenContextMenuMock;
    @Mock
    protected BaseMenuView gridContextMenuViewMock;
    @Mock
    protected BaseMenuView headerContextMenuViewMock;
    @Mock
    protected WorkspaceProjectContext workbenchContextMock;
    @Mock
    protected RightPanelPresenter rightPanelPresenterMock;
    @Mock
    protected AsyncPackageDataModelOracleFactory oracleFactoryMock;
    @Mock
    protected ScenarioGridModel scenarioGridModelMock;

    protected ScenarioSimulationModelContent content;
    protected ScenarioSimulationModel model;

    public void setup() {
        // Mock FileMenuBuilder usage since we cannot use FileMenuBuilderImpl either
        when(fileMenuBuilderMock.addSave(any(MenuItem.class))).thenReturn(fileMenuBuilderMock);
        when(fileMenuBuilderMock.addCopy(any(ObservablePath.class), any(DefaultFileNameValidator.class))).thenReturn(fileMenuBuilderMock);
        when(fileMenuBuilderMock.addRename(any(Command.class))).thenReturn(fileMenuBuilderMock);
        when(fileMenuBuilderMock.addDelete(any(ObservablePath.class))).thenReturn(fileMenuBuilderMock);
        when(fileMenuBuilderMock.addValidate(any(Command.class))).thenReturn(fileMenuBuilderMock);
        when(fileMenuBuilderMock.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(fileMenuBuilderMock);
        when(versionRecordManagerMock.getCurrentPath()).thenReturn(observablePathMock);
        when(versionRecordManagerMock.getPathToLatest()).thenReturn(observablePathMock);
        when(workbenchContextMock.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(gridContextMenuMock.getView()).thenReturn(gridContextMenuViewMock);
        when(headerGivenContextMenuMock.getView()).thenReturn(headerContextMenuViewMock);
        this.model = new ScenarioSimulationModel(ScenarioSimulationModel.Type.RULE, "default");
        this.content = new ScenarioSimulationModelContent(model,
                                                          overviewMock,
                                                          mock(PackageDataModelOracleBaselinePayload.class));
        when(scenarioSimulationServiceMock.loadContent(observablePathMock)).thenReturn(content);
    }
}

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

import org.drools.workbench.screens.scenariosimulation.client.editor.menu.BaseMenuView;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderContextMenu;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
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

public abstract class AbstractScenarioSimulationEditorTest {

    @Mock
    protected VersionRecordManager mockVersionRecordManager;

    @Mock
    protected FileMenuBuilder mockFileMenuBuilder;

    @Mock
    protected ScenarioSimulationService scenarioSimulationService;

    @Mock
    protected ObservablePath mockObservablePath;

    @Mock
    protected Overview mockOverview;

    @Mock
    protected GridContextMenu mockGridContextMenu;

    @Mock
    protected HeaderContextMenu mockHeaderContextMenu;

    @Mock
    protected BaseMenuView mockGridContextMenuView;

    @Mock
    protected BaseMenuView mockHeaderContextMenuView;

    @Mock
    protected WorkspaceProjectContext mockWorkbenchContext;

    protected ScenarioSimulationModelContent content;
    protected ScenarioSimulationModel model;

    public void setup() {

//        //Mock FileMenuBuilder usage since we cannot use FileMenuBuilderImpl either
        when(mockFileMenuBuilder.addSave(any(MenuItem.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addCopy(any(ObservablePath.class), any(DefaultFileNameValidator.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addRename(any(Command.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addDelete(any(ObservablePath.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addValidate(any(Command.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(mockFileMenuBuilder);

        when(mockVersionRecordManager.getCurrentPath()).thenReturn(mockObservablePath);
        when(mockVersionRecordManager.getPathToLatest()).thenReturn(mockObservablePath);

        when(mockWorkbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());

        when(mockGridContextMenu.getView()).thenReturn(mockGridContextMenuView);
        when(mockHeaderContextMenu.getView()).thenReturn(mockHeaderContextMenuView);

        this.model = new ScenarioSimulationModel();
        this.content = new ScenarioSimulationModelContent(model,
                                                          mockOverview,
                                                          mock(PackageDataModelOracleBaselinePayload.class));

        when(scenarioSimulationService.loadContent(mockObservablePath)).thenReturn(content);
    }
}

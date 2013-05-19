/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.projecteditor.client.forms;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Ignore
public class ProjectScreenTest {
    private ProjectScreenView view;
    private POMEditorPanel pomPanel;
    private KModuleEditorPanel kModuleEditorPanel;
    private ProjectScreenPresenter screen;
    private MockProjectEditorServiceCaller projectEditorServiceCaller;
    private MockBuildServiceCaller buildServiceCaller;
    private MockMetadataServiceCaller metadataServiceCaller;
    private SaveOperationService saveOperationService;

    @Before
    public void setUp() throws Exception {
        view = mock(ProjectScreenView.class);
        when(view.getSaveMenuItemText()).thenReturn("");
        when(view.getBuildMenuItemText()).thenReturn("Build");
        when(view.getEnableKieProjectMenuItemText()).thenReturn("");
        pomPanel = mock(POMEditorPanel.class);
        kModuleEditorPanel = mock(KModuleEditorPanel.class);
        projectEditorServiceCaller = new MockProjectEditorServiceCaller();
        buildServiceCaller = new MockBuildServiceCaller();
        metadataServiceCaller = new MockMetadataServiceCaller();
        saveOperationService = mock(SaveOperationService.class);
        WorkbenchContext workbenchContext = mock(WorkbenchContext.class);

        MockProjectServiceCaller projectServiceCaller = new MockProjectServiceCaller();

        ImportsWidgetPresenter importsWidgetPresenter = mock(ImportsWidgetPresenter.class);
        screen = new ProjectScreenPresenter(view, pomPanel, kModuleEditorPanel, importsWidgetPresenter, workbenchContext, projectServiceCaller, projectEditorServiceCaller, buildServiceCaller, metadataServiceCaller, saveOperationService);
        screen.getMenus();
    }

    @Test
    public void testBasicSetup() throws Exception {
        verify(view).setPOMEditorPanel(pomPanel);
        verify(view, never()).setKModuleEditorPanel(kModuleEditorPanel);
    }

    @Test
    public void testInit() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);
        screen.selectedPathChanged(new PathChangeEvent(path));

        verify(pomPanel).init(path, false);
        verify(kModuleEditorPanel, never()).init(any(Path.class), eq(false));
    }

    @Test
    public void testInitWithKModuleFile() throws Exception {
        Path pathToKModuleXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKModuleXML);
        Path path = mock(Path.class);
        screen.selectedPathChanged(new PathChangeEvent(path));

        verify(pomPanel).init(path, false);
        verify(view).setKModuleEditorPanel(kModuleEditorPanel);
    }

//    @Test
//    public void testEnableKProject() throws Exception {
//        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
//        Path pathToGav = mock(Path.class);
//        screen.init(pathToGav);
//
//        Path pathToKProjectXML = mock(Path.class);
//        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKProjectXML);
//        MenuBar menuBar = screen.buildMenuBar();
//        clickThird(menuBar);
//
//        verify(kModuleEditorPanel).init(pathToKProjectXML);
//        verify(view).setKModuleEditorPanel(kModuleEditorPanel);
//    }

    @Test
    public void testSave() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);
        screen.selectedPathChanged(new PathChangeEvent(path));

        //menuBuilder.clickSave();

        clickOkToCommitPopup();

        verify(pomPanel).save(anyString(), any(Command.class), any(Metadata.class));
        verify(kModuleEditorPanel, never()).save(anyString(), any(Command.class), any(Metadata.class));
    }

    @Test
    public void testSaveBoth() throws Exception {
        Path pathToKProjectXML = mock(Path.class);
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(pathToKProjectXML);
        Path path = mock(Path.class);
        screen.selectedPathChanged(new PathChangeEvent(path));

        initKModuleEditorPanel();

//        menuBuilder.clickSave();

        clickOkToCommitPopup();

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(pomPanel).save(anyString(), commandArgumentCaptor.capture(), any(Metadata.class));
        commandArgumentCaptor.getValue().execute();
        verify(kModuleEditorPanel).save(anyString(), any(Command.class), any(Metadata.class));
    }

    @Test
    public void testBuild() throws Exception {
        projectEditorServiceCaller.setPathToRelatedKModuleFileIfAny(null);
        Path path = mock(Path.class);

        screen.selectedPathChanged(new PathChangeEvent(path));

//        menuBuilder.click("Build");

        assertTrue(buildServiceCaller.isBuildWasCalled());
    }

    private void initKModuleEditorPanel() {
        when(
                kModuleEditorPanel.hasBeenInitialized()
        ).thenReturn(
                true
        );
    }

    private void clickOkToCommitPopup() {
        ArgumentCaptor<CommandWithCommitMessage> saveCommandArgumentCaptor = ArgumentCaptor.forClass(CommandWithCommitMessage.class);
        verify(saveOperationService).save(any(Path.class), saveCommandArgumentCaptor.capture());
        saveCommandArgumentCaptor.getValue().execute("Commit Message");
    }
}

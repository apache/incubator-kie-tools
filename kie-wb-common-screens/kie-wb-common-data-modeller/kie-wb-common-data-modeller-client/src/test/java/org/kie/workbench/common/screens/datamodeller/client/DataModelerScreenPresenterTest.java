/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.widgets.metadata.client.validation.JavaAssetUpdateValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataModelerScreenPresenterTest
        extends DataModelerScreenPresenterTestBase {

    private static final String FILE_NAME = "FILE_NAME";

    private static final String COMMIT_MESSAGE = "COMMIT_MESSAGE";

    private ArgumentCaptor<Path> pathArgumentCaptor;

    private ArgumentCaptor<Validator> validatorArgumentCaptor;

    private ArgumentCaptor<CommandWithFileNameAndCommitMessage> commandWithFileNameArgumentCaptor;

    @Mock
    private FileNameAndCommitMessage commitMessage;

    @Mock
    protected CopyPopUpPresenter.View copyPopUpPresenterView;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(copyPopUpPresenter.getView()).thenReturn(copyPopUpPresenterView);
        pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        validatorArgumentCaptor = ArgumentCaptor.forClass(Validator.class);
        commandWithFileNameArgumentCaptor = ArgumentCaptor.forClass(CommandWithFileNameAndCommitMessage.class);
    }

    /**
     * This test emulates the loading of a java file that was parsed without errors.
     * Additionally it supposes that the DataModelerWBContext has not yet the information about property types
     * and annotations definitions, and thus should also check that this information was also loaded from server and
     * properly initialized.
     */
    @Test
    public void loadFileSuccessfulWithTypesInfoTest() {
        loadFileSuccessfulTest(true);
    }

    /**
     * This test emulates the loading of a java file that was parsed without errors.
     * Additionally it supposes that the property types and annotations definitions were already loaded into the
     * DataModelerWBContext, and thus they shouldn't be loaded from server.
     */
    @Test
    public void loadFileSuccessfulWithNoTypesInfoTest() {
        loadFileSuccessfulTest(false);
    }

    /**
     * This test emulates the loading of a mal formed java file, and thus with parse errors.
     * Additionally it supposes that the DataModelerWBContext has not yet the information about property types
     * and annotations definitions, and thus should also check that this information was also loaded from server and
     * properly initialized.
     */
    @Test
    public void loadFileUnSuccessfulWithTypesInfoTest() {
        loadFileUnSuccessfulTest(true);
    }

    /**
     * This test emulates the loading of a mal formed java file, and thus with parse errors.
     * Additionally it supposes that the property types and annotations definitions were already loaded into the
     * DataModelerWBContext, and thus they shouldn't be loaded from server.
     */
    @Test
    public void loadFileUnSuccessfulWithNoTypesInfoTest() {
        loadFileUnSuccessfulTest(false);
    }

    /**
     * Tests that a java file without parse errors was successfully loaded.
     * @param loadTypesInfo indicates if the types and annotations definitions loading should be simulated.
     */
    private void loadFileSuccessfulTest(boolean loadTypesInfo) {

        EditorModelContent content = createContent(loadTypesInfo,
                                                   false);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(modelerService.loadContent(path,
                                        loadTypesInfo)).thenReturn(content);
        when(javaSourceEditor.getContent()).thenReturn(content.getSource());

        if (loadTypesInfo) {
            //types info is not loaded into the DataModelerWBContext.
            when(dataModelerWBContext.isTypesInfoLoaded()).thenReturn(false);
        } else {
            //types info is already into the DataModelerWBContext.
            when(dataModelerWBContext.isTypesInfoLoaded()).thenReturn(true);
        }

        //just for convenience, since the DataModelerContext is initialized by taking this definitions from the DMWC.
        when(dataModelerWBContext.getAnnotationDefinitions()).thenReturn(testAnnotationDefs);
        when(dataModelerWBContext.getPropertyTypes()).thenReturn(testTypeDefs);

        presenter.onStartup(path,
                            placeRequest);

        //Verifications during and after model loading.

        verify(view,
               times(1)).showLoading();
        verify(view,
               times(1)).hideBusyIndicator();

        //presenter should ask the DataModelerWBContext if the types info is already loaded.
        verify(dataModelerWBContext,
               times(1)).isTypesInfoLoaded();

        if (loadTypesInfo) {
            //the types info should have been set into the DataModelerWBContext as part of the presenter loading.
            verify(dataModelerWBContext,
                   times(1)).setPropertyTypes(testTypeDefs);
            verify(dataModelerWBContext,
                   times(1)).setAnnotationDefinitions(testAnnotationDefs);
        } else {
            //the types info shouldn't have been set into the DataModelerWBContext as part of the presenter loading.
            verify(dataModelerWBContext,
                   times(0)).setPropertyTypes(testTypeDefs);
            verify(dataModelerWBContext,
                   times(0)).setAnnotationDefinitions(testAnnotationDefs);
        }

        //presenter should clear the system messages related to this editor.
        verify(unpublishMessagesEvent,
               times(1)).fire(any(UnpublishMessagesEvent.class));

        //presenter should read the expected path.
        verify(modelerService,
               times(1)).loadContent(path,
                                     loadTypesInfo);

        //verify that the context created by the presenter was properly initialized.
        DataModelerContext context = presenter.context;

        assertEquals(testModel,
                     context.getDataModel());
        assertEquals(testObject1,
                     context.getDataObject());
        assertEquals(kieModule,
                     context.getCurrentProject());
        assertEquals(testPackages,
                     context.getCurrentProjectPackages());
        assertEquals(testAnnotationDefs,
                     context.getAnnotationDefinitions());
        assertEquals(content,
                     context.getEditorModelContent());

        //the file was read successfully, so the status should be PARSED
        assertEquals(DataModelerContext.ParseStatus.PARSED,
                     context.getParseStatus());
        //the file was read and parsed successfully, so the editor should be now in the editor tab.
        assertEquals(DataModelerContext.EditionMode.GRAPHICAL_MODE,
                     context.getEditionMode());
        //file was just read, so the status should be NO_CHANGES.
        assertEquals(DataModelerContext.EditionStatus.NO_CHANGES,
                     context.getEditionStatus());

        //the view should have been initialized with the context.
        verify(view,
               times(1)).setContext(context);

        //the source editor should have been initialized with the source returned form server.
        verify(javaSourceEditor,
               times(1)).setContent(testSource);

        //current context should have been activated
        verify(dataModelerWBContext,
               times(1)).setActiveContext(context);
        //and notifications should have been sent.
        verify(dataModelerFocusEvent,
               times(1)).fire(any(DataModelerWorkbenchFocusEvent.class));
    }

    /**
     * Tests that a java file with parse errors was successfully loaded.
     * @param loadTypesInfo indicates if the types and annotations definitions loading should be simulated.
     */
    private void loadFileUnSuccessfulTest(boolean loadTypesInfo) {

        EditorModelContent content = createContent(loadTypesInfo,
                                                   true);
        //when there are parse errors the returned data object is null.
        content.setDataObject(null);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(modelerService.loadContent(path,
                                        loadTypesInfo)).thenReturn(content);
        when(javaSourceEditor.getContent()).thenReturn(content.getSource());

        if (loadTypesInfo) {
            //types info is not loaded into the DataModelerWBContext.
            when(dataModelerWBContext.isTypesInfoLoaded()).thenReturn(false);
        } else {
            //types info is already into the DataModelerWBContext.
            when(dataModelerWBContext.isTypesInfoLoaded()).thenReturn(true);
        }

        //just for convenience, since the DataModelerContext is initialized by taking this definitions from the DMWC.
        when(dataModelerWBContext.getAnnotationDefinitions()).thenReturn(testAnnotationDefs);
        when(dataModelerWBContext.getPropertyTypes()).thenReturn(testTypeDefs);

        presenter.onStartup(path,
                            placeRequest);

        //Verifications during and after model loading.

        verify(view,
               times(1)).showLoading();
        verify(view,
               times(1)).hideBusyIndicator();

        //presenter should ask the DataModelerWBContext if the types info is already loaded.
        verify(dataModelerWBContext,
               times(1)).isTypesInfoLoaded();

        if (loadTypesInfo) {
            //the types info should have been set into the DataModelerWBContext as part of the presenter loading.
            verify(dataModelerWBContext,
                   times(1)).setPropertyTypes(testTypeDefs);
            verify(dataModelerWBContext,
                   times(1)).setAnnotationDefinitions(testAnnotationDefs);
        } else {
            //the types info shouldn't have been set into the DataModelerWBContext as part of the presenter loading.
            verify(dataModelerWBContext,
                   times(0)).setPropertyTypes(testTypeDefs);
            verify(dataModelerWBContext,
                   times(0)).setAnnotationDefinitions(testAnnotationDefs);
        }

        //presenter should clear the system messages related to this editor.
        verify(unpublishMessagesEvent,
               times(1)).fire(any(UnpublishMessagesEvent.class));

        //presenter should read the expected path.
        verify(modelerService,
               times(1)).loadContent(path,
                                     loadTypesInfo);

        //parse errors should have been published.
        verify(publishBatchMessagesEvent,
               times(1)).fire(any(PublishBatchMessagesEvent.class));
        //parse errors dialog should have been raised.
        verify(view,
               times(1)).showParseErrorsDialog(anyString(),
                                               anyString(),
                                               any(Command.class));

        //at this point the parse errors popup is raised and waiting for the user to press the ok button.
        //emulate the user click on the button.
        presenter.getOnLoadParseErrorCommand().execute();

        //verify that the context created by the presenter was properly initialized.
        DataModelerContext context = presenter.context;

        assertEquals(testModel,
                     context.getDataModel());
        assertEquals(null,
                     context.getDataObject());
        assertEquals(kieModule,
                     context.getCurrentProject());
        assertEquals(testPackages,
                     context.getCurrentProjectPackages());
        assertEquals(testAnnotationDefs,
                     context.getAnnotationDefinitions());
        assertEquals(content,
                     context.getEditorModelContent());

        //parse errors wherer produced on server so the status should be PARSE_ERRORS
        assertEquals(DataModelerContext.ParseStatus.PARSE_ERRORS,
                     context.getParseStatus());
        //the file wasn't parsed the editor should go to the source tab.
        assertEquals(DataModelerContext.EditionMode.SOURCE_MODE,
                     context.getEditionMode());
        //file was just read, so the status should be NO_CHANGES.
        assertEquals(DataModelerContext.EditionStatus.NO_CHANGES,
                     context.getEditionStatus());

        //context wasn't set on the view since there aren't a data object to show.
        verify(view,
               times(0)).setContext(context);

        //the source editor should have been initialized with the source returned form server.
        verify(javaSourceEditor,
               times(2)).setContent(testSource);

        //current context should have been activated
        verify(dataModelerWBContext,
               times(1)).setActiveContext(context);
        //and notifications should have been sent.
        verify(dataModelerFocusEvent,
               times(1)).fire(any(DataModelerWorkbenchFocusEvent.class));
    }

    @Test
    public void onSaveValidationFailedTest() {
        when(validationService.validateForSave(any(Path.class),
                                               any(DataObject.class))).thenReturn(Arrays.asList(new ValidationMessage()));
        presenter.context = mock(DataModelerContext.class);

        presenter.save();

        verify(savePopUpPresenter,
               never()).show(any(Path.class),
                             any(ParameterizedCommand.class));
        verify(validationPopup,
               times(1)).showSaveValidationMessages(any(Command.class),
                                                    any(Command.class),
                                                    anyListOf(ValidationMessage.class));
    }

    @Test
    public void onSaveValidationSucceededTest() {
        when(validationService.validateForSave(any(Path.class),
                                               any(DataObject.class))).thenReturn(Collections.emptyList());
        presenter.context = mock(DataModelerContext.class);

        presenter.save();

        verify(savePopUpPresenter,
               times(1)).show(any(Path.class),
                              any(ParameterizedCommand.class));
        verify(validationPopup,
               never()).showCopyValidationMessages(any(Command.class),
                                                   any(Command.class),
                                                   anyListOf(ValidationMessage.class));
    }

    @Test
    public void onSafeDeleteWithOriginalClassName() {
        loadFileSuccessfulTest(false);

        presenter.onSafeDelete();

        verify(showAssetUsages).showAssetUsages(anyString(),
                                                any(),
                                                anyString(),
                                                any(),
                                                any(),
                                                any());
        verify(validationService,
               never()).validateForDelete(any(),
                                          any());
        verify(deletePopUpPresenter,
               never()).show(any());
    }

    @Test
    public void onSafeDeleteWithoutOriginalClassName() {
        loadFileSuccessfulTest(false);

        presenter.context.getEditorModelContent().setOriginalClassName(null);

        presenter.onSafeDelete();

        verify(showAssetUsages,
               never()).showAssetUsages(anyString(),
                                        any(),
                                        anyString(),
                                        any(),
                                        any(),
                                        any());
        verify(validationService).validateForDelete(any(),
                                                    any());
        verify(deletePopUpPresenter).show(any(JavaAssetUpdateValidator.class),
                                          any());
    }

    @Test
    public void onSafeRenameWithOriginalClassName() {
        loadFileSuccessfulTest(false);

        presenter.onSafeRename();

        verify(showAssetUsages).showAssetUsages(anyString(),
                                                any(),
                                                anyString(),
                                                any(),
                                                any(),
                                                any());

        verify(renamePopUpPresenter,
               never()).show(any(Path.class),
                             any(JavaAssetUpdateValidator.class),
                             any(CommandWithFileNameAndCommitMessage.class));
    }

    @Test
    public void onSafeRenameWithoutOriginalClassName() {
        loadFileSuccessfulTest(false);

        presenter.context.getEditorModelContent().setOriginalClassName(null);

        presenter.onSafeRename();

        verify(showAssetUsages,
               never()).showAssetUsages(anyString(),
                                        any(),
                                        anyString(),
                                        any(),
                                        any(),
                                        any());

        verify(renamePopUpPresenter).show(any(Path.class),
                                          any(JavaAssetUpdateValidator.class),
                                          any(CommandWithFileNameAndCommitMessage.class));
    }

    @Test
    public void renameValidationSucceededTest() {
        presenter.context = mock(DataModelerContext.class);
        // Make sure the context is dirty
        presenter.setOriginalHash(0);

        presenter.rename();

        verify(view,
               times(1)).showYesNoCancelPopup(anyString(),
                                              anyString(),
                                              any(Command.class),
                                              any(Command.class));

        // Simulate "yes" action in YesNoCancelPopup, with no validation issues detected
        presenter.getRenameValidationCallback().callback(Collections.emptyList());

        verify(renamePopUpPresenter,
               times(1)).show(any(Path.class),
                              any(JavaAssetUpdateValidator.class),
                              any(CommandWithFileNameAndCommitMessage.class));
        verify(validationPopup,
               never()).showSaveValidationMessages(any(Command.class),
                                                   any(Command.class),
                                                   anyListOf(ValidationMessage.class));
    }

    @Test
    public void renameValidationFailedTest() {
        presenter.context = mock(DataModelerContext.class);
        // Make sure the context is dirty
        presenter.setOriginalHash(0);

        presenter.rename();

        verify(view,
               times(1)).showYesNoCancelPopup(anyString(),
                                              anyString(),
                                              any(Command.class),
                                              any(Command.class));

        // Simulate "yes" action in YesNoCancelPopup, with validation issues detected
        presenter.getRenameValidationCallback().callback(Arrays.asList(new ValidationMessage()));

        verify(renamePopUpPresenter,
               never()).show(any(Path.class),
                             any(JavaAssetUpdateValidator.class),
                             any(CommandWithFileNameAndCommitMessage.class));
        verify(validationPopup,
               times(1)).showSaveValidationMessages(any(Command.class),
                                                    any(Command.class),
                                                    anyListOf(ValidationMessage.class));
    }

    @Test
    public void onCopyValidationSucceededTest() {
        when(validationService.validateForCopy(any(Path.class),
                                               any(DataObject.class))).thenReturn(Collections.emptyList());
        presenter.context = mock(DataModelerContext.class);

        presenter.onCopy();

        verify(copyPopUpPresenter,
               times(1)).show(any(Path.class),
                              any(JavaAssetUpdateValidator.class),
                              any(CommandWithFileNameAndCommitMessage.class));
        verify(validationPopup,
               never()).showCopyValidationMessages(any(Command.class),
                                                   any(Command.class),
                                                   anyListOf(ValidationMessage.class));
    }

    @Test
    public void onCopyValidationFailedTest() {
        when(validationService.validateForCopy(any(Path.class),
                                               any(DataObject.class))).thenReturn(Arrays.asList(new ValidationMessage()));
        presenter.context = mock(DataModelerContext.class);

        presenter.onCopy();

        verify(copyPopUpPresenter,
               never()).show(any(Path.class),
                             any(JavaAssetUpdateValidator.class),
                             any(CommandWithFileNameAndCommitMessage.class));
        verify(validationPopup,
               times(1)).showCopyValidationMessages(any(Command.class),
                                                    any(Command.class),
                                                    anyListOf(ValidationMessage.class));
    }

    @Test
    public void onCopyWithTargetPathTest() {
        prepareOnCopyTest();
        presenter.onCopy();
        verify(copyPopUpPresenter,
               times(1)).show(pathArgumentCaptor.capture(),
                              validatorArgumentCaptor.capture(),
                              commandWithFileNameArgumentCaptor.capture());

        Path targetPath = mock(Path.class);
        when(copyPopUpPresenterView.getTargetPath()).thenReturn(targetPath);
        commandWithFileNameArgumentCaptor.getValue().execute(commitMessage);

        verify(modelerService,
               times(1)).copy(path,
                              FILE_NAME,
                              null,
                              targetPath,
                              COMMIT_MESSAGE,
                              true);
    }

    @Test
    public void onCopyWithNoTargetPathTest() {
        prepareOnCopyTest();
        presenter.onCopy();
        verify(copyPopUpPresenter,
               times(1)).show(pathArgumentCaptor.capture(),
                              validatorArgumentCaptor.capture(),
                              commandWithFileNameArgumentCaptor.capture());

        when(copyPopUpPresenterView.getTargetPath()).thenReturn(null);
        commandWithFileNameArgumentCaptor.getValue().execute(commitMessage);

        verify(modelerService,
               times(1)).copy(path,
                              FILE_NAME,
                              COMMIT_MESSAGE,
                              true);
    }

    private void prepareOnCopyTest() {
        when(validationService.validateForCopy(any(Path.class),
                                               any(DataObject.class))).thenReturn(Collections.emptyList());
        presenter.context = mock(DataModelerContext.class);

        when(commitMessage.getCommitMessage()).thenReturn(COMMIT_MESSAGE);
        when(commitMessage.getNewFileName()).thenReturn(FILE_NAME);
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
    }

    @Test
    public void testMakeMenuBar() {

        final DataModelerScreenPresenter presenter = spy(this.presenter);

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Command.class));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(Command.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(presenter).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Command.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Command.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Command.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void onFocusDoesNotRedrawViewWhenModuleIsNotActive() {
        doReturn(Optional.empty()).when(workbenchContext).getActiveModule();

        final DataModelerScreenPresenter presenter = spy(this.presenter);
        presenter.context = mock(DataModelerContext.class);

        assertFalse(presenter.loading);
        assertNotNull(presenter.context);

        presenter.onFocus();

        verify(view, never()).redraw();
        verify(dataModelerWBContext, never()).setActiveContext(any());
        verify(dataModelerFocusEvent, never()).fire(any());
    }

    @Test
    public void onFocusRedrawsViewWhenModuleIsActive() {
        doReturn(Optional.of(mock(Module.class))).when(workbenchContext).getActiveModule();

        final DataModelerScreenPresenter presenter = spy(this.presenter);
        presenter.context = mock(DataModelerContext.class);

        assertFalse(presenter.loading);
        assertNotNull(presenter.context);

        presenter.onFocus();

        verify(view).redraw();
        verify(dataModelerWBContext).setActiveContext(any());
        verify(dataModelerFocusEvent).fire(any());
    }
}

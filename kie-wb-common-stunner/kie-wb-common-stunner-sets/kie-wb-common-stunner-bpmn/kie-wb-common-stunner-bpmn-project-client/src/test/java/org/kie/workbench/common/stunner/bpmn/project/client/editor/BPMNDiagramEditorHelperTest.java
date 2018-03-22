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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramEditorService;
import org.kie.workbench.common.stunner.bpmn.project.service.MigrationResult;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorHelperTest {

    private static final String FILE_NAME = "MyProcess.bpmn";

    private static final String COMMIT_MESSAGE = "COMMIT_MESSAGE";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private PlaceManager placeManager;

    @Mock
    private BPMNDiagramEditorService editorService;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Mock
    private Path sourcePath;

    @Mock
    private PlaceRequest sourcePlace;

    private BPMNDiagramEditorHelper editorHelper;

    @Before
    public void setUp() {
        CallerMock<BPMNDiagramEditorService> editorServiceCaller = new CallerMock<>(editorService);
        editorHelper = spy(new BPMNDiagramEditorHelper(placeManager,
                                                       editorServiceCaller,
                                                       translationService,
                                                       errorPopupPresenter));
    }

    @Test
    public void testOnMigrateDiagramSuccessful() {
        prepareOnMigrate();
        final MigrationResult migrationResult = new MigrationResult(mock(Path.class));
        final PlaceRequest targetPlace = mock(PlaceRequest.class);
        doReturn(targetPlace).when(editorHelper).createTargetPlace(migrationResult.getPath());
        when(editorService.migrateDiagram(sourcePath,
                                          "MyProcess",
                                          ".bpmn2",
                                          COMMIT_MESSAGE)).thenReturn(migrationResult);
        editorHelper.onMigrateDiagram(new BPMNMigrateDiagramEvent(sourcePath,
                                                                  sourcePlace));
        verify(placeManager,
               times(1)).forceClosePlace(sourcePlace);
        verify(placeManager,
               times(1)).goTo(targetPlace);
    }

    private void prepareOnMigrate() {
        when(sourcePath.getFileName()).thenReturn(FILE_NAME);
        when(translationService.getValue(BPMNClientConstants.EditorMigrateCommitMessage,
                                         FILE_NAME)).thenReturn(COMMIT_MESSAGE);
    }

    @Test
    public void testOnMigrateDiagramFailed() {
        prepareOnMigrate();
        BPMNDiagramEditorService.ServiceError someError = BPMNDiagramEditorService.ServiceError.MIGRATION_ERROR_PROCESS_ALREADY_EXIST;
        final MigrationResult migrationResult = new MigrationResult(sourcePath,
                                                                    someError);
        when(translationService.getValue(BPMNClientConstants.EditorMigrateErrorProcessAlreadyExists,
                                         FILE_NAME)).thenReturn(ERROR_MESSAGE);

        when(editorService.migrateDiagram(sourcePath,
                                          "MyProcess",
                                          ".bpmn2",
                                          COMMIT_MESSAGE)).thenReturn(migrationResult);
        editorHelper.onMigrateDiagram(new BPMNMigrateDiagramEvent(sourcePath,
                                                                  sourcePlace));
        verify(placeManager,
               times(1)).forceClosePlace(sourcePlace);
        verify(placeManager,
               never()).goTo(any(PlaceRequest.class));
        verify(errorPopupPresenter,
               times(1)).showMessage(ERROR_MESSAGE);
    }
}

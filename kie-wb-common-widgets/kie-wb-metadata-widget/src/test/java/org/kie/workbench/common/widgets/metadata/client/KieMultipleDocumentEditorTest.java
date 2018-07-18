/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({BasicFileMenuBuilderImpl.class})
public class KieMultipleDocumentEditorTest
        extends KieMultipleDocumentEditorTestBase {

    @Mock
    private User user;

    @Test
    public void testSetupMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        editor.setupMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addRename(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addDelete(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addValidate(any(Command.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(downloadMenuItemButton);
        verify(fileMenuBuilder, times(3)).addNewTopLevelMenu(any(MenuItem.class));
    }

    @Test
    public void testSetupMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        editor.setupMenuBar();

        verify(fileMenuBuilder, never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder, never()).addCopy(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder, never()).addRename(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder, never()).addDelete(any(BasicFileMenuBuilder.PathProvider.class), eq(assetUpdateValidator));
        verify(fileMenuBuilder).addValidate(any(Command.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(downloadMenuItemButton);
        verify(fileMenuBuilder, times(3)).addNewTopLevelMenu(any(MenuItem.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullDocument() {
        registerDocument(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument(document);

        verify(path,
               times(1)).onRename(any(Command.class));
        verify(path,
               times(1)).onConcurrentRename(any(ParameterizedCommand.class));
        verify(path,
               times(1)).onDelete(any(Command.class));
        verify(path,
               times(1)).onConcurrentDelete(any(ParameterizedCommand.class));
        verify(path,
               times(1)).onConcurrentUpdate(any(ParameterizedCommand.class));
        verify(registeredDocumentsMenuBuilder,
               times(1)).registerDocument(document);
    }

    @Test
    public void testRegisterDocumentAlreadyRegistered() {
        final TestDocument document = createTestDocument();
        registerDocument(document);
        registerDocument(document);

        assertEquals(1,
                     editor.documents.size());
    }

    @Test
    public void testRenameCommand() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument(document);

        final ArgumentCaptor<Command> renameCommandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(path,
               times(1)).onRename(renameCommandCaptor.capture());
        final Command renameCommand = renameCommandCaptor.getValue();
        assertNotNull(renameCommand);
        renameCommand.execute();

        verify(editorView,
               times(2)).refreshTitle(any(String.class));
        verify(editorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(editor,
               times(2)).getDocumentTitle(eq(document));
        verify(editor,
               times(1)).refreshDocument(eq(document));
        verify(changeTitleEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentRenameCommandIgnore() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentRenameCommand = editor.getConcurrentRenameOnIgnoreCommand();

        editor.setupMenuBar();
        registerDocument(document);

        final ArgumentCaptor<ParameterizedCommand> renameCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(path,
               times(1)).onConcurrentRename(renameCommandCaptor.capture());
        final ParameterizedCommand renameCommand = renameCommandCaptor.getValue();
        assertNotNull(renameCommand);

        final ObservablePath.OnConcurrentRenameEvent info = mock(ObservablePath.OnConcurrentRenameEvent.class);
        renameCommand.execute(info);

        verify(editor,
               times(1)).enableMenus(eq(false));
        verify(editor,
               times(4)).enableMenuItem(eq(false),
                                        any(MenuItems.class));
        verify(saveMenuItem,
               times(1)).setEnabled(eq(false));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentRenameCommandReopen() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentRenameCommand = editor.getConcurrentRenameOnReopenCommand(document);

        editor.setupMenuBar();
        registerDocument(document);

        final ArgumentCaptor<ParameterizedCommand> renameCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(path,
               times(1)).onConcurrentRename(renameCommandCaptor.capture());
        final ParameterizedCommand renameCommand = renameCommandCaptor.getValue();
        assertNotNull(renameCommand);

        final ObservablePath.OnConcurrentRenameEvent info = mock(ObservablePath.OnConcurrentRenameEvent.class);
        renameCommand.execute(info);

        verify(document,
               times(1)).setConcurrentUpdateSessionInfo(eq(null));
        verify(editorView,
               times(2)).refreshTitle(any(String.class));
        verify(editorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(editor,
               times(2)).getDocumentTitle(eq(document));
        verify(editor,
               times(1)).refreshDocument(eq(document));
        verify(changeTitleEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void testDeleteCommand() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        editor.setupMenuBar();
        registerDocument(document);

        final ArgumentCaptor<Command> deleteCommandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(path,
               times(1)).onDelete(deleteCommandCaptor.capture());
        final Command deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull(deleteCommand);
        deleteCommand.execute();

        verify(editor,
               times(1)).enableMenus(eq(false));
        verify(editor,
               times(4)).enableMenuItem(eq(false),
                                        any(MenuItems.class));
        verify(saveMenuItem,
               times(1)).setEnabled(eq(false));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(false));
        verify(editor,
               times(1)).removeDocument(eq(document));
        verify(registeredDocumentsMenuBuilder,
               times(1)).deregisterDocument(document);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentDeleteCommandIgnore() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentDeleteCommand = editor.getConcurrentDeleteOnIgnoreCommand();

        editor.setupMenuBar();
        registerDocument(document);

        final ArgumentCaptor<ParameterizedCommand> deleteCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(path,
               times(1)).onConcurrentDelete(deleteCommandCaptor.capture());
        final ParameterizedCommand deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull(deleteCommand);

        final ObservablePath.OnConcurrentDelete info = mock(ObservablePath.OnConcurrentDelete.class);
        deleteCommand.execute(info);

        verify(editor,
               times(1)).enableMenus(eq(false));
        verify(editor,
               times(4)).enableMenuItem(eq(false),
                                        any(MenuItems.class));
        verify(saveMenuItem,
               times(1)).setEnabled(eq(false));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentDeleteCommandClose() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        concurrentDeleteCommand = editor.getConcurrentDeleteOnClose(document);

        editor.setupMenuBar();
        registerDocument(document);

        final ArgumentCaptor<ParameterizedCommand> deleteCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(path,
               times(1)).onConcurrentDelete(deleteCommandCaptor.capture());
        final ParameterizedCommand deleteCommand = deleteCommandCaptor.getValue();
        assertNotNull(deleteCommand);

        final ObservablePath.OnConcurrentDelete info = mock(ObservablePath.OnConcurrentDelete.class);
        deleteCommand.execute(info);

        verify(editor,
               times(1)).enableMenus(eq(false));
        verify(editor,
               times(4)).enableMenuItem(eq(false),
                                        any(MenuItems.class));
        verify(saveMenuItem,
               times(1)).setEnabled(eq(false));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(false));
        verify(editor,
               times(1)).removeDocument(eq(document));
        verify(registeredDocumentsMenuBuilder,
               times(1)).deregisterDocument(document);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentUpdate() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument(document);

        final ArgumentCaptor<ParameterizedCommand> updateCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        verify(path,
               times(1)).onConcurrentUpdate(updateCommandCaptor.capture());
        final ParameterizedCommand updateCommand = updateCommandCaptor.getValue();
        assertNotNull(updateCommand);

        final ObservablePath.OnConcurrentUpdateEvent info = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        updateCommand.execute(info);

        verify(document,
               times(1)).setConcurrentUpdateSessionInfo(eq(info));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeregisterNullDocument() {
        editor.deregisterDocument(null);
    }

    @Test()
    public void testDeregisterDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument(document);
        editor.deregisterDocument(document);

        verify(path,
               times(1)).dispose();
        verify(registeredDocumentsMenuBuilder,
               times(1)).deregisterDocument(document);
    }

    @Test
    public void testDeregisterDocumentNotRegistered() {
        final TestDocument document = createTestDocument();

        editor.deregisterDocument(document);

        assertEquals(0,
                     editor.documents.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullDocument() {
        editor.activateDocument(null,
                                mock(Overview.class),
                                mock(AsyncPackageDataModelOracle.class),
                                mock(Imports.class),
                                false);

        verify(editor,
               never()).initialiseVersionManager(any(TestDocument.class));
        verify(editor,
               never()).initialiseKieEditorTabs(any(TestDocument.class),
                                                any(Overview.class),
                                                any(AsyncPackageDataModelOracle.class),
                                                any(Imports.class),
                                                any(Boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullOverview() {
        editor.activateDocument(createTestDocument(),
                                null,
                                mock(AsyncPackageDataModelOracle.class),
                                mock(Imports.class),
                                false);

        verify(editor,
               never()).initialiseVersionManager(any(TestDocument.class));
        verify(editor,
               never()).initialiseKieEditorTabs(any(TestDocument.class),
                                                any(Overview.class),
                                                any(AsyncPackageDataModelOracle.class),
                                                any(Imports.class),
                                                any(Boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullAsyncPackageDataModelOracle() {
        editor.activateDocument(createTestDocument(),
                                mock(Overview.class),
                                null,
                                mock(Imports.class),
                                false);

        verify(editor,
               never()).initialiseVersionManager(any(TestDocument.class));
        verify(editor,
               never()).initialiseKieEditorTabs(any(TestDocument.class),
                                                any(Overview.class),
                                                any(AsyncPackageDataModelOracle.class),
                                                any(Imports.class),
                                                any(Boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocument_NullImports() {
        editor.activateDocument(createTestDocument(),
                                mock(Overview.class),
                                mock(AsyncPackageDataModelOracle.class),
                                null,
                                false);

        verify(editor,
               never()).initialiseVersionManager(any(TestDocument.class));
        verify(editor,
               never()).initialiseKieEditorTabs(any(TestDocument.class),
                                                any(Overview.class),
                                                any(AsyncPackageDataModelOracle.class),
                                                any(Imports.class),
                                                any(Boolean.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testActivateDocument() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        final Overview overview = mock(Overview.class);
        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        final Imports imports = mock(Imports.class);
        final boolean isReadOnly = false;

        registerDocument(document);
        editor.activateDocument(document,
                                overview,
                                dmo,
                                imports,
                                isReadOnly);

        assertEquals(document,
                     editor.getActiveDocument());
        verify(versionRecordManager,
               times(1)).init(eq(null),
                              eq(path),
                              any(Callback.class));
        verify(kieEditorWrapperView,
               times(1)).clear();
        verify(kieEditorWrapperView,
               times(1)).addMainEditorPage(eq(editorView));
        verify(kieEditorWrapperView,
               times(1)).addOverviewPage(eq(overviewWidget),
                                         any(com.google.gwt.user.client.Command.class));
        verify(kieEditorWrapperView,
               times(1)).addSourcePage(eq(editor.sourceWidget));
        verify(kieEditorWrapperView,
               times(1)).addImportsTab(eq(importsWidget));
        verify(overviewWidget,
               times(1)).setContent(eq(overview),
                                    eq(path));
        verify(importsWidget,
               times(1)).setContent(eq(dmo),
                                    eq(imports),
                                    eq(isReadOnly));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateDocumentNotRegistered() {
        final TestDocument document = createTestDocument();

        activateDocument(document);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVersionRecordManagerSelectionChange() {
        final TestDocument document = createTestDocument();
        final ObservablePath path = document.getLatestPath();

        registerDocument(document);
        activateDocument(document);

        final ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(versionRecordManager,
               times(1)).init(eq(null),
                              eq(path),
                              callbackArgumentCaptor.capture());

        final Callback<VersionRecord> callback = callbackArgumentCaptor.getValue();
        assertNotNull(callback);

        final VersionRecord versionRecord = mock(VersionRecord.class);
        final ObservablePath newPath = mock(ObservablePath.class);
        when(versionRecord.id()).thenReturn("id");
        when(versionRecordManager.getCurrentPath()).thenReturn(newPath);
        when(versionRecordManager.isLatest(versionRecord)).thenReturn(false);
        callback.callback(versionRecord);

        verify(versionRecordManager,
               times(1)).setVersion(eq("id"));
        verify(document,
               times(1)).setVersion(eq("id"));
        verify(document,
               times(1)).setCurrentPath(eq(newPath));
        verify(document,
               times(1)).setReadOnly(eq(true));
        verify(editor,
               times(1)).refreshDocument(document);
    }

    @Test
    public void verifyOverviewPageUsesVersionManagerVersion() {
        final TestDocument document = createTestDocument();

        registerDocument(document);
        activateDocument(document);

        final ArgumentCaptor<com.google.gwt.user.client.Command> onFocusCommandCaptor = ArgumentCaptor.forClass(com.google.gwt.user.client.Command.class);
        verify(kieEditorWrapperView,
               times(1)).addOverviewPage(any(OverviewWidgetPresenter.class),
                                         onFocusCommandCaptor.capture());

        final com.google.gwt.user.client.Command onFocusCommand = onFocusCommandCaptor.getValue();
        assertNotNull(onFocusCommand);
        onFocusCommand.execute();

        verify(versionRecordManager,
               times(1)).getVersion();
    }

    @Test
    public void testGetWidget() {
        editor.getWidget();
        verify(kieEditorWrapperView,
               times(1)).asWidget();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTitleWidgetNullDocument() {
        editor.getTitleWidget(null);

        verify(editorView,
               never()).refreshTitle(any(String.class));
        verify(editorView,
               never()).getTitleWidget();
    }

    @Test
    public void testGetTitleWidget() {
        final TestDocument document = createTestDocument();

        editor.getTitleWidget(document);

        verify(editorView,
               times(1)).refreshTitle(any(String.class));
        verify(editorView,
               times(1)).getTitleWidget();
    }

    @Test
    public void testMayCloseNoChange() {
        final TestDocument document = createTestDocument();

        registerDocument(document);
        activateDocument(document);

        editor.mayClose(null,
                        null);
        verify(editorView,
               never()).confirmClose();
    }

    @Test
    public void testOnClose() {
        final TestDocument document = createTestDocument();

        registerDocument(document);

        editor.onClose();

        verify(versionRecordManager,
               times(1)).clear();
        verify(registeredDocumentsMenuBuilder,
               times(1)).deregisterDocument(eq(document));
        verify(document.getCurrentPath(),
               times(1)).dispose();
        assertNull(editor.getActiveDocument());
    }

    @Test
    public void testUpdateSource() {
        editor.updateSource("source");

        verify(editor.sourceWidget,
               times(1)).setContent(eq("source"));
    }

    @Test
    public void testOnSourceTabSelected() {
        final TestDocument document = createTestDocument();

        registerDocument(document);
        activateDocument(document);

        editor.onSourceTabSelected();

        verify(editor,
               times(1)).onSourceTabSelected(eq(document));
    }

    @Test
    public void testGetSaveMenuItem() {
        editor.getSaveMenuItem();
        editor.getSaveMenuItem();

        verify(versionRecordManager,
               times(1)).newSaveMenuItem(any(Command.class));
    }

    @Test
    public void testSave() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        final TestDocument document = createTestDocument();
        registerDocument(document);
        activateDocument(document);

        editor.getSaveMenuItem();

        final ArgumentCaptor<Command> saveCommandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(versionRecordManager,
               times(1)).newSaveMenuItem(saveCommandCaptor.capture());

        final Command saveCommand = saveCommandCaptor.getValue();
        assertNotNull(saveCommand);
        saveCommand.execute();

        verify(editorView,
               times(1)).showSaving();
        verify(editor,
               times(1)).doSave(eq(document));
        verify(editor,
               times(1)).doSaveCheckForAndHandleConcurrentUpdate(eq(document));
        verify(editor,
               times(1)).onSave(eq(document),
                                eq("commit"));
        verify(document,
               times(1)).setConcurrentUpdateSessionInfo(eq(null));
    }

    @Test
    public void testSaveSuccessCallback() {
        final TestDocument document = createTestDocument();
        final int currentHashCode = document.hashCode();
        final Path documentPath = document.getCurrentPath();

        final RemoteCallback<Path> callback = editor.getSaveSuccessCallback(document,
                                                                            currentHashCode);

        callback.callback(documentPath);

        verify(editorView).hideBusyIndicator();
        verify(versionRecordManager).reloadVersions(eq(documentPath));
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(document).setOriginalHashCode(eq(currentHashCode));
        verify(overviewWidget).resetDirty();
    }

    @Test
    public void testDoSaveCheckForAndHandleConcurrentUpdate_NoConcurrentUpdate() {
        final TestDocument document = createTestDocument();

        editor.doSaveCheckForAndHandleConcurrentUpdate(document);

        verify(editor,
               times(1)).doSave(eq(document));
    }

    @Test
    public void testDoSaveCheckForAndHandleConcurrentUpdate_ConcurrentUpdate() {
        final TestDocument document = createTestDocument();
        when(document.getConcurrentUpdateSessionInfo()).thenReturn(mock(ObservablePath.OnConcurrentUpdateEvent.class));
        doNothing().when(editor).showConcurrentUpdatePopup(any(TestDocument.class));

        editor.doSaveCheckForAndHandleConcurrentUpdate(document);

        verify(editor,
               times(1)).showConcurrentUpdatePopup(eq(document));
        verify(editor,
               never()).doSave(eq(document));
    }

    @Test
    public void testGetVersionManagerMenuItem() {
        editor.getVersionManagerMenuItem();
        editor.getVersionManagerMenuItem();

        verify(versionRecordManager,
               times(1)).buildMenu();
    }

    @Test
    public void testOnRepositoryRemoved() {
        final Repository repository = mock(Repository.class);
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                                                       repository,
                                                                                                       mock(Branch.class),
                                                                                                       mock(Module.class))));

        editor.setupMenuBar();

        editor.onRepositoryRemoved(new RepositoryRemovedEvent(repository));

        verify(editor,
               times(1)).enableMenus(eq(false));
        verify(editor,
               times(4)).enableMenuItem(eq(false),
                                        any(MenuItems.class));
        verify(saveMenuItem,
               times(1)).setEnabled(eq(false));
        verify(versionManagerMenuItem,
               times(1)).setEnabled(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_NoDocuments() {
        //Mock no other documents being available.
        doAnswer((invocation) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[0];
            callback.callback(Collections.emptyList());
            return null;
        }).when(editor).getAvailableDocumentPaths(any(Callback.class));

        editor.openDocumentInEditor();

        verify(kieEditorWrapperView,
               times(1)).showNoAdditionalDocuments();
        verify(kieEditorWrapperView,
               never()).showAdditionalDocuments(any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_OneDocumentAlreadyRegistered() {
        //Mock one document being available, but it's the same as already registered.
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        registerDocument(document);

        doAnswer((invocation) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[0];
            callback.callback(new ArrayList<Path>() {{
                add(currentPath);
            }});
            return null;
        }).when(editor).getAvailableDocumentPaths(any(Callback.class));

        editor.openDocumentInEditor();

        verify(kieEditorWrapperView,
               times(1)).showNoAdditionalDocuments();
        verify(kieEditorWrapperView,
               never()).showAdditionalDocuments(any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenDocumentInEditor_OneDocumentNotAlreadyRegistered() {
        //Mock two documents being available, one is already registered; the other is not.
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        registerDocument(document);

        final Path newDocumentPath = mock(Path.class);

        doAnswer((invocation) -> {
            final Callback<List<Path>> callback = (Callback) invocation.getArguments()[0];
            callback.callback(new ArrayList<Path>() {{
                add(currentPath);
                add(newDocumentPath);
            }});
            return null;
        }).when(editor).getAvailableDocumentPaths(any(Callback.class));

        editor.openDocumentInEditor();

        final ArgumentCaptor<List> pathsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(kieEditorWrapperView,
               times(1)).showAdditionalDocuments(pathsArgumentCaptor.capture());

        final List<Path> paths = pathsArgumentCaptor.getValue();
        assertNotNull(paths);
        assertEquals(1,
                     paths.size());
        assertEquals(newDocumentPath,
                     paths.get(0));
    }
}

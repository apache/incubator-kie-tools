/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DOWNLOAD;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.HISTORY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.VALIDATE;

@RunWith(GwtMockitoTestRunner.class)
public class BaseEditorTest {

    private String fakeContent = "fakeContent";

    @Mock
    private VersionRecordManager versionRecordManager;

    @Mock
    private BaseEditorView baseView;

    @Mock
    private BasicFileMenuBuilder menuBuilder;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;

    @Mock
    private DownloadMenuItemBuilder downloadMenuItem;

    private SaveAndRenameCommandBuilder<String, DefaultMetadata> builder = spy(makeBuilder());

    private Promises promises;

    @InjectMocks
    private BaseEditor<String, DefaultMetadata> editor = spy(makeBaseEditor());

    @Test
    public void testSaveAndRename() {
        final Supplier pathSupplier = mock(Supplier.class);
        final Validator renameValidator = mock(Validator.class);
        final Supplier saveValidator = mock(Supplier.class);
        final Caller supportsSaveAndRename = mock(Caller.class);
        final Supplier metadataSupplier = mock(Supplier.class);
        final Supplier contentSupplier = mock(Supplier.class);
        final Supplier isDirtySupplier = mock(Supplier.class);
        final ParameterizedCommand parameterizedCommand = mock(ParameterizedCommand.class);
        final Command command = mock(Command.class);
        final Command beforeSaveCommand = mock(Command.class);


        doReturn(pathSupplier).when(editor).getPathSupplier();
        doReturn(renameValidator).when(editor).getRenameValidator();
        doReturn(saveValidator).when(editor).getSaveValidator();
        doReturn(supportsSaveAndRename).when(editor).getSaveAndRenameServiceCaller();
        doReturn(metadataSupplier).when(editor).getMetadataSupplier();
        doReturn(contentSupplier).when(editor).getContentSupplier();
        doReturn(isDirtySupplier).when(editor).isDirtySupplier();
        doReturn(parameterizedCommand).when(editor).onSuccess();
        doReturn(beforeSaveCommand).when(editor).getBeforeSaveAndRenameCommand();
        doReturn(command).when(builder).build();

        final Command saveAndRenameCommand = editor.getSaveAndRename();

        assertEquals(command, saveAndRenameCommand);

        verify(builder).addPathSupplier(pathSupplier);
        verify(builder).addValidator(renameValidator);
        verify(builder).addValidator(saveValidator);
        verify(builder).addRenameService(supportsSaveAndRename);
        verify(builder).addMetadataSupplier(metadataSupplier);
        verify(builder).addContentSupplier(contentSupplier);
        verify(builder).addIsDirtySupplier(isDirtySupplier);
        verify(builder).addSuccessCallback(parameterizedCommand);
        verify(builder).addBeforeSaveAndRenameCommand(beforeSaveCommand);
    }

    @Test
    public void testGetPathSupplier() {

        final ObservablePath observablePath = mock(ObservablePath.class);

        doReturn(observablePath).when(versionRecordManager).getPathToLatest();

        final Supplier<Path> pathSupplier = editor.getPathSupplier();

        assertEquals(observablePath, pathSupplier.get());
    }

    @Test
    public void testGetContentSupplier() {

        final Supplier<String> contentSupplier = editor.getContentSupplier();
        final String content = contentSupplier.get();

        assertEquals(fakeContent, content);
    }

    @Test
    public void testGetMetadataSupplier() {
        assertNull(editor.getMetadataSupplier().get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertNull(editor.getSaveAndRenameServiceCaller());
    }

    @Test
    public void testIsContentDirtyWhenEditorIsDirty() {

        doReturn(true).when(editor).isDirty(fakeContent.hashCode());

        assertTrue(editor.isContentDirty());
    }

    @Test
    public void testIsContentDirtyWhenEditorIsNotDirty() {

        doReturn(false).when(editor).isDirty(fakeContent.hashCode());

        assertFalse(editor.isContentDirty());
    }

    @Test
    public void testIsContentDirtyWhenGetContentRaisesAnException() {

        doReturn(null).when(editor).getContentSupplier();

        assertFalse(editor.isContentDirty());
    }

    @Test
    public void testIsMetadataDirtyWhenMetadataIsDirty() {

        final DefaultMetadata metadata = fakeMetadata(123);
        final Supplier<DefaultMetadata> metadataSupplier = () -> metadata;

        doReturn(metadataSupplier).when(editor).getMetadataSupplier();

        editor.metadataOriginalHash = 456;

        assertTrue(editor.isMetadataDirty());
    }

    @Test
    public void testIsMetadataDirtyWhenMetadataIsNotDirty() {

        final DefaultMetadata metadata = fakeMetadata(123);
        final Supplier<DefaultMetadata> metadataSupplier = () -> metadata;

        doReturn(metadataSupplier).when(editor).getMetadataSupplier();

        editor.metadataOriginalHash = 123;

        assertFalse(editor.isMetadataDirty());
    }

    @Test
    public void testIsMetadataDirtyWhenMetadataIsNull() {
        assertFalse(editor.isMetadataDirty());
    }

    @Test
    public void testIsDirtySupplierWhenContentIsDirty() {

        doReturn(true).when(editor).isContentDirty();
        doReturn(false).when(editor).isMetadataDirty();

        assertTrue(editor.isDirtySupplier().get());
    }

    @Test
    public void testIsDirtySupplierWhenMetadataIsDirty() {

        doReturn(false).when(editor).isContentDirty();
        doReturn(true).when(editor).isMetadataDirty();

        assertTrue(editor.isDirtySupplier().get());
    }

    @Test
    public void testIsDirtySupplierWhenContentAndMetdataAreDirty() {

        doReturn(true).when(editor).isContentDirty();
        doReturn(true).when(editor).isMetadataDirty();

        assertTrue(editor.isDirtySupplier().get());
    }

    @Test
    public void testIsDirtySupplierWhenContentAndMetdataAreNotDirty() {

        doReturn(false).when(editor).isContentDirty();
        doReturn(false).when(editor).isMetadataDirty();

        assertFalse(editor.isDirtySupplier().get());
    }

    @Test
    public void testGetSaveValidatorWhenItIsReadOnlyAndItIsCurrentLatest() {

        editor.isReadOnly = true;
        doReturn(true).when(versionRecordManager).isCurrentLatest();

        final boolean success = editor.getSaveValidator().get();

        verify(baseView).alertReadOnly();
        assertFalse(success);
    }

    @Test
    public void testGetSaveValidatorWhenItIsReadOnlyAndItIsNotCurrentLatest() {

        editor.isReadOnly = true;
        doReturn(false).when(versionRecordManager).isCurrentLatest();

        final boolean success = editor.getSaveValidator().get();

        verify(versionRecordManager).restoreToCurrentVersion(true);
        assertFalse(success);
    }

    @Test
    public void testGetSaveValidatorWhenConcurrentUpdateSessionInfoIsNotNull() {

        editor.isReadOnly = false;
        editor.concurrentUpdateSessionInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doNothing().when(editor).showConcurrentUpdatePopup();

        final boolean success = editor.getSaveValidator().get();

        verify(editor).showConcurrentUpdatePopup();
        assertFalse(success);
    }

    @Test
    public void testGetSaveValidatorWhenConcurrentUpdateSessionInfoIsNull() {

        editor.isReadOnly = false;
        editor.concurrentUpdateSessionInfo = null;

        final boolean success = editor.getSaveValidator().get();

        assertTrue(success);
    }

    @Test
    public void testOnSuccess() {

        final Path path = mock(Path.class);
        final String content = "content";
        final int contentHash = content.hashCode();
        final int metadataHash = 456;
        final Supplier<String> contentSupplier = () -> content;
        final Supplier<DefaultMetadata> metadataSupplier = () -> fakeMetadata(metadataHash);

        doReturn(contentSupplier).when(editor).getContentSupplier();
        doReturn(metadataSupplier).when(editor).getMetadataSupplier();

        editor.onSuccess().execute(path);

        verify(editor).setOriginalHash(contentHash);
        verify(editor).setMetadataOriginalHash(metadataHash);
    }

    @Test
    public void testOnSuccessShouldNotCallMetadataHashIfNotAvailable() {

        final Path path = mock(Path.class);
        final String content = "dora";
        final int contentHash = content.hashCode();
        final int metadataHash = 456;
        final Supplier<String> contentSupplier = () -> content;
        final Supplier<DefaultMetadata> metadataSupplier = () -> null;

        doReturn(contentSupplier).when(editor).getContentSupplier();
        doReturn(metadataSupplier).when(editor).getMetadataSupplier();

        editor.onSuccess().execute(path);

        verify(editor).setOriginalHash(contentHash);
        verify(editor, never()).setMetadataOriginalHash(metadataHash);
    }

    @Test
    public void testMakeMenuBarWhenItContainsAllMenuItems() {

        final ObservablePath path = mock(ObservablePath.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Command onValidate = mock(Command.class);
        final Command onSave = mock(Command.class);
        final Command saveAndRename = mock(Command.class);
        final Validator validator = mock(Validator.class);
        final Validator copyValidator = mock(Validator.class);
        final Caller copyServiceCaller = mock(Caller.class);
        final Caller deleteServiceCaller = mock(Caller.class);
        final MenuItem downloadMenuItemButton = mock(MenuItem.class);

        when(downloadMenuItem.build(any())).thenReturn(downloadMenuItemButton);

        editor.menuItems = new HashSet<>(Arrays.asList(SAVE, COPY, RENAME, DELETE, VALIDATE, HISTORY, DOWNLOAD));

        doReturn(path).when(versionRecordManager).getCurrentPath();
        doReturn(menuItem).when(versionRecordManager).buildMenu();
        doReturn(onValidate).when(editor).getValidateCommand();
        doReturn(onSave).when(editor).getOnSave();
        doReturn(saveAndRename).when(editor).getSaveAndRename();
        doReturn(validator).when(editor).getCopyValidator();
        doReturn(copyValidator).when(editor).getCopyValidator();
        doReturn(copyServiceCaller).when(editor).getCopyServiceCaller();
        doReturn(deleteServiceCaller).when(editor).getDeleteServiceCaller();

        editor.makeMenuBar();

        verify(menuBuilder).addSave(onSave);
        verify(menuBuilder).addCopy(path, copyValidator, copyServiceCaller);
        verify(menuBuilder).addRename(saveAndRename);
        verify(menuBuilder).addDelete(path, deleteServiceCaller);
        verify(menuBuilder).addValidate(onValidate);
        verify(menuBuilder).addNewTopLevelMenu(menuItem);
        verify(menuBuilder).addNewTopLevelMenu(downloadMenuItemButton);
    }

    @Test
    public void testMakeMenuBarWhenItDoesNotContainAllMenuItems() {

        editor.menuItems = new HashSet<>();

        editor.makeMenuBar();

        verify(menuBuilder, never()).addSave(any(Command.class));
        verify(menuBuilder, never()).addCopy(any(ObservablePath.class), any(Validator.class), any(Caller.class));
        verify(menuBuilder, never()).addRename(any());
        verify(menuBuilder, never()).addDelete(any(ObservablePath.class), any(Caller.class));
        verify(menuBuilder, never()).addValidate(any());
        verify(menuBuilder, never()).addNewTopLevelMenu(any());
    }

    @Test
    public void testReloadWithObservablePath() {

        final ObservablePath path = mock(ObservablePath.class);

        doNothing().when(editor).refreshTitle(path);
        doNothing().when(editor).showBusyIndicator();
        doNothing().when(editor).loadContent();
        doNothing().when(editor).notifyChangeTitle(path);
        doNothing().when(editor).initVersionRecordManager();

        editor.reload(path);

        verify(editor).refreshTitle(path);
        verify(editor).showBusyIndicator();
        verify(editor).loadContent();
        verify(editor).notifyChangeTitle(path);
        verify(editor).initVersionRecordManager();
    }

    @Test
    public void testRefreshTitleWithObservablePath() {

        final ObservablePath path = mock(ObservablePath.class);
        final String title = "title";

        doReturn(title).when(editor).getTitleText(path);

        editor.refreshTitle(path);

        verify(baseView).refreshTitle(title);
    }

    @Test
    public void testShowBusyIndicator() {

        final String loading = "Loading...";

        doReturn(loading).when(editor).makeLoading();

        editor.showBusyIndicator();

        verify(baseView).showBusyIndicator(loading);
    }

    @Test
    public void testNotifyChangeTitle() {

        final ObservablePath path = mock(ObservablePath.class);
        final ChangeTitleWidgetEvent widgetEvent = mock(ChangeTitleWidgetEvent.class);

        doReturn(widgetEvent).when(editor).makeChangeTitleWidgetEvent(path);

        editor.notifyChangeTitle(path);

        verify(changeTitleNotification).fire(widgetEvent);
    }

    @Test
    public void testMakeChangeTitleWidgetEvent() {

        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final String title = "title";
        final EditorTitle editorTitle = mock(EditorTitle.class);

        doReturn(placeRequest).when(editor).getPlace();
        doReturn(title).when(editor).getTitleText(path);
        doReturn(editorTitle).when(editor).getTitleWidget();

        final ChangeTitleWidgetEvent event = editor.makeChangeTitleWidgetEvent(path);

        assertEquals(placeRequest, event.getPlaceRequest());
        assertEquals(title, event.getTitle());
        assertEquals(editorTitle, event.getTitleDecoration());
    }

    @Test
    public void testInitVersionRecordManager() {

        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        final String version = "version";
        final Callback<VersionRecord> selectionCallback = (v) -> {
        };

        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(placeRequest.getParameter(anyString(), any())).thenReturn(version);
        doReturn(selectionCallback).when(editor).getSelectVersion();
        doReturn(placeRequest).when(editor).getPlace();

        editor.initVersionRecordManager();

        verify(versionRecordManager).init(version, path, selectionCallback);
    }

    @Test
    public void testGetTitleText() {

        final String expectedTitle = "file.drl - DRL";
        final ObservablePath path = mock(ObservablePath.class);
        final ClientResourceType type = mock(ClientResourceType.class);

        doReturn(type).when(editor).getType();
        when(path.getFileName()).thenReturn("file.drl");
        when(type.getDescription()).thenReturn("DRL");

        final String actualTitle = editor.getTitleText(path);

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testGetTitle() {

        final ObservablePath path = mock(ObservablePath.class);
        final EditorTitle expectedTitle = mock(EditorTitle.class);

        doNothing().when(editor).refreshTitle(path);
        doReturn(expectedTitle).when(editor).getTitleWidget();
        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        final IsWidget actualWidget = editor.getTitle();

        verify(editor).refreshTitle(path);
        assertEquals(expectedTitle, actualWidget);
    }

    @Test
    public void testGetCurrentContentHashWhenContentSupplierRaisesAnException() {

        // The `null` content causes an exception in the `getCurrentContentHash` method,
        // since a `NullPointerException` is raised here: `getContentSupplier().get().hashCode()`.
        fakeContent = null;

        final Integer actualHash = editor.getCurrentContentHash();
        final Integer expectedHash = null;

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testGetCurrentContentHash() {

        final Integer actualHash = editor.getCurrentContentHash();
        final Integer expectedHash = fakeContent.hashCode();

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testDisableMenuItem() {

        final Menus menus = mock(Menus.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Map<Object, MenuItem> itemMap = new HashMap<>();

        itemMap.put(SAVE, menuItem);
        when(menus.getItemsMap()).thenReturn(itemMap);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(menus);
            return null;
        }).when(editor).getMenus(any());

        editor.disableMenuItem(SAVE);

        verify(menuItem).setEnabled(false);
    }

    @Test
    public void testEnableMenuItem() {

        final Menus menus = mock(Menus.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Map<Object, MenuItem> itemMap = new HashMap<>();

        itemMap.put(SAVE, menuItem);
        when(menus.getItemsMap()).thenReturn(itemMap);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(menus);
            return null;
        }).when(editor).getMenus(any());

        editor.enableMenuItem(SAVE);

        verify(menuItem).setEnabled(true);
    }

    @Test
    public void testShowConcurrentUpdatePopupTwice() {
        final ConcurrentChangePopup concurrentChangePopup = mock(ConcurrentChangePopup.class);
        doReturn(concurrentChangePopup).when(editor).getConcurrentUpdatePopup();

        editor.showConcurrentUpdatePopup();
        editor.showConcurrentUpdatePopup();

        verify(editor, times(1)).getConcurrentUpdatePopup();
        verify(concurrentChangePopup, times(2)).show();
    }

    private DefaultMetadata fakeMetadata(final int hashCode) {
        return new DefaultMetadata() {
            @Override
            public int hashCode() {
                return hashCode;
            }
        };
    }

    private SaveAndRenameCommandBuilder<String, DefaultMetadata> makeBuilder() {
        return new SaveAndRenameCommandBuilder<>(null, null, null, null);
    }

    private BaseEditor<String, DefaultMetadata> makeBaseEditor() {
        promises = new SyncPromises();
        return new BaseEditor<String, DefaultMetadata>() {
            {
                promises = BaseEditorTest.this.promises;
            }

            @Override
            protected SaveAndRenameCommandBuilder<String, DefaultMetadata> getSaveAndRenameCommandBuilder() {
                return builder;
            }

            @Override
            protected void loadContent() {
            }

            @Override
            protected Supplier<String> getContentSupplier() {
                return () -> fakeContent;
            }
        };
    }
}

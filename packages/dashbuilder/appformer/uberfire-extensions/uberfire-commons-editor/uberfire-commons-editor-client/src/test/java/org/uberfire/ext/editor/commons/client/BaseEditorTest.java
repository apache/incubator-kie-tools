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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class BaseEditorTest {

    private String fakeContent = "fakeContent";

    @Mock
    private BaseEditorView baseView;

    @Mock
    private BasicFileMenuBuilder menuBuilder;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;

    @Mock
    private DownloadMenuItemBuilder downloadMenuItem;


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

        editor.reload(path);

        verify(editor).refreshTitle(path);
        verify(editor).showBusyIndicator();
        verify(editor).loadContent();
        verify(editor).notifyChangeTitle(path);
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

        when(placeRequest.getParameter(anyString(), any())).thenReturn(version);
        doReturn(placeRequest).when(editor).getPlace();

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
            invocationOnMock.getArgument(0, Consumer.class).accept(menus);
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
            invocationOnMock.getArgument(0, Consumer.class).accept(menus);
            return null;
        }).when(editor).getMenus(any());

        editor.enableMenuItem(SAVE);

        verify(menuItem).setEnabled(true);
    }

    private DefaultMetadata fakeMetadata(final int hashCode) {
        return new DefaultMetadata() {
            @Override
            public int hashCode() {
                return hashCode;
            }
        };
    }

    private BaseEditor<String, DefaultMetadata> makeBaseEditor() {
        promises = new SyncPromises();
        return new BaseEditor<String, DefaultMetadata>() {
            {
                promises = BaseEditorTest.this.promises;
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

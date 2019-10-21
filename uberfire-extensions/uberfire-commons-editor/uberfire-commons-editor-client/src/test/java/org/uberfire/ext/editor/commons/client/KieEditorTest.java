/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteIgnoredEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameIgnoredEvent;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieEditorTest {

    private BaseEditor kieEditor;
    private BaseEditorView view;
    private RestoreEvent restoreEvent;
    private ObservablePath observablePath;
    private Promises promises;
    private DeletePopUpPresenter deletePopUpPresenter;

    @Before
    public void setUp() throws Exception {
        view = mock(BaseEditorView.class);
        restoreEvent = mock(RestoreEvent.class);
        promises = new SyncPromises();
        deletePopUpPresenter = mock(DeletePopUpPresenter.class);

        kieEditor = spy(new BaseEditor<String, DefaultMetadata>(view) {

            @Override
            protected void loadContent() {
            }

            @Override
            protected void showVersions() {

            }

            @Override
            protected Promise<Void> makeMenuBar() {
                return promises.resolve();
            }

            @Override
            protected void showConcurrentUpdatePopup() {
                // Overriding for testing.
            }

            @Override
            void disableMenus() {

            }

            @Override
            public void reload() {

            }
        });

        kieEditor.placeManager = mock(PlaceManager.class);
        kieEditor.concurrentRenameIgnoredEvent = spy(new EventMock<>());
        kieEditor.concurrentRenameAcceptedEvent = spy(new EventMock<>());
        kieEditor.concurrentDeleteIgnoredEvent = spy(new EventMock<>());
        kieEditor.concurrentDeleteAcceptedEvent = spy(new EventMock<>());
        kieEditor.versionRecordManager = mock(VersionRecordManager.class);
        kieEditor.notification = new EventMock<>();
        kieEditor.promises = promises;
        kieEditor.deletePopUpPresenter = deletePopUpPresenter;
        observablePath = mock(ObservablePath.class);
        PlaceRequest placeRequest = mock(PlaceRequest.class);
        ClientResourceType resourceType = mock(ClientResourceType.class);
        kieEditor.init(observablePath,
                       placeRequest,
                       resourceType);
    }

    @Test
    public void testLoad() throws Exception {
        verify(kieEditor).loadContent();
    }

    @Test
    public void testSimpleSave() throws Exception {

        kieEditor.onSave();

        verify(kieEditor).save();
    }

    @Test
    public void testComplicatedSave() throws Exception {
        kieEditor.isReadOnly = false;

        when(kieEditor.versionRecordManager.isCurrentLatest()).thenReturn(true);

        kieEditor.onSave();

        verify(kieEditor).save();
    }

    @Test
    public void testSaveReadOnly() throws Exception {

        kieEditor.isReadOnly = true;

        when(kieEditor.versionRecordManager.isCurrentLatest()).thenReturn(true);

        kieEditor.onSave();

        verify(kieEditor,
               never()).save();
        verify(view).alertReadOnly();
    }

    @Test
    public void testRestore() throws Exception {

        kieEditor.isReadOnly = true;

        when(kieEditor.versionRecordManager.isCurrentLatest()).thenReturn(false);

        kieEditor.onSave();

        verify(kieEditor,
               never()).save();
        verify(kieEditor.versionRecordManager).restoreToCurrentVersion(true);
    }

    @Test
    public void testConcurrentSave() throws Exception {
        kieEditor.isReadOnly = false;

        when(kieEditor.versionRecordManager.isCurrentLatest()).thenReturn(true);

        kieEditor.concurrentUpdateSessionInfo = new ObservablePath.OnConcurrentUpdateEvent() {
            @Override
            public Path getPath() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public User getIdentity() {
                return null;
            }
        };

        kieEditor.onSave();

        verify(kieEditor,
               never()).save();
        verify(kieEditor).showConcurrentUpdatePopup();
    }

    // Calling init reloads the latest version of the content. Therefore save
    // shouldn't cause a concurrent modification popup if no update happened
    // after init.
    @Test
    public void testInitResetsConcurrentSessionInfo() throws Exception {
        kieEditor.isReadOnly = false;

        when(kieEditor.versionRecordManager.isCurrentLatest()).thenReturn(true);

        kieEditor.concurrentUpdateSessionInfo = new ObservablePath.OnConcurrentUpdateEvent() {
            @Override
            public Path getPath() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public User getIdentity() {
                return null;
            }
        };

        final Set<MenuItems> menuItems = kieEditor.menuItems;

        kieEditor.init(new ObservablePathImpl(),
                       kieEditor.place,
                       kieEditor.type,
                       menuItems.toArray(new MenuItems[0]));

        kieEditor.onSave();

        verify(kieEditor,
               never()).showConcurrentUpdatePopup();
    }

    @Test
    public void onRestoreShouldInitBaseEditorSuccessfully() throws Exception {
        when(kieEditor.versionRecordManager.getCurrentPath()).thenReturn(observablePath);
        when(restoreEvent.getPath()).thenReturn(observablePath);
        kieEditor.onRestore(restoreEvent);
        verify(kieEditor).onRestore(restoreEvent);
        verify(kieEditor.versionRecordManager).getPathToLatest();
    }

    @Test
    public void onRestoreWithNullCurrentPathShouldNotInitEditor() throws Exception {
        when(kieEditor.versionRecordManager.getCurrentPath()).thenReturn(null);
        kieEditor.onRestore(restoreEvent);
        verify(kieEditor).onRestore(restoreEvent);
        verify(kieEditor.versionRecordManager,
               never()).getPathToLatest();
    }

    @Test
    public void onRestoreWithNullRestoreEventPathShouldNotInitEditor() throws Exception {
        when(restoreEvent.getPath()).thenReturn(null);
        kieEditor.onRestore(restoreEvent);
        verify(kieEditor).onRestore(restoreEvent);
        verify(kieEditor.versionRecordManager,
               never()).getPathToLatest();
    }

    @Test
    public void onRestoreWithNullRestoreEventShouldNotInitEditor() throws Exception {
        kieEditor.onRestore(null);
        when(kieEditor.versionRecordManager.getPathToLatest()).thenReturn(new ObservablePathImpl());
        verify(kieEditor).onRestore(any());
        verify(kieEditor.versionRecordManager,
               never()).getPathToLatest();
    }

    @Test
    public void testOnValidateMethodIsCalled() throws Exception {
        kieEditor.onValidate(mock(Command.class));
        verify(kieEditor).onValidate(any(Command.class));
    }

    @Test
    public void testOnConcurrentRenameIgnoreCommand() {
        final Command onConcurrentRenameIgnoreCommand = kieEditor.onConcurrentRenameIgnoreCommand(observablePath);

        onConcurrentRenameIgnoreCommand.execute();

        verify(kieEditor).disableMenus();
        verify(kieEditor.concurrentRenameIgnoredEvent).fire(eq(new ConcurrentRenameIgnoredEvent(observablePath)));
    }

    @Test
    public void testOnConcurrentRenameAcceptedCommand() {
        final Command onConcurrentRenameCloseCommand = kieEditor.onConcurrentRenameCloseCommand(observablePath);

        onConcurrentRenameCloseCommand.execute();

        verify(kieEditor).reload();
        verify(kieEditor.concurrentRenameAcceptedEvent).fire(eq(new ConcurrentRenameAcceptedEvent(observablePath)));
    }

    @Test
    public void testOnConcurrentDeleteIgnoreCommand() {
        testOnConcurrentDeleteIgnoreCommand(false);
    }

    @Test
    public void testOnConcurrentDeleteIgnoreCommandDeletePopupOpened() {
        testOnConcurrentDeleteIgnoreCommand(true);
    }

    private void testOnConcurrentDeleteIgnoreCommand(final boolean isDeletePopupOpened) {

        when(deletePopUpPresenter.isOpened()).thenReturn(isDeletePopupOpened);

        final Command onConcurrentDeleteIgnoreCommand = kieEditor.onConcurrentDeleteIgnoreCommand(observablePath);

        onConcurrentDeleteIgnoreCommand.execute();

        verify(kieEditor).disableMenus();
        verify(kieEditor.concurrentDeleteIgnoredEvent).fire(eq(new ConcurrentDeleteIgnoredEvent(observablePath)));

        verify(deletePopUpPresenter, isDeletePopupOpened ? times(1) : never()).cancel();
    }

    @Test
    public void testOnConcurrentDeleteAcceptedCommand() {
        testOnConcurrentDeleteAcceptedCommand(false);
    }

    @Test
    public void testOnConcurrentDeleteAcceptedCommandDeletePopupOpened() {
        testOnConcurrentDeleteAcceptedCommand(true);
    }

    private void testOnConcurrentDeleteAcceptedCommand(final boolean isDeletePopupOpened) {

        when(deletePopUpPresenter.isOpened()).thenReturn(isDeletePopupOpened);

        final Command onConcurrentDeleteCloseCommand = kieEditor.onConcurrentDeleteCloseCommand(observablePath);

        onConcurrentDeleteCloseCommand.execute();

        verify(kieEditor.placeManager).closePlace(any(PlaceRequest.class));
        verify(kieEditor.concurrentDeleteAcceptedEvent).fire(eq(new ConcurrentDeleteAcceptedEvent(observablePath)));

        verify(deletePopUpPresenter, isDeletePopupOpened ? times(1) : never()).cancel();
    }
    public static class EventMock<T> extends EventSourceMock<T> {

        @Override
        public void fire(T event) {
            // Overriding for testing.
        }
    }
}
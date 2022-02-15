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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KieEditorTest {

    private BaseEditor kieEditor;
    private BaseEditorView view;
    private ObservablePath observablePath;
    private Promises promises;
    private DeletePopUpPresenter deletePopUpPresenter;

    @Before
    public void setUp() throws Exception {
        view = mock(BaseEditorView.class);
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
            void disableMenus() {

            }

            @Override
            public void reload() {

            }
        });

        kieEditor.placeManager = mock(PlaceManager.class);

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

        kieEditor.onSave();

        verify(kieEditor).save();
    }

    // Calling init reloads the latest version of the content. Therefore save
    // shouldn't cause a concurrent modification popup if no update happened
    // after init.
    @Test
    public void testInitResetsConcurrentSessionInfo() throws Exception {
        kieEditor.isReadOnly = false;

        kieEditor.concurrentUpdateSessionInfo = new ObservablePath.OnConcurrentUpdateEvent() {
            @Override
            public Path getPath() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

        };

        final Set<MenuItems> menuItems = kieEditor.menuItems;

        kieEditor.init(new ObservablePathImpl(),
                       kieEditor.place,
                       kieEditor.type,
                       menuItems.toArray(new MenuItems[0]));

        kieEditor.onSave();

    }

    @Test
    public void testOnValidateMethodIsCalled() throws Exception {
        kieEditor.onValidate(mock(Command.class));
        verify(kieEditor).onValidate(any(Command.class));
    }

    public static class EventMock<T> extends EventSourceMock<T> {

        @Override
        public void fire(T event) {
            // Overriding for testing.
        }
    }
}
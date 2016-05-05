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

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.mvp.PlaceRequest;

public class KieEditorTest {

    private BaseEditor kieEditor;
    private BaseEditorView view;

    @Before
    public void setUp() throws Exception {
        view = mock( BaseEditorView.class );
        kieEditor = spy( new BaseEditor( view ) {

            @Override
            protected void loadContent() {
            }

            @Override
            protected void showVersions() {

            }

            @Override
            protected void makeMenuBar() {

            }

            @Override
            protected void showConcurrentUpdatePopup() {
                // Overriding for testing.
            }
        } );

        kieEditor.versionRecordManager = mock( VersionRecordManager.class );
//        kieEditor.menuBuilder = new FileMenuBuilderMock();

        ObservablePath observablePath = mock( ObservablePath.class );
        PlaceRequest placeRequest = mock( PlaceRequest.class );
        ClientResourceType resourceType = mock( ClientResourceType.class );

        kieEditor.init( observablePath, placeRequest, resourceType );
    }

    @Test
    public void testLoad() throws Exception {
        verify( kieEditor ).loadContent();
    }

    @Test
    public void testSimpleSave() throws Exception {

        kieEditor.onSave();

        verify( kieEditor ).save();
    }

    @Test
    public void testComplicatedSave() throws Exception {
        kieEditor.isReadOnly = false;

        when( kieEditor.versionRecordManager.isCurrentLatest() ).thenReturn( true );

        kieEditor.onSave();

        verify( kieEditor ).save();
    }

    @Test
    public void testSaveReadOnly() throws Exception {

        kieEditor.isReadOnly = true;

        when( kieEditor.versionRecordManager.isCurrentLatest() ).thenReturn( true );

        kieEditor.onSave();

        verify( kieEditor, never() ).save();
        verify( view ).alertReadOnly();
    }

    @Test
    public void testRestore() throws Exception {

        kieEditor.isReadOnly = true;

        when( kieEditor.versionRecordManager.isCurrentLatest() ).thenReturn( false );

        kieEditor.onSave();

        verify( kieEditor, never() ).save();
        verify( kieEditor.versionRecordManager ).restoreToCurrentVersion();

    }

    @Test
    public void testConcurrentSave() throws Exception {
        kieEditor.isReadOnly = false;

        when( kieEditor.versionRecordManager.isCurrentLatest() ).thenReturn( true );

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

        verify( kieEditor, never() ).save();
        verify( kieEditor ).showConcurrentUpdatePopup();
    }
    
    // Calling init reloads the latest version of the content. Therefore save 
    // shouldn't cause a concurrent modification popup if no update happened 
    // after init.
    @Test
    public void testInitResetsConcurrentSessionInfo() throws Exception {
        kieEditor.isReadOnly = false;

        when( kieEditor.versionRecordManager.isCurrentLatest() ).thenReturn( true );

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
        
        kieEditor.init(new ObservablePathImpl(), kieEditor.place, kieEditor.type, kieEditor.menuItems.toArray( new MenuItems[0] ) );

        kieEditor.onSave();

        verify( kieEditor, never() ).showConcurrentUpdatePopup();
    }

}

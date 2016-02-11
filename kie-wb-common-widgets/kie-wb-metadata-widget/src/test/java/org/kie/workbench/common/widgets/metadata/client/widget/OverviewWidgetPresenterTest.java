/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

public class OverviewWidgetPresenterTest {

    private OverviewScreenView.Presenter presenter;
    private OverviewScreenView           view;
    private OverviewWidgetPresenter      editor;

    private Overview overview;

    @Before
    public void setUp() throws Exception {
        ClientTypeRegistry clientTypeRegistry = mock(ClientTypeRegistry.class);
        view = mock(OverviewScreenView.class);

        editor = new OverviewWidgetPresenter(
                clientTypeRegistry,
                view);
        editor.user = new UserImpl("");
        presenter = editor;

        overview = new Overview();
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testAddingDescription() throws Exception {

        Metadata metadata = new Metadata();
        overview.setMetadata(metadata);

        ObservablePath observablePath = mock(ObservablePath.class);
        editor.setContent(overview, observablePath);

        presenter.onDescriptionEdited("Hello");

        assertEquals("Hello", overview.getMetadata().getDescription());
    }

    @Test
    public void testDirty() throws Exception {

        Metadata metadata = new Metadata();
        overview.setMetadata(metadata);

        ObservablePath observablePath = mock(ObservablePath.class);
        editor.setContent(overview, observablePath);

        assertFalse(editor.isDirty());

        presenter.onDescriptionEdited("Hello");

        assertTrue(editor.isDirty());

        editor.resetDirty();

        assertFalse(editor.isDirty());

    }

    @Test
    public void testResetDirtyBeforeInit() throws Exception {
        /**
         * These should not give an exception
         */
        assertFalse(editor.isDirty());

        editor.resetDirty();

        assertFalse(editor.isDirty());
    }
    
    @Test
    public void testLockChangeDoesNotReloadAllMetadata() {
        final Path testPath = PathFactory.newPath( "test", "uri" );
        
        final Metadata metadata = mock(Metadata.class);
        when (metadata.getPath()).thenReturn( testPath );        
        overview.setMetadata(metadata);
        
        editor.setContent( overview, mock(ObservablePath.class) );
        verify(view, times(1)).setMetadata( any(Metadata.class), any(boolean.class) );

        // Verify that we only update the lock status but leave the rest of the metadata unchanged
        final LockInfo lockInfo = new LockInfo(true, "christian", testPath);
        editor.onLockChange(lockInfo );
        verify(view, times(1)).setLockStatus( lockInfo );
        verify(view, times(1)).setMetadata( any(Metadata.class), any(boolean.class) );
    }
}

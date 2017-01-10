/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({BasicFileMenuBuilderImpl.class})
public class KieMultipleDocumentEditorRestoreTest
        extends KieMultipleDocumentEditorTestBase {

    @Test
    public void restoreMakesTheDocumentEditable() throws
                                                  Exception {

        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        registerDocument( document );
        activateDocument( document );

        when( versionRecordManager.getCurrentPath() ).thenReturn( currentPath );

        editor.onRestore( new RestoreEvent( currentPath ) );

        final InOrder inOrder = inOrder( document,
                                         editor );

        // Order is important. Flip these around and document is editable, but the view is not.
        inOrder.verify( document )
                .setReadOnly( false );
        inOrder.verify( editor,
                        times( 1 ) )
                .refreshDocument( eq( document ) );

    }

    @Test
    public void testOnRestore() {
        final TestDocument document = createTestDocument();
        final ObservablePath currentPath = document.getCurrentPath();
        final ObservablePath latestPath = mock( ObservablePath.class );
        registerDocument( document );
        activateDocument( document );

        when( versionRecordManager.getCurrentPath() ).thenReturn( currentPath );
        when( versionRecordManager.getPathToLatest() ).thenReturn( latestPath );

        editor.onRestore( new RestoreEvent( currentPath ) );

        verify( document,
                times( 1 ) ).setVersion( eq( null ) );
        verify( document,
                times( 1 ) ).setLatestPath( latestPath );
        verify( document,
                times( 1 ) ).setCurrentPath( latestPath );
        verify( editor,
                times( 2 ) ).initialiseVersionManager( eq( document ) );
        verify( editor,
                times( 1 ) ).refreshDocument( eq( document ) );
        verify( notificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }
}
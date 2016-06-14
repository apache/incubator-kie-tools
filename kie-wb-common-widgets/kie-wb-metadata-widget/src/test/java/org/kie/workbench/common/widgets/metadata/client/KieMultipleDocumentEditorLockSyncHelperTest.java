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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper.LockSyncMenuStateHelper.Operation;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieMultipleDocumentEditorLockSyncHelperTest {

    @Mock
    private ObservablePath path;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private KieMultipleDocumentEditor<TestDocument> editor;

    @Mock
    private TestDocument document;

    private KieMultipleDocumentEditorLockSyncHelper lockSyncHelper;

    @Before
    public void setup() {
        this.lockSyncHelper = new KieMultipleDocumentEditorLockSyncHelper( editor );
    }

    @Test
    public void testLocked_NullActiveDocument() {
        when( editor.getActiveDocument() ).thenReturn( null );
        when( document.getCurrentPath() ).thenReturn( path );

        final Operation op = lockSyncHelper.enable( mock( Path.class ),
                                                    true,
                                                    false );
        assertEquals( Operation.VETO,
                      op );
    }

    @Test
    public void testLocked_NotActiveDocument() {
        when( editor.getActiveDocument() ).thenReturn( document );
        when( document.getCurrentPath() ).thenReturn( path );

        final Operation op = lockSyncHelper.enable( mock( ObservablePath.class ),
                                                    true,
                                                    false );
        assertEquals( Operation.VETO,
                      op );
    }

    @Test
    public void testLocked_ActiveDocument() {
        when( editor.getActiveDocument() ).thenReturn( document );
        when( document.getCurrentPath() ).thenReturn( path );

        final Operation op = lockSyncHelper.enable( path,
                                                    true,
                                                    false );
        assertEquals( Operation.DISABLE,
                      op );
    }

    @Test
    public void testLockedByCurrentUser_ActiveDocument() {
        when( editor.getActiveDocument() ).thenReturn( document );
        when( document.getCurrentPath() ).thenReturn( path );

        final Operation op = lockSyncHelper.enable( path,
                                                    true,
                                                    true );
        assertEquals( Operation.ENABLE,
                      op );
    }

}

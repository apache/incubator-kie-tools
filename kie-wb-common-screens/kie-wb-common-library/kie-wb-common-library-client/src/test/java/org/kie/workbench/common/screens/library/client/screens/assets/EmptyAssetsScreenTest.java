/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.client.screens.assets;

import com.google.gwt.core.client.Callback;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmptyAssetsScreenTest {

    private EmptyAssetsScreen emptyAssetsScreen;

    @Mock
    private EmptyAssetsScreen.View view;

    @Mock
    private NewFileUploader newFileUploader;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private LibraryPermissions libraryPermissions;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Before
    public void setUp() {

        this.emptyAssetsScreen = spy(new EmptyAssetsScreen(this.view,
                                                           this.newFileUploader,
                                                           this.newResourcePresenter,
                                                           this.libraryPermissions,
                                                           this.libraryPlaces));

        Command command = mock(Command.class);
        doNothing().when(command).execute();
        when(this.newFileUploader.getCommand(any())).thenReturn(command);
    }

    @Test
    public void testInitializeCheckButtonsCanUpdateProject() {
        doReturn(false).when(this.emptyAssetsScreen).canUpdateProject();
        this.emptyAssetsScreen.initialize();
        verify(this.view,
               times(1)).enableAddAssetButton(eq(false));
        verify(this.view,
               times(1)).enableImportButton(eq(false));
    }

    @Test
    public void testInitializeCheckButtonsCanNotUpdateProject() {
        doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
        this.emptyAssetsScreen.initialize();
        verify(this.view,
               times(1)).enableAddAssetButton(eq(true));
        verify(this.view,
               times(1)).enableImportButton(eq(true));
    }

    @Test
    public void testInitializeAcceptContentSuccess() {
        doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
        doAnswer(invocation -> {
            Callback<Boolean, Void> callback = invocation.getArgumentAt(0,
                                                                        Callback.class);
            callback.onSuccess(false);
            return null;
        }).when(this.newFileUploader).acceptContext(any());
        this.emptyAssetsScreen.initialize();
        verify(this.view).enableImportButton(eq(true));
        verify(this.view).enableImportButton(eq(false));
    }

    @Test
    public void testInitializeAcceptContentSuccessWithPermission() {
        doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
        doAnswer(invocation -> {
            Callback<Boolean, Void> callback = invocation.getArgumentAt(0,
                                                                        Callback.class);
            callback.onSuccess(true);
            return null;
        }).when(this.newFileUploader).acceptContext(any());
        this.emptyAssetsScreen.initialize();
        verify(this.view, times(2)).enableImportButton(eq(true));
    }

    @Test
    public void testInitializeAcceptContentSuccessWithoutPermission() {
        doReturn(false).when(this.emptyAssetsScreen).canUpdateProject();
        doAnswer(invocation -> {
            Callback<Boolean, Void> callback = invocation.getArgumentAt(0,
                                                                        Callback.class);
            callback.onSuccess(true);
            return null;
        }).when(this.newFileUploader).acceptContext(any());
        this.emptyAssetsScreen.initialize();
        verify(this.view, times(2)).enableImportButton(eq(false));
    }

    @Test
    public void testInitializeAcceptContentFailure() {
        doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
        doAnswer(invocation -> {
            Callback<Boolean, Void> callback = invocation.getArgumentAt(0,
                                                                        Callback.class);
            callback.onFailure(null);
            return null;
        }).when(this.newFileUploader).acceptContext(any());
        this.emptyAssetsScreen.initialize();
        verify(this.view).enableImportButton(eq(true));
        verify(this.view).enableImportButton(eq(false));
    }

    @Test
    public void testImportAsset() {
        {
            doReturn(false).when(this.emptyAssetsScreen).canUpdateProject();
            this.emptyAssetsScreen.importAsset();
            verify(this.newFileUploader,
                   never()).getCommand(any());
        }

        {
            doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
            this.emptyAssetsScreen.importAsset();
            verify(this.newFileUploader,
                   times(1)).getCommand(any());
        }
    }

    @Test
    public void testAddAsset() {
        {
            doReturn(false).when(this.emptyAssetsScreen).canUpdateProject();
            this.emptyAssetsScreen.addAsset();
            verify(this.libraryPlaces,
                   never()).goToAddAsset();
        }

        {
            doReturn(true).when(this.emptyAssetsScreen).canUpdateProject();
            this.emptyAssetsScreen.addAsset();
            verify(this.libraryPlaces,
                   times(1)).goToAddAsset();
        }
    }
}
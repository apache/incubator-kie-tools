/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.security.DataModelerFeatures;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class SourceEditorReadOnlyTest extends DataModelerScreenPresenterTestBase {

    @Mock
    private User user;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        final boolean loadTypesInfo = true;
        EditorModelContent content = createContent(loadTypesInfo, false);
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(modelerService.loadContent(path, loadTypesInfo)).thenReturn(content);
        when(javaSourceEditor.getContent()).thenReturn(content.getSource());

        //access modification related setup
        when(sessionInfo.getIdentity()).thenReturn(user);
    }

    @Test
    public void testSourceEditorEditable() {
        //authorization manager sets sourceEditionEnabled to true
        when(authorizationManager.authorize(DataModelerFeatures.EDIT_SOURCES, sessionInfo.getIdentity()))
                .thenReturn(true);
        presenter.onStartup(path, placeRequest);
        verify(javaSourceEditor).setReadonly(false);
        presenter.loadContent();
        verify(javaSourceEditor, times(2)).setReadonly(false);
    }

    @Test
    public void testSourceEditorReadOnly() {
        //authorization manager sets sourceEditionEnabled to false
        when(authorizationManager.authorize(DataModelerFeatures.EDIT_SOURCES, sessionInfo.getIdentity()))
                .thenReturn(false);
        presenter.onStartup(path, placeRequest);
        verify(javaSourceEditor).setReadonly(true);
        presenter.loadContent();
        verify(javaSourceEditor, times(2)).setReadonly(true);
    }
}
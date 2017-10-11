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
package org.uberfire.ext.plugin.client.perspective.editor;

import javax.inject.Inject;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.PerspectiveEditorSettings;
import org.uberfire.ext.plugin.client.security.PluginController;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PerspectiveEditorPresenterTest {

    @Mock
    PerspectiveEditorPresenter.View view;

    @Mock
    PluginController pluginController;

    @Mock
    ObservablePath observablePath;

    @Mock
    PlaceRequest placeRequest;

    @Mock
    VersionRecordManager versionRecordManager;

    @Mock
    BasicFileMenuBuilder menuBuilder;

    @Mock
    LayoutEditorPlugin layoutEditorPlugin;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PerspectiveEditorSettings settings;

    @InjectMocks
    PerspectiveEditorPresenter presenter;

    @Before
    public void setUp() {
        when(pluginController.canCreatePerspectives()).thenReturn(true);
        when(pluginController.canDelete(any())).thenReturn(true);
        when(pluginController.canUpdate(any())).thenReturn(true);
    }

    @Test
    public void testTagsDisabledByDefault() {
        presenter.onStartup(observablePath, placeRequest);

        verify(menuBuilder).addSave(any(Command.class));
        verify(menuBuilder).addCopy(any(Path.class), any(Validator.class), any(Caller.class));
        verify(menuBuilder).addRename(any(Path.class), any(Validator.class), any(Caller.class));
        verify(menuBuilder).addDelete(any(Path.class), any(Caller.class));
        verify(menuBuilder).addDelete(any(Path.class), any(Caller.class));
        verify(menuBuilder, never()).addNewTopLevelMenu(any());
    }

    @Test
    public void testTagsEnabled() {
        when(settings.isTagsEnabled()).thenReturn(true);
        presenter.onStartup(observablePath, placeRequest);

        verify(menuBuilder).addSave(any(Command.class));
        verify(menuBuilder).addCopy(any(Path.class), any(Validator.class), any(Caller.class));
        verify(menuBuilder).addRename(any(Path.class), any(Validator.class), any(Caller.class));
        verify(menuBuilder).addDelete(any(Path.class), any(Caller.class));
        verify(menuBuilder).addDelete(any(Path.class), any(Caller.class));
        verify(menuBuilder).addNewTopLevelMenu(any());
    }
}

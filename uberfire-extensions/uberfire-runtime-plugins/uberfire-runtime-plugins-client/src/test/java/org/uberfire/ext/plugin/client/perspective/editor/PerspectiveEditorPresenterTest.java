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

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.PerspectiveEditorSettings;
import org.uberfire.ext.plugin.client.security.PluginController;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
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

    @Mock
    PerspectiveEditorComponentGroupProvider perspectiveEditorGroupA;

    @Mock
    PerspectiveEditorComponentGroupProvider perspectiveEditorGroupB;

    @Mock
    SyncBeanDef<PerspectiveEditorComponentGroupProvider> perspectiveEditorGroupBeanA;

    @Mock
    SyncBeanDef<PerspectiveEditorComponentGroupProvider> perspectiveEditorGroupBeanB;

    @Mock
    LayoutEditorPresenter layoutEditorPresenter;

    @InjectMocks
    PerspectiveEditorPresenter presenter;

    LayoutDragComponentGroup dragComponentGroupA;
    LayoutDragComponentGroup dragComponentGroupB;

    public static final String COMPONENT_GROUP_A = "A";
    public static final String COMPONENT_GROUP_B = "B";

    @Before
    public void setUp() {
        when(pluginController.canCreatePerspectives()).thenReturn(true);
        when(pluginController.canDelete(any())).thenReturn(true);
        when(pluginController.canUpdate(any())).thenReturn(true);

        dragComponentGroupA = new LayoutDragComponentGroup(COMPONENT_GROUP_A);
        when(perspectiveEditorGroupA.getName()).thenReturn(COMPONENT_GROUP_A);
        when(perspectiveEditorGroupA.getInstance()).thenReturn(dragComponentGroupA);
        when(perspectiveEditorGroupBeanA.getInstance()).thenReturn(perspectiveEditorGroupA);

        dragComponentGroupB = new LayoutDragComponentGroup(COMPONENT_GROUP_B);
        when(perspectiveEditorGroupB.getName()).thenReturn(COMPONENT_GROUP_B);
        when(perspectiveEditorGroupB.getInstance()).thenReturn(dragComponentGroupB);
        when(perspectiveEditorGroupBeanB.getInstance()).thenReturn(perspectiveEditorGroupB);

        when(beanManager.lookupBeans(PerspectiveEditorComponentGroupProvider.class))
                .thenReturn(Arrays.asList(perspectiveEditorGroupBeanB, perspectiveEditorGroupBeanA));
    }

    @Test
    public void testInitDragComponentGroups() {
        presenter.onStartup(observablePath, placeRequest);

        ArgumentCaptor<List> groupListArg = ArgumentCaptor.forClass(List.class);
        verify(layoutEditorPlugin).init(anyString(), groupListArg.capture(), anyString(), anyString(), any());

        // The component groups are grouped by name
        List groupList = groupListArg.getValue();
        assertEquals(groupList.size(), 2);
        assertEquals(((LayoutDragComponentGroup) groupList.get(0)).getName(), COMPONENT_GROUP_A);
        assertEquals(((LayoutDragComponentGroup) groupList.get(1)).getName(), COMPONENT_GROUP_B);
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

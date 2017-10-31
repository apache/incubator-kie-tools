/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.impl;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.backend.PluginServicesImpl;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveServicesImplTest {

    @Mock
    PluginServicesImpl pluginServices;

    @Mock
    LayoutServicesImpl layoutServices;

    @Mock
    Path path;

    @Mock
    Path path2;

    PerspectiveServicesImpl perspectiveServices;

    @Before
    public void setup() {
        LayoutEditorModel layoutEditorModel = new LayoutEditorModel("layout", PluginType.PERSPECTIVE_LAYOUT, path2, "").emptyLayout();

        when(pluginServices.copy(any(), anyString(), anyString())).thenReturn(path2);
        when(pluginServices.copy(any(), anyString(), any(), anyString())).thenReturn(path2);
        when(pluginServices.rename(any(), anyString(), anyString())).thenReturn(path2);
        when(pluginServices.getLayoutEditor(eq(path2), eq(PluginType.PERSPECTIVE_LAYOUT))).thenReturn(layoutEditorModel);

        perspectiveServices = new PerspectiveServicesImpl(pluginServices, layoutServices);
    }

    @Test
    public void testList() {
        Plugin layoutPlugin = new Plugin("layout", PluginType.PERSPECTIVE_LAYOUT, path2);
        when(pluginServices.listPlugins()).thenReturn(Collections.singletonList(layoutPlugin));

        Collection<LayoutTemplate> layouts = perspectiveServices.listLayoutTemplates();
        assertEquals(layouts.size(), 1);
        LayoutTemplate layoutTemplate = layouts.iterator().next();
        assertEquals(layoutTemplate.getName(), "layout");
    }

    @Test
    public void testSave() {
        LayoutTemplate layoutTemplate = new LayoutTemplate("newName");
        perspectiveServices.saveLayoutTemplate(path, layoutTemplate, "save");
        ArgumentCaptor<LayoutEditorModel> layoutModelArg = ArgumentCaptor.forClass(LayoutEditorModel.class);
        ArgumentCaptor<String> commitArg = ArgumentCaptor.forClass(String.class);
        verify(pluginServices).saveLayout(layoutModelArg.capture(), commitArg.capture());

        LayoutEditorModel layoutModelCopy = layoutModelArg.getValue();
        assertEquals(layoutModelCopy.getName(), "newName");
        assertEquals(commitArg.getValue(), "save");
    }

    @Test
    public void testCopy() {
        Path result = perspectiveServices.copy(path, "newName", "");
        ArgumentCaptor<LayoutEditorModel> layoutModelArg = ArgumentCaptor.forClass(LayoutEditorModel.class);
        verify(pluginServices).saveLayout(layoutModelArg.capture(), anyString());
        LayoutEditorModel layoutModelCopy = layoutModelArg.getValue();

        assertEquals(layoutModelCopy.getName(), "newName");
        assertEquals(layoutModelCopy.getPath(), result);
    }

    @Test
    public void testCopyToTarget() {
        Path result = perspectiveServices.copy(path, "newName", path2, "");
        ArgumentCaptor<LayoutEditorModel> layoutModelArg = ArgumentCaptor.forClass(LayoutEditorModel.class);
        verify(pluginServices).saveLayout(layoutModelArg.capture(), anyString());
        LayoutEditorModel layoutModelCopy = layoutModelArg.getValue();

        assertEquals(layoutModelCopy.getName(), "newName");
        assertEquals(layoutModelCopy.getPath(), result);
        assertEquals(result, path2);
    }

    @Test
    public void testRename() {
        Path result = perspectiveServices.rename(path, "newName", "");
        ArgumentCaptor<LayoutEditorModel> layoutModelArg = ArgumentCaptor.forClass(LayoutEditorModel.class);
        verify(pluginServices).saveLayout(layoutModelArg.capture(), anyString());
        LayoutEditorModel layoutModelCopy = layoutModelArg.getValue();

        assertEquals(layoutModelCopy.getName(), "newName");
        assertEquals(layoutModelCopy.getPath(), result);
    }

    @Test
    public void testDelete() {
        perspectiveServices.delete(path, "");
        verify(pluginServices).delete(path, "");
    }
}
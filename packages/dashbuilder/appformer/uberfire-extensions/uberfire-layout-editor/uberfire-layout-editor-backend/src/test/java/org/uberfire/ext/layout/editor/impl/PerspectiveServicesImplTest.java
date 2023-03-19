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

import javax.enterprise.event.Event;

import org.dashbuilder.project.storage.impl.ProjectStorageServicesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveServicesImplTest {

    @Mock
    DefaultMetadata metadata;

    @Mock
    private Event<PluginAdded> pluginAddedEvent;

    @Mock
    private Event<PluginDeleted> pluginDeletedEvent;

    @Mock
    private Event<PluginSaved> pluginSavedEvent;

    @Mock
    private Event<PluginRenamed> pluginRenamedEvent;

    PerspectiveServicesImpl perspectiveServices;

    ProjectStorageServicesImpl projectStorageServices;

    private LayoutServicesImpl layoutServices;

    @Before
    public void setup() {

        layoutServices = new LayoutServicesImpl();
        layoutServices.init();

        projectStorageServices = new ProjectStorageServicesImpl();
        projectStorageServices.clear();
        projectStorageServices.createStructure();

        perspectiveServices = spy(new PerspectiveServicesImpl(projectStorageServices,
                                                              layoutServices,
                                                              pluginAddedEvent,
                                                              pluginDeletedEvent,
                                                              pluginSavedEvent,
                                                              pluginRenamedEvent));
    }

    @Test
    public void testCreate() {
        var plugin = perspectiveServices.createNewPerspective("test", LayoutTemplate.Style.FLUID);
        var lt = layoutServices.fromJson(projectStorageServices.getPerspective(plugin.getName()).get());
        
        verify(pluginAddedEvent).fire(any());
        assertEquals(lt.getName(), "test");
        assertEquals(lt.getStyle(), LayoutTemplate.Style.FLUID);
    }

    @Test
    public void testList() {
        var layout = "layout";
        perspectiveServices.saveLayoutTemplate(new LayoutTemplate(layout));
        var layouts = perspectiveServices.listLayoutTemplates();
        assertEquals(1, layouts.size());
        verify(pluginSavedEvent).fire(any());
        
        var layoutTemplate = layouts.iterator().next();
        assertEquals(layoutTemplate.getName(), layout);
    }

    @Test
    public void testSave() {
        var layoutTemplate = new LayoutTemplate("name");
        perspectiveServices.saveLayoutTemplate(layoutTemplate);
        var content = projectStorageServices.getPerspective(layoutTemplate.getName());
        assertEquals(layoutServices.toJson(layoutTemplate), content.get());
    }

    @Test
    public void testCopy() {
        var newName = "newName";
        var layoutTemplate = new LayoutTemplate("name");
        var path = perspectiveServices.saveLayoutTemplate(layoutTemplate);
        var result = perspectiveServices.copy(path, newName, "");
        assertEquals(newName, result.getFileName());
        assertEquals(newName, result.toURI());
        assertTrue(projectStorageServices.getPerspective(layoutTemplate.getName()).isPresent());
        assertTrue(projectStorageServices.getPerspective(newName).isPresent());
        verify(pluginAddedEvent).fire(any());
    }

    @Test
    public void testRename() {
        final var content = new LayoutTemplate("name");
        final var newName = "newName";
        final var path = PathFactory.newPath(content.getName(), content.getName());

        perspectiveServices.saveLayoutTemplate(content);

        var result = perspectiveServices.rename(path, newName, "");

        assertFalse(projectStorageServices.getPerspective(content.getName()).isPresent());
        assertEquals(newName, result.getFileName());
        assertEquals(newName, result.toURI());

        var renamedContent = projectStorageServices.getPerspective(newName);
        assertEquals(layoutServices.toJson(content), renamedContent.get());
        verify(pluginRenamedEvent).fire(any());
    }

    @Test
    public void testDelete() {
        final var content = new LayoutTemplate("name");
        var p = perspectiveServices.saveLayoutTemplate(content);
        assertNotNull(perspectiveServices.getLayoutTemplate(content.getName()));
        perspectiveServices.delete(p, "");
        assertNull(perspectiveServices.getLayoutTemplate(content.getName()));
        verify(pluginDeletedEvent).fire(any());
    }

    @Test
    public void testSaveAndRename() {
        final var comment = "comment";
        final var newFileName = "newFileName";
        final var name = "layout";
        final var content = new LayoutTemplate(name);
        final var path = PathFactory.newPath(name, name);

        perspectiveServices.save(path, content, metadata, comment);
        assertTrue(projectStorageServices.getPerspective(name).isPresent());

        perspectiveServices.saveAndRename(path, newFileName, metadata, content, comment);
        assertFalse(projectStorageServices.getPerspective(name).isPresent());
        assertTrue(projectStorageServices.getPerspective(newFileName).isPresent());

    }

    @Test
    public void testGetLayoutTemplate() {
        var layout = "layout";
        var path = PathFactory.newPath(layout, layout);
        perspectiveServices.saveLayoutTemplate(new LayoutTemplate(layout));
        assertTrue(perspectiveServices.getLayoutTemplate(path).getName().equals(layout));
    }

}

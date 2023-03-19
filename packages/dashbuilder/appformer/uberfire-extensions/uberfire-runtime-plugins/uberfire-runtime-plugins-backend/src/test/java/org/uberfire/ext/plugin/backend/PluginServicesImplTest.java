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
package org.uberfire.ext.plugin.backend;

import org.dashbuilder.project.storage.impl.ProjectStorageServicesImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.PathFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PluginServicesImplTest {
    
    private ProjectStorageServicesImpl projectStorageServices;
    private PluginServicesImpl pluginServicesImpl;

    @Before
    public void init() {
        projectStorageServices = new ProjectStorageServicesImpl();
        pluginServicesImpl = new PluginServicesImpl(projectStorageServices);
    }
    
    @After
    public void clear() {
        projectStorageServices.clear();
        projectStorageServices.createStructure();
    }
    
    @Test
    public void testListPluginsEmtpy() {
        var l = pluginServicesImpl.listPlugins();
        assertTrue(l.isEmpty());
    }
    
    @Test
    public void testListPlugins() {
        final var name = "test";
        final var content = "abc";
        projectStorageServices.savePerspective(name, content);
        var p = pluginServicesImpl.listPlugins().iterator().next();
        assertEquals(name, p.getName());
        assertEquals(name, p.getPath().getFileName());
    }
    
    @Test
    public void testGetLayoutEditorNull() {
        var m = pluginServicesImpl.getLayoutEditor(null);
        assertNull(m.getLayoutEditorModel());
    }
    
    @Test
    public void testGetLayoutEditorExisting() {
        final var name = "test";
        final var content = "abc";
        projectStorageServices.savePerspective(name, content);
        
        var m = pluginServicesImpl.getLayoutEditor(PathFactory.newPath(name, name));
        assertEquals(content, m.getLayoutEditorModel());
        assertEquals(name, m.getName());
        assertEquals(name, m.getPath().getFileName());
    }

}
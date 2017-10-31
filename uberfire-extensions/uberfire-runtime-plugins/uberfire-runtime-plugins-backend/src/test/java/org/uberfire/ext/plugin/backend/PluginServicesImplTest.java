/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.validation.DefaultFileNameValidator;
import org.uberfire.ext.plugin.event.MediaDeleted;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PluginServicesImplTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Mock(name = "MediaServletURI")
    private Instance<MediaServletURI> mediaServletURI;

    @Mock
    private transient SessionInfo sessionInfo;

    @Mock
    private Event<PluginAdded> pluginAddedEvent;

    @Mock
    private Event<PluginDeleted> pluginDeletedEvent;

    @Mock
    private Event<PluginSaved> pluginSavedEvent;

    @Mock
    private Event<PluginRenamed> pluginRenamedEvent;

    @Mock
    private Event<MediaDeleted> mediaDeletedEvent;

    @Spy
    private DefaultFileNameValidator defaultFileNameValidator;

    @Mock
    private User identity;

    private IOServiceDotFileImpl ioService;

    @Spy
    @InjectMocks
    private PluginServicesImpl pluginServices;

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        MockitoAnnotations.initMocks(this);
        ioService = spy((IOServiceDotFileImpl) fileSystemTestingUtils.getIoService());
        doReturn(fileSystemTestingUtils.getFileSystem()).when(ioService).getFileSystem(any(URI.class));
        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn("script").when(pluginServices).getFrameworkScript(any(Framework.class));

        doReturn(ioService).when(pluginServices).getIoService();

        pluginServices.init();
    }

    @Test(expected = PluginAlreadyExists.class)
    public void testCreateTwoPluginsWithTheSameName() {
        createPlugin("pluginName",
                     PluginType.SCREEN,
                     null);
        createPlugin("pluginName",
                     PluginType.EDITOR,
                     null);
    }

    @Test
    public void testListRuntimePluginsOfEmptyScreen() {
        createPlugin("emptyScreen",
                     PluginType.SCREEN,
                     null);

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(1,
                     runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                            "emptyScreen"));
    }

    @Test
    public void testListRuntimePluginsOfScreenWithFramework() {
        createPlugin("angularScreen",
                     PluginType.SCREEN,
                     Framework.ANGULAR);

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(2,
                     runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                            "angularScreen"));
    }

    @Test
    public void testListRuntimePluginsOfMultipleScreens() {
        createPlugin("emptyScreen",
                     PluginType.SCREEN,
                     null);
        createPlugin("angularScreen",
                     PluginType.SCREEN,
                     Framework.ANGULAR);
        createPlugin("knockoutScreen",
                     PluginType.SCREEN,
                     Framework.KNOCKOUT);

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(5,
                     runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                            "emptyScreen"));
        assertTrue(contains(runtimePlugins,
                            "angularScreen"));
        assertTrue(contains(runtimePlugins,
                            "knockoutScreen"));
    }

    @Test
    public void testCopyPlugin() {
        Path pluginPath = createPlugin("emptyScreen",
                                       PluginType.SCREEN,
                                       null);

        pluginServices.copy(pluginPath,
                            "newEmptyScreen",
                            "");
        verify(pluginAddedEvent,
               times(1)).fire(any(PluginAdded.class));

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(2,
                     runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                            "emptyScreen"));
        assertTrue(contains(runtimePlugins,
                            "newEmptyScreen"));
    }

    @Test
    public void testCopyPluginToAnotherDirectory() {
        Path pluginPath = createPlugin("emptyScreen",
                PluginType.SCREEN,
                null);

        Plugin targetPlugin = buildPlugin("newEmptyScreen",
                PluginType.SCREEN,
                null);

        pluginServices.copy(pluginPath,
                "newEmptyScreen",
                targetPlugin.getPath(),
                "");

        verify(pluginAddedEvent,
                times(1)).fire(any(PluginAdded.class));

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(2,
                runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                "emptyScreen"));
        assertTrue(contains(runtimePlugins,
                "newEmptyScreen"));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testCopyPluginAlreadyExists() {
        Path pluginPath = createPlugin("emptyScreen",
                PluginType.SCREEN,
                null);

        pluginServices.copy(pluginPath,
                "emptyScreen",
                pluginPath,
                "");
    }

    @Test
    public void testRenamePlugin() {
        Path pluginPath = createPlugin("emptyScreen",
                                       PluginType.SCREEN,
                                       null);

        pluginServices.rename(pluginPath,
                              "newEmptyScreen",
                              "");
        verify(pluginRenamedEvent,
               times(1)).fire(any(PluginRenamed.class));

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(1,
                     runtimePlugins.size());
        assertTrue(contains(runtimePlugins,
                            "newEmptyScreen"));
    }

    @Test
    public void testDeletePlugin() {
        Path pluginPath = createPlugin("emptyScreen",
                                       PluginType.SCREEN,
                                       null);

        pluginServices.delete(pluginPath,
                              "");
        verify(pluginDeletedEvent,
               times(1)).fire(any(PluginDeleted.class));

        Collection<RuntimePlugin> runtimePlugins = pluginServices.listRuntimePlugins();
        assertEquals(0,
                     runtimePlugins.size());
    }

    private Path createPlugin(String name,
                              PluginType type,
                              Framework framework) {
        pluginServices.createNewPlugin(name,
                                       type);
        verify(pluginAddedEvent,
               times(1)).fire(any(PluginAdded.class));
        reset(pluginAddedEvent);

        final PluginSimpleContent pluginSimpleContent = buildPlugin(name,
                                                                    type,
                                                                    framework);
        pluginServices.save(pluginSimpleContent,
                            "");
        verify(pluginSavedEvent,
               times(1)).fire(any(PluginSaved.class));
        reset(pluginSavedEvent);

        return pluginSimpleContent.getPath();
    }

    private PluginSimpleContent buildPlugin(String name,
                                            PluginType type,
                                            Framework framework) {
        Set<Framework> frameworks = new HashSet<Framework>();

        if (framework != null) {
            frameworks.add(framework);
        }

        return new PluginSimpleContent(name,
                                       type,
                                       PathFactory.newPath(type.name().toLowerCase() + ".plugin",
                                                           "git://amend-repo-test/" + name + "/" + type.name().toLowerCase() + ".plugin"),
                                       null,
                                       null,
                                       new HashMap<CodeType, String>(),
                                       frameworks,
                                       null);
    }

    private boolean contains(Collection<RuntimePlugin> runtimePlugins,
                             String pluginName) {
        for (RuntimePlugin runtimePlugin : runtimePlugins) {
            if (runtimePlugin.getScript().contains("$registerPlugin({id:\"" + pluginName)) {
                return true;
            }
        }

        return false;
    }
}

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.backend.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.plugin.event.MediaDeleted;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PluginServicesImplTest {

    private static FileSystemTestingUtils pluginsFSUtils = new FileSystemTestingUtils();
    private static FileSystemTestingUtils perspectivesFSUtils = new FileSystemTestingUtils();

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

    @Mock(name = "pluginsFS")
    private FileSystem pluginsFS;

    @Mock(name = "perspectivesFS")
    private FileSystem perspectivesFS;

    @Spy
    private DefaultFileNameValidator defaultFileNameValidator;

    @Mock
    private SaveAndRenameServiceImpl<Plugin, DefaultMetadata> saveAndRenameService;

    @Mock
    private User identity;

    @Mock
    private SpacesAPI spacesAPI;

    private IOServiceDotFileImpl ioService;

    private PluginServicesImpl pluginServices;

    private String PLUGINS_PATH = "git://plugins";
    private String PLUGINS_REPO_PATH = "git://master@plugins";
    private String PERSPECTIVES_PATH = "git://perspectives";
    private String PERSPECTIVES_REPO_PATH = "git://master@perspectives";

    @After
    public void cleanupFileSystem() {
        pluginsFSUtils.cleanup();
        perspectivesFSUtils.cleanup();
    }

    @Before
    public void setup() throws IOException {
        pluginsFSUtils.setup(PLUGINS_PATH);
        perspectivesFSUtils.setup(PERSPECTIVES_PATH);

        MockitoAnnotations.initMocks(this);
        ioService = spy((IOServiceDotFileImpl) pluginsFSUtils.getIoService());

        doReturn("plugins").when(pluginsFS).getName();
        doReturn("perspectives").when(perspectivesFS).getName();

        pluginServices = spy(new PluginServicesImpl(ioService,
                                                    mediaServletURI,
                                                    sessionInfo,
                                                    pluginAddedEvent,
                                                    pluginDeletedEvent,
                                                    pluginSavedEvent,
                                                    pluginRenamedEvent,
                                                    mediaDeletedEvent,
                                                    defaultFileNameValidator,
                                                    identity,
                                                    pluginsFSUtils.getFileSystem(),
                                                    perspectivesFSUtils.getFileSystem(),
                                                    saveAndRenameService,
                                                    spacesAPI) {
            @Override
            String getFrameworkScript(Framework framework) throws IOException {
                return "script";
            }

            @Override
            IOService getIoService() {
                return ioService;
            }
        });

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

        Path targetDir = Paths.convert(Paths.convert(targetPlugin.getPath()).getParent());
        Path resultPath = pluginServices.copy(pluginPath,
                                              "newEmptyScreen",
                                              targetDir,
                                              "");

        assertEquals(Paths.convert(resultPath),
                     Paths.convert(targetPlugin.getPath()));
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

    @Test
    public void testLoadEmptyLayout() {
        Path pluginPath = createPlugin("emptyLayout",
                                       PluginType.PERSPECTIVE_LAYOUT,
                                       null);

        LayoutEditorModel layoutEditorModel = pluginServices.getLayoutEditor(pluginPath,
                                                                             PluginType.PERSPECTIVE_LAYOUT);
        assertEquals(layoutEditorModel.getName(),
                     "emptyLayout");
        assertEquals(layoutEditorModel.getPath(),
                     pluginPath);
        assertTrue(layoutEditorModel.isEmptyLayout());
    }

    @Test
    public void testSimpleSaveWithPluginSimpleContent() {

        final org.uberfire.backend.vfs.Path expected = mock(org.uberfire.backend.vfs.Path.class);
        final PluginSimpleContent content = mock(PluginSimpleContent.class);
        final String comment = "comment";

        doReturn(expected).when(pluginServices).save(content, comment);

        final Path actual = pluginServices.save((Plugin) content, comment);

        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleSaveWithoutPluginSimpleContent() {

        final DynamicMenu content = mock(DynamicMenu.class);
        final String comment = "comment";

        final Path actual = pluginServices.save(content, comment);

        assertNull(actual);
    }

    @Test
    public void testSave() {

        final org.uberfire.backend.vfs.Path expected = mock(org.uberfire.backend.vfs.Path.class);
        final org.uberfire.backend.vfs.Path path = mock(org.uberfire.backend.vfs.Path.class);
        final DefaultMetadata metadata = mock(DefaultMetadata.class);
        final PluginSimpleContent content = mock(PluginSimpleContent.class);
        final String comment = "comment";

        doReturn(expected).when(pluginServices).save(content, comment);

        final Path actual = pluginServices.save(path, content, metadata, comment);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveAndRename() {

        final org.uberfire.backend.vfs.Path expected = mock(org.uberfire.backend.vfs.Path.class);
        final org.uberfire.backend.vfs.Path path = mock(org.uberfire.backend.vfs.Path.class);
        final String newFileName = "newFileName";
        final DefaultMetadata metadata = mock(DefaultMetadata.class);
        final PluginSimpleContent content = mock(PluginSimpleContent.class);
        final String comment = "comment";

        doReturn(expected).when(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);

        final Path actual = pluginServices.saveAndRename(path, newFileName, metadata, content, comment);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRootAndGetFileSystem() {
        assertTrue(pluginServices.getRoot().getFileSystem().getName().equals(pluginsFS.getName()));
        assertTrue(pluginServices.getFileSystem().getName().equals(pluginsFS.getName()));
        for (PluginType type : PluginType.values()) {
            String expected = type == PluginType.PERSPECTIVE_LAYOUT
                              ? perspectivesFS.getName()
                              : pluginsFS.getName();
            assertTrue(pluginServices.getRoot(type).getFileSystem().getName().equals(expected));
            assertTrue(pluginServices.getFileSystem(type).getName().equals(expected));
        }
    }

    @Test
    public void testCreatePluginsDifferentTypes() {
        Collection<Plugin> listOfDefaultPlugins = pluginServices.listPlugins();
        Collection<Plugin> listOfPerspectivesPlugins = pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT);
        assertEquals(0, listOfDefaultPlugins.size());
        assertEquals(0, listOfPerspectivesPlugins.size());

        pluginServices.createNewPlugin("test", PluginType.DEFAULT);
        listOfDefaultPlugins = pluginServices.listPlugins();
        listOfPerspectivesPlugins = pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT);
        assertEquals(1, listOfDefaultPlugins.size());
        assertEquals(0, listOfPerspectivesPlugins.size());
        assertTrue(listOfDefaultPlugins.iterator().next().getPath().toURI().startsWith(PLUGINS_REPO_PATH));

        pluginServices.createNewPlugin("test", PluginType.PERSPECTIVE_LAYOUT);
        listOfDefaultPlugins = pluginServices.listPlugins();
        listOfPerspectivesPlugins = pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT);
        assertEquals(1, listOfDefaultPlugins.size());
        assertEquals(1, listOfPerspectivesPlugins.size());
        assertTrue(listOfDefaultPlugins.iterator().next().getPath().toURI().startsWith(PLUGINS_REPO_PATH));
        assertTrue(listOfPerspectivesPlugins.iterator().next().getPath().toURI().startsWith(PERSPECTIVES_REPO_PATH));
    }

    @Test
    public void testDeletePluginsDifferentTypes() {
        Plugin defaultPlugin = pluginServices.createNewPlugin("test", PluginType.DEFAULT);
        assertEquals(1, pluginServices.listPlugins().size());
        assertEquals(0, pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT).size());

        Plugin perspectivePlugin = pluginServices.createNewPlugin("test", PluginType.PERSPECTIVE_LAYOUT);
        assertEquals(1, pluginServices.listPlugins().size());
        assertEquals(1, pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT).size());

        pluginServices.delete(defaultPlugin.getPath(), "delete plugin");
        assertEquals(0, pluginServices.listPlugins().size());
        assertEquals(1, pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT).size());

        pluginServices.delete(perspectivePlugin.getPath(), "delete plugin");
        assertEquals(0, pluginServices.listPlugins().size());
        assertEquals(0, pluginServices.listPlugins(PluginType.PERSPECTIVE_LAYOUT).size());
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

        String basePath = type == PluginType.PERSPECTIVE_LAYOUT
                          ? PERSPECTIVES_PATH
                          : PLUGINS_PATH;

        String fileName = type.name().toLowerCase() + ".plugin";

        String fullPath = basePath
                          + "/"
                          + name
                          + "/"
                          + fileName;

        return new PluginSimpleContent(name,
                                       type,
                                       PathFactory.newPath(fileName, fullPath),
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

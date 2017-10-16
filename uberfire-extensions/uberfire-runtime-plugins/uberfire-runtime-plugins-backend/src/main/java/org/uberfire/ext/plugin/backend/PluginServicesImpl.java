/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.editor.commons.backend.validation.DefaultFileNameValidator;
import org.uberfire.ext.plugin.event.MediaDeleted;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.DynamicMenuItem;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.Language;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.ext.plugin.type.TypeConverterUtil;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.backend.server.util.Paths.convert;
import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

@Service
@ApplicationScoped
public class PluginServicesImpl implements PluginServices {

    private static final Logger logger = LoggerFactory.getLogger(PluginServicesImpl.class);

    private static final String MENU_ITEM_DELIMITER = " / ";
    protected Gson gson;
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    @Named("MediaServletURI")
    private Instance<MediaServletURI> mediaServletURI;
    @Inject
    private transient SessionInfo sessionInfo;
    @Inject
    private Event<PluginAdded> pluginAddedEvent;
    @Inject
    private Event<PluginDeleted> pluginDeletedEvent;
    @Inject
    private Event<PluginSaved> pluginSavedEvent;
    @Inject
    private Event<PluginRenamed> pluginRenamedEvent;
    @Inject
    private Event<MediaDeleted> mediaDeletedEvent;
    @Inject
    private DefaultFileNameValidator defaultFileNameValidator;
    @Inject
    private User identity;
    private FileSystem fileSystem;
    private Path root;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            fileSystem = getIoService().newFileSystem(URI.create("default://plugins"),
                                                      new HashMap<String, Object>() {{
                                                          put("init",
                                                              Boolean.TRUE);
                                                          put("internal",
                                                              Boolean.TRUE);
                                                      }});
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = getIoService().getFileSystem(URI.create("default://plugins"));
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public String getMediaServletURI() {
        return mediaServletURI.get().getURI();
    }

    @Override
    public Collection<RuntimePlugin> listRuntimePlugins() {
        return listRuntimePlugins(root);
    }

    @Override
    public Collection<RuntimePlugin> listPluginRuntimePlugins(final org.uberfire.backend.vfs.Path pluginPath) {
        return listRuntimePlugins(convert(pluginPath).getParent());
    }

    private Collection<RuntimePlugin> listRuntimePlugins(Path path) {
        final Collection<RuntimePlugin> result = new ArrayList<RuntimePlugin>();

        if (getIoService().exists(path)) {
            walkFileTree(checkNotNull("path",
                                      path),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);
                                     checkNotNull("attrs",
                                                  attrs);

                                     if (attrs.isRegularFile()) {
                                         result.addAll(buildPluginRuntimePlugins(file));
                                     }
                                 } catch (final Exception ex) {
                                     logger.error("An unexpected exception was thrown: ",
                                                  ex);
                                     return FileVisitResult.TERMINATE;
                                 }
                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }

        return result;
    }

    private Collection<RuntimePlugin> buildPluginRuntimePlugins(final Path pluginPath) {
        final Collection<RuntimePlugin> result = new ArrayList<RuntimePlugin>();

        if (pluginPath.getFileName().toString().endsWith(".registry.js")) {
            final String pluginName = pluginPath.getParent().getFileName().toString();
            result.addAll(buildRuntimePluginsFromFrameworks(loadFramework(pluginName)));
            result.add(new RuntimePlugin(loadCss(pluginName),
                                         getIoService().readAllString(pluginPath)));
        }

        return result;
    }

    private Collection<RuntimePlugin> buildRuntimePluginsFromFrameworks(Collection<Framework> frameworks) {
        final Collection<RuntimePlugin> result = new ArrayList<RuntimePlugin>();

        try {
            for (Framework framework : frameworks) {
                result.add(new RuntimePlugin("",
                                             getFrameworkScript(framework)));
            }
        } catch (java.io.IOException e) {
            logger.error("An unexpected exception was thrown: ",
                         e);
        }

        return result;
    }

    String getFrameworkScript(final Framework framework) throws java.io.IOException {
        final StringWriter writer = new StringWriter();
        final InputStream frameworkStream = getClass().getClassLoader().getResourceAsStream("/frameworks/" + framework.toString().toLowerCase() + ".dependency");

        IOUtils.copy(frameworkStream,
                     writer);

        return writer.toString();
    }

    @Override
    public Collection<Plugin> listPlugins() {
        final Collection<Plugin> result = new ArrayList<Plugin>();

        if (getIoService().exists(root)) {
            walkFileTree(checkNotNull("root",
                                      root),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);
                                     checkNotNull("attrs",
                                                  attrs);

                                     if (file.getFileName().toString().endsWith(".plugin") && attrs.isRegularFile()) {
                                         final org.uberfire.backend.vfs.Path path = convert(file);
                                         result.add(new Plugin(file.getParent().getFileName().toString(),
                                                               TypeConverterUtil.fromPath(path),
                                                               path));
                                     }
                                 } catch (final Exception ex) {
                                     return FileVisitResult.TERMINATE;
                                 }
                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }

        return result;
    }

    @Override
    public Plugin createNewPlugin(final String pluginName,
                                  final PluginType type) {
        checkNotEmpty("pluginName",
                      pluginName);
        checkCondition("valid plugin name",
                       defaultFileNameValidator.isValid(pluginName));

        final Path pluginRoot = getPluginPath(pluginName);
        if (getIoService().exists(pluginRoot)) {
            throw new PluginAlreadyExists();
        }

        final Path pluginPath = pluginRoot.resolve(type.toString().toLowerCase() + ".plugin");
        final Plugin plugin = new Plugin(pluginName,
                                         type,
                                         convert(pluginPath));
        updatePlugin(pluginPath,
                     plugin,
                     true);

        return plugin;
    }

    private void updatePlugin(final Path pluginPath,
                              final Plugin plugin,
                              final boolean isNewPlugIn) {
        updatePlugin(pluginPath,
                     plugin,
                     isNewPlugIn,
                     null);
    }

    private void updatePlugin(final Path pluginPath,
                              final Plugin plugin,
                              final boolean isNewPlugIn,
                              final String registry) {
        try {
            getIoService().startBatch(fileSystem);
            getIoService().write(pluginPath,
                                 new Date().toString());
        } finally {
            getIoService().endBatch();
        }

        if (isNewPlugIn) {
            pluginAddedEvent.fire(new PluginAdded(plugin,
                                                  sessionInfo));
        } else {
            pluginSavedEvent.fire(new PluginSaved(plugin,
                                                  sessionInfo));
        }
    }

    @Override
    public PluginContent getPluginContent(final org.uberfire.backend.vfs.Path path) {
        final String pluginName = convert(path).getParent().getFileName().toString();
        return new PluginContent(pluginName,
                                 TypeConverterUtil.fromPath(path),
                                 path,
                                 loadTemplate(pluginName),
                                 loadCss(pluginName),
                                 loadCodeMap(pluginName),
                                 loadFramework(pluginName),
                                 Language.JAVASCRIPT,
                                 loadMediaLibrary(pluginName));
    }

    @Override
    public org.uberfire.backend.vfs.Path save(final PluginSimpleContent plugin,
                                              final String commitMessage) {

        final Path pluginPath = convert(plugin.getPath());
        final boolean isNewPlugin = !getIoService().exists(pluginPath);

        try {
            getIoService().startBatch(fileSystem,
                                      commentedOption(commitMessage));

            saveCodeMap(plugin.getName(),
                        plugin.getCodeMap());

            if (plugin.getTemplate() != null) {
                getIoService().write(getTemplatePath(getPluginPath(plugin.getName())),
                                     plugin.getTemplate());
            }

            if (plugin.getCss() != null) {
                getIoService().write(getCssPath(getPluginPath(plugin.getName())),
                                     plugin.getCss());
            }

            clearDirectory(getPluginPath(plugin.getName()).resolve("dependencies"));

            if (plugin.getFrameworks() != null && !plugin.getFrameworks().isEmpty()) {
                final Framework framework = plugin.getFrameworks().iterator().next();
                getIoService().write(getDependencyPath(getPluginPath(plugin.getName()),
                                                       framework),
                                     "--");
            }

            createRegistry(plugin);

            updatePlugin(pluginPath,
                         plugin,
                         isNewPlugin);
        } finally {
            getIoService().endBatch();
        }

        return plugin.getPath();
    }

    private void clearDirectory(Path directory) {
        if (getIoService().exists(directory)) {
            for (Path path : getIoService().newDirectoryStream(directory)) {
                boolean b = getIoService().deleteIfExists(path);
            }
        }
    }

    private Path getDependencyPath(final Path pluginPath,
                                   final Framework framework) {
        return pluginPath.resolve("dependencies").resolve(framework.toString() + ".dependency");
    }

    private String createRegistry(final PluginSimpleContent plugin) {
        final Path path = getPluginPath(plugin.getName());

        final String registry = new JSRegistry().convertToJSRegistry(plugin);

        getIoService().write(path.resolve(plugin.getName() + ".registry.js"),
                             registry);

        return registry;
    }

    private void saveCodeMap(final String pluginName,
                             final Map<CodeType, String> codeMap) {
        final Path rootPlugin = getPluginPath(pluginName);
        for (final Map.Entry<CodeType, String> entry : codeMap.entrySet()) {
            final Path codePath = getCodePath(rootPlugin,
                                              entry.getKey());
            getIoService().write(codePath,
                                 entry.getValue());
        }
    }

    private Map<CodeType, String> loadCodeMap(final String pluginName) {
        try {
            final Path rootPlugin = getPluginPath(pluginName);
            final DirectoryStream<Path> stream = getIoService().newDirectoryStream(getCodeRoot(rootPlugin),
                                                                                   new DirectoryStream.Filter<Path>() {
                                                                                       @Override
                                                                                       public boolean accept(final Path entry) throws IOException {
                                                                                           return entry.getFileName().toString().endsWith(".code");
                                                                                       }
                                                                                   });

            final Map<CodeType, String> result = new HashMap<CodeType, String>();

            for (final Path path : stream) {
                final CodeType type = getCodeType(path);
                if (type != null) {
                    result.put(type,
                               getIoService().readAllString(path));
                }
            }

            return result;
        } catch (final NotDirectoryException exception) {
            return Collections.emptyMap();
        }
    }

    private Set<Media> loadMediaLibrary(final String pluginName) {
        try {
            final Path rootPlugin = getPluginPath(pluginName);
            final DirectoryStream<Path> stream = getIoService().newDirectoryStream(getMediaRoot(rootPlugin));

            final Set<Media> result = new HashSet<Media>();

            for (final Path path : stream) {
                result.add(new Media(getMediaServletURI() + pluginName + "/media/" + path.getFileName(),
                                     convert(path)));
            }

            return result;
        } catch (final NotDirectoryException exception) {
            return Collections.emptySet();
        }
    }

    private String loadTemplate(final String pluginName) {
        final Path template = getTemplatePath(getPluginPath(pluginName));
        if (getIoService().exists(template)) {
            return getIoService().readAllString(template);
        }
        return "";
    }

    private String loadCss(final String pluginName) {
        final Path css = getCssPath(getPluginPath(pluginName));
        if (getIoService().exists(css)) {
            return getIoService().readAllString(css);
        }
        return "";
    }

    private Set<Framework> loadFramework(final String pluginName) {
        try {
            final Set<Framework> result = new HashSet<Framework>();
            final DirectoryStream<Path> stream = getIoService().newDirectoryStream(getPluginPath(pluginName).resolve("dependencies"));

            for (final Path path : stream) {
                try {
                    result.add(Framework.valueOf(path.getFileName().toString().replace(".dependency",
                                                                                       "").toUpperCase()));
                } catch (final Exception ignored) {
                }
            }

            return result;
        } catch (final NotDirectoryException exception) {
            return Collections.emptySet();
        }
    }

    private Path getTemplatePath(final Path rootPlugin) {
        return rootPlugin.resolve("template.html");
    }

    private Path getCssPath(final Path rootPlugin) {
        return rootPlugin.resolve("css").resolve("style.css");
    }

    private Path getCodePath(final Path rootPlugin,
                             final CodeType codeType) {
        return getCodeRoot(rootPlugin).resolve(codeType.toString().toLowerCase() + ".code");
    }

    private CodeType getCodeType(final Path path) {
        try {
            return CodeType.valueOf(path.getFileName().toString().replace(".code",
                                                                          "").toUpperCase());
        } catch (final Exception ignored) {
        }
        return null;
    }

    private Path getCodeRoot(final Path rootPlugin) {
        return rootPlugin.resolve("code");
    }

    private Path getMediaRoot(final Path rootPlugin) {
        return rootPlugin.resolve("media");
    }

    private Path getPluginPath(final String name) {
        return root.resolve(name);
    }

    @Override
    public void delete(final org.uberfire.backend.vfs.Path path,
                       final String comment) {
        final Plugin plugin = getPluginContent(path);
        final Path pluginPath = convert(plugin.getPath());
        if (getIoService().exists(pluginPath)) {

            try {
                getIoService().startBatch(fileSystem,
                                          commentedOption(comment));
                getIoService().deleteIfExists(pluginPath.getParent(),
                                              StandardDeleteOption.NON_EMPTY_DIRECTORIES);
            } finally {
                getIoService().endBatch();
            }

            pluginDeletedEvent.fire(new PluginDeleted(plugin,
                                                      sessionInfo));
        }
    }

    @Override
    public org.uberfire.backend.vfs.Path copy(final org.uberfire.backend.vfs.Path path,
                                              final String newName,
                                              final String comment) {

        final Path newPath = getPluginPath(newName);
        if (getIoService().exists(newPath)) {
            throw new RuntimeException(new FileAlreadyExistsException(newPath.toString()));
        }

        try {
            getIoService().startBatch(fileSystem,
                                      commentedOption(comment));
            getIoService().copy(convert(path).getParent(),
                                newPath);
        } finally {
            getIoService().endBatch();
        }

        final org.uberfire.backend.vfs.Path result = convert(newPath.resolve(path.getFileName()));
        final PluginContent pluginContent = getPluginContent(result);
        removeRegistry(newPath);
        String registry = createRegistry(pluginContent);

        pluginAddedEvent.fire(new PluginAdded(pluginContent,
                                              sessionInfo));

        return result;
    }

    @Override
    public org.uberfire.backend.vfs.Path copy(final org.uberfire.backend.vfs.Path path,
                                              final String newName,
                                              final org.uberfire.backend.vfs.Path targetDirectory,
                                              final String comment) {
        throw new UnsupportedOperationException("A plugin cannot be copied to another directory.");
    }

    @Override
    public org.uberfire.backend.vfs.Path rename(final org.uberfire.backend.vfs.Path path,
                                                final String newName,
                                                final String comment) {
        final Path newPath = getPluginPath(newName);
        if (getIoService().exists(newPath)) {
            throw new RuntimeException(new FileAlreadyExistsException(newPath.toString()));
        }

        try {
            getIoService().startBatch(fileSystem,
                                      commentedOption(comment));

            removeRegistry(convert(path).getParent());

            getIoService().move(convert(path).getParent(),
                                newPath);
        } finally {
            getIoService().endBatch();
        }

        final String oldPluginName = convert(path).getParent().getFileName().toString();

        final org.uberfire.backend.vfs.Path result = convert(newPath.resolve(path.getFileName()));
        final PluginContent pluginContent = getPluginContent(result);
        String registry = createRegistry(pluginContent);

        pluginRenamedEvent.fire(new PluginRenamed(oldPluginName,
                                                  pluginContent,
                                                  sessionInfo));

        return result;
    }

    private void removeRegistry(final Path path) {
        walkFileTree(path,
                     new SimpleFileVisitor<Path>() {
                         @Override
                         public FileVisitResult visitFile(final Path file,
                                                          final BasicFileAttributes attrs) throws IOException {
                             try {
                                 checkNotNull("file",
                                              file);
                                 checkNotNull("attrs",
                                              attrs);

                                 if (file.getFileName().toString().endsWith(".registry.js") && attrs.isRegularFile()) {
                                     final org.uberfire.backend.vfs.Path path = convert(file);
                                     getIoService().delete(file);
                                 }
                             } catch (final Exception ex) {
                                 return FileVisitResult.TERMINATE;
                             }
                             return FileVisitResult.CONTINUE;
                         }
                     });
    }

    private CommentedOption commentedOption(final String comment) {
        return new CommentedOption(sessionInfo != null ? sessionInfo.getId() : "--",
                                   identity.getIdentifier(),
                                   null,
                                   comment);
    }

    @Override
    public void deleteMedia(final Media media) {
        final Path mediaPath = convert(media.getPath());

        try {
            getIoService().startBatch(fileSystem);
            getIoService().delete(mediaPath);
        } finally {
            getIoService().endBatch();
        }

        mediaDeletedEvent.fire(new MediaDeleted(mediaPath.getParent().getParent().getFileName().toString(),
                                                media));
    }

    @Override
    public DynamicMenu getDynamicMenuContent(org.uberfire.backend.vfs.Path path) {
        final String pluginName = convert(path).getParent().getFileName().toString();
        return new DynamicMenu(pluginName,
                               TypeConverterUtil.fromPath(path),
                               path,
                               loadMenuItems(pluginName));
    }

    @Override
    public LayoutEditorModel getLayoutEditor(org.uberfire.backend.vfs.Path path,
                                             PluginType pluginType) {
        final String pluginName = convert(path).getParent().getFileName().toString();

        return loadLayoutEditor(pluginName,
                                path,
                                pluginType);
    }

    private LayoutEditorModel loadLayoutEditor(String pluginName,
                                               org.uberfire.backend.vfs.Path path,
                                               PluginType type) {
        final Path path1 = getLayoutEditorPath(getPluginPath(pluginName),
                                               type.toString().toLowerCase());
        if (getIoService().exists(path1)) {
            String fileContent = getIoService().readAllString(path1);

            return new LayoutEditorModel(pluginName,
                                         PluginType.PERSPECTIVE_LAYOUT,
                                         path,
                                         fileContent);
        }

        return new LayoutEditorModel().emptyLayout();
    }

    @Override
    public org.uberfire.backend.vfs.Path saveMenu(final DynamicMenu plugin,
                                                  final String commitMessage) {
        final Path pluginPath = convert(plugin.getPath());
        final boolean isNewPlugin = !getIoService().exists(pluginPath);

        try {
            getIoService().startBatch(fileSystem,
                                      commentedOption(commitMessage));

            final Path menuItemsPath = getMenuItemsPath(getPluginPath(plugin.getName()));
            final StringBuilder sb = new StringBuilder();
            for (DynamicMenuItem item : plugin.getMenuItems()) {
                sb.append(item.getActivityId()).append(MENU_ITEM_DELIMITER).append(item.getMenuLabel()).append("\n");
            }
            getIoService().write(menuItemsPath,
                                 sb.toString());

            updatePlugin(pluginPath,
                         plugin,
                         isNewPlugin);
        } finally {
            getIoService().endBatch();
        }

        return plugin.getPath();
    }

    @Override
    public org.uberfire.backend.vfs.Path saveLayout(LayoutEditorModel plugin,
                                                    String commitMessage) {
        final Path pluginPath = convert(plugin.getPath());
        final boolean isNewPlugin = !getIoService().exists(pluginPath);

        try {
            getIoService().startBatch(fileSystem,
                                      commentedOption(commitMessage));

            final Path itemsPath = getLayoutEditorPath(getPluginPath(plugin.getName()),
                                                       plugin.getType().toString().toLowerCase());

            getIoService().write(itemsPath,
                                 plugin.getLayoutEditorModel());

            updatePlugin(pluginPath,
                         plugin,
                         isNewPlugin);
        } finally {
            getIoService().endBatch();
        }
        return plugin.getPath();
    }

    private Path getLayoutEditorPath(final Path rootPlugin,
                                     final String type) {
        return rootPlugin.resolve(type);
    }

    @Override
    public Collection<DynamicMenu> listDynamicMenus() {
        final Collection<DynamicMenu> result = new ArrayList<DynamicMenu>();

        if (getIoService().exists(root)) {
            walkFileTree(checkNotNull("root",
                                      root),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);
                                     checkNotNull("attrs",
                                                  attrs);

                                     if (file.getFileName().toString().equalsIgnoreCase("info.dynamic") && attrs.isRegularFile()) {
                                         final String pluginName = file.getParent().getFileName().toString();
                                         result.add(new DynamicMenu(pluginName,
                                                                    PluginType.DYNAMIC_MENU,
                                                                    convert(file.getParent()),
                                                                    loadMenuItems(pluginName)));
                                     }
                                 } catch (final Exception ex) {
                                     return FileVisitResult.TERMINATE;
                                 }
                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }

        return result;
    }

    @Override
    public Collection<LayoutEditorModel> listLayoutEditor(final PluginType pluginType) {
        final Collection<LayoutEditorModel> result = new ArrayList<LayoutEditorModel>();

        if (getIoService().exists(root)) {
            walkFileTree(checkNotNull("root",
                                      root),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);
                                     checkNotNull("attrs",
                                                  attrs);
                                     if (file.getFileName().toString().equalsIgnoreCase(pluginType.toString().toLowerCase()) && attrs.isRegularFile()) {
                                         final LayoutEditorModel layoutEditorModel = getLayoutEditor(convert(file),
                                                                                                     pluginType);
                                         result.add(layoutEditorModel);
                                     }
                                 } catch (final Exception ex) {
                                     return FileVisitResult.TERMINATE;
                                 }
                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }

        return result;
    }

    private Collection<DynamicMenuItem> loadMenuItems(String pluginName) {
        final Collection<DynamicMenuItem> result = new ArrayList<DynamicMenuItem>();
        final Path menuItemsPath = getMenuItemsPath(getPluginPath(pluginName));
        if (getIoService().exists(menuItemsPath)) {
            final List<String> value = getIoService().readAllLines(menuItemsPath);
            for (final String s : value) {
                final String[] items = s.split(MENU_ITEM_DELIMITER);
                if (items.length == 2) {
                    result.add(new DynamicMenuItem(items[0],
                                                   items[1]));
                }
            }
        }
        return result;
    }

    IOService getIoService() {
        return ioService;
    }

    private Path getMenuItemsPath(final Path rootPlugin) {
        return rootPlugin.resolve("info.dynamic");
    }
}

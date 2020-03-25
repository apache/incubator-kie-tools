/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.guvnor.structure.backend.config.watch.AsyncConfigWatchService;
import org.guvnor.structure.backend.config.watch.AsyncWatchServiceCallback;
import org.guvnor.structure.backend.config.watch.ConfigServiceWatchServiceExecutor;
import org.guvnor.structure.backend.config.watch.ConfigServiceWatchServiceExecutorImpl;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService,
                                                 AsyncWatchServiceCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    protected static final String MONITOR_DISABLED = "org.uberfire.sys.repo.monitor.disabled";

    // mainly for windows as *NIX is based on POSIX but escape always to keep it consistent
    protected static final String INVALID_FILENAME_CHARS = "[\\,/,:,*,?,\",<,>,|]";

    protected org.guvnor.structure.repositories.Repository systemRepository;

    protected ConfigGroupMarshaller marshaller;

    protected User identity;

    //Cache of ConfigGroups to avoid reloading them from file
    protected final Map<ConfigType, List<ConfigGroup>> configGroupsByTypeWithoutNamespace = new ConcurrentHashMap<>();
    protected final Map<ConfigType, Map<String, List<ConfigGroup>>> configGroupsByTypeWithNamespace = new ConcurrentHashMap<>();

    protected AtomicLong localLastModifiedValue = new AtomicLong(-1);

    protected IOService ioService;

    // monitor capabilities
    protected Event<SystemRepositoryChangedEvent> repoChangedEvent;
    protected Event<SystemRepositoryChangedEvent> spaceChangedEvent;
    protected Event<SystemRepositoryChangedEvent> changedEvent;

    protected final ExecutorService executorService = Executors.newSingleThreadExecutor();

    protected final Set<Future<?>> jobs = new CopyOnWriteArraySet<>();

    protected ConfigServiceWatchServiceExecutor executor = null;

    protected CheckConfigurationUpdates configUpdates = null;

    protected WatchService watchService = null;

    protected FileSystem fs;

    public ConfigurationServiceImpl() {
    }

    @Inject
    public ConfigurationServiceImpl(final @Named("system") org.guvnor.structure.repositories.Repository systemRepository,
                                    final ConfigGroupMarshaller marshaller,
                                    final User identity,
                                    final @Named("configIO") IOService ioService,
                                    final @Repository Event<SystemRepositoryChangedEvent> repoChangedEvent,
                                    final @OrgUnit Event<SystemRepositoryChangedEvent> spaceChangedEvent,
                                    final Event<SystemRepositoryChangedEvent> changedEvent,
                                    final @Named("systemFS") FileSystem fs) {
        this.systemRepository = systemRepository;
        this.marshaller = marshaller;
        this.identity = identity;
        this.ioService = ioService;
        this.repoChangedEvent = repoChangedEvent;
        this.spaceChangedEvent = spaceChangedEvent;
        this.changedEvent = changedEvent;
        this.fs = fs;
    }

    @PostConstruct
    public void setup() {
        Path defaultRoot = null;
        for (final Path path : fs.getRootDirectories()) {
            if (path.toUri().toString().contains("/master@")) {
                defaultRoot = path;
                break;
            }
        }

        if (defaultRoot == null) {
            throw new RuntimeException("Could not resolve 'systemFS' main root directory.");
        }

        // enable monitor by default
        if (System.getProperty(MONITOR_DISABLED) == null) {
            watchService = fs.newWatchService();
            configUpdates = new CheckConfigurationUpdates(watchService);
            final ConfigServiceWatchServiceExecutor configServiceWatchServiceExecutor = getWatchServiceExecutor();
            jobs.add(executorService.submit(new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return configUpdates.getDescription();
                }

                @Override
                public void run() {
                    configUpdates.execute(configServiceWatchServiceExecutor);
                }
            }));
        }
    }

    @PreDestroy
    public void shutdown() {
        if (configUpdates != null) {
            configUpdates.deactivate();
        }
        if (watchService != null) {
            watchService.close();
        }
        for (Future<?> job : jobs) {
            if (!job.isCancelled() && !job.isDone()) {
                job.cancel(true);
            }
        }
        executorService.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(60,
                                                  TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(60,
                                                      TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void startBatch() {
        ioService.startBatch(ioService.get(systemRepository.getUri()).getFileSystem());
    }

    @Override
    public void endBatch() {
        ioService.endBatch();
    }

    @Override
    public List<ConfigGroup> getConfiguration(final ConfigType type) {
        if (type.hasNamespace()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " requires a namespace.");
        }

        if (configGroupsByTypeWithoutNamespace.containsKey(type)) {
            return configGroupsByTypeWithoutNamespace.get(type);
        }

        final Path typeDir = ioService.get(systemRepository.getUri()).resolve(type.getDir());

        final List<ConfigGroup> configGroups = getConfiguration(typeDir,
                                                                type);
        if (configGroups != null) {
            configGroupsByTypeWithoutNamespace.put(type,
                                                   configGroups);
        } else {
            return Collections.emptyList();
        }

        return configGroups;
    }

    @Override
    public List<ConfigGroup> getConfiguration(final ConfigType type,
                                              final String namespace) {
        if (!type.hasNamespace() && namespace != null && !namespace.isEmpty()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " does not support namespaces.");
        }

        if (configGroupsByTypeWithNamespace.containsKey(type)) {
            final Map<String, List<ConfigGroup>> configGroupsByNamespace = configGroupsByTypeWithNamespace.get(type);
            if (configGroupsByNamespace.containsKey(namespace)) {
                return configGroupsByNamespace.get(namespace);
            }
        }

        final Path typeDir = ioService.get(systemRepository.getUri()).resolve(type.getDir());
        final Path namespaceDir = typeDir.resolve(namespace);

        final List<ConfigGroup> configGroups = getConfiguration(namespaceDir,
                                                                type);
        if (configGroups != null) {
            if (!configGroupsByTypeWithNamespace.containsKey(type)) {
                configGroupsByTypeWithNamespace.put(type,
                                                    new ConcurrentHashMap<>());
            }

            final Map<String, List<ConfigGroup>> configGroupsByNamespace = configGroupsByTypeWithNamespace.get(type);
            configGroupsByNamespace.put(namespace,
                                        configGroups);
        } else {
            return Collections.emptyList();
        }

        return configGroups;
    }

    @Override
    public Map<String, List<ConfigGroup>> getConfigurationByNamespace(final ConfigType type) {
        if (!type.hasNamespace()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " does not support namespaces.");
        }

        final Path typeDir = ioService.get(systemRepository.getUri()).resolve(type.getDir());
        if (!ioService.exists(typeDir)) {
            return Collections.emptyMap();
        }

        final DirectoryStream<Path> foundNamespaces = getDirectoryStreamForDirectories(typeDir);

        // Force cache update for all namespaces in that type
        final Iterator<Path> it = foundNamespaces.iterator();
        while (it.hasNext()) {
            final String namespace = Paths.convert(it.next()).getFileName();
            getConfiguration(type,
                             namespace);
        }

        // Return the updated cache
        return configGroupsByTypeWithNamespace.get(type);
    }

    private List<ConfigGroup> getConfiguration(final Path dir,
                                               final ConfigType type) {
        final List<ConfigGroup> configGroups = new ArrayList<>();

        if (!ioService.exists(dir)) {
            return configGroups;
        }

        final DirectoryStream<Path> foundConfigs = getDirectoryStreamForFilesWithParticularExtension(dir,
                                                                                                     type.getExt());

        //Only load and cache if a file was found!
        final Iterator<Path> it = foundConfigs.iterator();
        if (it.hasNext()) {
            while (it.hasNext()) {
                final String content = ioService.readAllString(it.next());
                final ConfigGroup configGroup = marshaller.unmarshall(content);
                configGroups.add(configGroup);
            }

            return configGroups;
        }

        return null;
    }

    private DirectoryStream<Path> getDirectoryStreamForFilesWithParticularExtension(final Path dir,
                                                                                    final String extension) {
        return ioService.newDirectoryStream(dir,
                                            entry -> {
                                                if (!Files.isDirectory(entry) &&
                                                        !entry.getFileName().toString().startsWith(".") &&
                                                        entry.getFileName().toString().endsWith(extension)) {
                                                    return true;
                                                }
                                                return false;
                                            });
    }

    private DirectoryStream<Path> getDirectoryStreamForDirectories(final Path dir) {
        return ioService.newDirectoryStream(dir,
                                            entry -> Files.isDirectory(entry));
    }

    @Override
    public boolean addConfiguration(final ConfigGroup configGroup) {
        final Path filePath = resolveConfigGroupPath(configGroup);
        final String commitMessage = "Created config " + filePath.getFileName();

        return saveConfiguration(configGroup,
                                 filePath,
                                 commitMessage,
                                 true);
    }

    @Override
    public boolean updateConfiguration(ConfigGroup configGroup) {
        final Path filePath = resolveConfigGroupPath(configGroup);
        final String commitMessage = "Updated config " + filePath.getFileName();

        return saveConfiguration(configGroup,
                                 filePath,
                                 commitMessage,
                                 false);
    }

    private Path resolveConfigGroupPath(final ConfigGroup configGroup) {
        final ConfigType type = configGroup.getType();
        final String namespace = configGroup.getNamespace();

        if (type.hasNamespace() && (namespace == null || namespace.isEmpty())) {
            throw new RuntimeException("The ConfigType " + type.toString() + " requires a namespace.");
        } else if (!type.hasNamespace() && namespace != null && !namespace.isEmpty()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " does not support namespaces.");
        }

        final String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                                 "_");

        Path path = ioService.get(systemRepository.getUri()).resolve(type.getDir());
        if (type.hasNamespace()) {
            path = path.resolve(namespace);
        }

        return path.resolve(filename + type.getExt());
    }

    private void invalidateCacheAfterUpdatingConfigGroup(final ConfigGroup configGroup) {
        final ConfigType type = configGroup.getType();

        if (!type.hasNamespace()) {
            configGroupsByTypeWithoutNamespace.remove(type);
        } else {
            if (configGroupsByTypeWithNamespace.containsKey(type)) {
                configGroupsByTypeWithNamespace.get(type).remove(configGroup.getNamespace());
            }
        }
    }

    private boolean saveConfiguration(final ConfigGroup configGroup,
                                      final Path path,
                                      final String commitMessage,
                                      final boolean isNew) {
        // avoid duplicated writes to not cause cyclic cluster sync
        if (isNew && ioService.exists(path)) {
            return true;
        }

        final CommentedOption commentedOption = new CommentedOption(getIdentityName(),
                                                                    commitMessage);
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.write(path,
                            marshaller.marshall(configGroup),
                            commentedOption);

            updateLastModified();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }

        invalidateCacheAfterUpdatingConfigGroup(configGroup);

        return true;
    }

    @Override
    public boolean removeConfiguration(final ConfigGroup configGroup) {
        final Path filePath = resolveConfigGroupPath(configGroup);

        if (!ioService.exists(filePath)) {
            return true;
        }

        boolean result;
        try {
            ioService.startBatch(filePath.getFileSystem());
            result = ioService.deleteIfExists(filePath);
            if (result) {
                updateLastModified();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }

        invalidateCacheAfterUpdatingConfigGroup(configGroup);

        return result;
    }

    protected String getIdentityName() {
        try {
            return identity.getIdentifier();
        } catch (Exception e) {
            return "unknown";
        }
    }

    protected long getLastModified() {
        final Path lastModifiedPath = ioService.get(systemRepository.getUri()).resolve(LAST_MODIFIED_MARKER_FILE);

        return ioService.getLastModifiedTime(lastModifiedPath).toMillis();
    }

    protected void updateLastModified() {
        final Path lastModifiedPath = ioService.get(systemRepository.getUri()).resolve(LAST_MODIFIED_MARKER_FILE);
        final CommentedOption commentedOption = new CommentedOption("system",
                                                                    "system repo updated");

        ioService.write(lastModifiedPath,
                        new Date().toString().getBytes(),
                        commentedOption);

        // update the last value to avoid to be re-triggered by the monitor
        localLastModifiedValue.set(getLastModified());
    }

    @Override
    public void callback(long value) {
        localLastModifiedValue.set(value);

        // invalidate cached values as system repo has changed
        configGroupsByTypeWithoutNamespace.clear();
        configGroupsByTypeWithNamespace.clear();
    }

    @Override
    public boolean cleanUpSystemRepository() {
        try {
            final FileSystem fileSystem = ioService.get(systemRepository.getUri()).getFileSystem();
            if (fileSystem instanceof JGitFileSystem) {
                return ((JGitFileSystem) fileSystem)
                        .getGit()
                        .resetWithSquash("Repository clean up.");
            }
        } catch (IOException e) {
            LOGGER.error("Unable to reset git repository.", e);
        }
        return false;
    }

    private class CheckConfigurationUpdates implements AsyncConfigWatchService {

        private final WatchService ws;
        private boolean active = true;

        public CheckConfigurationUpdates(final WatchService watchService) {
            this.ws = watchService;
        }

        public void deactivate() {
            this.active = false;
        }

        @Override
        public void execute(final ConfigServiceWatchServiceExecutor wsExecutor) {
            while (active) {
                try {

                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch (final Exception ex) {
                        break;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();

                    boolean markerFileModified = false;
                    for (final WatchEvent<?> event : events) {
                        final WatchContext context = (WatchContext) event.context();
                        if (event.kind().equals(StandardWatchEventKind.ENTRY_MODIFY)) {
                            if (context.getOldPath().getFileName().toString().equals(LAST_MODIFIED_MARKER_FILE)) {
                                markerFileModified = true;
                                break;
                            }
                        } else if (event.kind().equals(StandardWatchEventKind.ENTRY_CREATE)) {
                            if (context.getPath().getFileName().toString().equals(LAST_MODIFIED_MARKER_FILE)) {
                                markerFileModified = true;
                                break;
                            }
                        } else if (event.kind().equals(StandardWatchEventKind.ENTRY_RENAME)) {
                            if (context.getOldPath().getFileName().toString().equals(LAST_MODIFIED_MARKER_FILE)) {
                                markerFileModified = true;
                                break;
                            }
                        } else if (event.kind().equals(StandardWatchEventKind.ENTRY_DELETE)) {
                            if (context.getOldPath().getFileName().toString().equals(LAST_MODIFIED_MARKER_FILE)) {
                                markerFileModified = true;
                                break;
                            }
                        }
                    }

                    if (markerFileModified) {
                        wsExecutor.execute(wk,
                                           localLastModifiedValue.get(),
                                           ConfigurationServiceImpl.this);
                    }

                    boolean valid = wk.reset();
                    if (!valid) {
                        break;
                    }
                } catch (final Exception ignored) {
                }
            }
        }

        @Override
        public String getDescription() {
            return "Config File Watch Service";
        }
    }

    protected ConfigServiceWatchServiceExecutor getWatchServiceExecutor() {
        if (executor == null) {
            ConfigServiceWatchServiceExecutor _executor = null;
            try {
                _executor = InitialContext.doLookup("java:module/ConfigServiceWatchServiceExecutorImpl");
            } catch (final Exception ignored) {
            }

            if (_executor == null) {
                _executor = new ConfigServiceWatchServiceExecutorImpl();
                ((ConfigServiceWatchServiceExecutorImpl) _executor).setConfig(systemRepository,
                                                                              ioService,
                                                                              repoChangedEvent,
                                                                              spaceChangedEvent,
                                                                              changedEvent);
            }
            executor = _executor;
        }

        return executor;
    }
}

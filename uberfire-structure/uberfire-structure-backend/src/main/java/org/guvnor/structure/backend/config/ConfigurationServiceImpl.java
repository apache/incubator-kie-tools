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

import java.util.ArrayList;
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
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
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

import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService,
                                                 AsyncWatchServiceCallback {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    private static final String MONITOR_DISABLED = "org.uberfire.sys.repo.monitor.disabled";
    //    private static final String MONITOR_CHECK_INTERVAL = "org.uberfire.sys.repo.monitor.interval";
    // mainly for windows as *NIX is based on POSIX but escape always to keep it consistent
    private static final String INVALID_FILENAME_CHARS = "[\\,/,:,*,?,\",<,>,|]";

    private org.guvnor.structure.repositories.Repository systemRepository;

    private ConfigGroupMarshaller marshaller;

    private User identity;

    //Cache of ConfigGroups to avoid reloading them from file
    private final Map<ConfigType, List<ConfigGroup>> configuration = new ConcurrentHashMap<ConfigType, List<ConfigGroup>>();
    private AtomicLong localLastModifiedValue = new AtomicLong(-1);

    private IOService ioService;

    // monitor capabilities
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;
    private Event<SystemRepositoryChangedEvent> orgUnitChangedEvent;
    private Event<SystemRepositoryChangedEvent> changedEvent;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>>();

    private ConfigServiceWatchServiceExecutor executor = null;

    private CheckConfigurationUpdates configUpdates = null;

    private WatchService watchService = null;

    private FileSystem fs;

    public ConfigurationServiceImpl() {
    }

    @Inject
    public ConfigurationServiceImpl(final @Named("system") org.guvnor.structure.repositories.Repository systemRepository,
                                    final ConfigGroupMarshaller marshaller,
                                    final User identity,
                                    final @Named("configIO") IOService ioService,
                                    final @Repository Event<SystemRepositoryChangedEvent> repoChangedEvent,
                                    final @OrgUnit Event<SystemRepositoryChangedEvent> orgUnitChangedEvent,
                                    final Event<SystemRepositoryChangedEvent> changedEvent,
                                    final @Named("systemFS") FileSystem fs) {
        this.systemRepository = systemRepository;
        this.marshaller = marshaller;
        this.identity = identity;
        this.ioService = ioService;
        this.repoChangedEvent = repoChangedEvent;
        this.orgUnitChangedEvent = orgUnitChangedEvent;
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

        systemRepository.setRoot(convert(defaultRoot));

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
        if (configuration.containsKey(type)) {
            return configuration.get(type);
        }
        final List<ConfigGroup> configGroups = new ArrayList<ConfigGroup>();
        final DirectoryStream<Path> foundConfigs = ioService.newDirectoryStream(ioService.get(systemRepository.getUri()),
                                                                                new DirectoryStream.Filter<Path>() {
                                                                                    @Override
                                                                                    public boolean accept(final Path entry) throws IOException {
                                                                                        if (!Files.isDirectory(entry) &&
                                                                                                !entry.getFileName().toString().startsWith(".") &&
                                                                                                entry.getFileName().toString().endsWith(type.getExt())) {
                                                                                            return true;
                                                                                        }
                                                                                        return false;
                                                                                    }
                                                                                }
        );
        //Only load and cache if a file was found!
        final Iterator<Path> it = foundConfigs.iterator();
        if (it.hasNext()) {
            while (it.hasNext()) {
                final String content = ioService.readAllString(it.next());
                final ConfigGroup configGroup = marshaller.unmarshall(content);
                configGroups.add(configGroup);
            }
            configuration.put(type,
                              configGroups);
        }
        return configGroups;
    }

    @Override
    public boolean addConfiguration(final ConfigGroup configGroup) {
        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");

        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());
        // avoid duplicated writes to not cause cyclic cluster sync
        if (ioService.exists(filePath)) {
            return true;
        }

        final CommentedOption commentedOption = new CommentedOption(getIdentityName(),
                                                                    "Created config " + filePath.getFileName());
        try {
            ioService.startBatch(filePath.getFileSystem());
            ioService.write(filePath,
                            marshaller.marshall(configGroup),
                            commentedOption);

            updateLastModified();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }
        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove(configGroup.getType());

        return true;
    }

    @Override
    public boolean updateConfiguration(ConfigGroup configGroup) {
        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");

        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());

        final CommentedOption commentedOption = new CommentedOption(getIdentityName(),
                                                                    "Updated config " + filePath.getFileName());
        try {
            ioService.startBatch(filePath.getFileSystem());
            ioService.write(filePath,
                            marshaller.marshall(configGroup),
                            commentedOption);

            updateLastModified();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }
        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove(configGroup.getType());

        return true;
    }

    @Override
    public boolean removeConfiguration(final ConfigGroup configGroup) {

        //Invalidate cache if an item has been removed; otherwise cached value is stale
        configuration.remove(configGroup.getType());
        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");
        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());

        // avoid duplicated writes to not cause cyclic cluster sync
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

        // update the last value to avoid to be retriggered byt the monitor
        localLastModifiedValue.set(getLastModified());
    }

    @Override
    public void callback(long value) {
        localLastModifiedValue.set(value);
        // invalidate cached values as system repo has changed
        configuration.clear();
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
                                                                              orgUnitChangedEvent,
                                                                              changedEvent);
            }
            executor = _executor;
        }

        return executor;
    }
}

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RuntimeModelWatcherServiceManager {

    Logger logger = LoggerFactory.getLogger(RuntimeModelWatcherServiceManager.class);

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    RuntimeModelRegistry registry;

    @Inject
    ManagedExecutor executor;

    private WatchService watchService;

    private Future<?> watcherTask;

    @PostConstruct
    public void start(@Observes StartupEvent startupEvent) {
        if (runtimeOptions.isWatchModels()) {
            logger.info("Scheduling model watcher");
            watcherTask = executor.submit(() -> {
                try {
                    createWatcherTask(runtimeOptions.getImportsBaseDir());
                } catch (IOException e) {
                    logger.error("Error setting models watcher: {}", e.getMessage());
                    logger.debug("Error setting models watcher.", e);
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    logger.info("Thread Interrupted: ", e.getMessage());
                    logger.debug("Thread Interrupted!", e);
                    Thread.currentThread().interrupt();
                }
            });

        } else {
            logger.info("Not watching for model changes.");
        }
    }

    @SuppressWarnings("unchecked")
    public void createWatcherTask(String baseDir) throws IOException, InterruptedException {
        var baseDirPath = Paths.get(baseDir);
        logger.info("Watching models directory for changes");
        watchService = FileSystems.getDefault().newWatchService();
        baseDirPath.register(watchService,
                             StandardWatchEventKinds.ENTRY_CREATE,
                             StandardWatchEventKinds.ENTRY_DELETE);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                final var kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                var ev = (WatchEvent<Path>) event;
                var modelPath = ev.context();

                var modelId = modelPath.toFile().getName().replaceAll(RuntimeOptions.DASHBOARD_EXTENSION, "");

                if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    unregister(modelId);
                }

                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    unregister(modelId);
                    register(modelId);
                }

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    register(modelId);
                }

                key.reset();
            }
        }

    }

    private void unregister(String modelId) {
        try {
            registry.unregister(modelId);
        } catch (Exception e) {
            logger.error("Error unregistering model {}", e.getMessage());
            logger.debug("Error unregistering model", e);
        }
    }

    private void register(String modelId) {
        var modelPathOp = runtimeOptions.modelPath(modelId);
        if (modelPathOp.isPresent()) {
            try {
                var modelPath = modelPathOp.get();
                registry.registerFile(modelPath);
            } catch (Exception e) {
                logger.error("Error registering model {}", e.getMessage());
                logger.debug("Error registering model", e);
            }
        } else {
            logger.info("File for model {} not found.", modelId);
        }
    }

    public void stop() {
        try {
            if (watcherTask != null) {
                watcherTask.cancel(true);
            }

        } catch (Exception e) {
            logger.warn("Error stopping watcher task: {}", e.getMessage());
            logger.debug("Error stopping watcher task: {}", e);
        }
        try {

            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            logger.warn("Error stopping watcher service: {}", e.getMessage());
            logger.debug("Error stopping watcher service.", e);
        }
    }

    void onShutDown(@Observes ShutdownEvent event) {
        this.stop();
    }
}

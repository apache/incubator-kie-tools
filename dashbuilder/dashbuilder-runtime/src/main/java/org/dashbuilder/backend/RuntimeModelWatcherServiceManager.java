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
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class RuntimeModelWatcherServiceManager {

    Logger logger = LoggerFactory.getLogger(RuntimeModelWatcherServiceManager.class);

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    RuntimeModelRegistry registry;

    @Resource
    private ManagedExecutorService executorService;

    private WatchService watchService;

    @PostConstruct
    public void start() {
        if (runtimeOptions.isWatchModels()) {
            logger.info("Scheduling model watcher");
            executorService.execute(() -> {
                try {
                    createWatcherTask(runtimeOptions.getImportsBaseDir());
                } catch (IOException e) {
                    logger.error("Error setting models watcher: {}", e.getMessage());
                    logger.debug("Error setting models watcher.", e);
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    logger.warn("Thread Interrupted!", e);
                    Thread.currentThread().interrupt();
                }
            });

        } else {
            logger.info("Not watching for model changes.");
        }
    }

    @SuppressWarnings("unchecked")
    public void createWatcherTask(String baseDir) throws IOException, InterruptedException {

        Path baseDirPath = Paths.get(baseDir);
        logger.info("Watching models directory for changes");
        watchService = FileSystems.getDefault().newWatchService();
        baseDirPath.register(watchService,
                             StandardWatchEventKinds.ENTRY_CREATE,
                             StandardWatchEventKinds.ENTRY_DELETE);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                final Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path modelPath = ev.context();

                String modelId = modelPath.toFile().getName().replaceAll(RuntimeOptions.DASHBOARD_EXTENSION, "");

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
        Optional<String> modelPathOp = runtimeOptions.modelPath(modelId);
        if (modelPathOp.isPresent()) {
            try {
                String modelPath = modelPathOp.get();
                registry.registerFile(modelPath);
            } catch (Exception e) {
                logger.error("Error registering model {}", e.getMessage());
                logger.debug("Error registering model", e);
            }
        } else {
            logger.info("File for model {} not found.", modelId);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            logger.warn("Error stopping watch service: {}", e.getMessage());
            logger.debug("Error stopping watch service.", e);
        }
    }
}

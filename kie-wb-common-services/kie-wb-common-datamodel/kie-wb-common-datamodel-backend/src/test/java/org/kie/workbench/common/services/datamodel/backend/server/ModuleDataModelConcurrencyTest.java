/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ModuleDataModelConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ModuleDataModelConcurrencyTest.class);

    @Inject
    private Paths paths;

    @Inject
    private BuildResultsObserver buildResultsObserver;

    @Inject
    private BuildService buildService;

    @Inject
    private KieModuleService moduleService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private ResourceChangeIncrementalBuilder buildChangeListener;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Event<InvalidateDMOModuleCacheEvent> invalidateDMOModuleCacheEvent;

    @Test
    public void testConcurrentResourceUpdates() throws URISyntaxException {
        final URL pomUrl = this.getClass().getResource("/DataModelBackendTest1/pom.xml");
        final org.uberfire.java.nio.file.Path nioPomPath = ioService.get(pomUrl.toURI());
        final Path pomPath = paths.convert(nioPomPath);

        final URL resourceUrl = this.getClass().getResource("/DataModelBackendTest1/src/main/resources/empty.rdrl");
        final org.uberfire.java.nio.file.Path nioResourcePath = ioService.get(resourceUrl.toURI());
        final Path resourcePath = paths.convert(nioResourcePath);

        //Force full build before attempting incremental changes
        final KieModule module = moduleService.resolveModule(resourcePath);
        final BuildResults buildResults = buildService.build(module);
        assertNotNull(buildResults);
        assertEquals(0,
                     buildResults.getErrorMessages().size());
        assertEquals(1,
                     buildResults.getInformationMessages().size());

        //Perform incremental build
        final int THREADS = 200;
        final Result result = new Result();
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < THREADS; i++) {
            final int operation = (i % 3);

            switch (operation) {
                case 0:
                    es.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] Request to update POM received");
                                invalidateCaches(module,
                                                 pomPath);
                                buildChangeListener.updateResource(pomPath);
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] POM update completed");
                            } catch (Throwable e) {
                                result.setFailed(true);
                                result.setMessage(e.getMessage());
                                ExceptionUtils.printRootCauseStackTrace(e);
                            }
                        }
                    });
                    break;
                case 1:
                    es.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] Request to update Resource received");
                                invalidateCaches(module,
                                                 resourcePath);
                                buildChangeListener.addResource(resourcePath);
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] Resource update completed");
                            } catch (Throwable e) {
                                result.setFailed(true);
                                result.setMessage(e.getMessage());
                                ExceptionUtils.printRootCauseStackTrace(e);
                            }
                        }
                    });
                    break;
                case 2:
                    es.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] Request for DataModel received");
                                dataModelService.getDataModel(resourcePath);
                                logger.debug("[Thread: " + Thread.currentThread().getName() + "] DataModel request completed");
                            } catch (Throwable e) {
                                result.setFailed(true);
                                result.setMessage(e.getMessage());
                                ExceptionUtils.printRootCauseStackTrace(e);
                            }
                        }
                    });
            }
        }

        es.shutdown();
        try {
            es.awaitTermination(5,
                                TimeUnit.MINUTES);
        } catch (InterruptedException e) {
        }
        if (result.isFailed()) {
            fail(result.getMessage());
        }
    }

    private void invalidateCaches(final KieModule module,
                                  final Path resourcePath) {
        invalidateDMOModuleCacheEvent.fire(new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                             module,
                                                                             resourcePath));
    }

    private static class Result {

        private boolean failed = false;
        private String message = "";

        public synchronized boolean isFailed() {
            return failed;
        }

        public synchronized void setFailed(boolean failed) {
            this.failed = failed;
        }

        public synchronized String getMessage() {
            return message;
        }

        public synchronized void setMessage(String message) {
            this.message = message;
        }
    }
}

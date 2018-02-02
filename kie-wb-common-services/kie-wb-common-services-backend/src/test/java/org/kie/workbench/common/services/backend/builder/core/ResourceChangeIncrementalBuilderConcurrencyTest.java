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

package org.kie.workbench.common.services.backend.builder.core;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.common.services.builder.IncrementalBuilderExecutorManagerFactory;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ResourceChangeIncrementalBuilderConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ResourceChangeIncrementalBuilderConcurrencyTest.class);

    private static final String GLOBAL_SETTINGS = "settings";

    private static final int THREADS = 200;

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    private Paths paths;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private BuildService buildService;

    @Inject
    private KieModuleService moduleService;

    @Inject
    private BeanManager beanManager;

    @Inject
    private IncrementalBuilderExecutorManagerFactory executorManagerFactory;

    @Inject
    private org.guvnor.common.services.builder.ResourceChangeIncrementalBuilder buildChangeListener;

    private Path pomPath;

    private Path resourcePath;

    private ThreadPoolExecutor executor;

    @Before
    public void setUp() throws Exception {
        //Define mandatory properties
        List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        boolean globalSettingsDefined = false;
        for (ConfigGroup globalConfigGroup : globalConfigGroups) {
            if (GLOBAL_SETTINGS.equals(globalConfigGroup.getName())) {
                globalSettingsDefined = true;
                break;
            }
        }
        if (!globalSettingsDefined) {
            configurationService.addConfiguration(getGlobalConfiguration());
        }

        final URL pomUrl = this.getClass().getResource("/BuildChangeListenerRepo/pom.xml");
        final org.uberfire.java.nio.file.Path nioPomPath = fs.getPath(pomUrl.toURI());
        pomPath = paths.convert(nioPomPath);

        final URL resourceUrl = this.getClass().getResource("/BuildChangeListenerRepo/src/main/resources/update.drl");
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath(resourceUrl.toURI());
        resourcePath = paths.convert(nioResourcePath);

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executorManagerFactory.getExecutorManager().setExecutorService(executor);
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                      GLOBAL_SETTINGS,
                                                                      "");
        group.addConfigItem(configurationFactory.newConfigItem("build.enable-incremental",
                                                               "true"));
        return group;
    }

    @Test
    public void testConcurrentResourceUpdates() throws URISyntaxException {
        //Force full build before attempting incremental changes
        ensureModuleBuild();

        //for every thread launched, internally a new task will be launched in the IncrementalBuilderExecutorManager
        final int totalTasks = THREADS * 2;
        final Result result = new Result();
        for (int i = 0; i < THREADS; i++) {
            //launch an incremental build for the resource
            executor.execute(newIncrementalBuildTask(resourcePath, result));
        }
        awaitTasksCompletion(executor, totalTasks);
        if (result.isFailed()) {
            fail(result.getMessage());
        }
    }

    @Test
    public void testConcurrentResourceUpdatesWithModuleChanges() throws URISyntaxException {
        //Force full build before attempting incremental changes
        ensureModuleBuild();

        //for every thread launched, internally a new task will be launched in the IncrementalBuilderExecutorManager
        final int totalTasks = THREADS * 2;
        final Result result = new Result();
        for (int i = 0; i < THREADS; i++) {
            final Path p = (i % 5 == 0) ? pomPath : resourcePath;
            //launch an incremental build for the resource or the module.
            executor.execute(newIncrementalBuildTask(p, result));
        }
        awaitTasksCompletion(executor, totalTasks);
        if (result.isFailed()) {
            fail(result.getMessage());
        }
    }

    private void ensureModuleBuild() {
        final KieModule module = moduleService.resolveModule(resourcePath);
        final BuildResults buildResults = buildService.build(module);
        assertNotNull(buildResults);
        assertEquals(0,
                     buildResults.getErrorMessages().size());
        assertEquals(1,
                     buildResults.getInformationMessages().size());
    }

    private Runnable newIncrementalBuildTask(Path resource, Result result) {
        return () -> {
            try {
                logger.debug("Thread " + Thread.currentThread().getName() + " has started for " + resource.toURI());
                buildChangeListener.updateResource(resource);
                logger.debug("Thread " + Thread.currentThread().getName() + " has completed " + resource.toURI());
            } catch (Throwable e) {
                result.setFailed(true);
                result.setMessage(e.getMessage());
                logger.debug(e.getMessage());
            }
        };
    }

    private void awaitTasksCompletion(ThreadPoolExecutor executor, int totalTasks) {
        boolean allTasksLaunched = false;
        int MAX_TIME = 5;
        int totalSleep = 0;
        while (!allTasksLaunched) {
            try {
                if (executor.getTaskCount() < totalTasks) {
                    //let's wait until all tasks has been launched.
                    TimeUnit.SECONDS.sleep(5);
                    totalSleep += 5;
                    if (TimeUnit.SECONDS.toMinutes(totalSleep) > MAX_TIME) {
                        //something has gone wrong with the test
                        fail("Test expected time of " + MAX_TIME + " minutes has elapsed.");
                    }
                } else {
                    //let's start the shutdown.
                    allTasksLaunched = true;
                    executor.shutdown();
                    executor.awaitTermination(MAX_TIME, TimeUnit.MINUTES);
                }
            } catch (InterruptedException e) {
                allTasksLaunched = true;
                fail(e.getMessage());
            }
        }
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
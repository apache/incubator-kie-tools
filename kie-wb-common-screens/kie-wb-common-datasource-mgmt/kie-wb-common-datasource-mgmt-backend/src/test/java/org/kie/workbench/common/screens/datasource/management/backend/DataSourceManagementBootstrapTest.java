/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceDefDeployer;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DefaultDriverInitializer;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDefDeployer;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.DataSourceRuntimeManagerImpl;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefChangeHandler;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefResourceChangeObserver;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceManagementBootstrapTest {

    private static final long RETRIES = 15;

    private static final long DELAY = 1000;

    private static final String DEF_CHANGE_HANDLER_BEAN_NAME = "DEF_CHANGE_HANDLER_BEAN_NAME";

    @Mock
    private DataSourceRuntimeManager dataSourceRuntimeManager;

    @Mock
    private DataSourceDefDeployer dataSourceDefDeployer;

    @Mock
    private DriverDefDeployer driverDefDeployer;

    @Mock
    private DefaultDriverInitializer driverInitializer;

    @Mock
    private DefResourceChangeObserver defResourceChangeObserver;

    @Mock
    private BeanManager beanManager;

    private DataSourceManagementBootstrap dataSourceManagementBootstrap;

    @Mock
    private DefChangeHandler defChangeHandler;

    @Mock
    private ScheduledExecutorService scheduler;

    private long dataSourceRuntimeMangerHasStartedFailures = -1;

    @Before
    public void setUp() throws Exception {
        System.getProperties().setProperty(DataSourceManagementBootstrap.DEPLOYMENTS_INITIALIZATION_DELAY,
                                           Long.toString(DELAY));
        System.getProperties().setProperty(DataSourceManagementBootstrap.DEPLOYMENTS_INITIALIZATION_RETRIES,
                                           Long.toString(RETRIES));
        System.getProperties().setProperty(DataSourceManagementBootstrap.DEF_CHANGE_HANDLER_BEAN,
                                           DEF_CHANGE_HANDLER_BEAN_NAME);

        dataSourceRuntimeMangerHasStartedFailures = -1;
        dataSourceRuntimeManager = spy(new DataSourceRuntimeManagerImpl() {
            int failures = 0;

            @Override
            public void hasStarted() throws Exception {
                if (failures++ < dataSourceRuntimeMangerHasStartedFailures) {
                    //make the initialization check fail the desired number of times.
                    throw new RuntimeException("Data source runtime manager has not started yet.");
                }
            }
        });

        dataSourceManagementBootstrap = new DataSourceManagementBootstrap(dataSourceRuntimeManager,
                                                                          dataSourceDefDeployer,
                                                                          driverDefDeployer,
                                                                          driverInitializer,
                                                                          defResourceChangeObserver,
                                                                          beanManager) {
            {
                super.scheduler.shutdown();
                super.scheduler = DataSourceManagementBootstrapTest.this.scheduler;
            }

            @Override
            protected DefChangeHandler getDefChangeHandler(String defChangeHandlerName) {
                return DEF_CHANGE_HANDLER_BEAN_NAME.equals(defChangeHandlerName) ? defChangeHandler : null;
            }
        };
    }

    @Test
    public void testInitializeConfigParams() throws Exception {
        dataSourceManagementBootstrap.init();
        assertEquals(DELAY,
                     dataSourceManagementBootstrap.deploymentsInitializationDelay);
        assertEquals(RETRIES,
                     dataSourceManagementBootstrap.deploymentsInitializationRetries);
    }

    @Test
    public void testInitializeDefChangeHandler() {
        dataSourceManagementBootstrap.init();
        verify(defResourceChangeObserver,
               times(1)).setDefChangeHandler(defChangeHandler);
    }

    @Test
    public void testInitializeDefaultDrivers() {
        dataSourceManagementBootstrap.init();
        verify(driverInitializer,
               times(1)).initializeDefaultDrivers();
    }

    @Test
    public void testInitializeDeploymentsOKWithNoRetries() throws Exception {
        //initialization works well at the first try.
        prepareExecutor();
        dataSourceManagementBootstrap.init();
        verify(scheduler,
               times(1)).schedule(any(Runnable.class),
                                  eq(DELAY),
                                  eq(TimeUnit.MILLISECONDS));
        verify(dataSourceRuntimeManager,
               times(1)).hasStarted();
        verify(driverDefDeployer,
               times(1)).deployGlobalDefs();
        verify(dataSourceDefDeployer,
               times(1)).deployGlobalDefs();
        verify(scheduler,
               times(1)).shutdown();
    }

    @Test
    public void testInitializeDeploymentsOKWithRetries() throws Exception {
        prepareExecutor();
        //let the hasStarted check fail a desired number of times.
        dataSourceRuntimeMangerHasStartedFailures = 4;

        dataSourceManagementBootstrap.init();

        verify(scheduler,
               times(1 + (int) dataSourceRuntimeMangerHasStartedFailures))
                .schedule(any(Runnable.class),
                          eq(DELAY),
                          eq(TimeUnit.MILLISECONDS));
        verify(dataSourceRuntimeManager,
               times(1 + (int) dataSourceRuntimeMangerHasStartedFailures)).hasStarted();
        //after the programmed number of failures the initialization works well.
        verify(driverDefDeployer,
               times(1)).deployGlobalDefs();
        verify(dataSourceDefDeployer,
               times(1)).deployGlobalDefs();
        verify(scheduler,
               times(1)).shutdown();
    }

    @Test
    public void testInitializeDeploymentsExceededTheRetries() throws Exception {
        //initialization works well at the first M retry.
        prepareExecutor();
        //let the hasStarted check fail for all the retries.
        dataSourceRuntimeMangerHasStartedFailures = RETRIES;

        dataSourceManagementBootstrap.init();

        verify(scheduler,
               times((int) RETRIES))
                .schedule(any(Runnable.class),
                          eq(DELAY),
                          eq(TimeUnit.MILLISECONDS));
        verify(dataSourceRuntimeManager,
               times((int) RETRIES)).hasStarted();
        //all the retries were consumed, and the deployments where never invoked.
        verify(driverDefDeployer,
               never()).deployGlobalDefs();
        verify(dataSourceDefDeployer,
               never()).deployGlobalDefs();
        verify(scheduler,
               times(1)).shutdown();
    }

    @Test
    public void testDestroyWhenTasksRunning() {
        dataSourceManagementBootstrap.init();
        when(scheduler.isShutdown()).thenReturn(false);
        dataSourceManagementBootstrap.destroy();
        verify(scheduler,
               times(1)).shutdownNow();
    }

    @Test
    public void testDestroyWhenNoTasksRunning() {
        dataSourceManagementBootstrap.init();
        when(scheduler.isShutdown()).thenReturn(true);
        dataSourceManagementBootstrap.destroy();
        verify(scheduler,
               never()).shutdownNow();
    }

    private void prepareExecutor() {
        doAnswer(new Answer< Void >() {
            public Void answer(InvocationOnMock invocation) {
                Runnable runnable = (Runnable) invocation.getArguments()[0];
                runnable.run();
                return null;
            }
        }).when(scheduler).schedule(any(Runnable.class),
                                    eq(DELAY),
                                    eq(TimeUnit.MILLISECONDS));
    }
}
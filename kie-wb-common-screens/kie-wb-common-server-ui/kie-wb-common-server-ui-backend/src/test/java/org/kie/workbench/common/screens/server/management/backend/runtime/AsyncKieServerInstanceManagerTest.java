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

package org.kie.workbench.common.screens.server.management.backend.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.Severity;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.service.NotificationService;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeState;
import org.kie.workbench.common.screens.server.management.model.ContainerUpdateEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AsyncKieServerInstanceManagerTest {

    private ServerTemplate serverTemplate;

    private ContainerSpec containerSpec;

    private AsyncKieServerInstanceManager kieServerInstanceManager;
    @Mock
    private NotificationService notificationService;

    private final List<ContainerUpdateEvent> receivedEvents = new ArrayList<ContainerUpdateEvent>();

    private final List<Container> returnedContainers = new ArrayList<Container>();

    private Event<ContainerUpdateEvent> containerUpdateEvent = new EventSourceMock<ContainerUpdateEvent>() {

        @Override
        public void fire(ContainerUpdateEvent event) {
            receivedEvents.add(event);
        }
    };

    private ExecutorService executor = new ExecutorService() {
        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long l,
                                        TimeUnit timeUnit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> callable) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable runnable,
                                    T t) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable runnable) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection,
                                             long l,
                                             TimeUnit timeUnit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> collection,
                               long l,
                               TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    };

    @Before
    public void prepare() {
        returnedContainers.clear();
        serverTemplate = new ServerTemplate();

        serverTemplate.setName("test server");
        serverTemplate.setId(UUID.randomUUID().toString());

        containerSpec = new ContainerSpec();
        containerSpec.setId("test container");
        containerSpec.setServerTemplateKey(new ServerTemplateKey(serverTemplate.getId(),
                                                                 serverTemplate.getName()));
        containerSpec.setReleasedId(new ReleaseId("org.kie",
                                                  "kie-server-kjar",
                                                  "1.0"));
        containerSpec.setStatus(KieContainerStatus.STOPPED);
        containerSpec.setConfigs(new HashMap<Capability, ContainerConfig>());

        serverTemplate.addContainerSpec(containerSpec);

        this.kieServerInstanceManager = new AsyncKieServerInstanceManager(notificationService,
                                                                          containerUpdateEvent,
                                                                          executor) {
            @Override
            protected List<Container> callRemoteKieServerOperation(ServerTemplate serverTemplate,
                                                                   ContainerSpec containerSpec,
                                                                   RemoteKieServerOperation operation) {
                return returnedContainers;
            }
        };
        this.kieServerInstanceManager.setExecutor(executor);
    }

    @Test
    public void testStartContainerSuccess() {
        testContainerOperationSuccess(ContainerRuntimeOperation.START_CONTAINER);
    }

    @Test
    public void testStartContainerFailure() {
        testContainerOperationFailure(ContainerRuntimeOperation.START_CONTAINER);
    }

    @Test
    public void testStartContainerPartialFailure() {
        testContainerOperationPartialFailure(ContainerRuntimeOperation.START_CONTAINER);
    }

    @Test
    public void testStopContainerSuccess() {
        testContainerOperationSuccess(ContainerRuntimeOperation.STOP_CONTAINER);
    }

    @Test
    public void testStopContainerFailure() {
        testContainerOperationFailure(ContainerRuntimeOperation.STOP_CONTAINER);
    }

    @Test
    public void testStopContainerPartialFailure() {
        testContainerOperationPartialFailure(ContainerRuntimeOperation.STOP_CONTAINER);
    }

    @Test
    public void testScanSuccess() {
        testContainerOperationSuccess(ContainerRuntimeOperation.SCAN);
    }

    @Test
    public void testScanFailure() {
        testContainerOperationFailure(ContainerRuntimeOperation.SCAN);
    }

    @Test
    public void testScanPartialFailure() {
        testContainerOperationPartialFailure(ContainerRuntimeOperation.SCAN);
    }

    @Test
    public void testStartScannerSuccess() {
        testContainerOperationSuccess(ContainerRuntimeOperation.START_SCANNER);
    }

    @Test
    public void testStartScannerFailure() {
        testContainerOperationFailure(ContainerRuntimeOperation.START_SCANNER);
    }

    @Test
    public void testStartScannerPartialFailure() {
        testContainerOperationPartialFailure(ContainerRuntimeOperation.START_SCANNER);
    }

    @Test
    public void testStopScannerSuccess() {
        testContainerOperationSuccess(ContainerRuntimeOperation.STOP_SCANNER);
    }

    @Test
    public void testStopScannerFailure() {
        testContainerOperationFailure(ContainerRuntimeOperation.STOP_SCANNER);
    }

    @Test
    public void testStopScannerPartialFailure() {
        testContainerOperationPartialFailure(ContainerRuntimeOperation.STOP_SCANNER);
    }

    private void testContainerOperationSuccess(ContainerRuntimeOperation operation) {
        List<Message> messages = new ArrayList<Message>();
        returnedContainers.addAll(createContainers(KieContainerStatus.STARTED,
                                                   messages,
                                                   1));

        switch (operation) {
            case STOP_CONTAINER:
                this.kieServerInstanceManager.stopContainer(serverTemplate,
                                                            containerSpec);
                break;
            case START_CONTAINER:
                this.kieServerInstanceManager.startContainer(serverTemplate,
                                                             containerSpec);
                break;
            case UPGRADE_CONTAINER:
                this.kieServerInstanceManager.upgradeContainer(serverTemplate,
                                                               containerSpec);
                break;
            case SCAN:
                this.kieServerInstanceManager.scanNow(serverTemplate,
                                                      containerSpec);
                break;
            case START_SCANNER:
                this.kieServerInstanceManager.startScanner(serverTemplate,
                                                           containerSpec,
                                                           10);
                break;
            case STOP_SCANNER:
                this.kieServerInstanceManager.stopScanner(serverTemplate,
                                                          containerSpec);
                break;
        }

        assertFalse(receivedEvents.isEmpty());
        assertEquals(1,
                     receivedEvents.size());

        ContainerUpdateEvent updateEvent = receivedEvents.get(0);
        assertContainerUpdateEvent(updateEvent,
                                   ContainerRuntimeState.ONLINE,
                                   0);
    }

    private void testContainerOperationFailure(ContainerRuntimeOperation operation) {
        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message(Severity.ERROR,
                                 "No kmodule found"));
        returnedContainers.addAll(createContainers(KieContainerStatus.FAILED,
                                                   messages,
                                                   1));

        switch (operation) {
            case STOP_CONTAINER:
                this.kieServerInstanceManager.stopContainer(serverTemplate,
                                                            containerSpec);
                break;
            case START_CONTAINER:
                this.kieServerInstanceManager.startContainer(serverTemplate,
                                                             containerSpec);
                break;
            case UPGRADE_CONTAINER:
                this.kieServerInstanceManager.upgradeContainer(serverTemplate,
                                                               containerSpec);
                break;
            case SCAN:
                this.kieServerInstanceManager.scanNow(serverTemplate,
                                                      containerSpec);
                break;
            case START_SCANNER:
                this.kieServerInstanceManager.startScanner(serverTemplate,
                                                           containerSpec,
                                                           10);
                break;
            case STOP_SCANNER:
                this.kieServerInstanceManager.stopScanner(serverTemplate,
                                                          containerSpec);
                break;
        }

        assertFalse(receivedEvents.isEmpty());
        assertEquals(1,
                     receivedEvents.size());

        ContainerUpdateEvent updateEvent = receivedEvents.get(0);
        assertContainerUpdateEvent(updateEvent,
                                   ContainerRuntimeState.OFFLINE,
                                   1);
    }

    private void testContainerOperationPartialFailure(ContainerRuntimeOperation operation) {
        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message(Severity.ERROR,
                                 "No kmodule found"));
        returnedContainers.addAll(createContainers(KieContainerStatus.FAILED,
                                                   messages,
                                                   1));

        messages.clear();
        returnedContainers.addAll(createContainers(KieContainerStatus.STARTED,
                                                   messages,
                                                   1));

        switch (operation) {
            case STOP_CONTAINER:
                this.kieServerInstanceManager.stopContainer(serverTemplate,
                                                            containerSpec);
                break;
            case START_CONTAINER:
                this.kieServerInstanceManager.startContainer(serverTemplate,
                                                             containerSpec);
                break;
            case UPGRADE_CONTAINER:
                this.kieServerInstanceManager.upgradeContainer(serverTemplate,
                                                               containerSpec);
                break;
            case SCAN:
                this.kieServerInstanceManager.scanNow(serverTemplate,
                                                      containerSpec);
                break;
            case START_SCANNER:
                this.kieServerInstanceManager.startScanner(serverTemplate,
                                                           containerSpec,
                                                           10);
                break;
            case STOP_SCANNER:
                this.kieServerInstanceManager.stopScanner(serverTemplate,
                                                          containerSpec);
                break;
        }

        assertFalse(receivedEvents.isEmpty());
        assertEquals(1,
                     receivedEvents.size());

        ContainerUpdateEvent updateEvent = receivedEvents.get(0);
        assertContainerUpdateEvent(updateEvent,
                                   ContainerRuntimeState.PARTIAL_ONLINE,
                                   1);
    }

    /*
     * helper methods
     */

    protected void assertContainerUpdateEvent(ContainerUpdateEvent updateEvent,
                                              ContainerRuntimeState state,
                                              int failedInstances) {
        assertEquals(state,
                     updateEvent.getContainerRuntimeState());
        assertEquals(failedInstances,
                     updateEvent.getFailedServerInstances().size());
        assertNotNull(updateEvent.getContainerSpec());
        assertEquals(containerSpec.getId(),
                     updateEvent.getContainerSpec().getId());
        assertNotNull(updateEvent.getServerTemplateKey());
        assertEquals(serverTemplate.getId(),
                     updateEvent.getServerTemplateKey().getId());
    }

    protected List<Container> createContainers(KieContainerStatus status,
                                               List<Message> messages,
                                               int instances) {
        List<Container> containerList = new ArrayList<Container>();
        for (int i = 0; i < instances; i++) {
            Container container = new Container("c" + i,
                                                "name" + i,
                                                new ServerInstanceKey(serverTemplate.getId(),
                                                                      serverTemplate.getName(),
                                                                      serverTemplate.getId(),
                                                                      "http://testurl.com"),
                                                messages,
                                                null,
                                                "");
            container.setStatus(status);

            containerList.add(container);
        }

        return containerList;
    }
}

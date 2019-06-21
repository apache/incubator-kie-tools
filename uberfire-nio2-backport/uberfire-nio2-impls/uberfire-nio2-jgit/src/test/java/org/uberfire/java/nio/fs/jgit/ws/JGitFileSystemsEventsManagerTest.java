/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.java.nio.fs.jgit.ws;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.cluster.ClusterParameters;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ConnectionMode;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.fs.jgit.ws.cluster.JGitEventsBroadcast;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.commons.cluster.ClusterParameters.APPFORMER_JMS_CONNECTION_MODE;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemsEventsManagerTest {

    JGitFileSystemsEventsManager manager;
    JGitEventsBroadcast jGitEventsBroadcastMock = mock(JGitEventsBroadcast.class);

    @Before
    public void setup() {
        setupClusterParameters();
        manager = new JGitFileSystemsEventsManager() {
            @Override
            void setupJGitEventsBroadcast() {
                jGitEventsBroadcast = jGitEventsBroadcastMock;
            }

            @Override
            JGitFileSystemWatchServices createFSWatchServicesManager() {
                return mock(JGitFileSystemWatchServices.class);
            }
        };
    }

    @AfterClass
    public static void clearProperty() {
        System.setProperty(ClusterParameters.APPFORMER_JMS_CONNECTION_MODE,
                           ConnectionMode.NONE.toString());
    }

    @Test
    public void doNotSetupClusterTest() {
        JGitFileSystemsEventsManager another = new JGitFileSystemsEventsManager() {

            @Override
            ClusterService createClusterJMSService() {
                return mock(ClusterJMSService.class);
            }
        };
        assertNull(another.getjGitEventsBroadcast());
    }

    @Test
    public void setupClusterTest() {
        assertNotNull(manager.getjGitEventsBroadcast());
    }

    @Test
    public void createWatchService() {
        manager = new JGitFileSystemsEventsManager() {
            @Override
            void setupJGitEventsBroadcast() {
                jGitEventsBroadcast = jGitEventsBroadcastMock;
            }
        };

        WatchService fs = manager.newWatchService("fs");

        assertNotNull(fs);
        assertTrue(manager.getFsWatchServices().containsKey("fs"));
        verify(jGitEventsBroadcastMock).createWatchServiceJMS("fs");
    }

    @Test
    public void shouldNotPublishEventsForANotWatchedFS() {
        WatchService fsDora = manager.newWatchService("fsDora");
        WatchService fsBento = manager.newWatchService("fsBento");

        List<WatchEvent<?>> elist = Arrays.asList(mock(WatchEvent.class),
                                                  mock(WatchEvent.class));

        manager.publishEvents("another",
                              mock(Path.class),
                              elist);

        verify(jGitEventsBroadcastMock,
               never()).broadcast(any(),
                                  any(),
                                  any());
    }

    @Test
    public void publishEventsShouldBeWatched() {
        WatchService fsDoraWS = manager.newWatchService("fsDora");
        WatchService fsBento = manager.newWatchService("fsBento");

        JGitFileSystemWatchServices fsDoraWServices = manager.getFsWatchServices().get("fsDora");
        JGitFileSystemWatchServices fsBentoWServices = manager.getFsWatchServices().get("fsBento");

        List<WatchEvent<?>> elist = Arrays.asList(mock(WatchEvent.class),
                                                  mock(WatchEvent.class));

        manager.publishEvents("fsDora",
                              mock(Path.class),
                              elist);

        verify(fsDoraWServices).publishEvents(any(),
                                              eq(elist));
        verify(jGitEventsBroadcastMock).broadcast(eq("fsDora"),
                                                  any(),
                                                  eq(elist));
        verify(fsBentoWServices,
               never()).publishEvents(any(),
                                      eq(elist));
    }

    @Test
    public void publishEventsWithoutBroadcast() {
        manager.newWatchService("fsDora");
        manager.newWatchService("fsBento");

        JGitFileSystemWatchServices fsDoraWServices = manager.getFsWatchServices().get("fsDora");
        JGitFileSystemWatchServices fsBentoWServices = manager.getFsWatchServices().get("fsBento");

        List<WatchEvent<?>> elist = Arrays.asList(mock(WatchEvent.class),
                                                  mock(WatchEvent.class));

        manager.publishEvents("fsDora",
                              mock(Path.class),
                              elist,
                              false);

        verify(fsDoraWServices).publishEvents(any(),
                                              eq(elist));
        verify(jGitEventsBroadcastMock,
               never()).broadcast(eq("fsDora"),
                                  any(),
                                  eq(elist));
        verify(fsBentoWServices,
               never()).publishEvents(any(),
                                      eq(elist));
    }

    @Test
    public void watchServicesEvents() {

        manager = new JGitFileSystemsEventsManager() {
            @Override
            void setupJGitEventsBroadcast() {
                jGitEventsBroadcast = jGitEventsBroadcastMock;
            }
        };

        WatchService fsDora1 = manager.newWatchService("fsDora");
        WatchService fsDora2 = manager.newWatchService("fsDora");

        List<WatchEvent<?>> list3events = Arrays.asList(mock(WatchEvent.class),
                                                        mock(WatchEvent.class),
                                                        mock(WatchEvent.class));

        List<WatchEvent<?>> list2events = Arrays.asList(mock(WatchEvent.class),
                                                        mock(WatchEvent.class));

        manager.publishEvents("fsDora",
                              mock(Path.class),
                              list3events,
                              false);

        List<WatchEvent<?>> watchEvents = fsDora1.poll().pollEvents();
        assertEquals(3,
                     watchEvents.size());
        watchEvents = fsDora2.poll().pollEvents();
        assertEquals(3,
                     watchEvents.size());

        manager.publishEvents("fsDora",
                              mock(Path.class),
                              list3events,
                              false);
        manager.publishEvents("fsDora",
                              mock(Path.class),
                              list2events,
                              false);

        watchEvents = fsDora2.poll().pollEvents();
        assertEquals(3,
                     watchEvents.size());

        watchEvents = fsDora2.poll().pollEvents();
        assertEquals(2,
                     watchEvents.size());

        watchEvents = fsDora1.poll().pollEvents();
        assertEquals(3,
                     watchEvents.size());

        watchEvents = fsDora1.poll().pollEvents();
        assertEquals(2,
                     watchEvents.size());
    }

    @Test
    public void closeTest() {
        manager.newWatchService("fsDora");
        manager.newWatchService("fsBento");

        JGitFileSystemWatchServices fsDoraWServices = manager.getFsWatchServices().get("fsDora");
        JGitFileSystemWatchServices fsBentoWServices = manager.getFsWatchServices().get("fsBento");

        manager.close("fsDora");

        verify(fsDoraWServices).close();
        verify(fsBentoWServices,
               never()).close();
    }

    @Test
    public void testShutdown() {
        manager.newWatchService("fsPetra");
        manager.newWatchService("fsEureka");

        JGitFileSystemWatchServices fsPetraWatchService = manager.getFsWatchServices().get("fsPetra");
        JGitFileSystemWatchServices fsEurekaWatchService = manager.getFsWatchServices().get("fsEureka");

        manager.shutdown();

        verify(fsPetraWatchService).close();
        verify(fsEurekaWatchService).close();
        verify(jGitEventsBroadcastMock).close();
    }

    private void setupClusterParameters() {
        System.setProperty(ClusterParameters.APPFORMER_JMS_CONNECTION_MODE,
                           ConnectionMode.REMOTE.toString());
    }
}
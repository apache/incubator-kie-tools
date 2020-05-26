/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NO_IMPL;

public class K8SWatchServiceTest {

    public static KubernetesMockServer SERVER =
            new KubernetesMockServer(new Context(), new MockWebServer(), new HashMap<ServerRequest, Queue<ServerResponse>>(), new KubernetesCrudDispatcher(), false);
    // The default namespace for MockKubernetes Server is 'test'
    protected static String TEST_NAMESPACE = "test";
    protected static ThreadLocal<KubernetesClient> CLIENT_FACTORY;

    protected static final FileSystemProvider fsProvider = new K8SFileSystemProvider() {

        @Override
        public KubernetesClient createKubernetesClient() {
            return CLIENT_FACTORY.get();
        }

    };

    @BeforeClass
    public static void setup() {
        SERVER.init();
        CLIENT_FACTORY = ThreadLocal.withInitial(() -> SERVER.createClient());
        //Checking the operating system before test execution
        Assume.assumeFalse("k8s does not support in Windows platform", System.getProperty("os.name").toLowerCase().contains("windows"));
        // Load testing KieServerState ConfigMap data into mock server from file
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SWatchServiceTest.class.getResourceAsStream("/test-k8sfs-dir-r-configmap.yml"))
                                                     .get());
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SWatchServiceTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                                     .get());
    }

    @AfterClass
    public static void tearDown() {
        CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE).delete();
        CLIENT_FACTORY.get().close();
        SERVER.destroy();
    }

    @Test
    public void testSetup() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fileSystem.getPath("/");
        final Path dir = fileSystem.getPath("/testDir");

        assertThat(root.getFileSystem().provider()).isEqualTo(fsProvider);
        assertThat(Files.exists(root)).isTrue();
        assertThat(Files.exists(dir)).isTrue();
        assertThat(Files.isDirectory(root)).isTrue();
        assertThat(Files.isDirectory(root)).isTrue();
    }

    @Test
    public void testWatchServiceOpenAndClose() throws URISyntaxException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final WatchService ws = fileSystem.newWatchService();
        assertThat(ws.isClose()).isFalse();
        ws.close();
        await().until(ws::isClose);
        assertThat(ws.isClose()).isTrue();
    }

    @Test
    public void testMapActionToKind() {
        assertThat(K8SFileSystemUtils.mapActionToKind(Action.ERROR).isPresent()).isFalse();
        assertThat(K8SFileSystemUtils.mapActionToKind(Action.ADDED).get())
            .isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
        assertThat(K8SFileSystemUtils.mapActionToKind(Action.MODIFIED).get())
            .isEqualTo(StandardWatchEventKind.ENTRY_MODIFY);
        assertThat(K8SFileSystemUtils.mapActionToKind(Action.DELETED).get())
            .isEqualTo(StandardWatchEventKind.ENTRY_DELETE);
    }

    @Test
    public void testWatchKey() {
        final K8SFileSystem fileSystem = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path dir = fileSystem.getPath("/testDir");

        boolean isWSClosed = false;
        K8SWatchKey wk = new K8SWatchKey(new K8SWatchService(fileSystem) {

            @Override
            public boolean isClose() {
                return isWSClosed;
            }
        }, dir);

        assertThat(wk.isValid()).isTrue();
        assertThat(wk.pollEvents()).asList().isEmpty();
        assertThat(wk.watchable()).isEqualTo(dir);
        assertThat(wk.postEvent(StandardWatchEventKind.ENTRY_CREATE)).isTrue();
        assertThat(wk.isQueued()).isFalse();

        wk.signal();
        assertThat(wk.isQueued()).isTrue();
        assertThat(wk.reset()).isTrue();
        assertThat(wk.pollEvents()).asList().isEmpty();
        assertThat(wk.isQueued()).isFalse();

        assertThat(wk.postEvent(StandardWatchEventKind.ENTRY_DELETE)).isTrue();
        wk.signal();
        assertThat(wk.isQueued()).isTrue();

        List<WatchEvent<?>> eventList = wk.pollEvents();
        assertThat(eventList.size()).isEqualTo(1);

        WatchEvent<?> event = eventList.get(0);
        assertThat(event.kind()).isEqualTo(StandardWatchEventKind.ENTRY_DELETE);
        assertThat(event.count()).isEqualTo(1);
        WatchContext wc = (WatchContext) event.context();
        assertThat(wc.getPath()).isEqualTo(dir.getFileName());
        assertThat(wc.getOldPath()).isEqualTo(dir.getFileName());
        assertThat(wc.getSessionId()).isEqualTo(K8S_FS_NO_IMPL);
        assertThat(wc.getMessage()).isEqualTo(K8S_FS_NO_IMPL);
        assertThat(wc.getUser()).isEqualTo(K8S_FS_NO_IMPL);

        wk.cancel();
        assertThat(wk.isValid()).isFalse();
    }
}

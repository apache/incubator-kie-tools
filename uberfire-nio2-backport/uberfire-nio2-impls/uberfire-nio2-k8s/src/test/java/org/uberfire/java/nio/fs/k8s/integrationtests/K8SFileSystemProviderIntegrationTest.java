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

package org.uberfire.java.nio.fs.k8s.integrationtests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.k8s.K8SFileSystem;
import org.uberfire.java.nio.fs.k8s.K8SFileSystemProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class K8SFileSystemProviderIntegrationTest {

    private static final String KUBERNETES_MASTER_API_URL = System.getProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY);
    private static final String KUBERNETES_MASTER_API_TOKEN = System.getProperty(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY);
    private static final String TEST_NAMESPACE = "k8sfsp-test";
    private static KubernetesClient client;

    protected static final FileSystemProvider fsProvider = new K8SFileSystemProvider();

    @BeforeClass
    public static void setup() {
        System.setProperty(Config.KUBERNETES_NAMESPACE_SYSTEM_PROPERTY, TEST_NAMESPACE);

        Config config = new ConfigBuilder()
                .withMasterUrl(KUBERNETES_MASTER_API_URL)
                .withOauthToken(KUBERNETES_MASTER_API_TOKEN)
                .withTrustCerts(true)
                .withNamespace(TEST_NAMESPACE)
                .build();
        client = new DefaultKubernetesClient(config);
        client.namespaces().createOrReplaceWithNew().withNewMetadata()
                                                    .withName(TEST_NAMESPACE)
                                                    .endMetadata()
                                                    .done();
    }

    @After
    public void cleanNamespace() {
        client.configMaps().inNamespace(TEST_NAMESPACE).delete();
    }

    @AfterClass
    public static void tearDown() {
        client.namespaces().withName(TEST_NAMESPACE).delete();
        client.close();
        System.clearProperty(Config.KUBERNETES_NAMESPACE_SYSTEM_PROPERTY);
    }

    @Test
    public void simpleRootFolderCreateDeleteTest() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        Path folderInRootFolder = fileSystem.getPath("/test");

        assertThat(Files.exists(folderInRootFolder)).isFalse();
        assertThat(Files.deleteIfExists(folderInRootFolder)).isFalse();
        Files.createDirectory(folderInRootFolder);
        assertThat(Files.deleteIfExists(folderInRootFolder)).isTrue();
    }

    @Test
    public void simpleRootFileCreateDeleteTest() throws IOException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        Path fileInRootFolder = fileSystem.getPath("/test.txt");

        assertThat(Files.exists(fileInRootFolder)).isFalse();
        assertThat(Files.deleteIfExists(fileInRootFolder)).isFalse();

        createOrEditFile(fileInRootFolder, "Hello");
        assertThat(Files.deleteIfExists(fileInRootFolder)).isTrue();
    }

    @Test
    public void testDeleteRoot() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("k8s:///"));
        final Path testDir = kfs.getPath("/.testDeleRootDir");
        final Path testFile = kfs.getPath("/.testDeleRootDir/.testDeleRootDirFile");
        final Path root = testFile.getRoot();

        String testFileContent = "Hello World";
        createOrEditFile(testFile, testFileContent);

        assertThat(Files.exists(testDir)).isTrue();
        Files.delete(root);
        assertThat(Files.exists(testDir)).isFalse();
        assertThat(Files.exists(testFile)).isFalse();
    }
    
    @Test
    public void testDeleteFolderWithFiles() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("k8s:///"));
        final Path testDir = kfs.getPath("/testDir");
        final Path testFirstFile = kfs.getPath("/testDir/testFirstFile");
        final Path testSecondFile = kfs.getPath("/testDir/testSecondFile");

        String testFileContent = "Hello World";
        createOrEditFile(testFirstFile, testFileContent);
        createOrEditFile(testSecondFile, testFileContent);

        assertThat(Files.exists(testDir)).isTrue();
        Files.delete(testDir);
        assertThat(Files.exists(testDir)).isFalse();
        assertThat(Files.exists(testFirstFile)).isFalse();
        assertThat(Files.exists(testSecondFile)).isFalse();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void simpleRootFolderCreateDuplicateFolderTest() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        Path folderInRootFolder = fileSystem.getPath("/test");

        Files.createDirectory(folderInRootFolder);
        Files.createDirectory(folderInRootFolder);
    }

    @Test
    public void simpleRootFileEditFileTest() throws IOException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        Path fileInRootFolder = fileSystem.getPath("/test.txt");

        createOrEditFile(fileInRootFolder, "Hello");
        assertThat(readFile(fileInRootFolder)).isEqualTo("Hello");
        createOrEditFile(fileInRootFolder, "Welcome");
        assertThat(readFile(fileInRootFolder)).isEqualTo("Welcome");
        createOrEditFile(fileInRootFolder, "Hi");
        assertThat(readFile(fileInRootFolder)).isEqualTo("Hi");
    }

    @Test(expected = NoSuchFileException.class)
    public void inputStreamFromNotExistingFileTest() throws IOException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        Path fileInRootFolder = fileSystem.getPath("/test.txt");

        readFile(fileInRootFolder);
    }

    @Test
    public void testWatchCreateDirectory() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            // Check directory creation events, root is created first
            Files.createDirectory(watchDir);
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            List<WatchEvent<?>> rootEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createRootKey, rootEvents, 1));
            assertThat(rootEvents).asList().hasSize(1);

            WatchEvent<?> firstEvent = rootEvents.get(0);
            assertThat(firstEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstEvent.context()).getPath()).isNull();

            // Watched directory is created then
            WatchKey createWatchDirKey = watcher.poll();
            assertThat(createWatchDirKey.isValid()).isTrue();
            assertThat(createWatchDirKey.watchable()).isEqualTo(watchDir);

            List<WatchEvent<?>> watchDirEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createWatchDirKey, watchDirEvents, 1));
            assertThat(watchDirEvents).asList().hasSize(1);

            WatchEvent<?> firstWatchDirEvent = watchDirEvents.get(0);
            assertThat(firstWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstWatchDirEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstWatchDirEvent.context()).getPath()).isEqualTo(watchDir.getFileName());

            // No more events
            assertThat(watcher.poll()).isNull();
        }
    }

    @Test
    public void testWatchCreateFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path fileInRootFolder = kfs.getPath("/.test.txt");

        try (WatchService watcher = kfs.newWatchService()){
            fileInRootFolder.register(watcher);

            // Check file creation events, root is created first
            createOrEditFile(fileInRootFolder, "Hi");
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            List<WatchEvent<?>> rootEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createRootKey, rootEvents, 1));
            assertThat(rootEvents).asList().hasSize(1);

            WatchEvent<?> firstEvent = rootEvents.get(0);
            assertThat(firstEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstEvent.context()).getPath()).isNull();

            // Watched file is created then
            WatchKey createWatchDirKey = watcher.poll();
            assertThat(createWatchDirKey.isValid()).isTrue();
            assertThat(createWatchDirKey.watchable()).isEqualTo(fileInRootFolder);

            List<WatchEvent<?>> watchDirEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createWatchDirKey, watchDirEvents, 1));
            assertThat(watchDirEvents).asList().hasSize(1);

            WatchEvent<?> firstWatchDirEvent = watchDirEvents.get(0);
            assertThat(firstWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstWatchDirEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstWatchDirEvent.context()).getPath()).isEqualTo(fileInRootFolder.getFileName());

            // No more events
            assertThat(watcher.poll()).isNull();
        }
    }

    @Test
    public void testWatchEditFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path fileInRootFolder = kfs.getPath("/test.txt");

        try (WatchService watcher = kfs.newWatchService()){
            fileInRootFolder.register(watcher);

            // Check file creation events, root is created first, also it is modified as folder tracks size of its content
            createOrEditFile(fileInRootFolder, "Hi");
            createOrEditFile(fileInRootFolder, "Welcome");
            createOrEditFile(fileInRootFolder, "Hello");
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            List<WatchEvent<?>> rootEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createRootKey, rootEvents, 2));
            assertThat(rootEvents).asList().hasSize(2);

            WatchEvent<?> firstEvent = rootEvents.get(0);
            assertThat(firstEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstEvent.context()).getPath()).isNull();

            WatchEvent<?> secondEvent = rootEvents.get(1);
            assertThat(secondEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_MODIFY);
            assertThat(secondEvent.count()).isEqualTo(2);
            assertThat(((WatchContext)secondEvent.context()).getPath()).isNull();

            // Watched file is created and modified then
            WatchKey createWatchDirKey = watcher.poll();
            assertThat(createWatchDirKey.isValid()).isTrue();
            assertThat(createWatchDirKey.watchable()).isEqualTo(fileInRootFolder);

            List<WatchEvent<?>> watchDirEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createWatchDirKey, watchDirEvents, 2));
            assertThat(watchDirEvents).asList().hasSize(2);

            WatchEvent<?> firstWatchDirEvent = watchDirEvents.get(0);
            assertThat(firstWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstWatchDirEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstWatchDirEvent.context()).getPath()).isEqualTo(fileInRootFolder.getFileName());

            WatchEvent<?> secondWatchDirEvent = watchDirEvents.get(1);
            assertThat(secondWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_MODIFY);
            assertThat(secondWatchDirEvent.count()).isEqualTo(2);
            assertThat(((WatchContext)secondWatchDirEvent.context()).getPath()).isEqualTo(fileInRootFolder.getFileName());

            // No more events
            assertThat(watcher.poll()).isNull();
        }
    }

    @Test
    public void testWatchDeleteFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path fileInRootFolder = kfs.getPath("/test.txt");

        try (WatchService watcher = kfs.newWatchService()){
            fileInRootFolder.register(watcher);

            // Check file creation events, root is created first, also it is modified as folder tracks size of its content
            createOrEditFile(fileInRootFolder, "Hi");
            assertThat(Files.deleteIfExists(fileInRootFolder)).isTrue();

            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            List<WatchEvent<?>> rootEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createRootKey, rootEvents, 2));
            assertThat(rootEvents).asList().hasSize(2);

            WatchEvent<?> firstEvent = rootEvents.get(0);
            assertThat(firstEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstEvent.context()).getPath()).isNull();

            WatchEvent<?> secondEvent = rootEvents.get(1);
            assertThat(secondEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_MODIFY);
            assertThat(secondEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)secondEvent.context()).getPath()).isNull();

            // Watched file is created and deleted then
            WatchKey createWatchDirKey = watcher.poll();
            assertThat(createWatchDirKey.isValid()).isTrue();
            assertThat(createWatchDirKey.watchable()).isEqualTo(fileInRootFolder);

            List<WatchEvent<?>> watchDirEvents = new ArrayList<>();
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(fetchWatchEvents(createWatchDirKey, watchDirEvents, 2));
            assertThat(watchDirEvents).asList().hasSize(2);

            WatchEvent<?> firstWatchDirEvent = watchDirEvents.get(0);
            assertThat(firstWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_CREATE);
            assertThat(firstWatchDirEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)firstWatchDirEvent.context()).getPath()).isEqualTo(fileInRootFolder.getFileName());

            WatchEvent<?> thirdWatchDirEvent = watchDirEvents.get(1);
            assertThat(thirdWatchDirEvent.kind()).isEqualTo(StandardWatchEventKind.ENTRY_DELETE);
            assertThat(thirdWatchDirEvent.count()).isEqualTo(1);
            assertThat(((WatchContext)thirdWatchDirEvent.context()).getPath()).isEqualTo(fileInRootFolder.getFileName());

            // No more events
            assertThat(watcher.poll()).isNull();
        }
    }

    @Test
    public void testCancelWatchKey() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            Files.createDirectory(watchDir);
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            createRootKey.cancel();

            assertThat(createRootKey.isValid()).isFalse();
        }
    }

    @Test
    public void testResetWatchKey() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            Files.createDirectory(watchDir);
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            // Reset the key
            assertThat(createRootKey.reset()).isTrue();

            // The creation event has been removed from poll events
            assertThat(createRootKey.pollEvents()).asList().isEmpty();
        }
    }

    @Test
    public void testResetCancelledWatchKey() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            Files.createDirectory(watchDir);
            WatchKey createRootKey = watcher.poll(30, TimeUnit.SECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);

            createRootKey.cancel();
            assertThat(createRootKey.reset()).isFalse();
        }
    }

    @Test
    public void testPollWatchKeyWithTimeout() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            Files.createDirectory(watchDir);
            WatchKey createRootKey = watcher.poll(1, TimeUnit.MILLISECONDS);
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);
        }
    }

    @Test(timeout = 30_000L)
    public void testTakeWatchKey() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = kfs.getPath("/");
        final Path watchDir = kfs.getPath("/watchDir");

        try (WatchService watcher = kfs.newWatchService()){
            watchDir.register(watcher);

            Runnable createDirectory = () -> Files.createDirectory(watchDir);
            Thread createDirectoryThread = new Thread(createDirectory);
            createDirectoryThread.start();

            WatchKey createRootKey = watcher.take();
            assertThat(createRootKey.isValid()).isTrue();
            assertThat(createRootKey.watchable()).isEqualTo(root);
        }
    }

    @Test
    public void testCloseAlreadyClosedWatchService() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        WatchService watcher = kfs.newWatchService();
        watcher.close();
        assertThat(watcher.isClose()).isTrue();
        watcher.close();
        assertThat(watcher.isClose()).isTrue();
    }

    private void createOrEditFile(Path file, String fileContent) throws IOException {
        try (OutputStream fileStream = Files.newOutputStream(file)) {
            fileStream.write(fileContent.getBytes());
            fileStream.flush();
        }
    }

    private String readFile(Path file) throws IOException {
        try (InputStream fileStream = Files.newInputStream(file)) {
            return IOUtils.toString(fileStream, StandardCharsets.UTF_8.name());
        }
    }

    private Callable<Boolean> fetchWatchEvents(WatchKey watchKey, List<WatchEvent<?>> foundEvents, int numberOfEventsExpected) {
        return new Callable<Boolean>() {
            public Boolean call() {
                foundEvents.addAll(watchKey.pollEvents());

                if (foundEvents.size() >= numberOfEventsExpected) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        };
    }
}

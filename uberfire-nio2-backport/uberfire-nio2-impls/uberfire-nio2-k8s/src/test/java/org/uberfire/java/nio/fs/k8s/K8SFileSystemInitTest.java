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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
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
import org.uberfire.java.nio.base.attributes.HiddenAttributes;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFileNameString;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjNameElementLabel;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getPathByFsObjCM;

public class K8SFileSystemInitTest {

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
    
    protected static FileStore fstore;

    @BeforeClass
    public static void setup() {
        SERVER.init();
        CLIENT_FACTORY = ThreadLocal.withInitial(() -> SERVER.createClient());
        //Checking the operating system before test execution
        Assume.assumeFalse("k8s does not support in Windows platform", System.getProperty("os.name").toLowerCase().contains("windows"));
        fstore = fsProvider.getFileSystem(URI.create("default:///")).getFileStores().iterator().next();
    }

    @AfterClass
    public static void tearDown() {
        CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE).delete();
        CLIENT_FACTORY.get().close();
        SERVER.destroy();
    }
    
    @Test
    public void testRoot() throws URISyntaxException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fileSystem.getPath("/");
        Map<String, String> ne = getFsObjNameElementLabel(root);
        
        List<Path> roots = StreamSupport.stream(fileSystem.getRootDirectories().spliterator(), false)
                                        .collect(Collectors.toList());
        assertThat(roots).asList().size().isEqualTo(1);
        assertThat(roots.get(0)).isEqualTo(root);

        /**
         * Handling this hard coded dependency
         * https://github.com/kiegroup/appformer/blob/92d05f8620fb775a9fdd96574273de2deda3d215/uberfire-structure/uberfire-structure-backend/src/main/java/org/guvnor/structure/backend/config/ConfigurationServiceImpl.java#L129
         */
        assertThat(root.toUri().toString().contains("/master@")).isTrue();
        assertThat(root).isEqualTo(fileSystem.getPath("/path").getRoot());
        assertThat(root).isEqualTo(fileSystem.getPath("/path").getParent());
        assertThat(root.getRoot().equals(root)).isTrue();
        assertThat(root.toString().equals("/")).isTrue();
        assertThat(root.toRealPath().toString().equals("/")).isTrue();
        assertThat(root.getParent()).isNull();
        assertThat(root.getFileName()).isNull();
        assertThat(root.getNameCount()).isEqualTo(0);
        assertThat(root.iterator().hasNext()).isEqualTo(false);
        assertThat(ne.size()).isEqualTo(0);
        assertThat(getFileNameString(root).equals("/")).isTrue();
    }

    @Test
    public void testInitRoot() {
        final FileSystem fs = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fs.getPath("/");
        final Path testParentDir = fs.getPath("/.testParentDir");
        final Path testDir = fs.getPath("/.testParentDir/.testInitRoot");
        final Path rPath = fs.getPath("./../.testInitRoot");
        
        assertThat(root.getParent()).isNull();
        assertThat(root.isAbsolute()).isTrue();

        assertThat(rPath.isAbsolute()).isFalse();
        assertThat(rPath.getRoot()).isNull();
        
        assertThat(testDir.isAbsolute()).isTrue();
        assertThat(testDir.getParent()).isEqualTo(testParentDir);
        assertThat(testDir.getRoot()).isEqualTo(root);
        assertThat(testDir.getFileName()).isEqualTo(rPath.getFileName());
        
        Path aPath = ((K8SFileSystemProvider)fsProvider).toAbsoluteRealPath(rPath);
        assertThat(aPath.getRoot()).isEqualTo(root);
        assertThat(aPath.getParent()).isNotNull();
        assertThat(aPath.isAbsolute()).isTrue();
        assertThat(aPath.getFileName()).isEqualTo(rPath.getFileName());
                                                  
        assertThat(testDir.isAbsolute()).isTrue();
        assertThat(testDir.getParent()).isEqualTo(testParentDir);
        
        assertThat(testParentDir.getParent()).isEqualTo(root);
        assertThat(testParentDir).isEqualTo(((K8SFileSystemProvider)fsProvider).toAbsoluteRealPath(testParentDir));
        
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-dir-r-empty-configmap.yml"))
                                                     .get());
        assertThat(Files.exists(root)).isTrue();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().isEmpty();
        }
        
        CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE).delete();
        assertThat(Files.exists(root)).isFalse();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().isEmpty();
        }
        assertThat(Files.exists(root)).isTrue();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), root).getData()).isNotNull();
        
        Files.createDirectory(testDir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testParentDir)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().containsExactly(testDir);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAmbiguousCM() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path file = fileSystem.getPath("/testDir/testFile");
        
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-file-configmap.yml"))
                                                     .get());
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-file-dup-configmap.yml"))
                                                     .get());        
        getFsObjCM(CLIENT_FACTORY.get(), file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFsObjCM() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final ConfigMap cm = CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-file-invalid-configmap.yml"))
                                                     .get();
        getPathByFsObjCM((K8SFileSystem)fileSystem, cm);
    }
    
    @Test
    public void testFileStore() {
        final K8SFileSystem fs = (K8SFileSystem)fsProvider.getFileSystem(URI.create("default:///"));
        fs.lock();
        fs.unlock();
        assertThat(fs).isNotNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFileStoreGetTotalSpace() {
        fstore.getTotalSpace();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testFileStoreGetUsableSpace() {
        fstore.getUsableSpace();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testFileStoreGetUnallocatedSpace() {
        fstore.getUnallocatedSpace();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testNewFileChannel() {
        final FileSystem fs = fsProvider.getFileSystem(URI.create("default:///"));
        fsProvider.newFileChannel(fs.getPath("/"), null);
    }
    
    @Test
    public void testGetFileStore() {
        final FileSystem fs = fsProvider.getFileSystem(URI.create("default:///"));
        FileStore fstore = fsProvider.getFileStore(fs.getPath("/"));
        assertThat(K8SFileStore.class.isInstance(fstore)).isTrue();
    }
    
    @Test(expected = FileAlreadyExistsException.class)
    public void testCheckFileExistsThenThrow() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path dir = fileSystem.getPath("/testDir");

        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                                     .get());
        fsProvider.createDirectory(dir);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testCopyDirShouldFail() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path source = fileSystem.getPath("/testDir");
        final Path target = fileSystem.getPath("/testDir2");
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                                     .get());
        fsProvider.copy(source, target);
    }
    
    @Test
    public void testReadAttributes() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path dir = fileSystem.getPath("/testDir");
        
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemInitTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                                     .get());
        assertThat(fsProvider.readAttributes(dir, HiddenAttributes.class)).isNull();
        assertThat(fsProvider.readAttributes(dir, BasicFileAttributes.class)).isNotNull();
        assertThat(fsProvider.readAttributes(dir, BasicFileAttributes.class).isDirectory()).isTrue();
        assertThat(fsProvider.readAttributes(dir, BasicFileAttributes.class).isRegularFile()).isFalse();
        assertThat(fsProvider.readAttributes(dir, BasicFileAttributes.class).isSymbolicLink()).isFalse();
        assertThat(fsProvider.readAttributes(dir, BasicFileAttributes.class).size()).isEqualTo(19);
    }
}

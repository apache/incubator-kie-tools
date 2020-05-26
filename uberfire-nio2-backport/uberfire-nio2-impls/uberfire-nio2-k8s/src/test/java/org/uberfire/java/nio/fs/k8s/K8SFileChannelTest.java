/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashMap;
import java.util.Queue;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.cloud.CloudClientFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class K8SFileChannelTest {

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
    }

    @AfterClass
    public static void tearDown() {
        CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE).delete();
        CLIENT_FACTORY.get().close();
        SERVER.destroy();
    }

    @SuppressWarnings("resource")
    @Test(expected = IOException.class)
    public void testOpenChannelWithInitializationErrors() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("k8s:///"));
        final Path invalid = kfs.getPath("/#weirdFileName$@#^&*");
        new K8SFileChannel(invalid, (CloudClientFactory) fsProvider);
    }

    @Test
    public void testOpenAndCloseChannel() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("k8s:///"));
        final Path valid = kfs.getPath("/test");
        K8SFileChannel k8sfc = new K8SFileChannel(valid, (CloudClientFactory) fsProvider);
        assertTrue(k8sfc.isOpen());
        k8sfc.close();
        assertFalse(k8sfc.isOpen());
    }

    @Test
    public void testChannelMustBeClosedRegardlessError() {
        FileSystemProvider fsProvider = new K8SFileSystemProvider() {

            @Override
            public KubernetesClient createKubernetesClient() {
                return CLIENT_FACTORY.get();
            }
        };

        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("k8s:///"));
        final Path valid = kfs.getPath("/test");
        K8SFileChannel k8sfc = new K8SFileChannel(valid, (CloudClientFactory) fsProvider);
        assertTrue(k8sfc.isOpen());

        k8sfc.ccf = null; // trigger an error
        try {
            k8sfc.close();
            fail("Channel close should throw an exception.");
        } catch (Exception e) {
        }
        assertFalse(k8sfc.isOpen());
    }
}

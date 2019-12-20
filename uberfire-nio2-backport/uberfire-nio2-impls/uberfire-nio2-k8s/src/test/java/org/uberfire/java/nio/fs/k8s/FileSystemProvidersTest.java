/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.api.FileSystemUtils;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class FileSystemProvidersTest {
    

    @Test
    public void generalTests() {
        assertThat(FileSystemProviders.installedProviders()).hasSize(2);
        assertThat(FileSystemProviders.getDefaultProvider()).isInstanceOf(SimpleFileSystemProvider.class);

        assertThat(FileSystemProviders.resolveProvider(URI.create("default:///"))).isInstanceOf(SimpleFileSystemProvider.class);
        assertThat(FileSystemProviders.resolveProvider(URI.create("file:///"))).isInstanceOf(SimpleFileSystemProvider.class);
        assertThat(FileSystemProviders.resolveProvider(URI.create("k8s:///"))).isInstanceOf(K8SFileSystemProvider.class);
    }
    
    @Test
    public void k8sFileSystemProivdeAsDefaultTests() {
        FileSystemUtils.getConfigProps().setProperty(FileSystemUtils.CFG_KIE_CONTROLLER_OCP_ENABLED, "true");
        assertThat(FileSystemProviders.resolveProvider(URI.create("default:///"))).isInstanceOf(K8SFileSystemProvider.class);
        assertThat(FileSystemProviders.resolveProvider(URI.create("k8s:///"))).isInstanceOf(K8SFileSystemProvider.class);
    }

}

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

import java.util.Collections;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.cloud.CloudClientFactory;

import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_FSOBJ_CONTENT_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_MAX_CAPACITY_PROPERTY_NAME;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceParentDirFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjCM;

public class K8SFileChannel extends SeekableInMemoryByteChannel {

    private static final int CAPACITY = Integer.parseInt(System.getProperty(K8S_FS_MAX_CAPACITY_PROPERTY_NAME,
                                                                            String.valueOf(100 * 1024)));
    protected CloudClientFactory ccf;
    private Path file;

    public K8SFileChannel(Path file, CloudClientFactory ccf) {
        super(CAPACITY);
        this.file = file;
        this.ccf = ccf;
        // Constructor is not necessarily Thread-Safe as per JLS (Java Language Specification)
        synchronized (this) {
            try {
                this.contents = ccf.executeCloudFunction(client -> getFsObjCM(client, file), KubernetesClient.class)
                                   .filter(K8SFileSystemUtils::isFile)
                                   .map(K8SFileSystemUtils::getFsObjContentBytes)
                                   .orElse(new byte[0]);
            } catch (Exception e) {
                this.ccf = null;
                this.file = null;
                super.close();
                throw e;
            }
        }
    }

    @Override
    public void close() {
        try {
            ccf.executeCloudFunction(client -> createOrReplaceFSCM(client,
                                                                   file,
                                                                   createOrReplaceParentDirFSCM(client, file, size(), false),
                                                                   Collections.singletonMap(CFG_MAP_FSOBJ_CONTENT_KEY,
                                                                                            super.toString()),
                                                                   false),
                                     KubernetesClient.class);
        } finally {
            this.ccf = null;
            this.file = null;
            super.close();
        }
    }
}

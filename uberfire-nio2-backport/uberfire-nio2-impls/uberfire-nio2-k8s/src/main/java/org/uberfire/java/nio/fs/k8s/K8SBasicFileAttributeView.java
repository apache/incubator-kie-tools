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

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.FileTimeImpl;
import org.uberfire.java.nio.base.LazyAttrLoader;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.cloud.CloudClientFactory;
import org.uberfire.java.nio.fs.file.SimpleBasicFileAttributeView;

import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NO_IMPL;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjCM;

public class K8SBasicFileAttributeView extends SimpleBasicFileAttributeView {
    
    private static final Logger logger = LoggerFactory.getLogger(K8SBasicFileAttributeView.class);

    private BasicFileAttributes attrs = null;
    private final CloudClientFactory ccf;

    public K8SBasicFileAttributeView(final Path path, final CloudClientFactory ccf) {
        super(path);
        this.ccf = ccf;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BasicFileAttributes> T readAttributes() {
        if (attrs == null) {
            final ConfigMap fileCM = ccf.executeCloudFunction(client -> getFsObjCM(client, path),
                                                              KubernetesClient.class)
                                        .orElseThrow(() -> new NoSuchFileException(path.toRealPath().toString()));

            this.attrs = new BasicFileAttributesImpl(path.toString(),
                                                     new FileTimeImpl(K8SFileSystemUtils.getLastModifiedTime(fileCM)),
                                                     new FileTimeImpl(K8SFileSystemUtils.getCreationTime(fileCM)),
                                                     null,
                                                     new LazyAttrLoader<Long>() {
                                                        private Long size = null;
                                                         @Override
                                                         public Long get() {
                                                             if (size == null) {
                                                                 size = K8SFileSystemUtils.getSize(fileCM);
                                                             }
                                                             return size;
                                                         }
                                                     },
                                                     K8SFileSystemUtils.isFile(fileCM),
                                                     K8SFileSystemUtils.isDirectory(fileCM));
        }
        return (T) attrs;
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        logger.debug(K8S_FS_NO_IMPL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{BasicFileAttributeView.class, K8SBasicFileAttributeView.class};
    }
}

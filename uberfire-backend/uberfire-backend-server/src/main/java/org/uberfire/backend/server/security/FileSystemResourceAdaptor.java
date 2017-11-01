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

package org.uberfire.backend.server.security;

import org.uberfire.backend.authz.FileSystemResourceType;
import org.uberfire.java.nio.file.FileSystemMetadata;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.RuntimeContentResource;

public class FileSystemResourceAdaptor implements RuntimeContentResource {

    public static final FileSystemResourceType RESOURCE_TYPE = new FileSystemResourceType();

    private final String identifier;
    private FileSystemMetadata fileSystemMetadata;

    public FileSystemResourceAdaptor(final FileSystemMetadata fsFileSystemMetadata) {
        this.fileSystemMetadata = fsFileSystemMetadata;
        if (fsFileSystemMetadata.isAFileSystemID()) {
            identifier = fsFileSystemMetadata.getId();
        } else {
            identifier = fsFileSystemMetadata.getUri();
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    FileSystemMetadata getFileSystemMetadata() {
        return fileSystemMetadata;
    }

    @Override
    public ResourceType getResourceType() {
        return RESOURCE_TYPE;
    }
}

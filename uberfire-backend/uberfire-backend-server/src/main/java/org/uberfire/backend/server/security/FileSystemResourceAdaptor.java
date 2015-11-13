/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.util.Collection;
import java.util.Collections;

import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.authz.RuntimeContentResource;

public class FileSystemResourceAdaptor implements RuntimeContentResource {

    private final FileSystem fileSystem;

    public FileSystemResourceAdaptor( FileSystem fileSystem ) {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String getSignatureId() {
        if ( fileSystem instanceof FileSystemId ) {
            return ( (FileSystemId) fileSystem ).id();
        }
        return fileSystem.toString();
    }

    @Override
    public Collection<String> getGroups() {

        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }
}

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

package org.uberfire.java.nio.file;

import java.io.IOException;
import java.nio.file.Paths;

import org.uberfire.java.nio.base.FileSystemId;

public class FileSystemMetadata {

    private String scheme;
    private final String uri;
    private boolean isAFileSystemID;
    private String id;

    public FileSystemMetadata(FileSystem fs) {
        if (fs.getRootDirectories().iterator().hasNext()) {
            Path root = fs.getRootDirectories().iterator().next();
            final FileSystem realFS = root.getFileSystem();

            isAFileSystemID = fs instanceof FileSystemId;
            if (isAFileSystemID) {
                id = ((FileSystemId) realFS).id();
            } else {
                id = fs.toString();
            }
            this.scheme = root.toUri().getScheme();
        }
        this.uri = fs.toString();
    }

    public boolean isAFileSystemID() {
        return isAFileSystemID;
    }

    public String getId() {
        return id;
    }

    public String getScheme() {
        return scheme;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileSystemMetadata that = (FileSystemMetadata) o;

        if (isAFileSystemID != that.isAFileSystemID) {
            return false;
        }
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) {
            return false;
        }
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) {
            return false;
        }
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (isAFileSystemID ? 1 : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public void closeFS() throws IOException {
        java.nio.file.Path path = Paths.get(uri);
        java.nio.file.FileSystem fileSystem = path.getFileSystem();
        fileSystem.close();
    }
}

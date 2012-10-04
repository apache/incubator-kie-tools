/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.fs.jgit;

import org.eclipse.jgit.lib.ObjectId;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

public class JGitFileAttributes implements BasicFileAttributes {

    private final String objectId;
    private final FileTime lastModifiedTime;
    private final FileTime creationTime;
    private final long size;
    private final boolean isRegularFile;
    private final boolean isDirectory;

    public JGitFileAttributes(final String objectId, final FileTime lastModifiedTime, final FileTime creationTime,
            final long size, final boolean isRegularFile, final boolean isDirectory) {
        this.objectId = objectId;
        this.lastModifiedTime = lastModifiedTime;
        this.creationTime = creationTime;
        this.size = size;
        this.isRegularFile = isRegularFile;
        this.isDirectory = isDirectory;
    }

    @Override
    public FileTime lastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public FileTime lastAccessTime() {
        return null;
    }

    @Override
    public FileTime creationTime() {
        return creationTime;
    }

    @Override
    public boolean isRegularFile() {
        return isRegularFile;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public Object fileKey() {
        return objectId;
    }
}

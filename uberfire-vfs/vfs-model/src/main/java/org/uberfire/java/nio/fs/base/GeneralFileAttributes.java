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

package org.uberfire.java.nio.fs.base;

import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.kie.commons.validation.PortablePreconditions.*;

public class GeneralFileAttributes implements BasicFileAttributes {

    private final Path path;
    private final FileTime lastModifiedTime;
    private final boolean isRegularFile;
    private final boolean isDirectory;
    private final boolean isHidden;
    private final boolean isExecutable;
    private final boolean isReadable;
    private long fileLenght = -1;

    GeneralFileAttributes(final Path path) {
        this.path = checkNotNull("path", path);
        this.lastModifiedTime = new FileTimeImpl(path.toFile().lastModified());
        this.isRegularFile = path.toFile().isFile();
        this.isDirectory = path.toFile().isDirectory();
        this.isHidden = path.toFile().isHidden();
        this.isExecutable = path.toFile().canExecute();
        this.isReadable = path.toFile().canRead();
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
        return null;
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
        if (fileLenght == -1) {
            fileLenght = path.toFile().length();
        }
        return fileLenght;
    }

    @Override
    public Object fileKey() {
        return null;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public boolean isHidden() {
        return isHidden;
    }
}

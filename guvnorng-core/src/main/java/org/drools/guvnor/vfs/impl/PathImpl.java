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

package org.drools.guvnor.vfs.impl;

import org.drools.guvnor.vfs.Path;
import org.drools.java.nio.file.attribute.FileTime;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PathImpl extends SimplePathImpl implements Path {

    private boolean exists;
    private boolean isFile;
    private boolean isDirectory;
    private boolean isAbsolute;
    private boolean isSymbolicLink;
    private boolean isReadable;
    private boolean isWritable;
    private boolean isHidden;
    //private FileTime lastModifiedTime;

    public PathImpl() {
    }

    public PathImpl(final String fileName, final String uri,
            final boolean exists, final boolean isFile, final boolean isDirectory,
            final boolean isAbsolute, final boolean isSymbolicLink, final boolean isReadable,
            final boolean isWritable, final boolean isHidden, final FileTime lastModifiedTime) {
        super(fileName, uri);
        this.exists = exists;
        this.isFile = isFile;
        this.isDirectory = isDirectory;
        this.isAbsolute = isAbsolute;
        this.isSymbolicLink = isSymbolicLink;
        this.isReadable = isReadable;
        this.isWritable = isWritable;
        this.isHidden = isHidden;
        //this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public boolean isFile() {
        return isFile;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @Override
    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    @Override
    public boolean isReadable() {
        return isReadable;
    }

    @Override
    public boolean isWritable() {
        return isWritable;
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public FileTime getLastModifiedTime() {
//        return lastModifiedTime;
        return null;
    }

    @Override public String toString() {
        return "PathImpl{" +
                "uri='" + uri + '\'' +
                ", exists=" + exists +
                ", isFile=" + isFile +
                ", isDirectory=" + isDirectory +
                ", isAbsolute=" + isAbsolute +
                ", isSymbolicLink=" + isSymbolicLink +
                ", isReadable=" + isReadable +
                ", isWritable=" + isWritable +
                ", isHidden=" + isHidden +
                '}';
    }
}

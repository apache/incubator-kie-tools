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

import java.util.Map;

import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileTime;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class BasicAttributesVO implements BasicFileAttributes {

    private boolean isRegularFile;
    private boolean isDirectory;
    private boolean isSymbolicLink;
    private boolean isOther;
    private Long fileLenght;
    //    private FileTime lastModifiedTime;
//    private FileTime lastAccessTime;
//    private FileTime creationTime;
    private Object fileKey;
    private Boolean exists = null;
    private Boolean isExecutable = null;
    private Boolean isReadable = null;
    private Boolean isHidden = null;

    public BasicAttributesVO() {
    }

    public BasicAttributesVO(Map attrs) {
        this.isRegularFile = (Boolean) attrs.get("isRegularFile");
        this.isDirectory = (Boolean) attrs.get("isDirectory");
        this.isOther = (Boolean) attrs.get("isOther");
        this.isSymbolicLink = (Boolean) attrs.get("isSymbolicLink");
        this.fileKey = attrs.get("fileKey");
//        this.creationTime = creationTime;
//        this.lastAccessTime = lastAccessTime;
//        this.lastModifiedTime = lastModifiedTime;
        this.fileLenght = (Long) attrs.get("fileLenght");
        this.exists = (Boolean) attrs.get("exists");
        this.isReadable = (Boolean) attrs.get("isReadable");
        this.isExecutable = (Boolean) attrs.get("isExecutable");
        this.isHidden = (Boolean) attrs.get("isHidden");
    }

    public BasicAttributesVO(final boolean isRegularFile, final boolean isDirectory, final boolean isOther, final boolean isSymbolicLink,
            final Object fileKey, final FileTime creationTime, final FileTime lastAccessTime, final FileTime lastModifiedTime, final long fileLenght) {
        this(isRegularFile, isDirectory, isOther, isSymbolicLink, fileKey, creationTime, lastAccessTime, lastModifiedTime, fileLenght, null, null, null, null);
    }

    public BasicAttributesVO(final boolean isRegularFile, final boolean isDirectory, final boolean isOther, final boolean isSymbolicLink,
            final Object fileKey, final FileTime creationTime, final FileTime lastAccessTime, final FileTime lastModifiedTime, final long fileLenght,
            final Boolean exists, final Boolean isReadable, final Boolean isExecutable, final Boolean isHidden) {
        this.isRegularFile = isRegularFile;
        this.isDirectory = isDirectory;
        this.isOther = isOther;
        this.isSymbolicLink = isSymbolicLink;
        this.fileKey = fileKey;
//        this.creationTime = creationTime;
//        this.lastAccessTime = lastAccessTime;
//        this.lastModifiedTime = lastModifiedTime;
        this.fileLenght = fileLenght;

        this.exists = exists;
        this.isReadable = isReadable;
        this.isExecutable = isExecutable;
        this.isHidden = isHidden;
    }

    @Override
    public FileTime lastModifiedTime() {
        //return lastModifiedTime;
        return null;
    }

    @Override
    public FileTime lastAccessTime() {
//        return lastAccessTime;
        return null;
    }

    @Override
    public FileTime creationTime() {
//        return creationTime;
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
        return isSymbolicLink;
    }

    @Override
    public boolean isOther() {
        return isOther;
    }

    @Override
    public long size() {
        return fileLenght;
    }

    @Override
    public Object fileKey() {
        return fileKey;
    }

    public boolean exists() {
        return exists;
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

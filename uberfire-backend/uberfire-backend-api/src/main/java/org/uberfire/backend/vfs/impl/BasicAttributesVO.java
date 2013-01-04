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

package org.uberfire.backend.vfs.impl;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.kie.commons.java.nio.file.attribute.FileTime;

@Portable
public class BasicAttributesVO implements BasicFileAttributes {

    private boolean isRegularFile;
    private boolean isDirectory;
    private boolean isSymbolicLink;
    private boolean isOther;
    private Long    size;
    //    private FileTime lastModifiedTime;
//    private FileTime lastAccessTime;
//    private FileTime creationTime;
    private Object  fileKey;

    public BasicAttributesVO() {
    }

    public BasicAttributesVO( Map attrs ) {
        this.isRegularFile = (Boolean) attrs.get( "isRegularFile" );
        this.isDirectory = (Boolean) attrs.get( "isDirectory" );
        this.fileKey = attrs.get( "fileKey" );
//        this.isOther = (Boolean) attrs.get( "isOther" );
//        this.isSymbolicLink = (Boolean) attrs.get( "isSymbolicLink" );
//        this.creationTime = creationTime;
//        this.lastAccessTime = lastAccessTime;
//        this.lastModifiedTime = lastModifiedTime;
        this.size = (Long) attrs.get( "size" );
    }

    public BasicAttributesVO( final boolean isRegularFile,
                              final boolean isDirectory,
                              final boolean isOther,
                              final boolean isSymbolicLink,
                              final Object fileKey,
                              final FileTime creationTime,
                              final FileTime lastAccessTime,
                              final FileTime lastModifiedTime,
                              final long fileLenght ) {
        this( isRegularFile, isDirectory, isOther, isSymbolicLink, fileKey, creationTime, lastAccessTime, lastModifiedTime, fileLenght, null, null, null, null );
    }

    public BasicAttributesVO( final boolean isRegularFile,
                              final boolean isDirectory,
                              final boolean isOther,
                              final boolean isSymbolicLink,
                              final Object fileKey,
                              final FileTime creationTime,
                              final FileTime lastAccessTime,
                              final FileTime lastModifiedTime,
                              final long fileLenght,
                              final Boolean exists,
                              final Boolean isReadable,
                              final Boolean isExecutable,
                              final Boolean isHidden ) {
        this.isRegularFile = isRegularFile;
        this.isDirectory = isDirectory;
        this.isOther = isOther;
        this.isSymbolicLink = isSymbolicLink;
        this.fileKey = fileKey;
//        this.creationTime = creationTime;
//        this.lastAccessTime = lastAccessTime;
//        this.lastModifiedTime = lastModifiedTime;
        this.size = fileLenght;
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
        return size;
    }

    @Override
    public Object fileKey() {
        return fileKey;
    }
}

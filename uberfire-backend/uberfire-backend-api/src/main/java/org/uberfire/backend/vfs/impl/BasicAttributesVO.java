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

import java.util.Date;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.BasicFileAttributes;

@Portable
public class BasicAttributesVO implements BasicFileAttributes {

    private boolean isRegularFile;
    private boolean isDirectory;
    private Long size;
    private Date lastModifiedTime;
    private Date lastAccessTime;
    private Date creationTime;

    public BasicAttributesVO() {
    }

    public BasicAttributesVO( final Map<String, ?> attrs ) {
        this.isRegularFile = (Boolean) attrs.get( "isRegularFile" );
        this.isDirectory = (Boolean) attrs.get( "isDirectory" );
        this.creationTime = (Date) attrs.get( "creationTime" );
        this.lastAccessTime = (Date) attrs.get( "lastAccessTime" );
        this.lastModifiedTime = (Date) attrs.get( "lastModifiedTime" );
        this.size = (Long) attrs.get( "size" );
    }

    @Override
    public Date lastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public Date lastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Date creationTime() {
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
    public long size() {
        return size;
    }
}

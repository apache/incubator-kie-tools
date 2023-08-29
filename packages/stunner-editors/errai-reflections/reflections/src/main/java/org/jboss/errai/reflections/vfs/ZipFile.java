/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/** an implementation of {@link org.jboss.errai.reflections.vfs.Vfs.File} for {@link java.util.zip.ZipEntry} */
public class ZipFile implements Vfs.File {
    private final ZipDir dir;
    private final ZipEntry entry;

    public ZipFile(final ZipDir dir, ZipEntry entry) {
        this.dir = dir;
        this.entry = entry;
    }

    public String getFullPath() {
        return dir.getPath() + "/" + entry.getName();
    }

    public String getName() {
        String name = entry.getName();
        return name.substring(name.lastIndexOf("/") + 1);
    }

    public String getPath() {
        return dir.getPath();
    }

    public String getRelativePath() {
        return entry.getName();
    }

    public InputStream openInputStream() throws IOException {
        return dir.zipFile.getInputStream(entry);
    }

    @Override
    public String toString() {
        return dir.getPath() + "!" + java.io.File.separatorChar + entry.toString();
    }
}

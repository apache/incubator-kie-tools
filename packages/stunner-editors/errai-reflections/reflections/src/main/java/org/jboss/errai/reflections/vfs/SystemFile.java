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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/** an implementation of {@link org.jboss.errai.reflections.vfs.Vfs.File} for a directory {@link java.io.File} */
public class SystemFile implements Vfs.File {
    private final SystemDir dir;
    private final java.io.File file;

    public SystemFile(final SystemDir dir, java.io.File file) {
        this.dir = dir;
        this.file = file;
    }

    public String getFullPath() {
        return file.getPath();
    }

    public String getName() {
        return file.getName();
    }

    public String getRelativePath() {
        if (file.getPath().startsWith(dir.getPath())) {
            return file.getPath().substring(dir.getPath().length() + 1).replace('\\', '/');
        }

        return null; //should not get here
    }

    public InputStream openInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return file.toString();
    }
}

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
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;

import com.google.common.collect.AbstractIterator;

/** an implementation of {@link org.jboss.errai.reflections.vfs.Vfs.Dir} for {@link java.util.zip.ZipFile} */
public class ZipDir implements Vfs.Dir {
    final java.util.zip.ZipFile zipFile;
    private String path;

    public ZipDir(URL url) {
        this(url.getPath());
    }

    public ZipDir(String p) {
        path = p;
        if (path.startsWith("jar:")) { path = path.substring("jar:".length()); }
        if (path.startsWith("file:")) { path = path.substring("file:".length()); }
        if (path.endsWith("!/")) { path = path.substring(0, path.lastIndexOf("!/")) + "/"; }

        try { zipFile = new java.util.zip.ZipFile(this.path); }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public String getPath() {
        return path;
    }

    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    protected Vfs.File computeNext() {
                        return entries.hasMoreElements() ? new ZipFile(ZipDir.this, entries.nextElement()) : endOfData();
                    }
                };
            }
        };
    }

    public void close() {
        if (zipFile != null) {
            try {zipFile.close();}
            catch (IOException e) {throw new RuntimeException("could not close zip file " + path, e);}
        }
    }

    @Override
    public String toString() {
        return zipFile.getName();
    }
}

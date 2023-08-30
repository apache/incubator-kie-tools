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

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

/** an implementation of {@link org.jboss.errai.reflections.vfs.Vfs.Dir} for directory {@link java.io.File} */
public class SystemDir implements Vfs.Dir {
    private final File file;

    public SystemDir(URL url) {
        file = new File(Vfs.normalizePath(url));
    }

    public String getPath() {
        return file.getPath();
    }

    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Stack<File> stack = new Stack<File>();
                    {stack.addAll(listFiles(file));}

                    protected Vfs.File computeNext() {
                        while (!stack.isEmpty()) {
                            final File file = stack.pop();
                            if (file.isDirectory()) {
                                stack.addAll(listFiles(file));
                            } else {
                                return new SystemFile(SystemDir.this, file);
                            }
                        }

                        return endOfData();
                    }
                };
            }
        };
    }

    public void close() {
    }

    @Override
    public String toString() {
        return file.toString();
    }

    private static List<File> listFiles(final File file) {
        File[] files = file.listFiles();

        if (files != null)
            return Lists.newArrayList(files);
        else
            return Lists.newArrayList();
    }
}

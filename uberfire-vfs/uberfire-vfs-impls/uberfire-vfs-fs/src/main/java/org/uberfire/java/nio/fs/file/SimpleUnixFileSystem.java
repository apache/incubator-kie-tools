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

package org.uberfire.java.nio.fs.file;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class SimpleUnixFileSystem extends BaseSimpleFileSystem {

    final FileStore fileStore = new SimpleUnixFileStore(null);

    SimpleUnixFileSystem(final FileSystemProvider provider, final String path) {
        super(provider, path);
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return new Iterable<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {
                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < 1;
                    }

                    @Override public Path next() {
                        if (i < 1) {
                            i++;
                            return getPath("/");
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return new Iterable<FileStore>() {
            @Override
            public Iterator<FileStore> iterator() {
                return new Iterator<FileStore>() {
                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < 1;
                    }

                    @Override public FileStore next() {
                        if (i < 1) {
                            i++;
                            return fileStore;
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}

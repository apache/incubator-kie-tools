/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class SimpleWindowsFileSystem extends BaseSimpleFileSystem {

    SimpleWindowsFileSystem( final FileSystemProvider provider,
                             final String path ) {
        super( provider, path );
    }

    SimpleWindowsFileSystem( final File[] roots,
                             final FileSystemProvider provider,
                             final String path ) {
        super( roots, provider, path );
    }

    @Override
    public String getSeparator() {
        return "\\";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return new Iterable<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {

                    int i = -1;

                    @Override
                    public boolean hasNext() {
                        return ( i + 1 ) < listRoots().length;
                    }

                    @Override
                    public Path next() {
                        i++;
                        if ( i >= listRoots().length ) {
                            throw new NoSuchElementException();
                        }

                        return getPath( listRoots()[ i ].toString() );
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
                    int i = -1;

                    @Override
                    public boolean hasNext() {
                        return ( i + 1 ) < listRoots().length;
                    }

                    @Override
                    public FileStore next() {
                        i++;
                        if ( i >= listRoots().length ) {
                            throw new NoSuchElementException();
                        }
                        return new SimpleWindowsFileStore( listRoots(), SimpleWindowsFileSystem.this, listRoots()[ i ].toString() );
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
    public void dispose() {
        close();
    }
}

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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.base.GeneralPathImpl;

import static org.uberfire.java.nio.util.Preconditions.*;

public class SimpleWindowsFileStore extends BaseSimpleFileStore {

    private static final Set<String> EXCLUDED_DRIVERS = new HashSet<String>() {{
        add("A");
        add("B");
    }};

    private int fstoreIndex = -1;
    private final File[] roots;

    public SimpleWindowsFileStore(final File[] roots, final FileSystem fs, final String path) {
        this(roots, GeneralPathImpl.create(fs, path, false));
    }

    SimpleWindowsFileStore(final File[] roots, final Path path) {
        super(roots, path);
        checkNotNull("roots", roots);
        checkCondition("should have at least one root", roots.length > 0);
        checkNotNull("path", path);
        this.roots = roots;

        if (path.isAbsolute()) {
            for (int i = 0; i < listRoots().length; i++) {
                if (listRoots()[i].toString().equals(path.getRoot().toString())) {
                    fstoreIndex = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < listRoots().length; i++) {
                if (!EXCLUDED_DRIVERS.contains(listRoots()[i].toString().substring(0, 1).toUpperCase())) {
                    fstoreIndex = i;
                    break;
                }
            }
        }
        if (fstoreIndex == -1) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String name() {
        return listRoots()[fstoreIndex].getName();
    }

    @Override
    public long getTotalSpace() throws IOException {
        return listRoots()[fstoreIndex].getTotalSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        return listRoots()[fstoreIndex].getUsableSpace();
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        throw new UnsupportedOperationException();
    }

    File[] listRoots() {
        return roots;
    }
}

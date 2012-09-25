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

package org.uberfire.java.nio.fs.base;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.util.Preconditions.*;

public class EclipsePathImpl extends AbstractPath {

    protected EclipsePathImpl(final FileSystem fs, final String path, boolean isRoot, boolean isRealPath, boolean isNormalized) {
        super(fs, path, isRoot, isRealPath, isNormalized);
    }

    public static EclipsePathImpl createRoot(final FileSystem fs, final String root, boolean isRealPath) {
        checkNotNull("fs", fs);
        checkNotNull("path", root);

        return new EclipsePathImpl(fs, root, true, isRealPath, true);
    }

    public static EclipsePathImpl create(final FileSystem fs, final String path, boolean isRealPath) {
        return create(fs, path, isRealPath, false);
    }

    public static EclipsePathImpl create(final FileSystem fs, final String path, boolean isRealPath, boolean isNormalized) {
        checkNotNull("fs", fs);
        checkNotNull("path", path);

        return new EclipsePathImpl(fs, path, false, isRealPath, isNormalized);
    }

    @Override Path newRoot(FileSystem fs, String substring, boolean realPath) {
        return new EclipsePathImpl(fs, substring, true, realPath, true);
    }

    @Override Path newPath(FileSystem fs, String substring, boolean realPath, boolean isNormalized) {
        return new EclipsePathImpl(fs, substring, false, realPath, isNormalized);
    }

    @Override
    public File toFile() throws UnsupportedOperationException {
        final org.eclipse.core.runtime.Path epath = new org.eclipse.core.runtime.Path(toString());
        final IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(epath);

        if (file == null) {
            synchronized (this) {
                file = ifile.getLocation().toFile();
            }
        }
        return file;
    }

}

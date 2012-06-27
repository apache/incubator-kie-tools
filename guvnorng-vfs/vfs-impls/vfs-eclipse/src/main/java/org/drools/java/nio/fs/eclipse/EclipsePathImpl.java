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

package org.drools.java.nio.fs.eclipse;

import java.io.File;

import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.fs.base.AbstractPathImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;

import static org.drools.java.nio.util.Preconditions.*;

public class EclipsePathImpl extends AbstractPathImpl {

    protected EclipsePathImpl(final FileSystem fs, final String path, boolean isRoot, boolean isRealPath) {
        super(fs, path, isRoot, isRealPath);
    }

    public static EclipsePathImpl createRoot(final FileSystem fs, final String root, boolean isRealPath) {
        checkNotNull("fs", fs);
        checkNotNull("path", root);

        return new EclipsePathImpl(fs, root, true, isRealPath);
    }

    public static EclipsePathImpl create(final FileSystem fs, final String path, boolean isRealPath) {
        checkNotNull("fs", fs);
        checkNotNull("path", path);

        return new EclipsePathImpl(fs, path, false, isRealPath);
    }

    @Override
    protected Path newRoot(FileSystem fs, String substring, boolean realPath) {
        return new EclipsePathImpl(fs, substring, true, realPath);
    }

    @Override
    protected Path newPath(FileSystem fs, String substring, boolean realPath) {
        return new EclipsePathImpl(fs, substring, false, realPath);
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

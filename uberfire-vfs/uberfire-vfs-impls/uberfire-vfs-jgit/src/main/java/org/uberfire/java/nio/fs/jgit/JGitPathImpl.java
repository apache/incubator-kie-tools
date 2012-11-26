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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.base.AbstractPath;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

import static org.eclipse.jgit.lib.Constants.*;

public class JGitPathImpl extends AbstractPath<JGitFileSystem> {

    public final static String DEFAULT_REF_TREE = MASTER;

    private final ObjectId objectId;

    private JGitPathImpl(final JGitFileSystem fs, final String path, final String host, final ObjectId id,
            final boolean isRoot, final boolean isRealPath, final boolean isNormalized) {
        super(fs, path, host, isRoot, isRealPath, isNormalized);
        this.objectId = id;
    }

    @Override
    protected RootInfo setupRoot(final JGitFileSystem fs, final String pathx, final String host, final boolean isRoot) {
        final boolean isRooted = isRoot ? true : pathx.startsWith("/");

        final boolean isAbsolute;
        if (isRooted) {
            isAbsolute = true;
        } else {
            isAbsolute = false;
        }

        int lastOffset = isAbsolute ? 1 : 0;

        final boolean isFinalRoot;
        if (pathx.length() == 1 && lastOffset == 1) {
            isFinalRoot = true;
        } else {
            isFinalRoot = isRoot;
        }

        return new RootInfo(lastOffset, isAbsolute, isFinalRoot, pathx.getBytes());
    }

    @Override
    protected String defaultDirectory() {
        return "/:";
    }

    @Override
    protected Path newRoot(final JGitFileSystem fs, String substring, final String host, boolean realPath) {
        return new JGitPathImpl(fs, substring, host, null, true, realPath, true);
    }

    @Override
    protected Path newPath(final JGitFileSystem fs, final String substring, final String host, final boolean isRealPath, final boolean isNormalized) {
        return new JGitPathImpl(fs, substring, host, null, false, isRealPath, isNormalized);
    }

    public static JGitPathImpl create(final JGitFileSystem fs, final String path, final String host, final ObjectId id, boolean isRealPath) {
        return new JGitPathImpl(fs, setupPath(path), setupHost(host), id, false, isRealPath, false);
    }

    public static JGitPathImpl create(final JGitFileSystem fs, final String path, final String host, boolean isRealPath) {
        return new JGitPathImpl(fs, setupPath(path), setupHost(host), null, false, isRealPath, false);
    }

    public static JGitPathImpl createRoot(final JGitFileSystem fs, final String path, final String host, boolean isRealPath) {
        return new JGitPathImpl(fs, setupPath(path), setupHost(host), null, true, isRealPath, true);
    }

    @Override
    public File toFile()
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BasicFileAttributes newAttrs() {
        return JGitUtil.buildBasicFileAttributes(this.getFileSystem().gitRepo(), this.getRefTree(), this.getPath());
    }

    private static String setupHost(final String host) {
        if (host.indexOf("@") == -1) {
            return DEFAULT_REF_TREE + "@" + host;
        }

        return host;
    }

    private static String setupPath(final String path) {
        if (path.isEmpty()) {
            return "/";
        }
        return path;
    }

    public String getRefTree() {
        return host.substring(0, host.indexOf("@"));
    }

    public String getPath() {
        return new String(path);
    }

}

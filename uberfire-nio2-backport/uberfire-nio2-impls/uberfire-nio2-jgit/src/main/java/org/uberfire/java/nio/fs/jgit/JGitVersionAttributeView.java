/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileTimeImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.jgit.util.model.CommitHistory;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathType;

/**
 *
 */
public class JGitVersionAttributeView extends VersionAttributeView<JGitPathImpl> {

    private VersionAttributes attrs = null;

    public JGitVersionAttributeView(final JGitPathImpl path) {
        super(path);
    }

    @Override
    public VersionAttributes readAttributes() throws IOException {
        if (attrs == null) {
            attrs = buildAttrs(path.getFileSystem(),
                               path.getRefTree(),
                               path.getPath());
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{VersionAttributeView.class, JGitVersionAttributeView.class};
    }

    private VersionAttributes buildAttrs(final JGitFileSystem fs,
                                         final String branchName,
                                         final String path) {
        final PathInfo pathInfo = fs.getGit().getPathInfo(branchName,
                                                          path);

        if (pathInfo == null || pathInfo.getPathType().equals(PathType.NOT_FOUND)) {
            throw new NoSuchFileException(path);
        }

        final Ref refId = fs.getGit().getRef(branchName);
        final List<VersionRecord> records = new ArrayList<>();

        if (refId != null) {
            try {
                final CommitHistory history = fs.getGit().listCommits(refId, pathInfo.getPath());
                for (final RevCommit commit : history.getCommits()) {
                    final String recordPath = history.trackedFileNameChangeFor(commit.getId());
                    records.add(new VersionRecord() {
                        @Override
                        public String id() {
                            return commit.name();
                        }

                        @Override
                        public String author() {
                            return commit.getAuthorIdent().getName();
                        }

                        @Override
                        public String email() {
                            return commit.getAuthorIdent().getEmailAddress();
                        }

                        @Override
                        public String comment() {
                            return commit.getFullMessage();
                        }

                        @Override
                        public Date date() {
                            return commit.getAuthorIdent().getWhen();
                        }

                        @Override
                        public String uri() {
                            return fs.getPath(commit.name(),
                                              recordPath).toUri().toString();
                        }
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Collections.reverse(records);

        return new VersionAttributes() {
            @Override
            public VersionHistory history() {
                return () -> records;
            }

            @Override
            public FileTime lastModifiedTime() {
                if (records.size() > 0) {
                    return new FileTimeImpl(records.get(records.size() - 1).date().getTime());
                }
                return null;
            }

            @Override
            public FileTime lastAccessTime() {
                return lastModifiedTime();
            }

            @Override
            public FileTime creationTime() {
                if (records.size() > 0) {
                    return new FileTimeImpl(records.get(0).date().getTime());
                }
                return null;
            }

            @Override
            public boolean isRegularFile() {
                return pathInfo.getPathType().equals(PathType.FILE);
            }

            @Override
            public boolean isDirectory() {
                return pathInfo.getPathType().equals(PathType.DIRECTORY);
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return pathInfo.getSize();
            }

            @Override
            public Object fileKey() {
                return pathInfo.getObjectId() == null ? null : pathInfo.getObjectId().toString();
            }
        };
    }
}

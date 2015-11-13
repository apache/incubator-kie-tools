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

package org.uberfire.ext.editor.commons.backend.version;

import java.net.URISyntaxException;
import java.util.List;
import javax.inject.Inject;

import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;

public class PathResolverImpl
        implements PathResolver {

        private VersionRecordService versionLoader;
        private VersionUtil          util;

        public PathResolverImpl() {
        }

        @Inject
        public PathResolverImpl(VersionRecordService versionLoader,
                                VersionUtil util) {
                this.util = util;
                this.versionLoader = versionLoader;
        }

        @Override
        public Path resolveMainFilePath(Path path) throws URISyntaxException {

                if (isDotFile(path)) {
                        Path mainPath = getMainPath(path);

                        VersionRecord currentMainRecord = versionLoader.loadRecord(path);
                        List<VersionRecord> versionRecords = versionLoader.loadVersionRecords(mainPath);

                        for (VersionRecord versionRecord : versionRecords) {
                                if (versionRecord.date().compareTo(currentMainRecord.date()) >= 0) {
                                        return util.getPath(mainPath, versionRecord.id());
                                }
                        }

                        return mainPath;
                } else {

                        return path;
                }
        }

        private Path getMainPath(Path path) {
                return path.resolveSibling(util.getFileName(path).substring(1));
        }

        public boolean isDotFile(Path path) {
                return util.getFileName(path).startsWith(".");
        }

}

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


package org.uberfire.backend.vfs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PathFactory {

    public static final String LOCK_FILE_EXTENSION = ".ulock";
    public static final String VERSION_PROPERTY = "hasVersionSupport";

    private PathFactory() {
    }

    public static Path newPath(final String fileName,
                               final String uri) {
        return new PathImpl(checkNotEmpty("fileName", fileName),
                            checkNotEmpty("uri", uri));
    }

    public static Path newPathBasedOn(final String fileName,
                                      final String uri,
                                      final Path path) {
        return new PathImpl(checkNotEmpty("fileName", fileName),
                            checkNotEmpty("uri", uri),
                            checkNotNull("path", path));
    }

    public static Path newPath(final String fileName,
                               final String uri,
                               final Map<String, Object> attrs) {
        return new PathImpl(checkNotEmpty("fileName", fileName),
                            checkNotEmpty("uri", uri),
                            attrs);
    }

    public static Path newLock(final Path path) {
        Path lockPath = newLockPath(path);
        return PathFactory.newPath(path.getFileName() + LOCK_FILE_EXTENSION,
                                   lockPath.toURI() + LOCK_FILE_EXTENSION);
    }

    /**
     * Returns a path of a lock for the provided file.
     * <p>
     * Examples:
     * <p>
     * <pre>
     * default://main@myteam/dora/src/main/resources/com/myteam/dora/sample.drl
     *           branch@space/project/path/to/file.extension                      =>
     * default://locks@system/system/myteam/main/dora/src/main/resources/com/myteam/dora/sample.drl
     *
     * </pre>
     *
     * @param path the path of a file for which a lock should be created, must not be null.
     * @return the lock path
     */
    public static Path newLockPath(final Path path) {
        checkNotNull("path", path);

        final String systemUri = path.toURI().replaceFirst("(/|\\\\)([^/&^\\\\]*)@([^/&^\\\\]*)",
                                                           "$1locks@system/system$1$3$1$2");

        return PathFactory.newPath("/",
                                   systemUri);
    }

    /**
     * Returns the path of the locked file for the provided lock.
     * <p>
     * Examples:
     * <p>
     * <pre>
     * default://locks@system/system/myteam/main/dora/src/main/resources/com/myteam/dora/sample.drl.ulock
     * default://main@myteam/dora/src/main/resources/com/myteam/dora/sample.drl
     *           branch@space/project/path/to/file.extension                      =>
     * </pre>
     *
     * @param lockPath the path of a lock, must not be null.
     * @return the locked path.
     */
    public static Path fromLock(final Path lockPath) {
        checkNotNull("path", lockPath);

        final String uri = lockPath.toURI().replaceFirst("locks@system/system(/|\\\\)([^/&^\\\\]*)(/|\\\\)([^/&^\\\\]*)",
                                                         "$4@$2");

        return PathFactory.newPath(lockPath.getFileName().replace(LOCK_FILE_EXTENSION,
                                                                  ""),
                                   uri.replace(LOCK_FILE_EXTENSION,
                                               ""));
    }

    private static String checkNotEmpty(String name, String parameter) {
        if (parameter == null || parameter.trim().length() == 0) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
        return parameter;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public static class PathImpl implements Path,
                                            IsVersioned {

        private String uri = null;
        private String fileName = null;
        private HashMap<String, Object> attributes = null;
        private boolean hasVersionSupport = false;

        public PathImpl() {
        }

        public PathImpl(final String fileName,
                         final String uri) {
            this(fileName,
                 uri,
                 (Map<String, Object>) null);
        }

        private PathImpl(final String fileName,
                         final String uri,
                         final Map<String, Object> attrs) {
            this.fileName = fileName;
            this.uri = uri;
            if (attrs == null) {
                this.attributes = new HashMap<>();
            } else {
                if (attrs.containsKey(VERSION_PROPERTY)) {
                    hasVersionSupport = (Boolean) attrs.remove(VERSION_PROPERTY);
                }
                if (attrs.size() > 0) {
                    this.attributes = new HashMap<>(attrs);
                } else {
                    this.attributes = new HashMap<>();
                }
            }
        }

        private PathImpl(final String fileName,
                         final String uri,
                         final Path path) {
            this.fileName = fileName;
            this.uri = uri;
            if (path instanceof PathImpl) {
                this.hasVersionSupport = ((PathImpl) path).hasVersionSupport;
            }
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String toURI() {
            return uri;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public int compareTo(final Path another) {
            return this.uri.compareTo(another.toURI());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Path)) {
                return false;
            }

            final Path path = (Path) o;

            return this.toURI().equals(path.toURI());
        }

        @Override
        public boolean hasVersionSupport() {
            return hasVersionSupport;
        }

        @Override
        public int hashCode() {
            return uri.hashCode();
        }

        @Override
        public String toString() {
            return "PathImpl{" +
                    "uri='" + uri + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", attrs=" + attributes +
                    '}';
        }
    }
}

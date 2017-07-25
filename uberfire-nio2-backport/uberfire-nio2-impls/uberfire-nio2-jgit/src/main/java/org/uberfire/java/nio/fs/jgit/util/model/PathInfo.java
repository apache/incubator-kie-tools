/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.model;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;

import static org.eclipse.jgit.lib.FileMode.TYPE_FILE;

public class PathInfo {

    private final long size;
    private final ObjectId objectId;
    private final String path;
    private final PathType pathType;

    public PathInfo(final ObjectId objectId,
                    final String path,
                    final FileMode fileMode) {
        this(objectId,
             path,
             convert(fileMode),
             -1);
    }

    public PathInfo(final ObjectId objectId,
                    final String path,
                    final FileMode fileMode,
                    final long size) {
        this(objectId,
             path,
             convert(fileMode));
    }

    public PathInfo(final ObjectId objectId,
                    final String path,
                    final PathType pathType) {
        this(objectId,
             path,
             pathType,
             -1);
    }

    public PathInfo(final ObjectId objectId,
                    final String path,
                    final PathType pathType,
                    final long size) {
        this.objectId = objectId;
        this.path = path;
        this.pathType = pathType;
        this.size = size;
    }

    private static PathType convert(final FileMode fileMode) {
        if (fileMode.equals(FileMode.TYPE_TREE)) {
            return PathType.DIRECTORY;
        } else if (fileMode.equals(TYPE_FILE)) {
            return PathType.FILE;
        }
        return null;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public String getPath() {
        return path;
    }

    public PathType getPathType() {
        return pathType;
    }

    public long getSize() {
        return size;
    }
}

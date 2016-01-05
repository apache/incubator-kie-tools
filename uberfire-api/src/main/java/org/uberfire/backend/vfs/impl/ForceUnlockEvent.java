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

package org.uberfire.backend.vfs.impl;

import org.uberfire.backend.vfs.Path;

/**
 * Client-local event to indicate that the specified path's lock should be
 * released. This is used in admin functionality for overriding locks. The user
 * currently holding the lock will not be notified and can potentially lose
 * data.
 */
public class ForceUnlockEvent {

    private final Path path;

    public ForceUnlockEvent( Path path ) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}

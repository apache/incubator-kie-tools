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

package org.uberfire.java.nio.security;

import org.uberfire.java.nio.file.FileSystem;

/**
 * Strategy for authorizing users to perform actions in a secured file system.
 */
public interface FileSystemAuthorizer {

    /**
     * Returns true if the given user is permitted to perform actions within the given file system.
     *
     * @param fs
     * @param fileSystemUser
     * @return
     */
    boolean authorize( final FileSystem fs,
                       final FileSystemUser fileSystemUser );

}

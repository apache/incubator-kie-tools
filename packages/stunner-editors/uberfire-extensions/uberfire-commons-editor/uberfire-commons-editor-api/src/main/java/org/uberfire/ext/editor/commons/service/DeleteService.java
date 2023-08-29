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

package org.uberfire.ext.editor.commons.service;

import java.util.Collection;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;

/**
 * Responsible for paths removal.
 */

public interface DeleteService extends SupportsDelete {

    /**
     * Deletes (in batch) the paths passed in {@param paths}, if they exist.
     * @param paths Paths that will be removed.
     */
    void deleteIfExists(final Collection<Path> paths,
                        final String comment);

    /**
     * Verifies if a path can be deleted.
     * @param path Path to be verified.
     * @return true if there is a restriction and the path cannot be deleted, and false otherwise.
     */
    boolean hasRestriction(Path path);
}

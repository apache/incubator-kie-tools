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
package org.uberfire.ext.editor.commons.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;

/**
 * Responsible for paths renaming.
 */
@Remote
public interface RenameService extends SupportsRename {

    /**
     * Renames (in batch) the paths passed in {@param paths}, if they exist.
     * @param paths Paths that will be renamed.
     * @param newName Path's new name.
     * @param comment Comment about the renaming.
     */
    void renameIfExists( final Collection<Path> paths,
                         final String newName,
                         final String comment );

    /**
     * Verifies if a path can be renamed.
     * @param path Path to be verified.
     * @return true if there is a restriction and the path cannot be renamed, and false otherwise.
     */
    boolean hasRestriction( Path path );
}

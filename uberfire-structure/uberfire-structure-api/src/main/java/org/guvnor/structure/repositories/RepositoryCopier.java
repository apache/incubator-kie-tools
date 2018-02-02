/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.repositories;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.vfs.Path;
import org.uberfire.spaces.Space;

public interface RepositoryCopier {

    /**
     * Notice that this does not clone the content from the origin,
     * this doeas a copy that does not include the history or the old commits IDs.
     * @param targetOU The OU for the new Repository
     * @param newRepositoryName Name for the new Repository
     * @param originRoot Root where the Repository is cloned from
     * @return The new Repository
     */
    Repository copy(final OrganizationalUnit targetOU,
                    final String newRepositoryName,
                    final Path originRoot);

    /**
     * Like {@link #copy(Path, Path)} but assumes current active space.
     */
    void copy(Path originRoot,
              Path targetRoot);

    void copy(Space space,
              Path originRoot,
              Path targetRoot);

    String makeSafeRepositoryName(String oldName);
}

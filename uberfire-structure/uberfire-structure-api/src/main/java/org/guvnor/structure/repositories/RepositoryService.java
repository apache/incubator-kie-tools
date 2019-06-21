/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.spaces.Space;

@Remote
/**
 * This RepositoryService is dependent on the WorkspaceProjectContext.
 * It uses WorkspaceProjectContext in order to lookup for the current space.
 *
 * The only exception is getRepositoryFromSpace(space, alias) and
 * getAllRepositoriesFromAllSpaces methods.
 *
 */
public interface RepositoryService {

    RepositoryInfo getRepositoryInfo(final Space space,
                                     final String alias);

    List<VersionRecord> getRepositoryHistory(final Space space,
                                             final String alias,
                                             final int startIndex);

    List<VersionRecord> getRepositoryHistory(final Space space,
                                             final String alias,
                                             final int startIndex,
                                             final int endIndex);

    List<VersionRecord> getRepositoryHistoryAll(final Space space,
                                                final String alias);

    Repository getRepositoryFromSpace(final Space currentSpace,
                                      final String alias);

    Repository getRepository(final Path root);

    Repository getRepository(final Space space,
                             final Path root);

    /**
     * Get all the repositories. Security checks are omitted.
     */
    Collection<Repository> getAllRepositories(final Space space);

    /**
     * Get all the repositories. Security checks are omitted.
     */
    Collection<Repository> getAllRepositories(final Space space,
                                              final boolean includeDeleted);

    /**
     * Get all the repositories. Security checks are omitted.
     */
    Collection<Repository> getAllDeletedRepositories(final Space space);

    /**
     * Get all the repositories from all user spaces. Security checks are omitted.
     */
    Collection<Repository> getAllRepositoriesFromAllUserSpaces();

    /**
     * Get only those repositories available within the current security context.
     */
    Collection<Repository> getRepositories(final Space space);

    Repository createRepository(final OrganizationalUnit organizationalUnit,
                                final String scheme,
                                final String alias,
                                final RepositoryEnvironmentConfigurations configurations) throws RepositoryAlreadyExistsException;

    Repository createRepository(final OrganizationalUnit organizationalUnit,
                                final String scheme,
                                final String alias,
                                final RepositoryEnvironmentConfigurations configurations,
                                final Collection<Contributor> contributors) throws RepositoryAlreadyExistsException;

    String normalizeRepositoryName(final String name);

    boolean validateRepositoryName(final String name);

    void addGroup(final Repository repository,
                  final String group);

    void removeGroup(final Repository repository,
                     final String group);

    void updateContributors(Repository repository,
                            List<Contributor> contributors);

    void removeRepository(final Space space,
                          final String alias);

    /**
     * Unlike {@link #removeRepository(Space, String)}, this method does not fire CDI events, since it is meant to be invoked
     * by other services while removing other constructs (such as an entire space).
     */
    void removeRepositories(final Space space,
                            final Set<String> aliases);
}

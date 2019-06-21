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
import java.util.Map;
import java.util.Optional;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.security.RepositoryResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeContentResource;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

public interface Repository
        extends RuntimeContentResource,
                Cacheable {

    RepositoryResourceType RESOURCE_TYPE = new RepositoryResourceType();

    /**
     * Most of the time, this can not be used as an unique ID.
     * If the Repository has branches each branch has the same alias.
     * @return short name for the repository
     */
    String getAlias();

    SpacesAPI.Scheme getScheme();

    Space getSpace();

    Map<String, Object> getEnvironment();

    void addEnvironmentParameter(final String key,
                                 final Object value);

    boolean isValid();

    String getUri();

    List<PublicURI> getPublicURIs();

    Optional<Branch> getBranch(final String branch);

    Optional<Branch> getBranch(final Path branchRoot);

    Collection<String> getGroups();

    Collection<Contributor> getContributors();

    /**
     * Returns "read-only" view of all branches available in this repository.
     * @return
     */
    Collection<Branch> getBranches();

    /**
     * In the case of Git repository this would be master.
     * @return empty if there are no branches.
     */
    Optional<Branch> getDefaultBranch();

}

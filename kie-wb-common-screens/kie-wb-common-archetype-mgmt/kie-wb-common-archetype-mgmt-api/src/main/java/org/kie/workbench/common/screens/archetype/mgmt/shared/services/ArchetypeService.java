/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.archetype.mgmt.shared.services;

import java.util.Optional;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;

/**
 * Service that manages the archetypes registered on the system.
 */
@Remote
public interface ArchetypeService {

    /**
     * Make the given {@link Archetype archetype} available to be used.
     * The final template will have the same groupId, artifactId, and version.
     *
     * @param archetypeGav groupId, artifactId, and version of the archetype
     */
    void add(GAV archetypeGav);

    /**
     * Make the given {@link Archetype archetype} available to be used.
     *
     * @param archetypeGav groupId, artifactId, and version of the archetype
     * @param templateGav  groupId, artifactId, and version of the final template
     */
    void add(GAV archetypeGav,
             GAV templateGav);

    /**
     * List registered {@link Archetype archetypes}
     *
     * @param page     the desired page
     * @param pageSize the size of the page
     * @param filter   string to filter the results
     * @return list of {@link Archetype archetypes}
     */
    PaginatedArchetypeList list(Integer page,
                                Integer pageSize,
                                String filter);

    /**
     * List registered {@link Archetype archetypes}
     *
     * @param page     the desired page
     * @param pageSize the size of the page
     * @param filter   string to filter the results
     * @param status   status to filter the results
     * @return list of {@link Archetype archetypes}
     */
    PaginatedArchetypeList list(Integer page,
                                Integer pageSize,
                                String filter,
                                ArchetypeStatus status);

    /**
     * Delete the {@link Archetype archetype} associated with the given alias.
     *
     * @param alias archetype alias
     */
    void delete(String alias);

    /**
     * Validate all registered {@link Archetype archetypes}.
     * It includes unpacking and executing <i>mvn clean install</i>.
     * In case of a failure, the associated archetype is removed.
     */
    void validateAll();

    /**
     * Validate the {@link Archetype archetype} associated with the given alias.
     *
     * @param alias archetype alias
     */
    void validate(String alias);

    /**
     * Return the repository where the {@link Archetype archetype} is stored.
     *
     * @param alias archetype alias
     * @return repository of the archetype
     */
    Repository getTemplateRepository(String alias);

    /**
     * Return the base KIE project repository where the {@link Archetype archetype} is stored.
     *
     * @return repository of the archetype
     */
    Optional<Repository> getBaseKieTemplateRepository();

    /**
     * Return the {@link Archetype archetype} associated with the base KIE project.
     *
     * @return the base KIE project archetype
     */
    Optional<Archetype> getBaseKieArchetype();
}
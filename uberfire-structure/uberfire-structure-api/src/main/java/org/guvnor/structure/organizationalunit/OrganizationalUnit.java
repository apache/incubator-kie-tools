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

package org.guvnor.structure.organizationalunit;

import java.util.Collection;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.OrgUnitResourceType;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeContentResource;
import org.uberfire.spaces.Space;

public interface OrganizationalUnit extends RuntimeContentResource,
                                            Cacheable {

    OrgUnitResourceType RESOURCE_TYPE = new OrgUnitResourceType();

    String getName();

    String getDescription();

    Space getSpace();

    String getOwner();

    String getDefaultGroupId();

    Collection<Repository> getRepositories();

    Collection<String> getGroups();

    Collection<Contributor> getContributors();

    boolean isDeleted();

    void setDescription(String description);
}

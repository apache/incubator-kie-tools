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

package org.guvnor.structure.organizationalunit.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.security.ResourceType;
import org.uberfire.spaces.Space;

@Portable
public class OrganizationalUnitImpl implements OrganizationalUnit {

    private String name;
    private String description;
    private String defaultGroupId;
    private boolean deleted;

    private Collection<Repository> repositories = new ArrayList<>();
    private Collection<String> groups = new ArrayList<>();
    private Collection<Contributor> contributors = new ArrayList<>();
    private boolean requiresRefresh = true;

    public OrganizationalUnitImpl() {
    }

    public OrganizationalUnitImpl(final String name,
                                  final String defaultGroupId) {
        this(name, defaultGroupId, false);
    }

    public OrganizationalUnitImpl(final String name,
                                  final String defaultGroupId,
                                  final boolean deleted) {
        this.name = name;
        this.defaultGroupId = defaultGroupId;
        this.deleted = deleted;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Space getSpace() {
        return new Space(name);
    }

    @Override
    public String getOwner() {
        final Optional<Contributor> owner = contributors.stream().filter(c -> c.getType().equals(ContributorType.OWNER)).findFirst();
        return owner.map(Contributor::getUsername).orElse(null);
    }

    @Override
    public String getDefaultGroupId() {
        return defaultGroupId;
    }

    @Override
    public Collection<Repository> getRepositories() {
        return repositories;
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public ResourceType getResourceType() {
        return RESOURCE_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationalUnitImpl)) {
            return false;
        }

        OrganizationalUnitImpl ou = (OrganizationalUnitImpl) o;

        if (name != null ? !name.equals(ou.name) : ou.name != null) {
            return false;
        }
        if (defaultGroupId != null ? !defaultGroupId.equals(ou.defaultGroupId) : ou.defaultGroupId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (defaultGroupId != null ? defaultGroupId.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "OrganizationalUnitImpl [name=" + name + ", repositories=" + repositories
                + ", groups=" + groups + ", contributors=" + contributors + ", defaultGroupId=" + defaultGroupId
                + ", deleted=" + deleted + "]";
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    @Override
    public Collection<String> getGroups() {
        return groups;
    }

    @Override
    public Collection<Contributor> getContributors() {
        return contributors;
    }
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDescription(String description) {
       this.description = description;
    }
}

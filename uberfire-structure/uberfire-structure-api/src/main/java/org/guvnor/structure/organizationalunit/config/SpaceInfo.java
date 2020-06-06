/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.organizationalunit.config;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.guvnor.structure.contributors.Contributor;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SpaceInfo {

    private String name;

    private String description;

    private String defaultGroupId;

    private Collection<Contributor> contributors;

    private List<RepositoryInfo> repositories;

    private List<String> securityGroups;

    public SpaceInfo(@MapsTo("name") final String name,
                     @MapsTo("description") final String description,
                     @MapsTo("defaultGroupId") final String defaultGroupId,
                     @MapsTo("contributors") final Collection<Contributor> contributors,
                     @MapsTo("repositories") final List<RepositoryInfo> repositories,
                     @MapsTo("securityGroups") final List<String> securityGroups) {
        this.name = name;
        this.description = description;
        this.defaultGroupId = defaultGroupId;
        this.contributors = contributors;
        this.repositories = repositories;
        this.securityGroups = securityGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultGroupId() {
        return defaultGroupId;
    }

    public void setDefaultGroupId(String defaultGroupId) {
        this.defaultGroupId = defaultGroupId;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Collection<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<RepositoryInfo> getRepositories() {
        return repositories;
    }

    public List<RepositoryInfo> getRepositories(Predicate<RepositoryInfo> query) {
        return repositories.stream().filter(query).collect(Collectors.toList());
    }

    public void setRepositories(List<RepositoryInfo> repositories) {
        this.repositories = repositories;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public void removeRepository(String name) {
        this.getRepositories().removeIf(repo -> repo.getName().equals(name));
    }
}

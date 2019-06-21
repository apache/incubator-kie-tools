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

package org.guvnor.structure.repositories.impl.git;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.ResourceType;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.uberfire.spaces.SpacesAPI.Scheme.GIT;

@Portable
public class GitRepository
        implements Repository {

    public static final SpacesAPI.Scheme SCHEME = GIT;

    private final Map<String, Object> environment = new HashMap<>();
    private final List<PublicURI> publicURIs = new ArrayList<>();
    private final Map<String, Branch> branches = new HashMap<>();
    private String alias = null;
    private Space space;

    private Collection<String> groups = new ArrayList<>();
    private Collection<Contributor> contributors = new ArrayList<>();
    private boolean requiresRefresh = true;
    private boolean deleted;

    public GitRepository() {
    }

    public GitRepository(final String alias,
                         Space space) {
        this.alias = alias;
        this.space = space;
    }

    public GitRepository(final String alias,
                         final Space space,
                         final List<PublicURI> publicURIs) {
        this(alias,
             space);

        if (publicURIs != null && !publicURIs.isEmpty()) {
            this.publicURIs.addAll(publicURIs);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public SpacesAPI.Scheme getScheme() {
        return SCHEME;
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return environment;
    }

    @Override
    public void addEnvironmentParameter(String key,
                                        Object value) {
        environment.put(key,
                        value);
    }

    public void setBranches(final Map<String, Branch> branches) {
        this.branches.clear();
        this.branches.putAll(branches);
    }

    @Override
    public Collection<Branch> getBranches() {
        return Collections.unmodifiableCollection(branches.values());
    }

    @Override
    public Optional<Branch> getBranch(final String branchName) {
        return Optional.ofNullable(branches.get(branchName));
    }

    @Override
    public Optional<Branch> getBranch(Path branchRoot) {

        for (final Branch branch : getBranches()) {
            if (branch.getPath().equals(branchRoot)) {
                return Optional.of(branch);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean isValid() {
        return alias != null;
    }

    @Override
    public String getUri() {

        String fsName = SpacesAPI.sanitizeFileSystemName(getAlias());
        return SpacesAPI.resolveFileSystemPath(getScheme(),
                                               getSpace(),
                                               fsName).toString();
    }

    @Override
    public List<PublicURI> getPublicURIs() {
        return publicURIs;
    }

    public void setPublicURIs(final List<PublicURI> publicURIs) {
        if (publicURIs != null && !publicURIs.isEmpty()) {
            this.publicURIs.clear();
            this.publicURIs.addAll(publicURIs);
        }
    }

    @Override
    public String getIdentifier() {
        return getUri();
    }

    @Override
    public ResourceType getResourceType() {
        return RESOURCE_TYPE;
    }

    @Override
    public Collection<String> getGroups() {
        return groups;
    }

    public void setGroups(Collection<String> groups) {
        this.groups = new ArrayList<>(groups);
    }

    @Override
    public Collection<Contributor> getContributors() {
        return contributors;
    }

    @Override
    public Optional<Branch> getDefaultBranch() {
        if (branches.containsKey("master")) {
            return getBranch("master");
        } else if (!branches.isEmpty()) {
            return Optional.of(branches.values().iterator().next());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GitRepository)) {
            return false;
        }

        final GitRepository that = (GitRepository) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) {
            return false;
        }
        if (!environment.equals(that.environment)) {
            return false;
        }
        if (!publicURIs.equals(that.publicURIs)) {
            return false;
        }
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) {
            return false;
        }
        if (branches != null ? !branches.equals(that.branches) : that.branches != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = environment.hashCode();
        result = ~~result;
        result = 31 * result + (publicURIs.hashCode());
        result = ~~result;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (branches != null ? branches.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "GitRepository [alias=" + alias + ", environment=" + environment + ", groups=" + groups
                + ", publicURI=" + publicURIs + ", branches=" + branches + "]";
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    public void addBranch(final Branch branch) {
        branches.put(branch.getName(),
                     branch);
    }
}
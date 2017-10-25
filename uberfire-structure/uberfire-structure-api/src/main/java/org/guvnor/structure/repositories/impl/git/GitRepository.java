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

import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.ResourceType;

@Portable
public class GitRepository
        implements Repository {

    public static final String SCHEME = "git";

    private final Map<String, Object> environment = new HashMap<String, Object>();
    private final List<PublicURI> publicURIs = new ArrayList<PublicURI>();

    private String alias = null;
    private Path root;

    private Collection<String> groups = new ArrayList<String>();

    private boolean requiresRefresh = true;
    private final Map<String, Path> branches = new HashMap<String, Path>();

    public GitRepository() {
    }

    public GitRepository(final String alias) {
        this.alias = alias;
    }

    public GitRepository(final String alias,
                         final List<PublicURI> publicURIs) {
        this(alias);

        if (publicURIs != null && !publicURIs.isEmpty()) {
            this.publicURIs.addAll(publicURIs);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getScheme() {
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

    public void setRoot(final Path root) {
        this.root = root;
    }

    public void setBranches(final Map<String, Path> branches) {
        this.branches.clear();
        this.branches.putAll(branches);
    }

    @Override
    public Collection<String> getBranches() {
        return Collections.unmodifiableSet(branches.keySet());
    }

    @Override
    public Path getRoot() {
        return root;
    }

    @Override
    public Path getBranchRoot(String branch) {
        return branches.get(branch);
    }

    @Override
    public boolean isValid() {
        return alias != null;
    }

    @Override
    public String getUri() {
        return getScheme() + "://" + getAlias();
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

    public Collection<String> getGroups() {
        return groups;
    }

    public void setGroups(Collection<String> groups) {
        this.groups = new ArrayList<String>(groups);
    }

    @Override
    public String getDefaultBranch() {
        if (branches.containsKey("master")) {
            return "master";
        } else if (!branches.isEmpty()) {
            return branches.keySet().iterator().next();
        } else {
            return null;
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
        if (root != null ? !root.equals(that.root) : that.root != null) {
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
        result = 31 * result + (root != null ? root.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (branches != null ? branches.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "GitRepository [alias=" + alias + ", environment=" + environment + ", root=" + root + ", groups=" + groups
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

    public void addBranch(final String branchName,
                          final Path path) {
        branches.put(branchName,
                     path);
    }
}
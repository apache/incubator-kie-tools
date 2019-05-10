/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class ImportProject {

    private Path root;
    private String name;
    private String description;
    private String origin;
    private List<String> tags;
    private List<ExampleProjectError> errors;
    private Credentials credentials;
    private List<String> allBranches;
    private List<String> selectedBranches;
    private boolean canSelectBranches;

    public ImportProject(final @MapsTo("root") Path root,
                         final @MapsTo("name") String name,
                         final @MapsTo("description") String description,
                         final @MapsTo("origin") String origin,
                         final @MapsTo("tags") List<String> tags,
                         final @MapsTo("errors") List<ExampleProjectError> errors,
                         final @MapsTo("credentials") Credentials credentials,
                         final @MapsTo("allBranches") List<String> allBranches,
                         final @MapsTo("selectedBranches") List<String> selectedBranches,
                         final @MapsTo("canSelectBranches") boolean canSelectBranches) {
        this.root = root;
        this.name = name;
        this.description = description;
        this.origin = origin;
        this.tags = tags;
        this.errors = errors;
        this.credentials = credentials;
        this.allBranches = allBranches;
        this.selectedBranches = selectedBranches;
        this.canSelectBranches = canSelectBranches;
    }

    public ImportProject(final Path root,
                         final String name,
                         final String description,
                         final String origin,
                         final List<String> tags) {
        this(root,
             name,
             description,
             origin,
             tags,
             new ArrayList<>(),
             null,
             null,
             null,
             false);
    }

    public ImportProject(final Path root,
                         final String name,
                         final String description,
                         final String origin,
                         final List<String> tags,
                         final Credentials credentials,
                         final List<String> allBranches,
                         final boolean canSelectBranches) {
        this(root,
             name,
             description,
             origin,
             tags,
             new ArrayList<>(),
             credentials,
             allBranches,
             Collections.emptyList(),
             canSelectBranches);
    }

    public Path getRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<ExampleProjectError> getErrors() {
        return errors;
    }

    public String getOrigin() {
        return origin;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public List<String> getAllBranches() {
        return allBranches;
    }

    public List<String> getSelectedBranches() {
        return selectedBranches;
    }

    public boolean canSelectBranches() {
        return canSelectBranches;
    }

    public void setSelectedBranches(final List<String> selectedBranches) {
        this.selectedBranches = selectedBranches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportProject)) {
            return false;
        }

        ImportProject that = (ImportProject) o;

        if (canSelectBranches != that.canSelectBranches) {
            return false;
        }
        if (root != null ? !root.equals(that.root) : that.root != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (origin != null ? !origin.equals(that.origin) : that.origin != null) {
            return false;
        }
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) {
            return false;
        }
        if (errors != null ? !errors.equals(that.errors) : that.errors != null) {
            return false;
        }
        if (credentials != null ? !credentials.equals(that.credentials) : that.credentials != null) {
            return false;
        }
        if (allBranches != null ? !allBranches.equals(that.allBranches) : that.allBranches != null) {
            return false;
        }
        return selectedBranches != null ? selectedBranches.equals(that.selectedBranches) : that.selectedBranches == null;
    }

    @Override
    public int hashCode() {
        int result = root != null ? root.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (origin != null ? origin.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (credentials != null ? credentials.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (allBranches != null ? allBranches.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (selectedBranches != null ? selectedBranches.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (canSelectBranches ? 1 : 0);
        result = ~~result;
        return result;
    }
}

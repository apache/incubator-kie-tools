/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.screens.library.api;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ProjectAssetsQuery {

    private final WorkspaceProject project;
    private final int startIndex;
    private final int amount;
    private String filter;
    private List<String> extensions;

    public ProjectAssetsQuery(@MapsTo("project") final WorkspaceProject project,
                              @MapsTo("filter") final String filter,
                              @MapsTo("startIndex") final int startIndex,
                              @MapsTo("amount") final int amount,
                              @MapsTo("extensions") final List<String> extensions) {
        this.project = checkNotNull("ProjectAssetsQuery.project",
                                    project);
        this.filter = checkNotNull("filter",
                                   filter);
        this.startIndex = checkNotNull("startIndex",
                                       startIndex);
        this.amount = checkNotNull("amount",
                                   amount);
        this.extensions = extensions;
    }

    public WorkspaceProject getProject() {
        return project;
    }

    public String getFilter() {
        return filter;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getAmount() {
        return amount;
    }

    public boolean hasFilter() {
        return filter != null && !filter.trim().isEmpty();
    }

    public boolean hasExtension() {
        return this.extensions != null && !this.extensions.isEmpty();
    }

    public List<String> getExtensions() {
        return extensions;
    }
}

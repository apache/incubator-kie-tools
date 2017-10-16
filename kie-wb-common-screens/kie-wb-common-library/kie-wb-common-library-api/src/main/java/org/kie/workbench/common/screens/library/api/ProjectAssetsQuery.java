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

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ProjectAssetsQuery {

    private final Project project;
    private final int startIndex;
    private final int amount;
    private String filter;

    public ProjectAssetsQuery(@MapsTo("project") final Project project,
                              @MapsTo("filter") final String filter,
                              @MapsTo("startIndex") final int startIndex,
                              @MapsTo("amount") final int amount) {
        this.project = checkNotNull("project",
                                    project);
        this.filter = checkNotNull("filter",
                                   filter);
        this.startIndex = checkNotNull("startIndex",
                                       startIndex);
        this.amount = checkNotNull("amount",
                                   amount);
    }

    public Project getProject() {
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
}

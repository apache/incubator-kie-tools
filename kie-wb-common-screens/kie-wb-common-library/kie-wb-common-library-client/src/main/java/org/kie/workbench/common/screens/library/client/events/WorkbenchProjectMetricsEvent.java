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
package org.kie.workbench.common.screens.library.client.events;

import org.guvnor.common.services.project.model.WorkspaceProject;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class WorkbenchProjectMetricsEvent {

    private WorkspaceProject project;

    public WorkbenchProjectMetricsEvent() {

    }

    public WorkbenchProjectMetricsEvent(final WorkspaceProject project) {
        this.project = checkNotNull("project",
                                    project);
    }

    public WorkspaceProject getProject() {
        return project;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkbenchProjectMetricsEvent)) {
            return false;
        }

        final WorkbenchProjectMetricsEvent that = (WorkbenchProjectMetricsEvent) o;

        return !(getProject() != null ? !getProject().equals(that.getProject()) : that.getProject() != null);
    }

    @Override
    public int hashCode() {
        return getProject() != null ? getProject().hashCode() : 0;
    }
}

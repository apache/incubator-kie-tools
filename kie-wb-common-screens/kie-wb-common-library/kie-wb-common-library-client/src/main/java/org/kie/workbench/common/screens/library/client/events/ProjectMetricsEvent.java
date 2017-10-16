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

import org.kie.workbench.common.screens.library.api.ProjectInfo;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

public class ProjectMetricsEvent {

    private ProjectInfo projectInfo;

    public ProjectMetricsEvent() {

    }

    public ProjectMetricsEvent(final ProjectInfo projectInfo) {
        this.projectInfo = checkNotNull("projectInfo",
                                        projectInfo);
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMetricsEvent)) {
            return false;
        }

        final ProjectMetricsEvent that = (ProjectMetricsEvent) o;

        return !(getProjectInfo() != null ? !getProjectInfo().equals(that.getProjectInfo()) : that.getProjectInfo() != null);
    }

    @Override
    public int hashCode() {
        return getProjectInfo() != null ? getProjectInfo().hashCode() : 0;
    }
}

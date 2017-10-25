/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.shared.metadata.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Overview {

    private Metadata metadata;
    private String projectName;

    public Metadata getMetadata() {
        return metadata;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Overview overview = (Overview) o;

        if (metadata != null ? !metadata.equals(overview.metadata) : overview.metadata != null) {
            return false;
        }
        if (projectName != null ? !projectName.equals(overview.projectName) : overview.projectName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = metadata != null ? metadata.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

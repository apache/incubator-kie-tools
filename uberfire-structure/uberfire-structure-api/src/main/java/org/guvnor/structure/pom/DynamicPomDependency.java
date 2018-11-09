/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.pom;

/***
 * Dependency used to add Maven dependencies on the pom
 */
public class DynamicPomDependency {

    private String groupID, artifactID, version, scope;

    public DynamicPomDependency(String groupID,
                                String artifactID,
                                String version,
                                String scope) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
        this.scope = scope;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DynamicPomDependency{");
        sb.append("groupID='").append(groupID).append('\'');
        sb.append(", artifactID='").append(artifactID).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", scope='").append(scope).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

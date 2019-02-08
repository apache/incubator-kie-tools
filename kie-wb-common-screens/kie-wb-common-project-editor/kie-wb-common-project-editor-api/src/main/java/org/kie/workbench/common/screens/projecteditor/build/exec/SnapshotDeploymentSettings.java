/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.build.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "SnapshotDeploymentSettings")
public class SnapshotDeploymentSettings implements BasePreference<SnapshotDeploymentSettings> {

    @Property
    private List<SnapshotDeployment> deployments = new ArrayList<>();

    public List<SnapshotDeployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(List<SnapshotDeployment> deployments) {
        this.deployments = deployments;
    }

    public void addDeployment(String groupId, String artifactId, String server) {
        Optional<SnapshotDeployment> optional = getDeployment(groupId, artifactId);

        if (optional.isPresent()) {
            optional.get().setServer(server);
        } else {
            deployments.add(new SnapshotDeployment(groupId, artifactId, server));
        }
    }

    public Optional<SnapshotDeployment> getDeployment(String groupId, String artifactId) {
        return deployments.stream()
                .filter(deployment -> deployment.getGroupId().equals(groupId) && deployment.getArtifactId().equals(artifactId))
                .findAny();
    }

    @Override
    public SnapshotDeploymentSettings defaultValue(SnapshotDeploymentSettings defaultValue) {
        return defaultValue;
    }
}

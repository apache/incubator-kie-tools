package org.kie.workbench.common.services.shared.builder.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DeployResult {

    private String groupId;
    private String artifactId;
    private String version;

    public DeployResult() {

    }

    public DeployResult(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

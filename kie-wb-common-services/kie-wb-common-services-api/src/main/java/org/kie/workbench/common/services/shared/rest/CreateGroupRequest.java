package org.kie.workbench.common.services.shared.rest;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CreateGroupRequest extends JobRequest {
    private String groupName;
    private String ownder;
    private String description;

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getOwnder() {
        return ownder;
    }
    public void setOwnder(String ownder) {
        this.ownder = ownder;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

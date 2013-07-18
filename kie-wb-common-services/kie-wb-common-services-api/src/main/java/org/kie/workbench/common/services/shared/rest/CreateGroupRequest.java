package org.kie.workbench.common.services.shared.rest;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CreateGroupRequest extends JobRequest {
    private String groupName;
    private String ownder;
    private String description;
    List<String> repositories;
    
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
	public List<String> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	} 
}

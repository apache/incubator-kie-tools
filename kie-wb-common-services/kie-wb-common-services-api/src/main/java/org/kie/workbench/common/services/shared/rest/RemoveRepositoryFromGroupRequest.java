package org.kie.workbench.common.services.shared.rest;


import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoveRepositoryFromGroupRequest extends JobRequest {
    private String groupName;
    private String repositoryName;
    
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

}

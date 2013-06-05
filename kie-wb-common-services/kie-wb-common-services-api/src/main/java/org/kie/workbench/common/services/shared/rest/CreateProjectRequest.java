package org.kie.workbench.common.services.shared.rest;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CreateProjectRequest extends JobRequest {
	private String repositoryName;
	private String projectName;
	private String description;

    
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}

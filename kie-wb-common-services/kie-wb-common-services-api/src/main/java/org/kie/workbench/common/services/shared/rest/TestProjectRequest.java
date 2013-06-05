package org.kie.workbench.common.services.shared.rest;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestProjectRequest extends JobRequest {
	private String repositoryName;
	private String projectName;
    private BuildConfig buildConfig;
    
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
	public BuildConfig getBuildConfig() {
		return buildConfig;
	}
	public void setBuildConfig(BuildConfig buildConfig) {
		this.buildConfig = buildConfig;
	}

}

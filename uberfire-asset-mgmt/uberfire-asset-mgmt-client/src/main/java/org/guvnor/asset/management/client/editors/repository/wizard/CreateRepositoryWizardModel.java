/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.wizard;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;

public class CreateRepositoryWizardModel {

    private boolean manged;

    private String repositoryName;

    private OrganizationalUnit organizationalUnit;

    private String projectName;

    private String projectDescription;

    private String groupId;

    private String artifactId;

    private String version;

    private boolean multiModule;

    private boolean configureRepository;

    private boolean mandatoryOU;

    public CreateRepositoryWizardModel() {
    }

    public boolean isManged() {
        return manged;
    }

    public void setManged(boolean manged) {
        this.manged = manged;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
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

    public boolean isMultiModule() {
        return multiModule;
    }

    public void setMultiModule(boolean multiModule) {
        this.multiModule = multiModule;
    }

    public boolean isConfigureRepository() {
        return configureRepository;
    }

    public void setConfigureRepository(boolean configureRepository) {
        this.configureRepository = configureRepository;
    }

    public boolean isMandatoryOU() {
        return mandatoryOU;
    }

    public void setMandatoryOU(boolean mandatoryOU) {
        this.mandatoryOU = mandatoryOU;
    }
}

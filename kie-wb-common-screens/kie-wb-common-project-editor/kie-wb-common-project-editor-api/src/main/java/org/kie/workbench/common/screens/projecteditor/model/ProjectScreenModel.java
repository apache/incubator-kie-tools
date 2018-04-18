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

package org.kie.workbench.common.screens.projecteditor.model;

import java.util.List;

import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.backend.vfs.Path;

@Portable
public class ProjectScreenModel {

    private POM pom;
    private KModuleModel KModule;
    private List<GitUrl> gitUrls;
    private ProjectImports projectImports;
    private ModuleRepositories repositories;
    private WhiteList whiteList;
    private Metadata POMMetaData;
    private Metadata KModuleMetaData;
    private Metadata projectImportsMetaData;
    private Metadata projectTagsMetaData;
    private Metadata whiteListMetaData;
    private Path pathToPOM;
    private Path pathToKModule;
    private Path pathToImports;
    private Path pathToRepositories;
    private Path pathToWhiteList;

    public POM getPOM() {
        return pom;
    }

    public void setPOM(final POM pom) {
        this.pom = pom;
    }

    public KModuleModel getKModule() {
        return KModule;
    }

    public void setKModule(final KModuleModel KModule) {
        this.KModule = KModule;
    }

    public List<GitUrl> getGitUrls() {
        return gitUrls;
    }

    public void setGitUrls(final List<GitUrl> gitUrls) {
        this.gitUrls = gitUrls;
    }

    public ProjectImports getProjectImports() {
        return projectImports;
    }

    public void setProjectImports(final ProjectImports projectImports) {
        this.projectImports = projectImports;
    }

    public ModuleRepositories getRepositories() {
        return repositories;
    }

    public void setRepositories(final ModuleRepositories repositories) {
        this.repositories = repositories;
    }

    public WhiteList getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(final WhiteList whiteList) {
        this.whiteList = whiteList;
    }

    public Metadata getPOMMetaData() {
        return POMMetaData;
    }

    public void setPOMMetaData(final Metadata POMMetaData) {
        this.POMMetaData = POMMetaData;
    }

    public Metadata getKModuleMetaData() {
        return KModuleMetaData;
    }

    public void setKModuleMetaData(final Metadata KModuleMetaData) {
        this.KModuleMetaData = KModuleMetaData;
    }

    public Metadata getProjectImportsMetaData() {
        return projectImportsMetaData;
    }

    public void setProjectImportsMetaData(final Metadata projectImportsMetaData) {
        this.projectImportsMetaData = projectImportsMetaData;
    }

    public Metadata getProjectTagsMetaData() {
        return projectTagsMetaData;
    }

    public void setProjectTagsMetaData(final Metadata projectTagsMetaData) {
        this.projectTagsMetaData = projectTagsMetaData;
    }

    public Metadata getWhiteListMetaData() {
        return whiteListMetaData;
    }

    public void setWhiteListMetaData(final Metadata whiteListMetaData) {
        this.whiteListMetaData = whiteListMetaData;
    }

    public Path getPathToPOM() {
        return pathToPOM;
    }

    public void setPathToPOM(final Path pathToPOM) {
        this.pathToPOM = pathToPOM;
    }

    public Path getPathToKModule() {
        return pathToKModule;
    }

    public void setPathToKModule(final Path pathToKModule) {
        this.pathToKModule = pathToKModule;
    }

    public Path getPathToImports() {
        return pathToImports;
    }

    public void setPathToImports(final Path pathToImports) {
        this.pathToImports = pathToImports;
    }

    public Path getPathToRepositories() {
        return pathToRepositories;
    }

    public void setPathToRepositories(final Path pathToRepositories) {
        this.pathToRepositories = pathToRepositories;
    }

    public Path getPathToWhiteList() {
        return pathToWhiteList;
    }

    public void setPathToWhiteList(final Path pathToWhiteList) {
        this.pathToWhiteList = pathToWhiteList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectScreenModel that = (ProjectScreenModel) o;

        if (pom != null ? !pom.equals(that.pom) : that.pom != null) {
            return false;
        }
        if (pathToPOM != null ? !pathToPOM.equals(that.pathToPOM) : that.pathToPOM != null) {
            return false;
        }
        if (POMMetaData != null ? !POMMetaData.equals(that.POMMetaData) : that.POMMetaData != null) {
            return false;
        }
        if (KModule != null ? !KModule.equals(that.KModule) : that.KModule != null) {
            return false;
        }
        if (gitUrls != null ? !gitUrls.equals(that.gitUrls) : that.gitUrls != null) {
            return false;
        }
        if (pathToKModule != null ? !pathToKModule.equals(that.pathToKModule) : that.pathToKModule != null) {
            return false;
        }
        if (KModuleMetaData != null ? !KModuleMetaData.equals(that.KModuleMetaData) : that.KModuleMetaData != null) {
            return false;
        }
        if (projectTagsMetaData != null ? !projectTagsMetaData.equals(that.projectTagsMetaData) : that.projectTagsMetaData != null) {
            return false;
        }
        if (projectImports != null ? !projectImports.equals(that.projectImports) : that.projectImports != null) {
            return false;
        }
        if (pathToImports != null ? !pathToImports.equals(that.pathToImports) : that.pathToImports != null) {
            return false;
        }
        if (projectImportsMetaData != null ? !projectImportsMetaData.equals(that.projectImportsMetaData) : that.projectImportsMetaData != null) {
            return false;
        }
        if (whiteList != null ? !whiteList.equals(that.whiteList) : that.whiteList != null) {
            return false;
        }
        if (pathToWhiteList != null ? !pathToWhiteList.equals(that.pathToWhiteList) : that.pathToWhiteList != null) {
            return false;
        }
        if (whiteListMetaData != null ? !whiteListMetaData.equals(that.whiteListMetaData) : that.whiteListMetaData != null) {
            return false;
        }
        if (repositories != null ? !repositories.equals(that.repositories) : that.repositories != null) {
            return false;
        }
        if (pathToRepositories != null ? !pathToRepositories.equals(that.pathToRepositories) : that.pathToRepositories != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = pom != null ? pom.hashCode() : 0;
        result = 31 * result + (pathToPOM != null ? pathToPOM.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (POMMetaData != null ? POMMetaData.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (KModule != null ? KModule.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (gitUrls != null ? gitUrls.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pathToKModule != null ? pathToKModule.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (KModuleMetaData != null ? KModuleMetaData.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (projectImports != null ? projectImports.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pathToImports != null ? pathToImports.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (projectImportsMetaData != null ? projectImportsMetaData.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (projectTagsMetaData != null ? projectTagsMetaData.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (whiteList != null ? whiteList.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pathToWhiteList != null ? pathToWhiteList.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (whiteListMetaData != null ? whiteListMetaData.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (repositories != null ? repositories.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pathToRepositories != null ? pathToRepositories.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

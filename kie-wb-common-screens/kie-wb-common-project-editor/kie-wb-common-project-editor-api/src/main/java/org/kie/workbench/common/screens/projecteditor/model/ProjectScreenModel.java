package org.kie.workbench.common.screens.projecteditor.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.project.service.model.KModuleModel;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.model.ProjectImports;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;

@Portable
public class ProjectScreenModel {

    private POM pom;
    private KModuleModel KModule;
    private Metadata POMMetaData;
    private Metadata KModuleMetaData;
    private ProjectImports projectImports;
    private Metadata projectImportsMetaData;

    public POM getPOM() {
        return pom;
    }

    public void setPOM(POM pom) {
        this.pom = pom;
    }

    public void setKModule(KModuleModel KModule) {
        this.KModule = KModule;
    }

    public KModuleModel getKModule() {
        return KModule;
    }

    public void setPOMMetaData(Metadata POMMetaData) {
        this.POMMetaData = POMMetaData;
    }

    public Metadata getPOMMetaData() {
        return POMMetaData;
    }

    public void setKModuleMetaData(Metadata KModuleMetaData) {
        this.KModuleMetaData = KModuleMetaData;
    }

    public Metadata getKModuleMetaData() {
        return KModuleMetaData;
    }

    public void setProjectImports(ProjectImports projectImports) {
        this.projectImports = projectImports;
    }

    public ProjectImports getProjectImports() {
        return projectImports;
    }

    public void setProjectImportsMetaData(Metadata projectImportsMetaData) {
        this.projectImportsMetaData = projectImportsMetaData;
    }

    public Metadata getProjectImportsMetaData() {
        return projectImportsMetaData;
    }
}

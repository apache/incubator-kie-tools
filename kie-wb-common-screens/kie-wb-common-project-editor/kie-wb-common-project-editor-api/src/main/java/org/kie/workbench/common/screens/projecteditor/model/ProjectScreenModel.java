package org.kie.workbench.common.screens.projecteditor.model;

import org.guvnor.common.services.project.model.KModuleModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;

@Portable
public class ProjectScreenModel {

    private POM pom;
    private KModuleModel KModule;
    private Metadata POMMetaData;
    private Metadata KModuleMetaData;
    private ProjectImports projectImports;
    private Metadata projectImportsMetaData;
    private Categories projectCategories;
    private Metadata projectCategoriesMetaData;
    private Path pathToPOM;
    private Path pathToKModule;
    private Path pathToImports;

    public POM getPOM() {
        return pom;
    }

    public void setPOM( POM pom ) {
        this.pom = pom;
    }

    public void setKModule( KModuleModel KModule ) {
        this.KModule = KModule;
    }

    public KModuleModel getKModule() {
        return KModule;
    }

    public void setPOMMetaData( Metadata POMMetaData ) {
        this.POMMetaData = POMMetaData;
    }

    public Metadata getPOMMetaData() {
        return POMMetaData;
    }

    public void setKModuleMetaData( Metadata KModuleMetaData ) {
        this.KModuleMetaData = KModuleMetaData;
    }

    public Metadata getKModuleMetaData() {
        return KModuleMetaData;
    }

    public void setProjectImports( ProjectImports projectImports ) {
        this.projectImports = projectImports;
    }

    public ProjectImports getProjectImports() {
        return projectImports;
    }

    public void setProjectImportsMetaData( Metadata projectImportsMetaData ) {
        this.projectImportsMetaData = projectImportsMetaData;
    }

    public Metadata getProjectImportsMetaData() {
        return projectImportsMetaData;
    }

    public Categories getProjectCategories() {
        return projectCategories;
    }

    public void setProjectCategories(Categories projectCategories) {
        this.projectCategories = projectCategories;
    }

    public Metadata getProjectCategoriesMetaData() {
        return projectCategoriesMetaData;
    }

    public void setProjectCategoriesMetaData(Metadata projectCategoriesMetaData) {
        this.projectCategoriesMetaData = projectCategoriesMetaData;
    }

    public Path getPathToPOM() {
        return pathToPOM;
    }

    public Path getPathToKModule() {
        return pathToKModule;
    }

    public Path getPathToImports() {
        return pathToImports;
    }

    public void setPathToPOM(Path pathToPOM) {
        this.pathToPOM = pathToPOM;
    }

    public void setPathToKModule(Path pathToKModule) {
        this.pathToKModule = pathToKModule;
    }

    public void setPathToImports(Path pathToImports) {
        this.pathToImports = pathToImports;
    }
}

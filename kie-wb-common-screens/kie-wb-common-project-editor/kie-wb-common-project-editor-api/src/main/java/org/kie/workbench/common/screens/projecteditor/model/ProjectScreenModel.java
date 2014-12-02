package org.kie.workbench.common.screens.projecteditor.model;

import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.annotations.Portable;
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ProjectScreenModel that = ( ProjectScreenModel ) o;

        if ( KModule != null ? !KModule.equals( that.KModule ) : that.KModule != null ) {
            return false;
        }
        if ( KModuleMetaData != null ? !KModuleMetaData.equals( that.KModuleMetaData ) : that.KModuleMetaData != null ) {
            return false;
        }
        if ( POMMetaData != null ? !POMMetaData.equals( that.POMMetaData ) : that.POMMetaData != null ) {
            return false;
        }
        if ( pathToImports != null ? !pathToImports.equals( that.pathToImports ) : that.pathToImports != null ) {
            return false;
        }
        if ( pathToKModule != null ? !pathToKModule.equals( that.pathToKModule ) : that.pathToKModule != null ) {
            return false;
        }
        if ( pathToPOM != null ? !pathToPOM.equals( that.pathToPOM ) : that.pathToPOM != null ) {
            return false;
        }
        if ( pom != null ? !pom.equals( that.pom ) : that.pom != null ) {
            return false;
        }
        if ( projectCategories != null ? !projectCategories.equals( that.projectCategories ) : that.projectCategories != null ) {
            return false;
        }
        if ( projectCategoriesMetaData != null ? !projectCategoriesMetaData.equals( that.projectCategoriesMetaData ) : that.projectCategoriesMetaData != null ) {
            return false;
        }
        if ( projectImports != null ? !projectImports.equals( that.projectImports ) : that.projectImports != null ) {
            return false;
        }
        if ( projectImportsMetaData != null ? !projectImportsMetaData.equals( that.projectImportsMetaData ) : that.projectImportsMetaData != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = pom != null ? pom.hashCode() : 0;
        result = 31 * result + ( KModule != null ? KModule.hashCode() : 0 );
        result = 31 * result + ( POMMetaData != null ? POMMetaData.hashCode() : 0 );
        result = 31 * result + ( KModuleMetaData != null ? KModuleMetaData.hashCode() : 0 );
        result = 31 * result + ( projectImports != null ? projectImports.hashCode() : 0 );
        result = 31 * result + ( projectImportsMetaData != null ? projectImportsMetaData.hashCode() : 0 );
        result = 31 * result + ( projectCategories != null ? projectCategories.hashCode() : 0 );
        result = 31 * result + ( projectCategoriesMetaData != null ? projectCategoriesMetaData.hashCode() : 0 );
        result = 31 * result + ( pathToPOM != null ? pathToPOM.hashCode() : 0 );
        result = 31 * result + ( pathToKModule != null ? pathToKModule.hashCode() : 0 );
        result = 31 * result + ( pathToImports != null ? pathToImports.hashCode() : 0 );
        return result;
    }
}

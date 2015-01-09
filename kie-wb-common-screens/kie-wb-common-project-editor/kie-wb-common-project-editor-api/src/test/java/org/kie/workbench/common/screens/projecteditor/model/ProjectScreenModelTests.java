package org.kie.workbench.common.screens.projecteditor.model;

import org.drools.workbench.models.datamodel.imports.Import;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.Repository;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;

import static org.jgroups.util.Util.assertTrue;
import static org.junit.Assert.*;

public class ProjectScreenModelTests {

    @Test
    public void testHashCode() {
        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( new POM( "test",
                               "test",
                               new GAV( "groupID",
                                        "artifactID",
                                        "version" ) ) );
        final Repository repository = new Repository();
        repository.setId( "guvnor-m2-repo" );
        repository.setName( "Guvnor M2 Repo" );
        repository.setUrl( "http://localhost/maven2/" );
        model.getPOM().addRepository( repository );
        model.setPOMMetaData( new Metadata() );

        model.setKModule( new KModuleModel() );
        model.setKModuleMetaData( new Metadata() );

        model.setProjectCategories( new Categories() );
        model.setProjectCategoriesMetaData( new Metadata() );

        model.setProjectImports( new ProjectImports() );
        model.setProjectImportsMetaData( new Metadata() );

        final int hashCode1 = model.hashCode();
        assertTrue( hashCode1 <= Integer.MAX_VALUE );
        assertTrue( hashCode1 >= Integer.MIN_VALUE );

        model.getProjectImports().getImports().addImport( new Import( "java.lang.List" ) );

        final int hashCode2 = model.hashCode();
        assertTrue( hashCode2 <= Integer.MAX_VALUE );
        assertTrue( hashCode2 >= Integer.MIN_VALUE );

        assertNotEquals( hashCode1,
                         hashCode2 );
    }

}

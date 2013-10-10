package org.kie.workbench.common.widgets.client.datamodel;

import java.util.Set;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.junit.Test;
import org.kie.api.definition.type.Role;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Product;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.RoleSmurf;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.Smurf;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Fact's annotations
 */
public class PackageDataModelFactAnnotationsTest {

    @Test
    public void testCorrectPackageDMOZeroAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Product.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" );
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "Product",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testCorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Smurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations" );
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "Smurf",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "Smurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfDescriptor",
                      annotation.getQualifiedTypeName() );
        assertEquals( "blue",
                      annotation.getAttributes().get( "colour" ) );
        assertEquals( "M",
                      annotation.getAttributes().get( "gender" ) );
        assertEquals( "Brains",
                      annotation.getAttributes().get( "description" ) );
    }

    @Test
    public void testCorrectPackageDMOAnnotationAttributes2() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          RoleSmurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations" );
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "RoleSmurf",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "RoleSmurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.api.definition.type.Role",
                      annotation.getQualifiedTypeName() );
        assertEquals( Role.Type.EVENT.name(),
                      annotation.getAttributes().get( "value" ) );
    }

    @Test
    public void testIncorrectPackageDMOZeroAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Product.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 0,
                      oracle.getFactTypes().length );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testIncorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Smurf.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 0,
                      oracle.getFactTypes().length );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "Smurf" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

}

package org.kie.workbench.common.services.datamodel.backend.server;

import java.lang.annotation.ElementType;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.Role;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf;
import org.kie.workbench.common.services.datamodel.model.Annotation;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;

import static org.junit.Assert.*;

/**
 * Tests for Fact's annotations
 */
public class DataModelFactAnnotationsTest {

    @Test
    public void testCorrectPackageDMOZeroAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Product.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" );
        packageBuilder.setProjectOracle( oracle );
        final PackageDataModelOracle packageOracle = packageBuilder.build();

        assertEquals( 1,
                      packageOracle.getFactTypes().length );
        assertEquals( "Product",
                      packageOracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = packageOracle.getTypeAnnotations( "Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testCorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Smurf.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations" );
        packageBuilder.setProjectOracle( oracle );
        final PackageDataModelOracle packageOracle = packageBuilder.build();

        assertEquals( 1,
                      packageOracle.getFactTypes().length );
        assertEquals( "Smurf",
                      packageOracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = packageOracle.getTypeAnnotations( "Smurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfDescriptor",
                      annotation.getQualifiedTypeName() );
        assertEquals( "blue",
                      annotation.getAttributes().get( "colour" ) );
        assertEquals( "M",
                      annotation.getAttributes().get( "gender" ) );
        assertEquals( "Brains",
                      annotation.getAttributes().get( "description" ) );
    }

    @Test
    @Ignore("manstis needs to fix")
    public void testCorrectPackageDMOAnnotationAttributes2() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          RoleSmurf.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations" );
        packageBuilder.setProjectOracle( oracle );
        final PackageDataModelOracle packageOracle = packageBuilder.build();

        assertEquals( 1,
                      packageOracle.getFactTypes().length );
        assertEquals( "RoleSmurf",
                      packageOracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = packageOracle.getTypeAnnotations( "RoleSmurf" );
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
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Product.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
        packageBuilder.setProjectOracle( oracle );
        final PackageDataModelOracle packageOracle = packageBuilder.build();

        assertEquals( 0,
                      packageOracle.getFactTypes().length );

        final Set<Annotation> annotations = packageOracle.getTypeAnnotations( "Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testIncorrectPackageDMOAnnotationAttributes() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          Smurf.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        //Build PackageDMO. Defaults to defaultpkg
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
        packageBuilder.setProjectOracle( oracle );
        final PackageDataModelOracle packageOracle = packageBuilder.build();

        assertEquals( 0,
                      packageOracle.getFactTypes().length );

        final Set<Annotation> annotations = packageOracle.getTypeAnnotations( "Smurf" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testProjectDMOZeroAnnotationAttributes() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          Product.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product" );
        assertNotNull( annotations );
        assertEquals( 0,
                      annotations.size() );
    }

    @Test
    public void testProjectDMOAnnotationAttributes() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          Smurf.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.Smurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfDescriptor",
                      annotation.getQualifiedTypeName() );
        assertEquals( "blue",
                      annotation.getAttributes().get( "colour" ) );
        assertEquals( "M",
                      annotation.getAttributes().get( "gender" ) );
        assertEquals( "Brains",
                      annotation.getAttributes().get( "description" ) );
    }

    @Test
    @Ignore("manstis needs to fix")
    public void testProjectDMOAnnotationAttributes2() throws Exception {
        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( builder,
                                                          RoleSmurf.class,
                                                          false,
                                                          false );
        cb.build( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf",
                      oracle.getFactTypes()[ 0 ] );

        final Set<Annotation> annotations = oracle.getTypeAnnotations( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.RoleSmurf" );
        assertNotNull( annotations );
        assertEquals( 1,
                      annotations.size() );

        final Annotation annotation = annotations.iterator().next();
        assertEquals( "org.kie.api.definition.type.Role",
                      annotation.getQualifiedTypeName() );
        assertEquals( Role.Type.EVENT.name(),
                      annotation.getAttributes().get( "value" ) );
    }

}

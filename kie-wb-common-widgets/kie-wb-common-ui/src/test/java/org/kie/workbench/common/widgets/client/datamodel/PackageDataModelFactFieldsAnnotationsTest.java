//package org.kie.workbench.common.widgets.client.datamodel;
//
//import java.util.Map;
//import java.util.Set;
//
//import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
//import org.drools.workbench.models.datamodel.oracle.Annotation;
//import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
//import org.drools.workbench.models.datamodel.oracle.TypeSource;
//import org.junit.Test;
//import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
//import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
//import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
//import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
//import org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse;
//
//import static org.junit.Assert.*;
//
///**
//* Tests for Fact's annotations
//*/
//public class PackageDataModelFactFieldsAnnotationsTest {
//
//    @Test
//    public void testCorrectPackageDMOZeroAnnotationAttributes() throws Exception {
//        //Build ProjectDMO
//        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
//                                                          Product.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        //Build PackageDMO
//        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" );
//        packageBuilder.setProjectOracle( oracle );
//        final PackageDataModelOracle packageOracle = packageBuilder.build();
//
//        assertEquals( 1,
//                      packageOracle.getFactTypes().length );
//        assertEquals( "Product",
//                      packageOracle.getFactTypes()[ 0 ] );
//
//        final Map<String, Set<Annotation>> fieldsAnnotations = packageOracle.getTypeFieldsAnnotations( "Product" );
//        assertNotNull( fieldsAnnotations );
//        assertEquals( 0,
//                      fieldsAnnotations.size() );
//    }
//
//    @Test
//    public void testCorrectPackageDMOAnnotationAttributes() throws Exception {
//        //Build ProjectDMO
//        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
//                                                          SmurfHouse.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        //Build PackageDMO
//        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations" );
//        packageBuilder.setProjectOracle( oracle );
//        final PackageDataModelOracle packageOracle = packageBuilder.build();
//
//        assertEquals( 1,
//                      packageOracle.getFactTypes().length );
//        assertEquals( "SmurfHouse",
//                      packageOracle.getFactTypes()[ 0 ] );
//
//        final Map<String, Set<Annotation>> fieldsAnnotations = packageOracle.getTypeFieldsAnnotations( "SmurfHouse" );
//        assertNotNull( fieldsAnnotations );
//        assertEquals( 2,
//                      fieldsAnnotations.size() );
//
//        assertTrue( fieldsAnnotations.containsKey( "occupant" ) );
//        final Set<Annotation> occupantAnnotations = fieldsAnnotations.get( "occupant" );
//        assertNotNull( occupantAnnotations );
//        assertEquals( 1,
//                      occupantAnnotations.size() );
//
//        final Annotation annotation = occupantAnnotations.iterator().next();
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldDescriptor",
//                      annotation.getQualifiedTypeName() );
//        assertEquals( "blue",
//                      annotation.getAttributes().get( "colour" ) );
//        assertEquals( "M",
//                      annotation.getAttributes().get( "gender" ) );
//        assertEquals( "Brains",
//                      annotation.getAttributes().get( "description" ) );
//
//        assertTrue( fieldsAnnotations.containsKey( "positionedOccupant" ) );
//        final Set<Annotation> posOccupantAnnotations = fieldsAnnotations.get( "positionedOccupant" );
//        assertNotNull( posOccupantAnnotations );
//        assertEquals( 1,
//                      posOccupantAnnotations.size() );
//
//        final Annotation annotation2 = posOccupantAnnotations.iterator().next();
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldPositionDescriptor",
//                      annotation2.getQualifiedTypeName() );
//        assertEquals( Integer.toString( 1 ),
//                      annotation2.getAttributes().get( "value" ) );
//    }
//
//    @Test
//    public void testIncorrectPackageDMOZeroAnnotationAttributes() throws Exception {
//        //Build ProjectDMO
//        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
//                                                          Product.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        //Build PackageDMO. Defaults to defaultpkg
//        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
//        packageBuilder.setProjectOracle( oracle );
//        final PackageDataModelOracle packageOracle = packageBuilder.build();
//
//        assertEquals( 0,
//                      packageOracle.getFactTypes().length );
//
//        final Map<String, Set<Annotation>> fieldsAnnotations = packageOracle.getTypeFieldsAnnotations( "Product" );
//        assertNotNull( fieldsAnnotations );
//        assertEquals( 0,
//                      fieldsAnnotations.size() );
//    }
//
//    @Test
//    public void testIncorrectPackageDMOAnnotationAttributes() throws Exception {
//        //Build ProjectDMO
//        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
//                                                          SmurfHouse.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        //Build PackageDMO. Defaults to defaultpkg
//        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder();
//        packageBuilder.setProjectOracle( oracle );
//        final PackageDataModelOracle packageOracle = packageBuilder.build();
//
//        assertEquals( 0,
//                      packageOracle.getFactTypes().length );
//
//        final Map<String, Set<Annotation>> fieldAnnotations = packageOracle.getTypeFieldsAnnotations( "SmurfHouse" );
//        assertNotNull( fieldAnnotations );
//        assertEquals( 0,
//                      fieldAnnotations.size() );
//    }
//
//    @Test
//    public void testProjectDMOZeroAnnotationAttributes() throws Exception {
//        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( builder,
//                                                          Product.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        assertEquals( 1,
//                      oracle.getFactTypes().length );
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product",
//                      oracle.getFactTypes()[ 0 ] );
//
//        final Map<String, Set<Annotation>> fieldAnnotations = oracle.getTypeFieldsAnnotations( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product" );
//        assertNotNull( fieldAnnotations );
//        assertEquals( 0,
//                      fieldAnnotations.size() );
//    }
//
//    @Test
//    public void testProjectDMOAnnotationAttributes() throws Exception {
//        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
//        final ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();
//
//        final ClassFactBuilder cb = new ClassFactBuilder( builder,
//                                                          SmurfHouse.class,
//                                                          false,
//                                                          TypeSource.JAVA_PROJECT );
//        cb.build( oracle );
//
//        assertEquals( 1,
//                      oracle.getFactTypes().length );
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse",
//                      oracle.getFactTypes()[ 0 ] );
//
//        final Map<String, Set<Annotation>> fieldsAnnotations = oracle.getTypeFieldsAnnotations( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfHouse" );
//        assertNotNull( fieldsAnnotations );
//        assertEquals( 2,
//                      fieldsAnnotations.size() );
//
//        assertTrue( fieldsAnnotations.containsKey( "occupant" ) );
//        final Set<Annotation> occupantAnnotations = fieldsAnnotations.get( "occupant" );
//        assertNotNull( occupantAnnotations );
//        assertEquals( 1,
//                      occupantAnnotations.size() );
//
//        final Annotation annotation = occupantAnnotations.iterator().next();
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldDescriptor",
//                      annotation.getQualifiedTypeName() );
//        assertEquals( "blue",
//                      annotation.getAttributes().get( "colour" ) );
//        assertEquals( "M",
//                      annotation.getAttributes().get( "gender" ) );
//        assertEquals( "Brains",
//                      annotation.getAttributes().get( "description" ) );
//
//        assertTrue( fieldsAnnotations.containsKey( "positionedOccupant" ) );
//        final Set<Annotation> posOccupantAnnotations = fieldsAnnotations.get( "positionedOccupant" );
//        assertNotNull( posOccupantAnnotations );
//        assertEquals( 1,
//                      posOccupantAnnotations.size() );
//
//        final Annotation annotation2 = posOccupantAnnotations.iterator().next();
//        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations.SmurfFieldPositionDescriptor",
//                      annotation2.getQualifiedTypeName() );
//        assertEquals( Integer.toString( 1 ),
//                      annotation2.getAttributes().get( "value" ) );
//    }
//
//}

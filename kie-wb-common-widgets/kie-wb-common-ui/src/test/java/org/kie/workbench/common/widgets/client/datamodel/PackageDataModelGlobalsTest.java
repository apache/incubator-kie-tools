//package org.kie.workbench.common.widgets.client.datamodel;
//
//import java.util.Arrays;
//
//import org.drools.workbench.models.datamodel.imports.Import;
//import org.drools.workbench.models.datamodel.imports.Imports;
//import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
//import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
//import org.junit.Test;
//import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
//import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
//import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
//
//import static org.junit.Assert.*;
//
///**
//* Tests for Globals
//*/
//public class PackageDataModelGlobalsTest {
//
//    @Test
//    public void testGlobal() throws Exception {
//        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
//                .addClass( Product.class )
//                .build();
//
//        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" )
//                .setProjectOracle( pd )
//                .addGlobals( "global org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product g;" )
//                .build();
//
//        assertNotNull( dmo );
//
//        assertEquals( 1,
//                      dmo.getFactTypes().length );
//        assertEquals( "Product",
//                      dmo.getFactTypes()[ 0 ] );
//
//        assertEquals( 1,
//                      dmo.getGlobalVariables().length );
//        assertEquals( "g",
//                      dmo.getGlobalVariables()[ 0 ] );
//        assertEquals( "Product",
//                      dmo.getGlobalVariable( "g" ) );
//
//        final String[] fields = dmo.getFieldCompletions( "Product" );
//        assertNotNull( fields );
//        assertTrue( Arrays.asList( fields ).contains( "this" ) );
//        assertTrue( Arrays.asList( fields ).contains( "colour" ) );
//
//        assertEquals( 0,
//                      dmo.getGlobalCollections().length );
//    }
//
//    @Test
//    public void testGlobalCollections() throws Exception {
//        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
//                .addClass( java.util.List.class )
//                .build();
//
//        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" )
//                .setProjectOracle( pd )
//                .addGlobals( "global java.util.List list;" )
//                .build();
//
//        final Imports imports = new Imports();
//        imports.addImport( new Import( "java.util.List" ) );
//        dmo.filter( imports );
//
//        assertNotNull( dmo );
//
//        assertEquals( 1,
//                      dmo.getFactTypes().length );
//        assertEquals( "List",
//                      dmo.getFactTypes()[ 0 ] );
//
//        assertEquals( 1,
//                      dmo.getGlobalVariables().length );
//        assertEquals( "list",
//                      dmo.getGlobalVariables()[ 0 ] );
//        assertEquals( "List",
//                      dmo.getGlobalVariable( "list" ) );
//
//        assertEquals( 1,
//                      dmo.getGlobalCollections().length );
//        assertEquals( "list",
//                      dmo.getGlobalCollections()[ 0 ] );
//    }
//
//}

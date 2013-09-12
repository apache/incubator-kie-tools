package org.kie.workbench.common.services.datamodel.backend.server;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.workbench.models.commons.shared.oracle.model.DataType;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.drools.workbench.models.commons.shared.oracle.ProjectDataModelOracle;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Purchase;

import static org.junit.Assert.*;

/**
 * Tests for MethodInfo and Parametric types
 */
public class DataModelParametricTypesTest {

    @Test
    public void testClassFieldInspector() throws Exception {
        final ClassFieldInspector cfi = new ClassFieldInspector( Purchase.class );
        final Type t1 = cfi.getFieldTypesField().get( "customerName" ).getGenericType();
        final Type t2 = cfi.getFieldTypesField().get( "items" ).getGenericType();

        assertNotNull( t1 );
        assertNotNull( t2 );

        assertFalse( t1 instanceof ParameterizedType );
        assertTrue( t2 instanceof ParameterizedType );
    }

    @Test
    public void testPackageDMOParametricReturnTypes() throws Exception {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Purchase.class )
                .addClass( Product.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertNotNull( dmo );

        assertEquals( 2,
                      dmo.getFactTypes().length );

        List<String> list = Arrays.asList( dmo.getFactTypes() );

        assertTrue( list.contains( "Purchase" ) );
        assertTrue( list.contains( "Product" ) );

        assertEquals( "java.util.Collection",
                      dmo.getFieldClassName( "Purchase",
                                             "items" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      dmo.getFieldType( "Purchase",
                                        "items" ) );
        assertEquals( "Product",
                      dmo.getParametricFieldType( "Purchase",
                                                  "items" ) );

    }

    @Test
    public void testProjectDMOParametricReturnTypes() throws Exception {
        final ProjectDataModelOracle oracle = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Purchase.class )
                .addClass( Product.class )
                .build();

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getFactTypes().length );

        List<String> list = Arrays.asList( oracle.getFactTypes() );

        assertTrue( list.contains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Purchase" ) );
        assertTrue( list.contains( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product" ) );

        assertEquals( "java.util.Collection",
                      oracle.getFieldClassName( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Purchase",
                                                "items" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      oracle.getFieldType( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Purchase",
                                           "items" ) );
        assertEquals( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product",
                      oracle.getParametricFieldType( "org.kie.workbench.common.services.datamodel.backend.server.testclasses.Purchase",
                                                     "items" ) );

    }

    @Test
    public void testParametricMethod() throws Exception {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Purchase.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertNotNull( dmo );

        assertEquals( "Product",
                      dmo.getParametricFieldType( "Purchase",
                                                  "customerPurchased(int)" ) );
    }

}

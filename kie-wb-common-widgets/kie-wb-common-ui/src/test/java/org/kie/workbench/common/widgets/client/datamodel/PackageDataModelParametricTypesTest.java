package org.kie.workbench.common.widgets.client.datamodel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Product;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Purchase;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MethodInfo and Parametric types
 */
public class PackageDataModelParametricTypesTest {

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
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Purchase.class )
                .addClass( Product.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getFactTypes().length );

        List<String> list = Arrays.asList( oracle.getFactTypes() );

        assertTrue( list.contains( "Purchase" ) );
        assertTrue( list.contains( "Product" ) );

        assertEquals( "java.util.Collection",
                      oracle.getFieldClassName( "Purchase",
                                                "items" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      oracle.getFieldType( "Purchase",
                                           "items" ) );
        assertEquals( "Product",
                      oracle.getParametricFieldType( "Purchase",
                                                     "items" ) );

    }

    @Test
    public void testParametricMethod() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( Purchase.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( "Product",
                      oracle.getParametricFieldType( "Purchase",
                                                     "customerPurchased(int)" ) );
    }

}

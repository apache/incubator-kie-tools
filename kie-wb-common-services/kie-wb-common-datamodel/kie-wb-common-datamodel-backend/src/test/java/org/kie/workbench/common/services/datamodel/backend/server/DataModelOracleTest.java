package org.kie.workbench.common.services.datamodel.backend.server;

import java.io.IOException;
import java.util.List;

import org.drools.workbench.models.commons.shared.oracle.DataType;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.TestDataTypes;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.TestDelegatedClass;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.TestSubClass;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.TestSuperClass;
import org.kie.workbench.common.services.datamodel.model.ModelField;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;

import static org.junit.Assert.*;

/**
 * Tests for the ProjectDataModelOracle
 */
public class DataModelOracleTest {

    @Test
    public void testDataTypes() throws IOException {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestDataTypes.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( TestDataTypes.class.getSimpleName(),
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 20,
                      dmo.getFieldCompletions( TestDataTypes.class.getSimpleName() ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldString" ) );
        assertEquals( DataType.TYPE_BOOLEAN,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldBooleanObject" ) );
        assertEquals( DataType.TYPE_DATE,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldDate" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldNumeric" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldBigDecimal" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGINTEGER,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldBigInteger" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldByteObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldDoubleObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldFloatObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldIntegerObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldLongObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldShortObject" ) );
        assertEquals( DataType.TYPE_BOOLEAN,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldBooleanPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldBytePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldDoublePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldFloatPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldIntegerPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldLongPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      dmo.getFieldType( TestDataTypes.class.getSimpleName(),
                                        "fieldShortPrimitive" ) );
    }

    @Test
    public void testSuperClass() throws IOException {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( TestSuperClass.class.getSimpleName(),
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 3,
                      dmo.getFieldCompletions( TestSuperClass.class.getSimpleName() ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( TestSuperClass.class.getSimpleName(),
                                        "this" ) );
        assertEquals( TestSuperClass.class.getSimpleName(),
                      dmo.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                             "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( TestSuperClass.class.getSimpleName(),
                                        "field1" ) );
        assertEquals( String.class.getName(),
                      dmo.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                             "field1" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      dmo.getFieldType( TestSuperClass.class.getSimpleName(),
                                        "list" ) );
        assertEquals( List.class.getName(),
                      dmo.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                             "list" ) );
        assertEquals( String.class.getName(),
                      dmo.getParametricFieldType( TestSuperClass.class.getSimpleName(),
                                                  "list" ) );
    }

    @Test
    public void testSubClass() throws IOException {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSubClass.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( TestSubClass.class.getSimpleName(),
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 4,
                      dmo.getFieldCompletions( TestSubClass.class.getSimpleName() ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( TestSubClass.class.getSimpleName(),
                                        "this" ) );
        assertEquals( TestSubClass.class.getSimpleName(),
                      dmo.getFieldClassName( TestSubClass.class.getSimpleName(),
                                             "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( TestSubClass.class.getSimpleName(),
                                        "field1" ) );
        assertEquals( String.class.getName(),
                      dmo.getFieldClassName( TestSubClass.class.getSimpleName(),
                                             "field1" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( TestSubClass.class.getSimpleName(),
                                        "field2" ) );
        assertEquals( String.class.getName(),
                      dmo.getFieldClassName( TestSubClass.class.getSimpleName(),
                                             "field2" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      dmo.getFieldType( TestSubClass.class.getSimpleName(),
                                        "list" ) );
        assertEquals( List.class.getName(),
                      dmo.getFieldClassName( TestSubClass.class.getSimpleName(),
                                             "list" ) );
        assertEquals( String.class.getName(),
                      dmo.getParametricFieldType( TestSubClass.class.getSimpleName(),
                                                  "list" ) );

        ModelField[] fields = dmo.getModelFields().get(TestSubClass.class.getSimpleName());
        for (ModelField field : fields) {
            if ("this".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.SELF, field.getOrigin());
            else if ("field1".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.INHERITED, field.getOrigin());
            else if ("field2".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.DECLARED, field.getOrigin());
            // TODO this last case is arguable : should be inherited, but is cualified as delegated, probably needs to be looked at
            // else if ("list".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin());
        }
    }

    @Test
    public void testDelegatedClass() throws IOException {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestDelegatedClass.class )
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.services.datamodel.backend.server.testclasses" ).setProjectOracle( pd ).build();

        assertEquals( 1,
                      dmo.getFactTypes().length );
        assertEquals( TestDelegatedClass.class.getSimpleName(),
                      dmo.getFactTypes()[ 0 ] );

        assertEquals( 3,
                      dmo.getFieldCompletions( TestDelegatedClass.class.getSimpleName() ).length );

        assertEquals( DataType.TYPE_THIS,
                      dmo.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                        "this" ) );
        assertEquals( TestDelegatedClass.class.getSimpleName(),
                      dmo.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                             "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                        "field1" ) );
        assertEquals( String.class.getName(),
                      dmo.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                             "field1" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      dmo.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                        "list" ) );
        assertEquals( List.class.getName(),
                      dmo.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                             "list" ) );
        assertEquals( String.class.getName(),
                      dmo.getParametricFieldType( TestDelegatedClass.class.getSimpleName(),
                                                  "list" ) );

        ModelField[] fields = dmo.getModelFields().get(TestDelegatedClass.class.getSimpleName());
        for (ModelField field : fields) {
            if ("this".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.SELF, field.getOrigin());
            else if ("field1".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin());
            else if ("list".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin());
        }
    }

}

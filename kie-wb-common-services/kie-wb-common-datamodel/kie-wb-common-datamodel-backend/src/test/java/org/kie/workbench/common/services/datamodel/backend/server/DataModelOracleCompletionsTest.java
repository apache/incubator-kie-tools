package org.kie.workbench.common.services.datamodel.backend.server;

import org.drools.workbench.models.commons.shared.oracle.DataType;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.FieldAccessorsAndMutators;
import org.kie.workbench.common.services.datamodel.model.ModelField;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;

import static org.junit.Assert.*;

/**
 * Tests for the ProjectDataModelOracle completions
 */
public class DataModelOracleCompletionsTest {

    @Test
    public void testFactsAndFields() {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( pd ).build();

        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      dmo.getFieldType( "Person",
                                        "age" ) );
        assertEquals( DataType.TYPE_STRING,
                      dmo.getFieldType( "Person",
                                        "sex" ) );
    }

    @Test
    public void testFactCompletions() {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addFact( "Vehicle" )
                .addField( new ModelField( "make",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "type",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( pd ).build();

        String[] types = dmo.getFactTypes();
        assertEquals( 2,
                      types.length );
        DataModelOracleTestUtils.assertContains( "Person",
                                                 types );
        DataModelOracleTestUtils.assertContains( "Vehicle",
                                                 types );
    }

    @Test
    public void testFactFieldCompletions() {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( pd ).build();

        String[] personFields = dmo.getFieldCompletions( "Person" );
        assertEquals( 4,
                      personFields.length );
        assertEquals( "this",
                      personFields[ 0 ] );
        assertEquals( "age",
                      personFields[ 1 ] );
        assertEquals( "rank",
                      personFields[ 2 ] );
        assertEquals( "name",
                      personFields[ 3 ] );

    }

    @Test
    public void testFactFieldOperatorCompletions() {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( pd ).build();

        String[] personThisOperators = dmo.getOperatorCompletions( "Person",
                                                                   "this" );
        assertEquals( 4,
                      personThisOperators.length );
        assertEquals( personThisOperators[ 0 ],
                      "==" );
        assertEquals( personThisOperators[ 1 ],
                      "!=" );
        assertEquals( personThisOperators[ 2 ],
                      "== null" );
        assertEquals( personThisOperators[ 3 ],
                      "!= null" );

        String[] personAgeOperators = dmo.getOperatorCompletions( "Person",
                                                                  "age" );
        assertEquals( 10,
                      personAgeOperators.length );
        assertEquals( personAgeOperators[ 0 ],
                      "==" );
        assertEquals( personAgeOperators[ 1 ],
                      "!=" );
        assertEquals( personAgeOperators[ 2 ],
                      "<" );
        assertEquals( personAgeOperators[ 3 ],
                      ">" );
        assertEquals( personAgeOperators[ 4 ],
                      "<=" );
        assertEquals( personAgeOperators[ 5 ],
                      ">=" );
        assertEquals( personAgeOperators[ 6 ],
                      "== null" );
        assertEquals( personAgeOperators[ 7 ],
                      "!= null" );
        assertEquals( personAgeOperators[ 8 ],
                      "in" );
        assertEquals( personAgeOperators[ 9 ],
                      "not in" );

        String[] personRankOperators = dmo.getOperatorCompletions( "Person",
                                                                   "rank" );
        assertEquals( 8,
                      personRankOperators.length );
        assertEquals( personRankOperators[ 0 ],
                      "==" );
        assertEquals( personRankOperators[ 1 ],
                      "!=" );
        assertEquals( personRankOperators[ 2 ],
                      "<" );
        assertEquals( personRankOperators[ 3 ],
                      ">" );
        assertEquals( personRankOperators[ 4 ],
                      "<=" );
        assertEquals( personRankOperators[ 5 ],
                      ">=" );
        assertEquals( personRankOperators[ 6 ],
                      "== null" );
        assertEquals( personRankOperators[ 7 ],
                      "!= null" );

        String[] personNameOperators = dmo.getOperatorCompletions( "Person",
                                                                   "name" );
        assertEquals( 12,
                      personNameOperators.length );
        assertEquals( "==",
                      personNameOperators[ 0 ] );
        assertEquals( "!=",
                      personNameOperators[ 1 ] );
        assertEquals( "<",
                      personNameOperators[ 2 ] );
        assertEquals( ">",
                      personNameOperators[ 3 ] );
        assertEquals( "<=",
                      personNameOperators[ 4 ] );
        assertEquals( ">=",
                      personNameOperators[ 5 ] );
        assertEquals( "matches",
                      personNameOperators[ 6 ] );
        assertEquals( "soundslike",
                      personNameOperators[ 7 ] );
        assertEquals( "== null",
                      personNameOperators[ 8 ] );
        assertEquals( "!= null",
                      personNameOperators[ 9 ] );
        assertEquals( "in",
                      personNameOperators[ 10 ] );
        assertEquals( "not in",
                      personNameOperators[ 11 ] );
    }

    @Test
    public void testFactFieldConnectiveOperatorCompletions() {
        final ProjectDataModelOracle pd = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "rank",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_COMPARABLE ) )
                .addField( new ModelField( "name",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .build();

        final PackageDataModelOracle dmo = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( pd ).build();

        String[] personThisConnectiveOperators = dmo.getConnectiveOperatorCompletions( "Person",
                                                                                       "this" );
        assertEquals( 3,
                      personThisConnectiveOperators.length );
        assertEquals( personThisConnectiveOperators[ 0 ],
                      "|| ==" );
        assertEquals( personThisConnectiveOperators[ 1 ],
                      "|| !=" );
        assertEquals( personThisConnectiveOperators[ 2 ],
                      "&& !=" );

        String[] personAgeConnectiveOperators = dmo.getConnectiveOperatorCompletions( "Person",
                                                                                      "age" );
        assertEquals( 11,
                      personAgeConnectiveOperators.length );
        assertEquals( personAgeConnectiveOperators[ 0 ],
                      "|| ==" );
        assertEquals( personAgeConnectiveOperators[ 1 ],
                      "|| !=" );
        assertEquals( personAgeConnectiveOperators[ 2 ],
                      "&& !=" );
        assertEquals( personAgeConnectiveOperators[ 3 ],
                      "&& >" );
        assertEquals( personAgeConnectiveOperators[ 4 ],
                      "&& <" );
        assertEquals( personAgeConnectiveOperators[ 5 ],
                      "|| >" );
        assertEquals( personAgeConnectiveOperators[ 6 ],
                      "|| <" );
        assertEquals( personAgeConnectiveOperators[ 7 ],
                      "&& >=" );
        assertEquals( personAgeConnectiveOperators[ 8 ],
                      "&& <=" );
        assertEquals( personAgeConnectiveOperators[ 9 ],
                      "|| <=" );
        assertEquals( personAgeConnectiveOperators[ 10 ],
                      "|| >=" );

        String[] personRankConnectiveOperators = dmo.getConnectiveOperatorCompletions( "Person",
                                                                                       "rank" );
        assertEquals( 11,
                      personRankConnectiveOperators.length );
        assertEquals( personRankConnectiveOperators[ 0 ],
                      "|| ==" );
        assertEquals( personRankConnectiveOperators[ 1 ],
                      "|| !=" );
        assertEquals( personRankConnectiveOperators[ 2 ],
                      "&& !=" );
        assertEquals( personRankConnectiveOperators[ 3 ],
                      "&& >" );
        assertEquals( personRankConnectiveOperators[ 4 ],
                      "&& <" );
        assertEquals( personRankConnectiveOperators[ 5 ],
                      "|| >" );
        assertEquals( personRankConnectiveOperators[ 6 ],
                      "|| <" );
        assertEquals( personRankConnectiveOperators[ 7 ],
                      "&& >=" );
        assertEquals( personRankConnectiveOperators[ 8 ],
                      "&& <=" );
        assertEquals( personRankConnectiveOperators[ 9 ],
                      "|| <=" );
        assertEquals( personRankConnectiveOperators[ 10 ],
                      "|| >=" );

        String[] personNameConnectiveOperators = dmo.getConnectiveOperatorCompletions( "Person",
                                                                                       "name" );
        assertEquals( 13,
                      personNameConnectiveOperators.length );
        assertEquals( personNameConnectiveOperators[ 0 ],
                      "|| ==" );
        assertEquals( personNameConnectiveOperators[ 1 ],
                      "|| !=" );
        assertEquals( personNameConnectiveOperators[ 2 ],
                      "&& !=" );
        assertEquals( personNameConnectiveOperators[ 3 ],
                      "&& >" );
        assertEquals( personNameConnectiveOperators[ 4 ],
                      "&& <" );
        assertEquals( personNameConnectiveOperators[ 5 ],
                      "|| >" );
        assertEquals( personNameConnectiveOperators[ 6 ],
                      "|| <" );
        assertEquals( personNameConnectiveOperators[ 7 ],
                      "&& >=" );
        assertEquals( personNameConnectiveOperators[ 8 ],
                      "&& <=" );
        assertEquals( personNameConnectiveOperators[ 9 ],
                      "|| <=" );
        assertEquals( personNameConnectiveOperators[ 10 ],
                      "|| >=" );
        assertEquals( personNameConnectiveOperators[ 11 ],
                      "&& matches" );
        assertEquals( personNameConnectiveOperators[ 12 ],
                      "|| matches" );
    }

}

/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.DecisionTableCellValueFactory;
import org.guvnor.common.services.shared.config.ApplicationPreferences;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.uberfire.backend.vfs.Path;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CellValueFactory
 */
public class CellValueFactoryTests {

    private AsyncPackageDataModelOracle oracle;
    private GuidedDecisionTable52 model = null;
    private DecisionTableCellValueFactory factory = null;

    private AttributeCol52 at1 = null;
    private AttributeCol52 at2 = null;
    private ConditionCol52 c1 = null;
    private ConditionCol52 c2 = null;
    private ConditionCol52 c3 = null;
    private ConditionCol52 c4 = null;
    private ConditionCol52 c5 = null;
    private ConditionCol52 c6 = null;
    private ConditionCol52 c7 = null;
    private ConditionCol52 c8 = null;
    private ConditionCol52 c9 = null;
    private ConditionCol52 c10 = null;
    private ConditionCol52 c11 = null;
    private ActionSetFieldCol52 a1 = null;
    private ActionInsertFactCol52 a2 = null;

    @Before
    @SuppressWarnings("serial")
    public void setup() {
        final ProjectDataModelOracle loader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "MyClass" )
                .addField( new ModelField( "bigDecimalField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_BIGDECIMAL ) )
                .addField( new ModelField( "bigIntegerField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_BIGINTEGER ) )
                .addField( new ModelField( "byteField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_BYTE ) )
                .addField( new ModelField( "doubleField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_DOUBLE ) )
                .addField( new ModelField( "floatField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_FLOAT ) )
                .addField( new ModelField( "integerField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "longField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_LONG ) )
                .addField( new ModelField( "shortField",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_SHORT ) )
                .addField( new ModelField( "stringField",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "dateField",
                                           Boolean.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_DATE ) )
                .addField( new ModelField( "booleanField",
                                           Boolean.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_BOOLEAN ) )
                .end()
                .build();

        model = new GuidedDecisionTable52();

        //Emulate server-to-client conversions
        oracle = new AsyncPackageDataModelOracleImpl();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields( loader.getProjectModelFields() );
        populateDataModelOracle( mock( Path.class ),
                                 model,
                                 oracle,
                                 dataModel );

        at1 = new AttributeCol52();
        at1.setAttribute( "salience" );
        at2 = new AttributeCol52();
        at2.setAttribute( "enabled" );

        model.getAttributeCols().add( at1 );
        model.getAttributeCols().add( at2 );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "MyClass" );

        c1 = new ConditionCol52();
        c1.setFactField( "stringField" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c1 );
        model.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "MyClass" );

        c2 = new ConditionCol52();
        c2.setFactField( "bigDecimalField" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c2 );
        model.getConditions().add( p2 );

        c3 = new ConditionCol52();
        c3.setFactField( "bigIntegerField" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c3 );

        c4 = new ConditionCol52();
        c4.setFactField( "byteField" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c4 );

        c5 = new ConditionCol52();
        c5.setFactField( "doubleField" );
        c5.setOperator( "==" );
        c5.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c5 );

        c6 = new ConditionCol52();
        c6.setFactField( "floatField" );
        c6.setOperator( "==" );
        c6.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c6 );

        c7 = new ConditionCol52();
        c7.setFactField( "integerField" );
        c7.setOperator( "==" );
        c7.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c7 );

        c8 = new ConditionCol52();
        c8.setFactField( "longField" );
        c8.setOperator( "==" );
        c8.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c8 );

        c9 = new ConditionCol52();
        c9.setFactField( "shortField" );
        c9.setOperator( "==" );
        c9.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c9 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "MyClass" );

        c10 = new ConditionCol52();
        c10.setFactField( "dateField" );
        c10.setOperator( "==" );
        c10.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getChildColumns().add( c10 );
        model.getConditions().add( p3 );

        Pattern52 p4 = new Pattern52();
        p4.setBoundName( "c4" );
        p4.setFactType( "MyClass" );

        c11 = new ConditionCol52();
        c11.setFactField( "booleanField" );
        c11.setOperator( "==" );
        c11.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getChildColumns().add( c11 );
        model.getConditions().add( p4 );

        a1 = new ActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "stringField" );
        model.getActionCols().add( a1 );

        a2 = new ActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "MyClass" );
        a2.setFactField( "stringField" );
        model.getActionCols().add( a2 );

        factory = new DecisionTableCellValueFactory( model,
                                                     oracle );

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT,
                         "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        DTCellValueUtilities.injectDateConvertor( JVMDateConverter.getInstance() );

    }

    @Test
    public void testDataTypes() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

        DTCellValue52 dcv1 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv2 = new DTCellValue52( dob );
        DTCellValue52 dcv3 = new DTCellValue52( new BigDecimal( 1 ) );
        DTCellValue52 dcv4 = new DTCellValue52( new BigInteger( "1" ) );
        DTCellValue52 dcv5 = new DTCellValue52( new Byte( "1" ) );
        DTCellValue52 dcv6 = new DTCellValue52( 1.0d );
        DTCellValue52 dcv7 = new DTCellValue52( 1.0f );
        DTCellValue52 dcv8 = new DTCellValue52( new Integer( 1 ) );
        DTCellValue52 dcv9 = new DTCellValue52( 1l );
        DTCellValue52 dcv10 = new DTCellValue52( new Short( "1" ) );
        DTCellValue52 dcv11 = new DTCellValue52( "Smurf" );

        assertEquals( dcv1.getDataType(),
                      DataType.DataTypes.BOOLEAN );
        assertEquals( dcv2.getDataType(),
                      DataType.DataTypes.DATE );
        assertEquals( dcv3.getDataType(),
                      DataType.DataTypes.NUMERIC_BIGDECIMAL );
        assertEquals( dcv4.getDataType(),
                      DataType.DataTypes.NUMERIC_BIGINTEGER );
        assertEquals( dcv5.getDataType(),
                      DataType.DataTypes.NUMERIC_BYTE );
        assertEquals( dcv6.getDataType(),
                      DataType.DataTypes.NUMERIC_DOUBLE );
        assertEquals( dcv7.getDataType(),
                      DataType.DataTypes.NUMERIC_FLOAT );
        assertEquals( dcv8.getDataType(),
                      DataType.DataTypes.NUMERIC_INTEGER );
        assertEquals( dcv9.getDataType(),
                      DataType.DataTypes.NUMERIC_LONG );
        assertEquals( dcv10.getDataType(),
                      DataType.DataTypes.NUMERIC_SHORT );
        assertEquals( dcv11.getDataType(),
                      DataType.DataTypes.STRING );
    }

    @Test
    public void testEmptyCells() {

        CellValue<? extends Comparable<?>> cell1 = factory.convertModelCellValue( at1,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell2 = factory.convertModelCellValue( at2,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell3 = factory.convertModelCellValue( c1,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell4 = factory.convertModelCellValue( c2,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell5 = factory.convertModelCellValue( c3,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell6 = factory.convertModelCellValue( c4,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell7 = factory.convertModelCellValue( c5,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell8 = factory.convertModelCellValue( c6,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell9 = factory.convertModelCellValue( c7,
                                                                                  new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell10 = factory.convertModelCellValue( c8,
                                                                                   new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell11 = factory.convertModelCellValue( c9,
                                                                                   new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell12 = factory.convertModelCellValue( c10,
                                                                                   new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell13 = factory.convertModelCellValue( c11,
                                                                                   new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell14 = factory.convertModelCellValue( a1,
                                                                                   new DTCellValue52() );
        CellValue<? extends Comparable<?>> cell15 = factory.convertModelCellValue( a2,
                                                                                   new DTCellValue52() );

        assertEquals( cell1.getValue(),
                      null );
        assertEquals( cell2.getValue(),
                      Boolean.FALSE );
        assertEquals( cell3.getValue(),
                      null );
        assertEquals( cell4.getValue(),
                      null );
        assertEquals( cell5.getValue(),
                      null );
        assertEquals( cell6.getValue(),
                      null );
        assertEquals( cell7.getValue(),
                      null );
        assertEquals( cell8.getValue(),
                      null );
        assertEquals( cell9.getValue(),
                      null );
        assertEquals( cell10.getValue(),
                      null );
        assertEquals( cell11.getValue(),
                      null );
        assertEquals( cell12.getValue(),
                      null );
        assertEquals( cell13.getValue(),
                      Boolean.FALSE );
        assertEquals( cell14.getValue(),
                      null );
        assertEquals( cell15.getValue(),
                      null );
    }

    @Test
    public void testTypedValues() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

        DTCellValue52 dcv1 = new DTCellValue52( new Integer( 1 ) );
        DTCellValue52 dcv2 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv3 = new DTCellValue52( "Michael" );
        DTCellValue52 dcv4 = new DTCellValue52( new BigDecimal( 11 ) );
        DTCellValue52 dcv5 = new DTCellValue52( new BigInteger( "11" ) );
        DTCellValue52 dcv6 = new DTCellValue52( new Byte( "11" ) );
        DTCellValue52 dcv7 = new DTCellValue52( 11.0d );
        DTCellValue52 dcv8 = new DTCellValue52( 11.0f );
        DTCellValue52 dcv9 = new DTCellValue52( new Integer( 11 ) );
        DTCellValue52 dcv10 = new DTCellValue52( 11l );
        DTCellValue52 dcv11 = new DTCellValue52( new Short( "11" ) );
        DTCellValue52 dcv12 = new DTCellValue52( dob );
        DTCellValue52 dcv13 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv14 = new DTCellValue52( "Mike" );
        DTCellValue52 dcv15 = new DTCellValue52( "Mike" );

        CellValue<? extends Comparable<?>> cell1 = factory.convertModelCellValue( at1,
                                                                                  dcv1 );
        CellValue<? extends Comparable<?>> cell2 = factory.convertModelCellValue( at2,
                                                                                  dcv2 );
        CellValue<? extends Comparable<?>> cell3 = factory.convertModelCellValue( c1,
                                                                                  dcv3 );
        CellValue<? extends Comparable<?>> cell4 = factory.convertModelCellValue( c2,
                                                                                  dcv4 );
        CellValue<? extends Comparable<?>> cell5 = factory.convertModelCellValue( c3,
                                                                                  dcv5 );
        CellValue<? extends Comparable<?>> cell6 = factory.convertModelCellValue( c4,
                                                                                  dcv6 );
        CellValue<? extends Comparable<?>> cell7 = factory.convertModelCellValue( c5,
                                                                                  dcv7 );
        CellValue<? extends Comparable<?>> cell8 = factory.convertModelCellValue( c6,
                                                                                  dcv8 );
        CellValue<? extends Comparable<?>> cell9 = factory.convertModelCellValue( c7,
                                                                                  dcv9 );
        CellValue<? extends Comparable<?>> cell10 = factory.convertModelCellValue( c8,
                                                                                   dcv10 );
        CellValue<? extends Comparable<?>> cell11 = factory.convertModelCellValue( c9,
                                                                                   dcv11 );
        CellValue<? extends Comparable<?>> cell12 = factory.convertModelCellValue( c10,
                                                                                   dcv12 );
        CellValue<? extends Comparable<?>> cell13 = factory.convertModelCellValue( c11,
                                                                                   dcv13 );
        CellValue<? extends Comparable<?>> cell14 = factory.convertModelCellValue( a1,
                                                                                   dcv14 );
        CellValue<? extends Comparable<?>> cell15 = factory.convertModelCellValue( a2,
                                                                                   dcv15 );

        assertEquals( cell1.getValue(),
                      new Integer( 1 ) );
        assertEquals( cell2.getValue(),
                      Boolean.TRUE );
        assertEquals( cell3.getValue(),
                      "Michael" );
        assertEquals( cell4.getValue(),
                      new BigDecimal( 11 ) );
        assertEquals( cell5.getValue(),
                      new BigInteger( "11" ) );
        assertEquals( cell6.getValue(),
                      new Byte( "11" ) );
        assertEquals( cell7.getValue(),
                      11.0d );
        assertEquals( cell8.getValue(),
                      11.0f );
        assertEquals( cell9.getValue(),
                      new Integer( 11 ) );
        assertEquals( cell10.getValue(),
                      11l );
        assertEquals( cell11.getValue(),
                      new Short( "11" ) );
        assertEquals( cell12.getValue(),
                      dob );
        assertEquals( cell13.getValue(),
                      Boolean.TRUE );
        assertEquals( cell14.getValue(),
                      "Mike" );
        assertEquals( cell15.getValue(),
                      "Mike" );
    }

    @Test
    public void testStringValues() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

        DTCellValue52 dcv1 = new DTCellValue52( "1" );
        DTCellValue52 dcv2 = new DTCellValue52( "true" );
        DTCellValue52 dcv3 = new DTCellValue52( "Michael" );
        DTCellValue52 dcv4 = new DTCellValue52( "11" );
        DTCellValue52 dcv5 = new DTCellValue52( "11" );
        DTCellValue52 dcv6 = new DTCellValue52( "11" );
        DTCellValue52 dcv7 = new DTCellValue52( "11" );
        DTCellValue52 dcv8 = new DTCellValue52( "11" );
        DTCellValue52 dcv9 = new DTCellValue52( "11" );
        DTCellValue52 dcv10 = new DTCellValue52( "11" );
        DTCellValue52 dcv11 = new DTCellValue52( "11" );
        DTCellValue52 dcv12 = new DTCellValue52( "01-JAN-2000" );
        DTCellValue52 dcv13 = new DTCellValue52( "true" );
        DTCellValue52 dcv14 = new DTCellValue52( "Mike" );
        DTCellValue52 dcv15 = new DTCellValue52( "Mike" );

        CellValue<? extends Comparable<?>> cell1 = factory.convertModelCellValue( at1,
                                                                                  dcv1 );
        CellValue<? extends Comparable<?>> cell2 = factory.convertModelCellValue( at2,
                                                                                  dcv2 );
        CellValue<? extends Comparable<?>> cell3 = factory.convertModelCellValue( c1,
                                                                                  dcv3 );
        CellValue<? extends Comparable<?>> cell4 = factory.convertModelCellValue( c2,
                                                                                  dcv4 );
        CellValue<? extends Comparable<?>> cell5 = factory.convertModelCellValue( c3,
                                                                                  dcv5 );
        CellValue<? extends Comparable<?>> cell6 = factory.convertModelCellValue( c4,
                                                                                  dcv6 );
        CellValue<? extends Comparable<?>> cell7 = factory.convertModelCellValue( c5,
                                                                                  dcv7 );
        CellValue<? extends Comparable<?>> cell8 = factory.convertModelCellValue( c6,
                                                                                  dcv8 );
        CellValue<? extends Comparable<?>> cell9 = factory.convertModelCellValue( c7,
                                                                                  dcv9 );
        CellValue<? extends Comparable<?>> cell10 = factory.convertModelCellValue( c8,
                                                                                   dcv10 );
        CellValue<? extends Comparable<?>> cell11 = factory.convertModelCellValue( c9,
                                                                                   dcv11 );
        CellValue<? extends Comparable<?>> cell12 = factory.convertModelCellValue( c10,
                                                                                   dcv12 );
        CellValue<? extends Comparable<?>> cell13 = factory.convertModelCellValue( c11,
                                                                                   dcv13 );
        CellValue<? extends Comparable<?>> cell14 = factory.convertModelCellValue( a1,
                                                                                   dcv14 );
        CellValue<? extends Comparable<?>> cell15 = factory.convertModelCellValue( a2,
                                                                                   dcv15 );

        assertEquals( cell1.getValue(),
                      new Integer( 1 ) );
        assertEquals( cell2.getValue(),
                      Boolean.TRUE );
        assertEquals( cell3.getValue(),
                      "Michael" );
        assertEquals( cell4.getValue(),
                      new BigDecimal( 11 ) );
        assertEquals( cell5.getValue(),
                      new BigInteger( "11" ) );
        assertEquals( cell6.getValue(),
                      new Byte( "11" ) );
        assertEquals( cell7.getValue(),
                      11.0d );
        assertEquals( cell8.getValue(),
                      11.0f );
        assertEquals( cell9.getValue(),
                      new Integer( 11 ) );
        assertEquals( cell10.getValue(),
                      11l );
        assertEquals( cell11.getValue(),
                      new Short( "11" ) );
        assertEquals( cell12.getValue(),
                      dob );
        assertEquals( cell13.getValue(),
                      Boolean.TRUE );
        assertEquals( cell14.getValue(),
                      "Mike" );
        assertEquals( cell15.getValue(),
                      "Mike" );
    }

    @Test
    public void testConversionEmptyValues() {

        DTCellValue52 dcv1 = new DTCellValue52( "" );
        DTCellValue52 dcv2 = new DTCellValue52( "" );
        DTCellValue52 dcv3 = new DTCellValue52( "" );
        DTCellValue52 dcv4 = new DTCellValue52( "" );
        DTCellValue52 dcv5 = new DTCellValue52( "" );
        DTCellValue52 dcv6 = new DTCellValue52( "" );
        DTCellValue52 dcv7 = new DTCellValue52( "" );
        DTCellValue52 dcv8 = new DTCellValue52( "" );
        DTCellValue52 dcv9 = new DTCellValue52( "" );
        DTCellValue52 dcv10 = new DTCellValue52( "" );
        DTCellValue52 dcv11 = new DTCellValue52( "" );
        DTCellValue52 dcv12 = new DTCellValue52( "" );
        DTCellValue52 dcv13 = new DTCellValue52( "" );
        DTCellValue52 dcv14 = new DTCellValue52( "" );
        DTCellValue52 dcv15 = new DTCellValue52( "" );

        CellValue<? extends Comparable<?>> cell1 = factory.convertModelCellValue( at1,
                                                                                  dcv1 );
        CellValue<? extends Comparable<?>> cell2 = factory.convertModelCellValue( at2,
                                                                                  dcv2 );
        CellValue<? extends Comparable<?>> cell3 = factory.convertModelCellValue( c1,
                                                                                  dcv3 );
        CellValue<? extends Comparable<?>> cell4 = factory.convertModelCellValue( c2,
                                                                                  dcv4 );
        CellValue<? extends Comparable<?>> cell5 = factory.convertModelCellValue( c3,
                                                                                  dcv5 );
        CellValue<? extends Comparable<?>> cell6 = factory.convertModelCellValue( c4,
                                                                                  dcv6 );
        CellValue<? extends Comparable<?>> cell7 = factory.convertModelCellValue( c5,
                                                                                  dcv7 );
        CellValue<? extends Comparable<?>> cell8 = factory.convertModelCellValue( c6,
                                                                                  dcv8 );
        CellValue<? extends Comparable<?>> cell9 = factory.convertModelCellValue( c7,
                                                                                  dcv9 );
        CellValue<? extends Comparable<?>> cell10 = factory.convertModelCellValue( c8,
                                                                                   dcv10 );
        CellValue<? extends Comparable<?>> cell11 = factory.convertModelCellValue( c9,
                                                                                   dcv11 );
        CellValue<? extends Comparable<?>> cell12 = factory.convertModelCellValue( c10,
                                                                                   dcv12 );
        CellValue<? extends Comparable<?>> cell13 = factory.convertModelCellValue( c11,
                                                                                   dcv13 );
        CellValue<? extends Comparable<?>> cell14 = factory.convertModelCellValue( a1,
                                                                                   dcv14 );
        CellValue<? extends Comparable<?>> cell15 = factory.convertModelCellValue( a2,
                                                                                   dcv15 );

        assertEquals( cell1.getValue(),
                      null );
        assertEquals( cell2.getValue(),
                      Boolean.FALSE );
        assertEquals( cell3.getValue(),
                      null );
        assertEquals( cell4.getValue(),
                      null );
        assertEquals( cell5.getValue(),
                      null );
        assertEquals( cell6.getValue(),
                      null );
        assertEquals( cell7.getValue(),
                      null );
        assertEquals( cell8.getValue(),
                      null );
        assertEquals( cell9.getValue(),
                      null );
        assertEquals( cell10.getValue(),
                      null );
        assertEquals( cell11.getValue(),
                      null );
        assertEquals( cell12.getValue(),
                      null );
        assertEquals( cell13.getValue(),
                      Boolean.FALSE );
        assertEquals( cell14.getValue(),
                      null );
        assertEquals( cell15.getValue(),
                      null );

        assertEquals( dcv1.getDataType(),
                      DataType.DataTypes.NUMERIC_INTEGER );
        assertEquals( dcv2.getDataType(),
                      DataType.DataTypes.BOOLEAN );
        assertEquals( dcv3.getDataType(),
                      DataType.DataTypes.STRING );
        assertEquals( dcv4.getDataType(),
                      DataType.DataTypes.NUMERIC_BIGDECIMAL );
        assertEquals( dcv5.getDataType(),
                      DataType.DataTypes.NUMERIC_BIGINTEGER );
        assertEquals( dcv6.getDataType(),
                      DataType.DataTypes.NUMERIC_BYTE );
        assertEquals( dcv7.getDataType(),
                      DataType.DataTypes.NUMERIC_DOUBLE );
        assertEquals( dcv8.getDataType(),
                      DataType.DataTypes.NUMERIC_FLOAT );
        assertEquals( dcv9.getDataType(),
                      DataType.DataTypes.NUMERIC_INTEGER );
        assertEquals( dcv10.getDataType(),
                      DataType.DataTypes.NUMERIC_LONG );
        assertEquals( dcv11.getDataType(),
                      DataType.DataTypes.NUMERIC_SHORT );
        assertEquals( dcv12.getDataType(),
                      DataType.DataTypes.DATE );
        assertEquals( dcv13.getDataType(),
                      DataType.DataTypes.BOOLEAN );
        assertEquals( dcv14.getDataType(),
                      DataType.DataTypes.STRING );
        assertEquals( dcv15.getDataType(),
                      DataType.DataTypes.STRING );
    }

    private void populateDataModelOracle( final Path resourcePath,
                                          final HasImports hasImports,
                                          final AsyncPackageDataModelOracle oracle,
                                          final PackageDataModelOracleBaselinePayload payload ) {
        populate( oracle,
                  payload );
        oracle.init( resourcePath );
        oracle.filter( hasImports.getImports() );
    }

    private static void populate( final AsyncPackageDataModelOracle oracle,
                                  final PackageDataModelOracleBaselinePayload payload ) {
        oracle.setProjectName( payload.getProjectName() );
        oracle.addModelFields( payload.getModelFields() );
        oracle.addFieldParametersType( payload.getFieldParametersType() );
        oracle.addEventTypes( payload.getEventTypes() );
        oracle.addTypeSources( payload.getTypeSources() );
        oracle.addSuperTypes( payload.getSuperTypes() );
        oracle.addTypeAnnotations( payload.getTypeAnnotations() );
        oracle.addTypeFieldsAnnotations( payload.getTypeFieldsAnnotations() );
        oracle.addJavaEnumDefinitions( payload.getJavaEnumDefinitions() );
        oracle.addMethodInformation( payload.getMethodInformation() );
        oracle.addCollectionTypes( payload.getCollectionTypes() );
        oracle.addPackageNames( payload.getPackageNames() );

        oracle.setPackageName( payload.getPackageName() );
        oracle.addWorkbenchEnumDefinitions( payload.getWorkbenchEnumDefinitions() );
        oracle.addDslConditionSentences( payload.getDslConditionSentences() );
        oracle.addDslActionSentences( payload.getDslActionSentences() );
        oracle.addGlobals( payload.getGlobals() );
    }

}

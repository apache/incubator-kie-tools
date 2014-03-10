package org.drools.workbench.screens.guided.rule.client.editor.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConstraintValueEditorHelperTest {

    private RuleModel model;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
    }

    @Test
    public void testSimplePattern() throws Exception {
        AsyncPackageDataModelOracle oracle = mock( AsyncPackageDataModelOracle.class );

        FactPattern pattern = new FactPattern();
        pattern.setBoundName( "pp" );
        pattern.setFactType( "House" );
        model.addLhsItem( pattern );

        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType( "House" );
        constraint.setFieldName( "this" );
        constraint.setFieldName( "org.mortgages.House" );
        pattern2.addConstraint( constraint );
        model.addLhsItem( pattern );

        when(
                oracle.getFieldClassName( "House", "this" )
            ).thenReturn(
                "org.mortgages.House"
                        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper( model,
                                                                              oracle,
                                                                              "House",
                                                                              "this",
                                                                              constraint,
                                                                              "House",
                                                                              new DropDownData() );

        helper.isApplicableBindingsInScope( "pp", new Callback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );

    }

    @Test
    public void testSimpleField() throws Exception {
        AsyncPackageDataModelOracle oracle = mock( AsyncPackageDataModelOracle.class );

        FactPattern pattern = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFieldBinding( "pp" );
        constraint.setFactType( "House" );
        constraint.setFieldName( "parent" );
        constraint.setFieldType( "org.mortgages.Parent" );
        pattern.addConstraint( constraint );
        model.addLhsItem( pattern );

        when(
                oracle.getFieldClassName( "House", "parent" )
            ).thenReturn(
                "org.mortgages.Parent"
                        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper( model,
                                                                              oracle,
                                                                              "House",
                                                                              "parent",
                                                                              constraint,
                                                                              "Parent",
                                                                              new DropDownData() );

        helper.isApplicableBindingsInScope( "pp", new Callback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                assertTrue( result );
            }
        } );

    }

    @Test
    public void testEvents_BothTypesAreEvents() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        oracle.setPackageName( "org.test" );

        oracle.addModelFields( new HashMap<String, ModelField[]>() {{
            put( "org.test.Event1", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event1",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event1" ) } );
            put( "org.test.Event2", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event2",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event2" ) } );
        }} );

        oracle.addEventTypes( new HashMap<String, Boolean>() {{
            put( "org.test.Event1", true );
            put( "org.test.Event2", true );
        }} );
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType( "Event1" );
        pattern1.setBoundName( "$e" );

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType( "Event1" );
        constraint.setFieldName( "this" );
        constraint.setFieldType( "Event1" );
        constraint.setOperator( OperatorsOracle.SIMPLE_CEP_OPERATORS[ 0 ] );
        pattern1.addConstraint( constraint );

        model.addLhsItem( pattern1 );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper( model,
                                                                              oracle,
                                                                              "Event2",
                                                                              "this",
                                                                              constraint,
                                                                              "Event2",
                                                                              new DropDownData() );

        helper.isApplicableBindingsInScope( "$e",
                                            new Callback<Boolean>() {
                                                @Override
                                                public void callback( Boolean result ) {
                                                    assertTrue( result );
                                                }
                                            } );
    }

    @Test
    public void testEvents_BoundTypeIsEvent() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        oracle.setPackageName( "org.test" );

        oracle.addModelFields( new HashMap<String, ModelField[]>() {{
            put( "org.test.Event1", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event1",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event1" ) } );
            put( "org.test.Event2", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event2",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event2" ) } );
        }} );

        oracle.addEventTypes( new HashMap<String, Boolean>() {{
            put( "org.test.Event1", true );
            put( "org.test.Event2", false );
        }} );
        oracle.addSuperTypes( new HashMap<String, List<String>>() {{
            put( "org.test.Event1", Collections.EMPTY_LIST );
            put( "org.test.Event2", Collections.EMPTY_LIST );
        }} );
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType( "Event1" );
        pattern1.setBoundName( "$e" );

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType( "Event1" );
        constraint.setFieldName( "this" );
        constraint.setFieldType( "Event1" );
        constraint.setOperator( OperatorsOracle.SIMPLE_CEP_OPERATORS[ 0 ] );
        pattern1.addConstraint( constraint );

        model.addLhsItem( pattern1 );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper( model,
                                                                              oracle,
                                                                              "Event2",
                                                                              "this",
                                                                              constraint,
                                                                              "Event2",
                                                                              new DropDownData() );

        helper.isApplicableBindingsInScope( "$e",
                                            new Callback<Boolean>() {
                                                @Override
                                                public void callback( Boolean result ) {
                                                    assertFalse( result );
                                                }
                                            } );
    }

    @Test
    public void testEvents_BoundTypeIsNotEvent() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        oracle.setPackageName( "org.test" );

        oracle.addModelFields( new HashMap<String, ModelField[]>() {{
            put( "org.test.Event1", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event1",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event1" ) } );
            put( "org.test.Event2", new ModelField[]{ new ModelField( "this",
                                                                      "org.test.Event2",
                                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                      ModelField.FIELD_ORIGIN.SELF,
                                                                      FieldAccessorsAndMutators.ACCESSOR,
                                                                      "org.test.Event2" ) } );
        }} );

        oracle.addEventTypes( new HashMap<String, Boolean>() {{
            put( "org.test.Event1", false );
            put( "org.test.Event2", true );
        }} );
        oracle.addSuperTypes( new HashMap<String, List<String>>() {{
            put( "org.test.Event1", Collections.EMPTY_LIST );
            put( "org.test.Event2", Collections.EMPTY_LIST );
        }} );
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType( "Event1" );
        pattern1.setBoundName( "$e" );

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType( "Event1" );
        constraint.setFieldName( "this" );
        constraint.setFieldType( "Event1" );
        constraint.setOperator( OperatorsOracle.SIMPLE_CEP_OPERATORS[ 0 ] );
        pattern1.addConstraint( constraint );

        model.addLhsItem( pattern1 );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper( model,
                                                                              oracle,
                                                                              "Event2",
                                                                              "this",
                                                                              constraint,
                                                                              "Event2",
                                                                              new DropDownData() );

        helper.isApplicableBindingsInScope( "$e",
                                            new Callback<Boolean>() {
                                                @Override
                                                public void callback( Boolean result ) {
                                                    assertFalse( result );
                                                }
                                            } );
    }

}

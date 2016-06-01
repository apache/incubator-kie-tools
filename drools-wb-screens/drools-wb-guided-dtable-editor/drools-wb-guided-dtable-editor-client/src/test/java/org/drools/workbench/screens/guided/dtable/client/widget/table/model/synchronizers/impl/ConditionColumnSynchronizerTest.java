/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ConditionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.*;
import static org.junit.Assert.*;

public class ConditionColumnSynchronizerTest extends BaseSynchronizerTest {

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        oracle.addModelFields( new HashMap<String, ModelField[]>() {
                                   {
                                       put( "Applicant",
                                            new ModelField[]{
                                                    new ModelField( "this",
                                                                    "Applicant",
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    "Applicant" ),
                                                    new ModelField( "age",
                                                                    Integer.class.getName(),
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    DataType.TYPE_NUMERIC_INTEGER ),
                                                    new ModelField( "name",
                                                                    String.class.getName(),
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    DataType.TYPE_STRING ) } );
                                       put( "Address",
                                            new ModelField[]{
                                                    new ModelField( "this",
                                                                    "Address",
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    "Address" ),
                                                    new ModelField( "state",
                                                                    String.class.getName(),
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    DataType.TYPE_STRING ),
                                                    new ModelField( "country",
                                                                    String.class.getName(),
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    DataType.TYPE_STRING ) } );
                                   }
                               }

                             );
        return oracle;
    }

    @Override
    protected List<BaseColumnConverter> getConverters() {
        final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();
        converters.add( new ConditionColumnConverter() );
        return converters;
    }

    @Override
    protected List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers() {
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();
        synchronizers.add( new ConditionColumnSynchronizer() );
        synchronizers.add( new RowSynchronizer() );
        return synchronizers;
    }

    @Test
    public void testAppend1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition.setHeader( "col1" );
        condition.setFactField( "age" );
        condition.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
    }

    @Test
    public void testAppend2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition1 );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col1" );
        condition2.setFactField( "name" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition2 );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 2,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );
    }

    @Test
    public void testAppend3() throws ModelSynchronizer.MoveColumnVetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition1 );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col2" );
        condition2.setFactField( "country" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern2,
                                        condition2 );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$d" ).getChildColumns().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );
    }

    @Test
    public void testUpdate1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition.setHeader( "col1" );
        condition.setFactField( "age" );
        condition.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition );

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setBoundName( "$a" );
        editedPattern.setFactType( "Applicant" );

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        editedCondition.setHeader( "col1" );
        editedCondition.setFactField( "name" );
        editedCondition.setOperator( "==" );

        modelSynchronizer.updateColumn( pattern,
                                        condition,
                                        editedPattern,
                                        editedCondition );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
    }

    @Test
    public void testUpdate2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition1 );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col2" );
        condition2.setFactField( "name" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition2 );

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setBoundName( "$a2" );
        editedPattern.setFactType( "Applicant" );

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        editedCondition.setHeader( "col1" );
        editedCondition.setFactField( "age" );
        editedCondition.setOperator( "==" );

        modelSynchronizer.updateColumn( pattern,
                                        condition1,
                                        editedPattern,
                                        editedCondition );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a2" ).getChildColumns().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof IntegerUiColumn );
    }

    @Test
    public void testUpdate3() throws ModelSynchronizer.MoveColumnVetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition1 );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col2" );
        condition2.setFactField( "name" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition2 );

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setBoundName( "$d" );
        editedPattern.setFactType( "Address" );

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        editedCondition.setHeader( "col1" );
        editedCondition.setFactField( "country" );
        editedCondition.setOperator( "==" );

        modelSynchronizer.updateColumn( pattern1,
                                        condition1,
                                        editedPattern,
                                        editedCondition );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$d" ).getChildColumns().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );
    }

    @Test
    public void testUpdate4() throws ModelSynchronizer.MoveColumnVetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition1 );

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setBoundName( "$d" );
        editedPattern.setFactType( "Address" );

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        editedCondition.setHeader( "col1" );
        editedCondition.setFactField( "country" );
        editedCondition.setOperator( "==" );

        modelSynchronizer.updateColumn( pattern1,
                                        condition1,
                                        editedPattern,
                                        editedCondition );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertNull( model.getConditionPattern( "$a" ) );
        assertEquals( 1,
                      model.getConditionPattern( "$d" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
    }

    @Test
    public void testUpdate5() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );
        condition1.setHeader( "age" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition1 );

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setBoundName( "$a" );
        editedPattern.setFactType( "Applicant" );

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        editedCondition.setFactField( "age" );
        editedCondition.setOperator( "==" );
        editedCondition.setHideColumn( true );
        editedCondition.setHeader( "updated" );

        modelSynchronizer.updateColumn( pattern1,
                                        condition1,
                                        editedPattern,
                                        editedCondition );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertEquals( false,
                      uiModel.getColumns().get( 2 ).isVisible() );
        assertEquals( "updated",
                      uiModel.getColumns().get( 2 ).getHeaderMetaData().get( 0 ).getTitle() );
    }

    @Test
    public void testDelete1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition.setHeader( "col1" );
        condition.setFactField( "age" );
        condition.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( condition );

        assertEquals( 2,
                      model.getExpandedColumns().size() );
        assertEquals( 0,
                      model.getConditions().size() );
        assertNull( model.getConditionPattern( "$a" ) );

        assertEquals( 2,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testDelete2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition1 );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col2" );
        condition2.setFactField( "name" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern,
                                        condition2 );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( condition1 );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testDelete3() throws ModelSynchronizer.MoveColumnVetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 condition1 = new ConditionCol52();
        condition1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition1.setHeader( "col1" );
        condition1.setFactField( "age" );
        condition1.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern1,
                                        condition1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        condition2.setHeader( "col2" );
        condition2.setFactField( "country" );
        condition2.setOperator( "==" );

        modelSynchronizer.appendColumn( pattern2,
                                        condition2 );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$a" ).getChildColumns().size() );
        assertEquals( 1,
                      model.getConditionPattern( "$d" ).getChildColumns().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( condition1 );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );
        assertNull( model.getConditionPattern( "$a" ) );
        assertEquals( 1,
                      model.getConditionPattern( "$d" ).getChildColumns().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 column1 = new ConditionCol52();
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1.setFactField( "age" );
        column1.setOperator( "==" );
        column1.setHeader( "age" );

        final ConditionCol52 column2 = new ConditionCol52();
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2.setFactField( "name" );
        column2.setOperator( "==" );
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern,
                                        column1 );
        modelSynchronizer.appendColumn( pattern,
                                        column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1,
                      conditionColumns1_1.get( 0 ) );
        assertEquals( column2,
                      conditionColumns1_1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 2,
                              uiModelColumn2_1 );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column2,
                      conditionColumns1_2.get( 0 ) );
        assertEquals( column1,
                      conditionColumns1_2.get( 1 ) );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 3 ).getNumericValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof IntegerUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn2_2.getIndex() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 column1 = new ConditionCol52();
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1.setFactField( "age" );
        column1.setOperator( "==" );
        column1.setHeader( "age" );

        final ConditionCol52 column2 = new ConditionCol52();
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2.setFactField( "name" );
        column2.setOperator( "==" );
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern,
                                        column1 );
        modelSynchronizer.appendColumn( pattern,
                                        column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1,
                      conditionColumns1_1.get( 0 ) );
        assertEquals( column2,
                      conditionColumns1_1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 3,
                              uiModelColumn1_1 );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column2,
                      conditionColumns1_2.get( 0 ) );
        assertEquals( column1,
                      conditionColumns1_2.get( 1 ) );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 3 ).getNumericValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof IntegerUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn2_2.getIndex() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_OutOfBounds_OutOfConditions() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "Applicant" );

        final ConditionCol52 column1 = new ConditionCol52();
        column1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1.setFactField( "age" );
        column1.setOperator( "==" );
        column1.setHeader( "age" );

        final ConditionCol52 column2 = new ConditionCol52();
        column2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2.setFactField( "name" );
        column2.setOperator( "==" );
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern,
                                        column1 );
        modelSynchronizer.appendColumn( pattern,
                                        column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1,
                      conditionColumns1_1.get( 0 ) );
        assertEquals( column2,
                      conditionColumns1_1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 0,
                              uiModelColumn1_1 );

        assertEquals( 1,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1,
                      conditionColumns1_2.get( 0 ) );
        assertEquals( column2,
                      conditionColumns1_2.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_OutOfBounds_OutOfPattern() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );

        final ConditionCol52 column2p1 = new ConditionCol52();
        column2p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p1.setFactField( "name" );
        column2p1.setOperator( "==" );
        column2p1.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern1,
                                        column2p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$a2" );
        pattern2.setFactType( "Applicant" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "name" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Fred" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "Smurf" ) );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_1.get( 1 ) );
        final List<ConditionCol52> conditionColumns2_1 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns2_1.get( 0 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Fred",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a2 : Applicant",
                      uiModelColumn3_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Fred",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 4,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_2.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_2.get( 1 ) );
        final List<ConditionCol52> conditionColumns2_2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns2_2.get( 0 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Fred",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a2 : Applicant",
                      uiModelColumn3_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "Fred",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_SingleColumnPattern() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$a2" );
        pattern2.setFactType( "Applicant" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "name" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1.get( 0 ) );
        final List<ConditionCol52> conditionColumns2_1 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns2_1.get( 0 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a2 : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 3,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_2.get( 0 ) );
        final List<ConditionCol52> conditionColumns2_2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns2_2.get( 0 ) );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 3 ).getNumericValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "$a2 : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof IntegerUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn2_2.getIndex() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnsTo_MoveLeft() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        final ConditionCol52 column2p1 = new ConditionCol52();
        column2p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p1.setFactField( "name" );
        column2p1.setOperator( "==" );
        column2p1.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );
        modelSynchronizer.appendColumn( pattern1,
                                        column2p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "state" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "state" );

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p2.setFactField( "country" );
        column2p2.setOperator( "==" );
        column2p2.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );
        modelSynchronizer.appendColumn( pattern2,
                                        column2p2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "NY" ) );
        uiModel.setCell( 0,
                         5,
                         new BaseGridCellValue<String>( "America" ) );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_1p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_1p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_1p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );

        assertEquals( 6,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get( 5 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn3_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn4_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn4_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 5,
                      uiModelColumn4_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnsTo( 2,
                               new ArrayList<GridColumn<?>>() {{
                                   add( uiModelColumn3_1 );
                                   add( uiModelColumn4_1 );
                               }} );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_2p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_2p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_2p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_2p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 4 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );

        assertEquals( 6,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get( 5 );
        assertEquals( "$d : Address",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn3_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn4_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn4_2 instanceof StringUiColumn );
        assertEquals( 4,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 5,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn4_2.getIndex() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnsTo_MoveLeft_MidPoint() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        final ConditionCol52 column2p1 = new ConditionCol52();
        column2p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p1.setFactField( "name" );
        column2p1.setOperator( "==" );
        column2p1.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );
        modelSynchronizer.appendColumn( pattern1,
                                        column2p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "state" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "state" );

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p2.setFactField( "country" );
        column2p2.setOperator( "==" );
        column2p2.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );
        modelSynchronizer.appendColumn( pattern2,
                                        column2p2 );

        final Pattern52 pattern3 = new Pattern52();
        pattern3.setBoundName( "$d2" );
        pattern3.setFactType( "Address" );

        final ConditionCol52 column1p3 = new ConditionCol52();
        column1p3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p3.setFactField( "state" );
        column1p3.setOperator( "==" );
        column1p3.setHeader( "state" );

        final ConditionCol52 column2p3 = new ConditionCol52();
        column2p3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p3.setFactField( "country" );
        column2p3.setOperator( "==" );
        column2p3.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern3,
                                        column1p3 );
        modelSynchronizer.appendColumn( pattern3,
                                        column2p3 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "NY" ) );
        uiModel.setCell( 0,
                         5,
                         new BaseGridCellValue<String>( "America" ) );
        uiModel.setCell( 0,
                         6,
                         new BaseGridCellValue<String>( "Essex" ) );
        uiModel.setCell( 0,
                         7,
                         new BaseGridCellValue<String>( "England" ) );

        assertEquals( 3,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_1p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_1p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_1p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p3 = model.getPatterns().get( 2 ).getChildColumns();
        assertEquals( column1p3,
                      conditionColumns1_1p3.get( 0 ) );
        assertEquals( column2p3,
                      conditionColumns1_1p3.get( 1 ) );
        assertEquals( "Essex",
                      model.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "England",
                      model.getData().get( 0 ).get( 7 ).getStringValue() );

        assertEquals( 8,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get( 5 );
        final GridColumn<?> uiModelColumn5_1 = uiModel.getColumns().get( 6 );
        final GridColumn<?> uiModelColumn6_1 = uiModel.getColumns().get( 7 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn3_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn4_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn5_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn6_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn4_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn5_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn6_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 5,
                      uiModelColumn4_1.getIndex() );
        assertEquals( 6,
                      uiModelColumn5_1.getIndex() );
        assertEquals( 7,
                      uiModelColumn6_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_1.getIndex() ).getValue().getValue() );
        assertEquals( "Essex",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn5_1.getIndex() ).getValue().getValue() );
        assertEquals( "England",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn6_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnsTo( 4,
                               new ArrayList<GridColumn<?>>() {{
                                   add( uiModelColumn5_1 );
                                   add( uiModelColumn6_1 );
                               }} );

        assertEquals( 3,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_2p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_2p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p3 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p3,
                      conditionColumns1_2p3.get( 0 ) );
        assertEquals( column2p3,
                      conditionColumns1_2p3.get( 1 ) );
        assertEquals( "Essex",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "England",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get( 2 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_2p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_2p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 7 ).getStringValue() );

        assertEquals( 8,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get( 5 );
        final GridColumn<?> uiModelColumn5_2 = uiModel.getColumns().get( 6 );
        final GridColumn<?> uiModelColumn6_2 = uiModel.getColumns().get( 7 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn3_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn4_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn5_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn6_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn4_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn5_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn6_2 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 6,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 7,
                      uiModelColumn4_2.getIndex() );
        assertEquals( 4,
                      uiModelColumn5_2.getIndex() );
        assertEquals( 5,
                      uiModelColumn6_2.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( "Essex",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
        assertEquals( "England",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_2.getIndex() ).getValue().getValue() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn5_2.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn6_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnsTo_MoveRight() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        final ConditionCol52 column2p1 = new ConditionCol52();
        column2p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p1.setFactField( "name" );
        column2p1.setOperator( "==" );
        column2p1.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );
        modelSynchronizer.appendColumn( pattern1,
                                        column2p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "state" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "state" );

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p2.setFactField( "country" );
        column2p2.setOperator( "==" );
        column2p2.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );
        modelSynchronizer.appendColumn( pattern2,
                                        column2p2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "NY" ) );
        uiModel.setCell( 0,
                         5,
                         new BaseGridCellValue<String>( "America" ) );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_1p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_1p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_1p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );

        assertEquals( 6,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get( 5 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn3_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn4_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn4_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 5,
                      uiModelColumn4_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_1.getIndex() ).getValue().getValue() );

        // The target column index is the right-most of the target pattern. This
        // index is provided by wires-grid's at runtime when dragging blocked columns.
        uiModel.moveColumnsTo( 5,
                               new ArrayList<GridColumn<?>>() {{
                                   add( uiModelColumn1_1 );
                                   add( uiModelColumn2_1 );
                               }} );

        assertEquals( 2,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_2p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_2p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_2p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_2p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 4 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );

        assertEquals( 6,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get( 5 );
        assertEquals( "$d : Address",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn3_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn4_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn4_2 instanceof StringUiColumn );
        assertEquals( 4,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 5,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn4_2.getIndex() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnsTo_MoveRight_MidPoint() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 pattern1 = new Pattern52();
        pattern1.setBoundName( "$a" );
        pattern1.setFactType( "Applicant" );

        final ConditionCol52 column1p1 = new ConditionCol52();
        column1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p1.setFactField( "age" );
        column1p1.setOperator( "==" );
        column1p1.setHeader( "age" );

        final ConditionCol52 column2p1 = new ConditionCol52();
        column2p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p1.setFactField( "name" );
        column2p1.setOperator( "==" );
        column2p1.setHeader( "name" );

        modelSynchronizer.appendColumn( pattern1,
                                        column1p1 );
        modelSynchronizer.appendColumn( pattern1,
                                        column2p1 );

        final Pattern52 pattern2 = new Pattern52();
        pattern2.setBoundName( "$d" );
        pattern2.setFactType( "Address" );

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p2.setFactField( "state" );
        column1p2.setOperator( "==" );
        column1p2.setHeader( "state" );

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p2.setFactField( "country" );
        column2p2.setOperator( "==" );
        column2p2.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern2,
                                        column1p2 );
        modelSynchronizer.appendColumn( pattern2,
                                        column2p2 );

        final Pattern52 pattern3 = new Pattern52();
        pattern3.setBoundName( "$d2" );
        pattern3.setFactType( "Address" );

        final ConditionCol52 column1p3 = new ConditionCol52();
        column1p3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column1p3.setFactField( "state" );
        column1p3.setOperator( "==" );
        column1p3.setHeader( "state" );

        final ConditionCol52 column2p3 = new ConditionCol52();
        column2p3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        column2p3.setFactField( "country" );
        column2p3.setOperator( "==" );
        column2p3.setHeader( "country" );

        modelSynchronizer.appendColumn( pattern3,
                                        column1p3 );
        modelSynchronizer.appendColumn( pattern3,
                                        column2p3 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 45 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "NY" ) );
        uiModel.setCell( 0,
                         5,
                         new BaseGridCellValue<String>( "America" ) );
        uiModel.setCell( 0,
                         6,
                         new BaseGridCellValue<String>( "Essex" ) );
        uiModel.setCell( 0,
                         7,
                         new BaseGridCellValue<String>( "England" ) );

        assertEquals( 3,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_1p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_1p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_1p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_1p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_1p3 = model.getPatterns().get( 2 ).getChildColumns();
        assertEquals( column1p3,
                      conditionColumns1_1p3.get( 0 ) );
        assertEquals( column2p3,
                      conditionColumns1_1p3.get( 1 ) );
        assertEquals( "Essex",
                      model.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "England",
                      model.getData().get( 0 ).get( 7 ).getStringValue() );

        assertEquals( 8,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get( 5 );
        final GridColumn<?> uiModelColumn5_1 = uiModel.getColumns().get( 6 );
        final GridColumn<?> uiModelColumn6_1 = uiModel.getColumns().get( 7 );
        assertEquals( "$a : Applicant",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn3_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn4_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn5_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn6_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn4_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn5_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn6_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 5,
                      uiModelColumn4_1.getIndex() );
        assertEquals( 6,
                      uiModelColumn5_1.getIndex() );
        assertEquals( 7,
                      uiModelColumn6_1.getIndex() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_1.getIndex() ).getValue().getValue() );
        assertEquals( "Essex",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn5_1.getIndex() ).getValue().getValue() );
        assertEquals( "England",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn6_1.getIndex() ).getValue().getValue() );

        // The target column index is the right-most of the target pattern. This
        // index is provided by wires-grid's at runtime when dragging blocked columns.
        uiModel.moveColumnsTo( 5,
                               new ArrayList<GridColumn<?>>() {{
                                   add( uiModelColumn1_1 );
                                   add( uiModelColumn2_1 );
                               }} );

        assertEquals( 3,
                      model.getPatterns().size() );
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get( 0 ).getChildColumns();
        assertEquals( column1p2,
                      conditionColumns1_2p2.get( 0 ) );
        assertEquals( column2p2,
                      conditionColumns1_2p2.get( 1 ) );
        assertEquals( "NY",
                      model.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "America",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get( 1 ).getChildColumns();
        assertEquals( column1p1,
                      conditionColumns1_2p1.get( 0 ) );
        assertEquals( column2p1,
                      conditionColumns1_2p1.get( 1 ) );
        assertEquals( 45,
                      model.getData().get( 0 ).get( 4 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 5 ).getStringValue() );
        final List<ConditionCol52> conditionColumns1_2p3 = model.getPatterns().get( 2 ).getChildColumns();
        assertEquals( column1p3,
                      conditionColumns1_2p3.get( 0 ) );
        assertEquals( column2p3,
                      conditionColumns1_2p3.get( 1 ) );
        assertEquals( "Essex",
                      model.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "England",
                      model.getData().get( 0 ).get( 7 ).getStringValue() );

        assertEquals( 8,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get( 5 );
        final GridColumn<?> uiModelColumn5_2 = uiModel.getColumns().get( 6 );
        final GridColumn<?> uiModelColumn6_2 = uiModel.getColumns().get( 7 );
        assertEquals( "$d : Address",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d : Address",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn3_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$a : Applicant",
                      uiModelColumn4_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn5_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "$d2 : Address",
                      uiModelColumn6_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn4_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn5_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn6_2 instanceof StringUiColumn );
        assertEquals( 4,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 5,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn4_2.getIndex() );
        assertEquals( 6,
                      uiModelColumn5_2.getIndex() );
        assertEquals( 7,
                      uiModelColumn6_2.getIndex() );
        assertEquals( "NY",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "America",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( 45,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn4_2.getIndex() ).getValue().getValue() );
        assertEquals( "Essex",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn5_2.getIndex() ).getValue().getValue() );
        assertEquals( "England",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn6_2.getIndex() ).getValue().getValue() );
    }

}

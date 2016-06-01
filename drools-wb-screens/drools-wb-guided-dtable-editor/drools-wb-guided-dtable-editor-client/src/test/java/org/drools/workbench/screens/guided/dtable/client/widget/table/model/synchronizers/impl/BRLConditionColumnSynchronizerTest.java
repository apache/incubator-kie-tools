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
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.LongUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.BRLConditionVariableColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.*;
import static org.junit.Assert.*;

public class BRLConditionColumnSynchronizerTest extends BaseSynchronizerTest {

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
                                                    new ModelField( "salary",
                                                                    Long.class.getName(),
                                                                    ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                    ModelField.FIELD_ORIGIN.SELF,
                                                                    FieldAccessorsAndMutators.ACCESSOR,
                                                                    DataType.TYPE_NUMERIC_LONG ),
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
        converters.add( new BRLConditionVariableColumnConverter() );
        return converters;
    }

    @Override
    protected List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers() {
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();
        synchronizers.add( new BRLConditionColumnSynchronizer() );
        synchronizers.add( new RowSynchronizer() );
        return synchronizers;
    }

    @Test
    public void testAppend1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, single variable
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        column.getChildColumns().add( columnV0 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
    }

    @Test
    public void testAppend2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn( "$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name" );
        column.getChildColumns().add( columnV0 );
        column.getChildColumns().add( columnV1 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );
    }

    @Test
    public void testUpdate1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, single variable
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        column.getChildColumns().add( columnV0 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedColumnV0 = new BRLConditionVariableColumn( "$name",
                                                                                          DataType.TYPE_STRING,
                                                                                          "Applicant",
                                                                                          "name" );
        edited.getChildColumns().add( editedColumnV0 );
        edited.setHideColumn( true );
        edited.setHeader( "updated" );

        modelSynchronizer.updateColumn( column,
                                        edited );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
        assertEquals( "updated",
                      uiModel.getColumns().get( 2 ).getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( false,
                      uiModel.getColumns().get( 2 ).isVisible() );
    }

    @Test
    public void testUpdate2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn( "$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name" );
        column.getChildColumns().add( columnV0 );
        column.getChildColumns().add( columnV1 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedColumnV0 = new BRLConditionVariableColumn( "$name",
                                                                                          DataType.TYPE_STRING,
                                                                                          "Applicant",
                                                                                          "name" );
        edited.getChildColumns().add( editedColumnV0 );
        edited.setHideColumn( true );
        edited.setHeader( "updated" );

        modelSynchronizer.updateColumn( column,
                                        edited );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof StringUiColumn );
        assertEquals( "updated",
                      uiModel.getColumns().get( 2 ).getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( false,
                      uiModel.getColumns().get( 2 ).isVisible() );
    }

    @Test
    public void testUpdate3() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn( "$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name" );
        column.getChildColumns().add( columnV0 );
        column.getChildColumns().add( columnV1 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof IntegerUiColumn );
        assertTrue( uiModel.getColumns().get( 3 ) instanceof StringUiColumn );

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedV0 = new BRLConditionVariableColumn( "$s",
                                                                                    DataType.TYPE_NUMERIC_LONG,
                                                                                    "Applicant",
                                                                                    "salary" );
        edited.getChildColumns().add( editedV0 );
        edited.setHideColumn( true );
        edited.setHeader( "updated" );

        modelSynchronizer.updateColumn( column,
                                        edited );

        assertEquals( 3,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof LongUiColumn );
        assertEquals( "updated",
                      uiModel.getColumns().get( 2 ).getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( false,
                      uiModel.getColumns().get( 2 ).isVisible() );
    }

    @Test
    public void testDelete() throws ModelSynchronizer.MoveColumnVetoException {
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn( "$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age" );
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn( "$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name" );
        column.getChildColumns().add( columnV0 );
        column.getChildColumns().add( columnV1 );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 4,
                      model.getExpandedColumns().size() );
        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 4,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( column );

        assertEquals( 2,
                      model.getExpandedColumns().size() );
        assertEquals( 0,
                      model.getConditions().size() );

        assertEquals( 2,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testMoveColumnTo() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new BRLConditionColumn();
        final BRLConditionVariableColumn column1v0 = new BRLConditionVariableColumn( "$age",
                                                                                     DataType.TYPE_NUMERIC_INTEGER,
                                                                                     "Applicant",
                                                                                     "age" );
        column1v0.setHeader( "age" );
        final BRLConditionVariableColumn column1v1 = new BRLConditionVariableColumn( "$name",
                                                                                     DataType.TYPE_STRING,
                                                                                     "Applicant",
                                                                                     "name" );
        column1v1.setHeader( "name" );

        final CompositeColumn<BRLConditionVariableColumn> column2 = new BRLConditionColumn();
        final BRLConditionVariableColumn column2v0 = new BRLConditionVariableColumn( "$country",
                                                                                     DataType.TYPE_STRING,
                                                                                     "Address",
                                                                                     "country" );
        column2v0.setHeader( "country" );

        column1.getChildColumns().add( column1v0 );
        column1.getChildColumns().add( column1v1 );
        column2.getChildColumns().add( column2v0 );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Integer>( 55 ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<String>( "Smurf" ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<String>( "Canada" ) );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( 55,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "Canada",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get( 4 );
        assertEquals( "age",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_1 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_1 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_1.getIndex() );
        assertEquals( 55,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );
        assertEquals( "Canada",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 2,
                              uiModelColumn2_1 );

        //The move should have been vetoed and nothing changed
        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( 55,
                      model.getData().get( 0 ).get( 2 ).getNumericValue() );
        assertEquals( "Smurf",
                      model.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "Canada",
                      model.getData().get( 0 ).get( 4 ).getStringValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get( 4 );
        assertEquals( "age",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof IntegerUiColumn );
        assertTrue( uiModelColumn2_2 instanceof StringUiColumn );
        assertTrue( uiModelColumn3_2 instanceof StringUiColumn );
        assertEquals( 2,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( 4,
                      uiModelColumn3_2.getIndex() );
        assertEquals( 55,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( "Smurf",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
        assertEquals( "Canada",
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn3_2.getIndex() ).getValue().getValue() );
    }

}

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class ActionWorkItemSetFieldColumnSynchronizerTest extends BaseSynchronizerTest {

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
                                   }
                               }

                             );
        return oracle;
    }

    @Test
    public void testAppend() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 1,
                      model.getActionCols().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof BooleanUiColumn );
        assertEquals( true,
                      ( (BaseUiColumn) uiModel.getColumns().get( 2 ) ).isEditable() );
    }

    @Test
    public void testUpdate() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionWorkItemSetFieldCol52 column = spy( new ActionWorkItemSetFieldCol52() );
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        final ActionWorkItemSetFieldCol52 edited = new ActionWorkItemSetFieldCol52();
        edited.setHideColumn( true );
        edited.setHeader( "updated" );

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn( column,
                                                                          edited );
        assertEquals( 2,
                      diffs.size() );
        verify( column ).diff( edited );

        assertEquals( 1,
                      model.getActionCols().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof BooleanUiColumn );
        assertEquals( "updated",
                      uiModel.getColumns().get( 2 ).getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( false,
                      uiModel.getColumns().get( 2 ).isVisible() );
    }

    @Test
    public void testDelete() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 1,
                      model.getActionCols().size() );
        assertEquals( 3,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( column );
        assertEquals( 0,
                      model.getActionCols().size() );
        assertEquals( 2,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws ModelSynchronizer.MoveColumnVetoException {
        //Add a Pattern to be updated
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setBoundName( "$a" );
        column1.setFactField( "age" );
        column1.setHeader( "wid1" );
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setBoundName( "$a" );
        column2.setFactField( "name" );
        column2.setHeader( "wid2" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column1,
                      model.getActionCols().get( 0 ) );
        assertEquals( column2,
                      model.getActionCols().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 4 );
        assertEquals( "wid1",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid2",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 3,
                              uiModelColumn2_1 );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column2,
                      model.getActionCols().get( 0 ) );
        assertEquals( column1,
                      model.getActionCols().get( 1 ) );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( true,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 4 );
        assertEquals( "wid2",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid1",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 4,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws ModelSynchronizer.MoveColumnVetoException {
        //Add a Pattern to be updated
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setBoundName( "$a" );
        column1.setFactField( "age" );
        column1.setHeader( "wid1" );
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setBoundName( "$a" );
        column2.setFactField( "name" );
        column2.setHeader( "wid2" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column1,
                      model.getActionCols().get( 0 ) );
        assertEquals( column2,
                      model.getActionCols().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 4 );
        assertEquals( "wid1",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid2",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 4,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column2,
                      model.getActionCols().get( 0 ) );
        assertEquals( column1,
                      model.getActionCols().get( 1 ) );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( true,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 4 );
        assertEquals( "wid2",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid1",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 4,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws ModelSynchronizer.MoveColumnVetoException {
        //Add a Pattern to be updated
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setBoundName( "$a" );
        column1.setFactField( "age" );
        column1.setHeader( "wid1" );
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setBoundName( "$a" );
        column2.setFactField( "name" );
        column2.setHeader( "wid2" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         4,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column1,
                      model.getActionCols().get( 0 ) );
        assertEquals( column2,
                      model.getActionCols().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 4 );
        assertEquals( "wid1",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid2",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 4,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 0,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getActionCols().size() );
        assertEquals( column1,
                      model.getActionCols().get( 0 ) );
        assertEquals( column2,
                      model.getActionCols().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 4 ).getBooleanValue() );

        assertEquals( 5,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 3 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 4 );
        assertEquals( "wid1",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "wid2",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 4,
                      uiModelColumn2_2.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

}

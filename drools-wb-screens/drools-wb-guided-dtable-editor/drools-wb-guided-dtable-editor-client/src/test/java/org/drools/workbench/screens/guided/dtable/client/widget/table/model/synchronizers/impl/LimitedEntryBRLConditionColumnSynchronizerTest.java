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
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.LimitedEntryColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.*;
import static org.junit.Assert.*;

public class LimitedEntryBRLConditionColumnSynchronizerTest extends BaseSynchronizerTest {

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl();
        return oracle;
    }

    @Override
    protected List<BaseColumnConverter> getConverters() {
        final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();
        converters.add( new LimitedEntryColumnConverter() );
        return converters;
    }

    @Override
    protected List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers() {
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();
        synchronizers.add( new LimitedEntryBRLConditionColumnSynchronizer() );
        synchronizers.add( new RowSynchronizer() );
        return synchronizers;
    }

    @Test
    public void testAppend() throws ModelSynchronizer.MoveColumnVetoException {
        final LimitedEntryBRLConditionColumn column = new LimitedEntryBRLConditionColumn();
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 1,
                      model.getConditions().size() );

        assertEquals( 3,
                      uiModel.getColumns().size() );
        assertTrue( uiModel.getColumns().get( 2 ) instanceof BooleanUiColumn );
        assertEquals( true,
                      ( (BaseUiColumn) uiModel.getColumns().get( 2 ) ).isEditable() );
    }

    @Test
    public void testUpdate() throws ModelSynchronizer.MoveColumnVetoException {
        final LimitedEntryBRLConditionColumn column = new LimitedEntryBRLConditionColumn();
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        final LimitedEntryBRLConditionColumn edited = new LimitedEntryBRLConditionColumn();
        edited.setHideColumn( true );
        edited.setHeader( "updated" );

        modelSynchronizer.updateColumn( column,
                                        edited );

        assertEquals( 1,
                      model.getConditions().size() );

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
        final LimitedEntryBRLConditionColumn column = new LimitedEntryBRLConditionColumn();
        column.setHeader( "col1" );

        modelSynchronizer.appendColumn( column );

        assertEquals( 1,
                      model.getConditions().size() );
        assertEquals( 3,
                      uiModel.getColumns().size() );

        modelSynchronizer.deleteColumn( column );
        assertEquals( 0,
                      model.getConditions().size() );
        assertEquals( 2,
                      uiModel.getColumns().size() );
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new LimitedEntryBRLConditionColumn();
        column1.setHeader( "age" );
        final CompositeColumn<BRLConditionVariableColumn> column2 = new LimitedEntryBRLConditionColumn();
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "age",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 2,
                              uiModelColumn2_1 );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column2,
                      model.getConditions().get( 0 ) );
        assertEquals( column1,
                      model.getConditions().get( 1 ) );
        assertEquals( false,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "name",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "age",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn2_2.getIndex() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new LimitedEntryBRLConditionColumn();
        column1.setHeader( "age" );
        final CompositeColumn<BRLConditionVariableColumn> column2 = new LimitedEntryBRLConditionColumn();
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "age",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 3,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column2,
                      model.getConditions().get( 0 ) );
        assertEquals( column1,
                      model.getConditions().get( 1 ) );
        assertEquals( false,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( true,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "name",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "age",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 3,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 2,
                      uiModelColumn2_2.getIndex() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new LimitedEntryBRLConditionColumn();
        column1.setHeader( "age" );
        final CompositeColumn<BRLConditionVariableColumn> column2 = new LimitedEntryBRLConditionColumn();
        column2.setHeader( "name" );

        modelSynchronizer.appendColumn( column1 );
        modelSynchronizer.appendColumn( column2 );

        modelSynchronizer.appendRow();
        uiModel.setCell( 0,
                         2,
                         new BaseGridCellValue<Boolean>( true ) );
        uiModel.setCell( 0,
                         3,
                         new BaseGridCellValue<Boolean>( false ) );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get( 3 );
        assertEquals( "age",
                      uiModelColumn1_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_1.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_1 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_1 instanceof BooleanUiColumn );
        assertEquals( 2,
                      uiModelColumn1_1.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_1.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_1.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_1.getIndex() ).getValue().getValue() );

        uiModel.moveColumnTo( 0,
                              uiModelColumn1_1 );

        assertEquals( 2,
                      model.getConditions().size() );
        assertEquals( column1,
                      model.getConditions().get( 0 ) );
        assertEquals( column2,
                      model.getConditions().get( 1 ) );
        assertEquals( true,
                      model.getData().get( 0 ).get( 2 ).getBooleanValue() );
        assertEquals( false,
                      model.getData().get( 0 ).get( 3 ).getBooleanValue() );

        assertEquals( 4,
                      uiModel.getColumns().size() );
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get( 2 );
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get( 3 );
        assertEquals( "age",
                      uiModelColumn1_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertEquals( "name",
                      uiModelColumn2_2.getHeaderMetaData().get( 0 ).getTitle() );
        assertTrue( uiModelColumn1_2 instanceof BooleanUiColumn );
        assertTrue( uiModelColumn2_2 instanceof BooleanUiColumn );
        assertEquals( 2,
                      uiModelColumn1_2.getIndex() );
        assertEquals( 3,
                      uiModelColumn2_2.getIndex() );
        assertEquals( true,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn1_2.getIndex() ).getValue().getValue() );
        assertEquals( false,
                      uiModel.getRow( 0 ).getCells().get( uiModelColumn2_2.getIndex() ).getValue().getValue() );
    }

}

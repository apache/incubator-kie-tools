/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.gwt.BoundFactsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoDeletePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoUpdatePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.PatternConditionMetaData;

@Dependent
public class ConditionColumnSynchronizer extends BaseColumnSynchronizer<PatternConditionMetaData, PatternConditionMetaData, BaseColumnSynchronizer.ColumnMetaData> {

    public static class PatternConditionMetaData extends BaseColumnSynchronizer.ColumnMetaDataImpl {

        private final Pattern52 pattern;

        public PatternConditionMetaData(final Pattern52 pattern,
                                        final ConditionCol52 column) {
            super(column);
            this.pattern = PortablePreconditions.checkNotNull("pattern",
                                                              pattern);
        }

        public Pattern52 getPattern() {
            return pattern;
        }
    }

    @Override
    public boolean handlesAppend(final MetaData metaData) throws VetoException {
        return handlesUpdate(metaData);
    }

    @Override
    public void append(final PatternConditionMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesAppend(metaData)) {
            return;
        }

        final Pattern52 pattern = metaData.getPattern();
        final ConditionCol52 column = (ConditionCol52) metaData.getColumn();

        //Add pattern if it does not already exist
        if (!model.getConditions().contains(pattern)) {
            model.getConditions().add(pattern);

            //Signal patterns changed event
            final BoundFactsChangedEvent bfce = new BoundFactsChangedEvent(rm.getLHSBoundFacts());
            eventBus.fireEvent(bfce);
        }

        pattern.getChildColumns().add(column);
        synchroniseAppendColumn(column);
    }

    @Override
    public boolean handlesUpdate(final MetaData metaData) throws VetoException {
        return metaData instanceof PatternConditionMetaData;
    }

    @Override
    public List<BaseColumnFieldDiff> update(final PatternConditionMetaData originalMetaData,
                                            final PatternConditionMetaData editedMetaData) throws VetoException {
        //Check operation is supported
        if (!(handlesUpdate(originalMetaData) && handlesUpdate(editedMetaData))) {
            return Collections.emptyList();
        }

        //Get differences between original and edited column
        final Pattern52 originalPattern = originalMetaData.getPattern();
        final Pattern52 editedPattern = editedMetaData.getPattern();
        final ConditionCol52 originalColumn = (ConditionCol52) originalMetaData.getColumn();
        final ConditionCol52 editedColumn = (ConditionCol52) editedMetaData.getColumn();

        final List<BaseColumnFieldDiff> patternDiffs = originalPattern.diff(editedPattern);
        final List<BaseColumnFieldDiff> columnDiffs = originalColumn.diff(editedColumn);

        final List<BaseColumnFieldDiff> diffs = new ArrayList<BaseColumnFieldDiff>();
        if (patternDiffs != null) {
            diffs.addAll(patternDiffs);
        }
        if (columnDiffs != null) {
            diffs.addAll(columnDiffs);
        }

        //Changes to the Pattern create the new column and remove the old column
        final boolean isNewPattern = isNewPattern(editedPattern);
        final boolean isBoundNameUpdated = BaseColumnFieldDiffImpl.hasChanged(Pattern52.FIELD_BOUND_NAME, diffs);
        final boolean isEntryPointNameUpdated = BaseColumnFieldDiffImpl.hasChanged(Pattern52.FIELD_ENTRY_POINT_NAME, diffs);
        final boolean isUpdatedCondition = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_BINDING, diffs);

        //Check if pattern change can be applied to model
        if (isUpdatedCondition) {
            if (!isPotentialConditionDeletionSafe(originalColumn)) {
                throw new VetoUpdatePatternInUseException();
            }
        }
        if (isBoundNameUpdated) {
            if (!isPotentialPatternDeletionSafe(originalPattern)) {
                throw new VetoUpdatePatternInUseException();
            }
        }

        //Perform update
        if (isNewPattern || isBoundNameUpdated) {
            append(editedMetaData);
            copyColumnData(originalColumn,
                           editedColumn,
                           diffs);
            delete(originalMetaData);
            return diffs;
        }

        if (isEntryPointNameUpdated) {
            originalPattern.setEntryPointName(editedPattern.getEntryPointName());
        }

        //Changes to the Condition, but Pattern remains unchanged
        update(originalColumn,
               editedColumn);

        final boolean isHideUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_HIDE_COLUMN,
                                                                         diffs);
        final boolean isHeaderUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_HEADER,
                                                                           diffs);
        final boolean isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(Pattern52.FIELD_FACT_TYPE,
                                                                             diffs);
        final boolean isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_FACT_FIELD,
                                                                              diffs);
        final boolean isFieldTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_FIELD_TYPE,
                                                                              diffs);
        final boolean isConstraintValueTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE,
                                                                                        diffs);

        if (isFactTypeUpdated || isFactFieldUpdated || isFieldTypeUpdated || isConstraintValueTypeUpdated) {
            clearColumnData(originalColumn);
        } else {
            cleanColumnData(originalColumn,
                            editedColumn,
                            diffs);
        }

        synchroniseUpdateColumn(originalColumn);

        if (isHideUpdated) {
            setColumnVisibility(originalColumn,
                                originalColumn.isHideColumn());
        }
        if (isHeaderUpdated) {
            setColumnHeader(originalColumn,
                            originalColumn.getHeader());
        }

        return diffs;
    }

    @Override
    public boolean handlesDelete(final MetaData metaData) throws VetoException {
        if (!(metaData instanceof ColumnMetaData)) {
            return false;
        }
        return ((ColumnMetaData) metaData).getColumn() instanceof ConditionCol52;
    }

    @Override
    public void delete(final ColumnMetaData metaData) throws VetoException {
        //Check operation is supported
        if (!handlesDelete(metaData)) {
            return;
        }

        final ConditionCol52 column = (ConditionCol52) metaData.getColumn();
        final int columnIndex = model.getExpandedColumns().indexOf(column);
        final Pattern52 pattern = model.getPattern(column);

        //Check if pattern change can be applied to model
        if (!isPotentialConditionDeletionSafe(column)) {
            throw new VetoDeletePatternInUseException();
        }
        if (!isPotentialPatternDeletionSafe(pattern)) {
            throw new VetoDeletePatternInUseException();
        }

        //Perform deletion
        pattern.getChildColumns().remove(column);

        //Remove pattern if it contains zero conditions
        if (pattern.getChildColumns().isEmpty()) {
            model.getConditions().remove(pattern);

            //Signal patterns changed event to Decision Table Widget
            final BoundFactsChangedEvent bfce = new BoundFactsChangedEvent(rm.getLHSBoundFacts());
            eventBus.fireEvent(bfce);
        }

        synchroniseDeleteColumn(columnIndex);
    }

    @Override
    public boolean handlesMoveColumnsTo(final List<? extends MetaData> metaData) throws VetoException {
        for (MetaData md : metaData) {
            if (!(md instanceof MoveColumnToMetaData)) {
                return false;
            }
            if (((MoveColumnToMetaData) md).getColumn() instanceof BRLConditionColumn) {
                return false;
            }
            if (!(((MoveColumnToMetaData) md).getColumn() instanceof ConditionCol52)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void moveColumnsTo(final List<MoveColumnToMetaData> metaData) throws VetoException {
        //Check operation is supported
        if (!handlesMoveColumnsTo(metaData)) {
            return;
        }
        if (isPattern(metaData)) {
            doMovePattern(metaData);
        } else if (isSingleCondition(metaData)) {
            doMoveSingleCondition(metaData.get(0));
        } else {
            throw new ModelSynchronizer.MoveVetoException();
        }
    }

    @Override
    protected void setColumnHeader(final BaseColumn modelColumn,
                                   final String header) {
        modelColumn.setHeader(header);
        final int iModelColumn = model.getExpandedColumns().indexOf(modelColumn);
        uiModel.getColumns().get(iModelColumn).getHeaderMetaData().get(1).setTitle(header);
    }

    private boolean isPattern(final List<MoveColumnToMetaData> metaData) {
        if (!metaData.stream().allMatch((c) -> c.getColumn() instanceof ConditionCol52)) {
            return false;
        }
        final MoveColumnToMetaData md = metaData.get(0);
        final ConditionCol52 srcModelColumn = (ConditionCol52) md.getColumn();
        final Pattern52 srcModelPattern = model.getPattern(srcModelColumn);
        return srcModelPattern.getChildColumns().size() == metaData.size();
    }

    private boolean isSingleCondition(final List<MoveColumnToMetaData> metaData) {
        if (metaData.size() != 1) {
            return false;
        }
        if (metaData.get(0).getColumn() instanceof BRLConditionVariableColumn) {
            return false;
        }
        return metaData.get(0).getColumn() instanceof ConditionCol52;
    }

    private void doMovePattern(final List<MoveColumnToMetaData> metaData) throws VetoException {
        final MoveColumnToMetaData md = metaData.get(0);
        final ConditionCol52 srcModelColumn = (ConditionCol52) md.getColumn();
        final Pattern52 srcModelPattern = model.getPattern(srcModelColumn);
        if (srcModelPattern == null) {
            throw new ModelSynchronizer.MoveVetoException();
        }
        final List<ConditionCol52> srcModelPatternConditionColumns = srcModelPattern.getChildColumns();
        final int srcModelPatternConditionColumnCount = srcModelPatternConditionColumns.size();
        if (srcModelPatternConditionColumnCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }
        if (srcModelPatternConditionColumnCount != metaData.size()) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final int tgtColumnIndex = md.getTargetColumnIndex();
        final int tgtPatternIndex = findTargetPatternIndex(md);
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();

        moveModelData(tgtColumnIndex,
                      allModelColumns.indexOf(srcModelPatternConditionColumns.get(0)),
                      allModelColumns.indexOf(srcModelPatternConditionColumns.get(0)) + srcModelPatternConditionColumnCount - 1);

        model.getConditions().remove(srcModelPattern);
        model.getConditions().add(tgtPatternIndex,
                                  srcModelPattern);
    }

    //Move a single Condition column; it must remain within the bounds of it's parent Pattern's columns
    private void doMoveSingleCondition(final MoveColumnToMetaData metaData) throws VetoException {
        final ConditionCol52 modelColumn = (ConditionCol52) metaData.getColumn();
        final Pattern52 modelPattern = model.getPattern(modelColumn);
        if (modelPattern == null) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final List<ConditionCol52> modelPatternConditionColumns = modelPattern.getChildColumns();
        final int modelPatternConditionColumnCount = modelPatternConditionColumns.size();
        if (modelPatternConditionColumnCount == 0) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int minColumnIndex = allModelColumns.indexOf(modelPatternConditionColumns.get(0));
        final int maxColumnIndex = allModelColumns.indexOf(modelPatternConditionColumns.get(modelPatternConditionColumnCount - 1));

        final int targetColumnIndex = metaData.getTargetColumnIndex();
        final int sourceColumnIndex = metaData.getSourceColumnIndex();
        if (targetColumnIndex < minColumnIndex || targetColumnIndex > maxColumnIndex) {
            throw new ModelSynchronizer.MoveVetoException();
        }

        moveModelData(targetColumnIndex,
                      sourceColumnIndex,
                      sourceColumnIndex);

        modelPatternConditionColumns.remove(modelColumn);
        modelPatternConditionColumns.add(targetColumnIndex - minColumnIndex,
                                         modelColumn);
    }

    private boolean isNewPattern(final Pattern52 editedPattern) {
        if (model.getConditions() == null || model.getConditions().isEmpty()) {
            return true;
        }
        boolean isNewPattern = true;
        for (CompositeColumn<? extends BaseColumn> column : model.getConditions()) {
            if (column instanceof Pattern52) {
                final Pattern52 existingPattern = (Pattern52) column;
                if (existingPattern.getBoundName().equals(editedPattern.getBoundName())) {
                    isNewPattern = false;
                    break;
                }
            }
        }
        return isNewPattern;
    }

    private void copyColumnData(final ConditionCol52 originalColumn,
                                final ConditionCol52 editedColumn,
                                final List<BaseColumnFieldDiff> diffs) {
        final boolean isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(Pattern52.FIELD_FACT_TYPE,
                                                                             diffs);
        final boolean isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_FACT_FIELD,
                                                                              diffs);
        final boolean isConstraintValueTypeUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE,
                                                                                        diffs);

        // If the FactType, FieldType and ConstraintValueType are unchanged we can copy cell values from the old column into the new
        if (!(isFactTypeUpdated || isFactFieldUpdated || isConstraintValueTypeUpdated)) {
            final int originalColumnIndex = model.getExpandedColumns().indexOf(originalColumn);
            final int editedColumnIndex = model.getExpandedColumns().indexOf(editedColumn);

            for (int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++) {
                final List<DTCellValue52> modelRow = model.getData().get(rowIndex);
                final DTCellValue52 modelCell = modelRow.get(originalColumnIndex);
                modelRow.set(editedColumnIndex,
                             modelRow.get(originalColumnIndex));

                //BaseGridData is sparsely populated; only add values if needed.
                if (modelCell.hasValue()) {
                    uiModel.setCellValue(rowIndex,
                                         editedColumnIndex,
                                         gridWidgetCellFactory.convertCell(modelCell,
                                                                           editedColumn,
                                                                           cellUtilities,
                                                                           columnUtilities));
                }
            }
        }
    }

    private void cleanColumnData(final ConditionCol52 originalColumn,
                                 final ConditionCol52 editedColumn,
                                 final List<BaseColumnFieldDiff> diffs) {
        final boolean isOperatorUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_OPERATOR,
                                                                             diffs);
        final boolean isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged(ConditionCol52.FIELD_VALUE_LIST,
                                                                              diffs);

        //Clear "otherwise" if the column cannot accept them
        if (isOperatorUpdated && !ColumnUtilities.canAcceptOtherwiseValues(editedColumn)) {
            removeOtherwiseStates(originalColumn);
        }

        //Clear comma-separated values if the column cannot accept them
        if (isOperatorUpdated && !canAcceptCommaSeparatedValues(editedColumn)) {
            cellUtilities.removeCommaSeparatedValue(editedColumn.getDefaultValue());
            removeCommaSeparatedValues(originalColumn);
        }

        // Update column's cell content if the Optional Value list has changed
        if (isValueListUpdated) {
            updateCellsForOptionValueList(originalColumn,
                                          editedColumn);
        }
    }

    //Remove Otherwise state from column cells
    private void removeOtherwiseStates(final BaseColumn column) {
        final int columnIndex = this.model.getExpandedColumns().indexOf(column);
        for (List<DTCellValue52> row : this.model.getData()) {
            final DTCellValue52 dcv = row.get(columnIndex);
            dcv.setOtherwise(false);
        }
    }

    // Check whether the given column can accept comma-separated values
    private boolean canAcceptCommaSeparatedValues(final ConditionCol52 column) {
        //Check column contains literal values
        if (column.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL) {
            return false;
        }

        //Check operator is supported
        final List<String> ops = Arrays.asList(OperatorsOracle.EXPLICIT_LIST_OPERATORS);
        return ops.contains(column.getOperator());
    }

    //Convert comma-separated values to the first in the list
    private void removeCommaSeparatedValues(final BaseColumn column) {
        final int columnIndex = this.model.getExpandedColumns().indexOf(column);
        for (List<DTCellValue52> row : this.model.getData()) {
            final DTCellValue52 dcv = row.get(columnIndex);
            cellUtilities.removeCommaSeparatedValue(dcv);
        }
    }

    private void update(final ConditionCol52 originalColumn,
                        final ConditionCol52 editedColumn) {
        originalColumn.setConstraintValueType(editedColumn.getConstraintValueType());
        originalColumn.setFactField(editedColumn.getFactField());
        originalColumn.setFieldType(editedColumn.getFieldType());
        originalColumn.setHeader(editedColumn.getHeader());
        originalColumn.setOperator(editedColumn.getOperator());
        originalColumn.setValueList(editedColumn.getValueList());
        originalColumn.setDefaultValue(editedColumn.getDefaultValue());
        originalColumn.setHideColumn(editedColumn.isHideColumn());
        originalColumn.setParameters(editedColumn.getParameters());
        originalColumn.setBinding(editedColumn.getBinding());
        if (originalColumn instanceof LimitedEntryCol && editedColumn instanceof LimitedEntryCol) {
            ((LimitedEntryCol) originalColumn).setValue(((LimitedEntryCol) editedColumn).getValue());
        }
    }

    private boolean isPotentialConditionDeletionSafe(final ConditionCol52 condition) {
        final String binding = condition.getBinding();
        if (!(binding == null || binding.isEmpty())) {
            return !rm.isBoundFactUsed(binding);
        }
        return true;
    }

    private boolean isPotentialPatternDeletionSafe(final Pattern52 pattern) {
        if (pattern.getChildColumns().size() == 1) {
            final String binding = pattern.getBoundName();
            if (!(binding == null || binding.isEmpty())) {
                return !rm.isBoundFactUsed(binding);
            }
        }
        return true;
    }
}

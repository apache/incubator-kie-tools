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
package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.template.client.editor.RuleModelPeerVariableVisitor;
import org.drools.workbench.screens.guided.template.client.editor.RuleModelPeerVariableVisitor.ValueHolder;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DropDownDataValueMapProvider;

/**
 * A utility class to get the values of all Constraints\Actions in the scope of
 * a Template Key to drive dependent enumerations. A value is in scope if it is
 * on a Constraint or Action on the same Pattern of the base column.
 */
public class DependentEnumsUtilities
        implements
        DropDownDataValueMapProvider<DependentEnumsUtilities.Context> {

    public static class Context {

        private final int rowIndex;
        private final int columnIndex;

        public Context(final int rowIndex,
                       final int columnIndex) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }
    }

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final CellUtilities cellUtilities;

    public DependentEnumsUtilities(final GuidedDecisionTable52 model,
                                   final AsyncPackageDataModelOracle oracle) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.oracle = PortablePreconditions.checkNotNull("oracle",
                                                         oracle);
        this.cellUtilities = new CellUtilities();
    }

    /**
     * Create a map of Field Values keyed on Field Names used by
     * SuggestionCompletionEngine.getEnums(String, String, Map<String, String>)
     * to drive dependent enumerations.
     *
     * @param context The Context of the cell being edited containing physical
     *                coordinate in the data-space.
     */
    @Override
    public Map<String, String> getCurrentValueMap(final Context context) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final int iBaseRowIndex = context.getRowIndex();
        final int iBaseColIndex = context.getColumnIndex();
        final List<DTCellValue52> rowData = this.model.getData().get(iBaseRowIndex);

        //Get the column for the cell being edited
        final List<BaseColumn> allColumns = this.model.getExpandedColumns();
        final BaseColumn baseColumn = allColumns.get(iBaseColIndex);

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if (baseColumn instanceof BRLConditionVariableColumn) {
            final BRLConditionVariableColumn baseBRLConditionColumn = (BRLConditionVariableColumn) baseColumn;
            final BRLConditionColumn brl = model.getBRLColumn(baseBRLConditionColumn);
            final RuleModel rm = new RuleModel();
            IPattern[] lhs = new IPattern[brl.getDefinition().size()];
            brl.getDefinition().toArray(lhs);
            rm.lhs = lhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor(rm,
                                                                                                      baseBRLConditionColumn.getVarName());
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for (ValueHolder valueHolder : peerVariables) {
                switch (valueHolder.getType()) {
                    case TEMPLATE_KEY:
                        final BRLConditionVariableColumn vc = getConditionVariableColumnIndex(brl.getChildColumns(),
                                                                                              valueHolder.getValue());
                        final int iCol = allColumns.indexOf(vc);
                        final DTCellValue52 dcv = rowData.get(iCol);
                        final String field = vc.getFactField();
                        currentValueMap.put(field,
                                            cellUtilities.asString(dcv));
                        break;
                    case VALUE:
                        currentValueMap.put(valueHolder.getFieldName(),
                                            valueHolder.getValue());
                }
            }
        } else if (baseColumn instanceof BRLActionVariableColumn) {
            final BRLActionVariableColumn baseBRLActionColumn = (BRLActionVariableColumn) baseColumn;
            final BRLActionColumn brl = model.getBRLColumn(baseBRLActionColumn);
            final RuleModel rm = new RuleModel();
            IAction[] rhs = new IAction[brl.getDefinition().size()];
            brl.getDefinition().toArray(rhs);
            rm.rhs = rhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor(rm,
                                                                                                      baseBRLActionColumn.getVarName());
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for (ValueHolder valueHolder : peerVariables) {
                switch (valueHolder.getType()) {
                    case TEMPLATE_KEY:
                        final BRLActionVariableColumn vc = getActionVariableColumnIndex(brl.getChildColumns(),
                                                                                        valueHolder.getValue());
                        final int iCol = allColumns.indexOf(vc);
                        final DTCellValue52 dcv = rowData.get(iCol);
                        final String field = vc.getFactField();
                        currentValueMap.put(field,
                                            cellUtilities.asString(dcv));
                        break;
                    case VALUE:
                        currentValueMap.put(valueHolder.getFieldName(),
                                            valueHolder.getValue());
                }
            }
        } else if (baseColumn instanceof ConditionCol52) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            final Pattern52 basePattern = this.model.getPattern(baseConditionColumn);
            for (ConditionCol52 cc : basePattern.getChildColumns()) {
                final int iCol = allColumns.indexOf(cc);
                final DTCellValue52 dcv = rowData.get(iCol);
                currentValueMap.put(cc.getFactField(),
                                    cellUtilities.asString(dcv));
            }
        } else if (baseColumn instanceof ActionSetFieldCol52) {
            ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for (ActionCol52 ac : this.model.getActionCols()) {
                if (ac instanceof ActionSetFieldCol52) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if (asf.getBoundName().equals(binding)) {
                        final int iCol = allColumns.indexOf(asf);
                        final DTCellValue52 dcv = rowData.get(iCol);
                        currentValueMap.put(asf.getFactField(),
                                            cellUtilities.asString(dcv));
                    }
                }
            }
        } else if (baseColumn instanceof ActionInsertFactCol52) {
            ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for (ActionCol52 ac : this.model.getActionCols()) {
                if (ac instanceof ActionInsertFactCol52) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if (aif.getBoundName().equals(binding)) {
                        final int iCol = allColumns.indexOf(aif);
                        final DTCellValue52 dcv = rowData.get(iCol);
                        currentValueMap.put(aif.getFactField(),
                                            cellUtilities.asString(dcv));
                    }
                }
            }
        }
        return currentValueMap;
    }

    private BRLConditionVariableColumn getConditionVariableColumnIndex(final List<BRLConditionVariableColumn> definition,
                                                                       final String variableName) {
        for (BRLConditionVariableColumn vc : definition) {
            if (vc.getVarName().equals(variableName)) {
                return vc;
            }
        }
        //This should never happen
        throw new IllegalArgumentException("Variable '" + variableName + "' not found. This suggests a programming error.");
    }

    private BRLActionVariableColumn getActionVariableColumnIndex(final List<BRLActionVariableColumn> definition,
                                                                 final String variableName) {
        for (BRLActionVariableColumn ac : definition) {
            if (ac.getVarName().equals(variableName)) {
                return ac;
            }
        }
        //This should never happen
        throw new IllegalArgumentException("Variable '" + variableName + "' not found. This suggests a programming error.");
    }

    @Override
    public Set<Integer> getDependentColumnIndexes(final Context context) {
        final int iBaseColIndex = context.getColumnIndex();
        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        //Get the column for the cell being edited
        final List<BaseColumn> allColumns = this.model.getExpandedColumns();
        final BaseColumn baseColumn = allColumns.get(iBaseColIndex);

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if (baseColumn instanceof BRLConditionVariableColumn) {
            final BRLConditionVariableColumn baseBRLConditionColumn = (BRLConditionVariableColumn) baseColumn;
            final BRLConditionColumn brl = model.getBRLColumn(baseBRLConditionColumn);
            final RuleModel rm = new RuleModel();
            IPattern[] lhs = new IPattern[brl.getDefinition().size()];
            brl.getDefinition().toArray(lhs);
            rm.lhs = lhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor(rm,
                                                                                                      baseBRLConditionColumn.getVarName());
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for (ValueHolder valueHolder : peerVariables) {
                switch (valueHolder.getType()) {
                    case TEMPLATE_KEY:
                        if (oracle.isDependentEnum(baseBRLConditionColumn.getFactType(),
                                                   baseBRLConditionColumn.getFactField(),
                                                   valueHolder.getFieldName())) {
                            final BRLConditionVariableColumn vc = getConditionVariableColumnIndex(brl.getChildColumns(),
                                                                                                  valueHolder.getValue());
                            final int iCol = allColumns.indexOf(vc);
                            dependentColumnIndexes.add(iCol);
                        }
                        break;
                }
            }
        } else if (baseColumn instanceof BRLActionVariableColumn) {
            final BRLActionVariableColumn baseBRLActionColumn = (BRLActionVariableColumn) baseColumn;
            final BRLActionColumn brl = model.getBRLColumn(baseBRLActionColumn);
            final RuleModel rm = new RuleModel();
            IAction[] rhs = new IAction[brl.getDefinition().size()];
            brl.getDefinition().toArray(rhs);
            rm.rhs = rhs;

            final RuleModelPeerVariableVisitor peerVariableVisitor = new RuleModelPeerVariableVisitor(rm,
                                                                                                      baseBRLActionColumn.getVarName());
            List<ValueHolder> peerVariables = peerVariableVisitor.getPeerVariables();

            //Add other variables values
            for (ValueHolder valueHolder : peerVariables) {
                switch (valueHolder.getType()) {
                    case TEMPLATE_KEY:
                        if (oracle.isDependentEnum(baseBRLActionColumn.getFactType(),
                                                   baseBRLActionColumn.getFactField(),
                                                   valueHolder.getFieldName())) {
                            final BRLActionVariableColumn vc = getActionVariableColumnIndex(brl.getChildColumns(),
                                                                                            valueHolder.getValue());
                            final int iCol = allColumns.indexOf(vc);
                            dependentColumnIndexes.add(iCol);
                        }
                        break;
                }
            }
        } else if (baseColumn instanceof ConditionCol52) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            final Pattern52 basePattern = this.model.getPattern(baseConditionColumn);
            for (ConditionCol52 cc : basePattern.getChildColumns()) {
                if (oracle.isDependentEnum(basePattern.getFactType(),
                                           baseConditionColumn.getFactField(),
                                           cc.getFactField())) {
                    dependentColumnIndexes.add(allColumns.indexOf(cc));
                }
            }
        } else if (baseColumn instanceof ActionSetFieldCol52) {
            final ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final Pattern52 basePattern = model.getConditionPattern(baseActionColumn.getBoundName());
            final String binding = baseActionColumn.getBoundName();
            for (ActionCol52 ac : this.model.getActionCols()) {
                if (ac instanceof ActionSetFieldCol52) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if (asf.getBoundName().equals(binding)) {
                        if (oracle.isDependentEnum(basePattern.getFactType(),
                                                   baseActionColumn.getFactField(),
                                                   asf.getFactField())) {
                            dependentColumnIndexes.add(allColumns.indexOf(ac));
                        }
                    }
                }
            }
        } else if (baseColumn instanceof ActionInsertFactCol52) {
            final ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for (ActionCol52 ac : this.model.getActionCols()) {
                if (ac instanceof ActionInsertFactCol52) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if (aif.getBoundName().equals(binding)) {
                        if (oracle.isDependentEnum(baseActionColumn.getFactType(),
                                                   baseActionColumn.getFactField(),
                                                   aif.getFactField())) {
                            dependentColumnIndexes.add(allColumns.indexOf(ac));
                        }
                    }
                }
            }
        }

        return dependentColumnIndexes;
    }
}

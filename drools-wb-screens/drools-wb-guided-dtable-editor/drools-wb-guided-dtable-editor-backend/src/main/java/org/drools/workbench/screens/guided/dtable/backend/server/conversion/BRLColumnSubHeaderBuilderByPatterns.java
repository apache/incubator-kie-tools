/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.FromTo;

/**
 * Splits the BRL column so that each value has a column.
 */
public class BRLColumnSubHeaderBuilderByPatterns
        implements BRLColumnSubHeaderBuilder {

    private SubHeaderBuilder subHeaderBuilder;
    private ColumnContext columnContext;

    private GuidedDecisionTable52 dtable;

    public BRLColumnSubHeaderBuilderByPatterns(final SubHeaderBuilder subHeaderBuilder,
                                               final ColumnContext columnContext,
                                               final GuidedDecisionTable52 dtable) {
        this.subHeaderBuilder = subHeaderBuilder;
        this.columnContext = columnContext;
        this.dtable = dtable;
    }

    @Override
    public void buildBrlActions(final BRLActionColumn brlColumn) {

        for (final IAction iAction : brlColumn.getDefinition()) {
            final Iterator<String> variablesIterator = columnContext.getVariablesInOrderOfUse(iAction).iterator();

            final String boundName = getBoundName(iAction);

            while (variablesIterator.hasNext()) {
                final ActionCol52 childColumn = getChildActionColumn(variablesIterator.next(),
                                                                     brlColumn.getChildColumns());
                if (childColumn instanceof BRLActionVariableColumn) {
                    addBRLActionVariableColumn(brlColumn,
                                               boundName,
                                               (BRLActionVariableColumn) childColumn);
                }

                updateColumnContext(brlColumn,
                                    childColumn);

                if (variablesIterator.hasNext()) {
                    subHeaderBuilder.incrementTargetIndex();
                }
            }
        }
    }

    public void addBRLActionVariableColumn(final BRLActionColumn brlColumn,
                                           final String boundName,
                                           final BRLActionVariableColumn childColumn) {
        final boolean madeInsert = subHeaderBuilder.addInsert(brlColumn.getHeader(),
                                                              boundName,
                                                              childColumn.getFactType(),
                                                              childColumn.getFactField(),
                                                              "");
        if (madeInsert) {
            addInsertColumn(brlColumn,
                            childColumn);
        }
    }

    /**
     * Adds a column that creates a new fact and inserts it.
     */
    public void addInsertColumn(final BRLActionColumn brlColumn,
                                final BRLActionVariableColumn childColumn) {
        subHeaderBuilder.getColumnContext().put(brlColumn,
                                                FromTo.makePlaceHolder(dtable.getExpandedColumns().indexOf(childColumn),
                                                                       subHeaderBuilder.getTargetColumnIndex() - 1));
    }

    private String getBoundName(final IAction iAction) {
        if (iAction instanceof ActionSetField && StringUtils.isNotEmpty(((ActionSetField) iAction).getVariable())) {
            return ((ActionSetField) iAction).getVariable();
        } else {
            return columnContext.getNextFreeColumnFactName();
        }
    }

    @Override
    public void buildBrlConditions(final BRLConditionColumn brlColumn) {
        final Iterator<String> variablesIterator = columnContext.getVariablesInOrderOfUse(brlColumn).iterator();
        while (variablesIterator.hasNext()) {
            final ConditionCol52 childColumn = getChildConditionColumn(variablesIterator.next(),
                                                                       brlColumn.getChildColumns());
            subHeaderBuilder.addCondition(childColumn);
            updateColumnContext(brlColumn,
                                childColumn);

            if (variablesIterator.hasNext()) {
                subHeaderBuilder.incrementTargetIndex();
            }
        }
    }

    private ActionCol52 getChildActionColumn(final String varName,
                                             final List<BRLActionVariableColumn> childColumns) {
        for (BRLActionVariableColumn childColumn : childColumns) {
            if (Objects.equals(childColumn.getVarName(), varName)) {
                return childColumn;
            }
        }

        throw new IllegalArgumentException("Found a variable for a column that does not exist");
    }

    private ConditionCol52 getChildConditionColumn(final String varName,
                                                   final List<BRLConditionVariableColumn> childColumns) {
        for (BRLConditionVariableColumn childColumn : childColumns) {
            if (Objects.equals(childColumn.getVarName(), varName)) {
                return childColumn;
            }
        }

        throw new IllegalArgumentException("Found a variable for a column that does not exist");
    }

    public void updateColumnContext(final BRLConditionColumn brlColumn, final ConditionCol52 childColumn) {
        subHeaderBuilder.getColumnContext().put(brlColumn,
                                                FromTo.makeFromTo(dtable.getExpandedColumns().indexOf(childColumn),
                                                                  dtable.getExpandedColumns().indexOf(childColumn)));
    }

    public void updateColumnContext(final BRLActionColumn brlColumn,
                                    final ActionCol52 childColumn) {
        subHeaderBuilder.getColumnContext().put(brlColumn,
                                                FromTo.makeFromTo(dtable.getExpandedColumns().indexOf(childColumn),
                                                                  subHeaderBuilder.getTargetColumnIndex()));
    }
}

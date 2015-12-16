/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.Actions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.Conditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;

public class RowInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting {

    private final GuidedDecisionTable52.TableFormat tableFormat;
    private final RowInspectorCache cache;

    private final Conditions conditions = new Conditions();
    private final Actions actions = new Actions();

    private int rowIndex;

    public RowInspector( final int rowIndex,
                         final GuidedDecisionTable52.TableFormat tableFormat,
                         final RowInspectorCache cache ) {
        this.rowIndex = rowIndex;
        this.tableFormat = tableFormat;
        this.cache = cache;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public RowInspectorCache getCache() {
        return cache;
    }

    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return tableFormat;
    }

    public void addConditionInspector( final ConditionInspector conditionInspector ) {
        conditions.put( conditionInspector.getKey(),
                        conditionInspector );
    }

    public void addActionInspector( final ActionInspector actionInspector ) {
        actions.put( actionInspector.getKey(),
                     actionInspector );
    }

    public Actions getActions() {
        return actions;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void setRowIndex( int rowIndex ) {
        this.rowIndex = rowIndex;
    }

    @Override
    public boolean isRedundant( Object other ) {
        if ( other instanceof RowInspector ) {
            if ( areConditionsRedundant( (RowInspector) other ) &&
                    areActionsRedundant( (RowInspector) other ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean subsumes( Object other ) {
        if ( other instanceof RowInspector ) {
            if ( actions.subsumes( ( (RowInspector) other ).getActions() ) && conditions.subsumes( ( (RowInspector) other ).getConditions() ) ) {
                return true;
            }
        }

        return false;
    }

    private boolean areActionsRedundant( final RowInspector other ) {
        return other.actions.isRedundant( actions );
    }

    private boolean areConditionsRedundant( final RowInspector other ) {
        return other.conditions.isRedundant( conditions );
    }

    @Override
    public boolean conflicts( Object other ) {
        if ( other instanceof RowInspector ) {

        }
        return false;
    }
}

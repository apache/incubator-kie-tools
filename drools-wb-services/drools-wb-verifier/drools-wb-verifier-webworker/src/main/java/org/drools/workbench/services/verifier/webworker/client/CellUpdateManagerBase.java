/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.webworker.client;

import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Actions;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.Rule;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.Logger;

public abstract class CellUpdateManagerBase {

    protected final Column column;
    protected final Actions actions;
    protected final Conditions conditions;
    protected final GuidedDecisionTable52 model;
    protected final Coordinate coordinate;

    public CellUpdateManagerBase(final Index index,
                                 final GuidedDecisionTable52 model,
                                 final Coordinate coordinate) throws
            UpdateException {
        this.model = model;
        this.coordinate = coordinate;

        Logger.add("Updating: " + coordinate.toString());

        try {
            column = index.getColumns()
                    .where(Column.index()
                                   .is(coordinate.getCol()))
                    .select()
                    .first();

            final Rule rule = index.getRules()
                    .where(Rule.index()
                                   .is(coordinate.getRow()))
                    .select()
                    .first();
            actions = rule.getActions();
            conditions = rule.getConditions();
        } catch (final Exception e) {
            throw new UpdateException("Failed to update: " + coordinate.toString());
        }
    }

    /**
     * @return Returns true if the cell content was found and the value changed.
     */
    public boolean update() {

        if (!updateCondition()) {
            return updateAction();
        } else {
            return true;
        }
    }

    private boolean updateCondition() {

        final Condition condition = conditions.where(Condition.columnUUID()
                                                             .is(column.getUuidKey()))
                .select()
                .first();

        if (condition != null) {
            return updateCondition(condition);
        } else {
            return false;
        }
    }

    private boolean updateAction() {
        final Action action = actions.where(Action.columnUUID()
                                                    .is(column.getUuidKey()))
                .select()
                .first();

        if (action != null) {
            return updateAction(action);
        } else {
            return false;
        }
    }

    protected abstract boolean updateCondition(final Condition condition);

    protected abstract boolean updateAction(final Action action);
}

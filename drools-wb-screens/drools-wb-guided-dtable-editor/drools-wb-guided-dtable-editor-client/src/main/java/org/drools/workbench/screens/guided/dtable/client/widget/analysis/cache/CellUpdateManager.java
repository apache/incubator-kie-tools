/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.Actions;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Conditions;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

public abstract class CellUpdateManager {

    protected final Column column;
    protected final Actions actions;
    protected final Conditions conditions;
    protected final GuidedDecisionTable52 model;
    protected final Coordinate coordinate;

    public CellUpdateManager( final Index index,
                              final GuidedDecisionTable52 model,
                              final Coordinate coordinate ) {
        this.model = model;
        this.coordinate = coordinate;

        column = index.columns
                .where( Column.index()
                                .is( coordinate.getCol() ) )
                .select()
                .first();

        final Rule rule = index.rules
                .where( Rule.index()
                                .is( coordinate.getRow() ) )
                .select()
                .first();
        actions = rule.getActions();
        conditions = rule.getConditions();

    }

    /**
     * @return Returns true if the cell content was found and the value changed.
     */
    public boolean update() {

        if ( !updateCondition() ) {
            return updateAction();
        } else {
            return true;
        }
    }


    private boolean updateCondition() {

        final Condition condition = conditions.where( Condition.columnUUID()
                                                              .is( column.getUuidKey() ) )
                .select()
                .first();

        if ( condition != null ) {
            return updateCondition( condition );
        } else {
            return false;
        }
    }

    private boolean updateAction() {
        final Action action = actions.where( Action.columnUUID()
                                                     .is( column.getUuidKey() ) )
                .select()
                .first();

        if ( action != null ) {
            return updateAction( action );
        } else {
            return false;
        }
    }

    protected abstract boolean updateCondition( final Condition condition );

    protected abstract boolean updateAction( final Action action );

}

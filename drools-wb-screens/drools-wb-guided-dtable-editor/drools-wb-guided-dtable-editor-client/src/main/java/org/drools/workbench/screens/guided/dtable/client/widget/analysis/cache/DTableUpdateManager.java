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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.cache.UpdateManager;
import org.drools.workbench.services.verifier.api.client.checks.base.Check;
import org.drools.workbench.services.verifier.api.client.checks.base.CheckRunner;
import org.drools.workbench.services.verifier.api.client.checks.util.NullEqualityOperator;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.uberfire.commons.validation.PortablePreconditions;

public class DTableUpdateManager
        extends UpdateManager {

    private static final int ROW_NUMBER_COLUMN  = 0;
    private static final int DESCRIPTION_COLUMN = 1;

    private final GuidedDecisionTable52 model;

    public DTableUpdateManager( final Index index,
                                final GuidedDecisionTable52 model,
                                final DtableRuleInspectorCache cache,
                                final CheckRunner checkRunner ) {
        super( PortablePreconditions.checkNotNull( "index",
                                                   index ),
               PortablePreconditions.checkNotNull( "cache",
                                                   cache ),
               PortablePreconditions.checkNotNull( "updateHandler",
                                                   checkRunner ) );
        this.model = PortablePreconditions.checkNotNull( "model", model );
    }

    public boolean update( final List<Coordinate> coordinates ) {

        final Set<Check> checks = new HashSet<>();

        for ( final Coordinate coordinate : coordinates ) {
            if ( coordinate.getCol() != ROW_NUMBER_COLUMN
                    && coordinate.getCol() != DESCRIPTION_COLUMN ) {

                if ( getCellUpdateManager( coordinate ).update() ) {
                    checks.addAll( cache.getRuleInspector( coordinate.getRow() ).getChecks() );
                }

            }
        }

        if ( !checks.isEmpty() ) {
            checkRunner.addChecks( checks );
        }

        return !checks.isEmpty();
    }

    private CellUpdateManager getCellUpdateManager( final Coordinate coordinate ) {
        final BaseColumn baseColumn = model.getExpandedColumns()
                .get( coordinate.getCol() );

        if ( isConditionColumnWithSpecialOperator( baseColumn ) ) {
            return new NullEqualityOperatorCellUpdateManager( index,
                                                              model,
                                                              coordinate );
        } else {
            return new RegularCellUpdateManager( index,
                                                 model,
                                                 coordinate );
        }
    }

    private boolean isConditionColumnWithSpecialOperator( final BaseColumn baseColumn ) {
        return baseColumn instanceof ConditionCol52
                &&
                NullEqualityOperator.contains( ( (ConditionCol52) baseColumn ).getOperator() ) ;
    }

}

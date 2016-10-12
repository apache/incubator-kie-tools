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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.CellBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.ColumnBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.RuleBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.services.verifier.api.client.cache.RuleInspectorCache;
import org.drools.workbench.services.verifier.api.client.cache.util.HasIndex;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.Fields;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.api.client.index.matchers.UUIDMatcher;
import org.uberfire.commons.validation.PortablePreconditions;

public class DtableRuleInspectorCache
        extends RuleInspectorCache {

    private final ColumnUtilities utils;
    private final GuidedDecisionTable52 model;
    private final AnalyzerConfiguration configuration;

    public DtableRuleInspectorCache( final ColumnUtilities utils,
                                     final GuidedDecisionTable52 model,
                                     final Index index,
                                     final AnalyzerConfiguration configuration ) {
        super( PortablePreconditions.checkNotNull( "index",
                                                   index ),
               PortablePreconditions.checkNotNull( "configuration",
                                                   configuration ) );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.utils = PortablePreconditions.checkNotNull( "utils",
                                                         utils );
        this.configuration = configuration;

        reset();
    }

    public void newColumn( final int columnIndex ) {

        index.columns.add( new ColumnBuilder( model,
                                              model.getExpandedColumns()
                                                      .get( columnIndex ),
                                              configuration ).build() );

        int rowIndex = 0;

        for ( final List<DTCellValue52> row : model.getData() ) {
            final BaseColumn baseColumn = model.getExpandedColumns()
                    .get( columnIndex );

            final Rule rule = getRule( rowIndex );

            new CellBuilder( index,
                             model,
                             columnIndex,
                             utils,
                             baseColumn,
                             configuration ).build( rule,
                                                    row );

            rowIndex++;
        }

    }

    public void deleteColumns( final int firstColumnIndex,
                               final int numberOfColumns ) {

        final Collection<Column> all = index.columns
                .where( HasIndex.index()
                                .is( firstColumnIndex ) )
                .select()
                .all();

        final Fields.FieldSelector fieldSelector =
                index.rules
                        .where( UUIDMatcher.uuid()
                                        .any() )
                        .select()
                        .patterns()
                        .where( UUIDMatcher.uuid()
                                        .any() )
                        .select()
                        .fields()
                        .where( UUIDMatcher.uuid()
                                        .any() )
                        .select();


        final ArrayList<Action> actions = new ArrayList<Action>();
        final ArrayList<Condition> conditions = new ArrayList<Condition>();

        for ( final Field field : fieldSelector.all() ) {
            for ( final Column column : all ) {
                final Collection<Action> all1 = field.getActions()
                        .where( Action.columnUUID()
                                        .is( column.getUuidKey() ) )
                        .select()
                        .all();
                final Collection<Condition> all2 = field.getConditions()
                        .where( Condition.columnUUID()
                                        .is( column.getUuidKey() ) )
                        .select()
                        .all();
                actions.addAll( all1 );
                conditions.addAll( all2 );
            }
        }

        for ( final Action action : actions ) {
            action.getUuidKey()
                    .retract();
        }

        for ( final Condition condition : conditions ) {
            condition.getUuidKey()
                    .retract();
        }

        for ( final Column column : all ) {
            column.getUuidKey()
                    .retract();
        }

        reset();
    }

    @Override
    protected Rule makeRule( int index ) {
        return new RuleBuilder( this.index,
                                model,
                                index,
                                model.getData()
                                        .get( index ),
                                utils,
                                configuration ).build();
    }
}
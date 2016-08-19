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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.CellBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ColumnBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Fields;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Index;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.RuleBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatcher;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

public class RuleInspectorCache {

    private final Map<Rule, RuleInspector> ruleInspectors = new HashMap<>();

    private final Index           index;

    private final ColumnUtilities utils;
    private final GuidedDecisionTable52 model;

    private CheckManager checkManager = new CheckManager();

    public RuleInspectorCache( final ColumnUtilities utils,
                               final GuidedDecisionTable52 model,
                               final Index index ) {
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.utils = PortablePreconditions.checkNotNull( "utils", utils );
        this.index = PortablePreconditions.checkNotNull( "index", index );

        reset();
    }

    public void newColumn( final int columnIndex ) {

        index.columns.add( new ColumnBuilder( model,
                                              model.getExpandedColumns().get( columnIndex ) ).build() );

        int rowIndex = 0;

        for ( final List<DTCellValue52> row : model.getData() ) {
            final BaseColumn baseColumn = model.getExpandedColumns().get( columnIndex );

            final Rule rule = getRule( rowIndex );

            new CellBuilder( index,
                             model,
                             columnIndex,
                             utils,
                             baseColumn ).build( rule,
                                                 row );

            rowIndex++;
        }

        reset();
    }

    public void deleteColumns( final int firstColumnIndex,
                               final int numberOfColumns ) {

        final Collection<Column> all = index.columns
                .where( HasIndex.index().is( firstColumnIndex ) )
                .select().all();

        final Fields.FieldSelector fieldSelector =
                index.rules
                        .where( UUIDMatcher.uuid().any() )
                        .select().patterns()
                        .where( UUIDMatcher.uuid().any() )
                        .select().fields()
                        .where( UUIDMatcher.uuid().any() )
                        .select();


        final ArrayList<Action> actions = new ArrayList<Action>();
        final ArrayList<Condition> conditions = new ArrayList<Condition>();

        for ( final Field field : fieldSelector.all() ) {
            for ( final Column column : all ) {
                final Collection<Action> all1 = field.getActions()
                                                     .where( Action.columnUUID().is( column.getUuidKey() ) )
                                                     .select().all();
                final Collection<Condition> all2 = field.getConditions()
                                                        .where( Condition.columnUUID().is( column.getUuidKey() ) )
                                                        .select().all();
                actions.addAll( all1 );
                conditions.addAll( all2 );
            }
        }

        for ( final Action action : actions ) {
            action.getUuidKey().retract();
        }

        for ( final Condition condition : conditions ) {
            condition.getUuidKey().retract();
        }

        for ( final Column column : all ) {
            column.getUuidKey().retract();
        }

        reset();
    }

    public void reset() {
        ruleInspectors.clear();

        for ( final Rule rule : index.rules.where( Rule.uuid().any() ).select().all() ) {
            add( new RuleInspector( rule,
                                    checkManager, this ) );
        }
    }

    public Collection<RuleInspector> all() {
        return ruleInspectors.values();
    }

    public Collection<RuleInspector> all( final Filter filter ) {
        final ArrayList<RuleInspector> result = new ArrayList<RuleInspector>();
        for ( final RuleInspector ruleInspector : all() ) {
            if ( filter.accept( ruleInspector ) ) {
                result.add( ruleInspector );
            }
        }
        return result;
    }

    private void add( final RuleInspector ruleInspector ) {
        ruleInspectors.put( ruleInspector.getRule(),
                            ruleInspector );
    }

    public RuleInspector removeRow( final int rowNumber ) {

        final Rule rule = getRule( rowNumber );

        final RuleInspector remove = ruleInspectors.remove( rule );

        index.rules.remove( rule );

        return remove;
    }

    private Rule getRule( final int rowNumber ) {
        return index.rules.where( HasIndex.index().is( rowNumber ) ).select().first();
    }

    public RuleInspector addRow( final int index ) {
        final Rule rule = new RuleBuilder( this.index,
                                           model,
                                           index,
                                           model.getData().get( index ),
                                           utils ).build();
        this.index.rules.add( rule );

        final RuleInspector ruleInspector = new RuleInspector( rule,
                                                               checkManager,
                                                               this );

        add( ruleInspector );

        return ruleInspector;
    }

    public RuleInspector getRuleInspector( final int row ) {
        return ruleInspectors.get( getRule( row ) );
    }

    public Collection<RuleInspector> allRuleInspectors() {
        return ruleInspectors.values();
    }

    public interface Filter {

        boolean accept( final RuleInspector ruleInspector );

    }
}
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

package org.drools.workbench.services.verifier.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.base.CheckStorage;

public class RuleInspectorCache {

    private final Map<Rule, RuleInspector> ruleInspectors = new HashMap<>();
    protected Index index;
    private CheckStorage checkStorage;
    private AnalyzerConfiguration configuration;

    public RuleInspectorCache( final Index index,
                               final AnalyzerConfiguration configuration ) {
        this.index = PortablePreconditions.checkNotNull( "index",
                                                         index );
        this.checkStorage = new CheckStorage( PortablePreconditions.checkNotNull( "configuration",
                                                                                  configuration ) );
        this.configuration = configuration;
    }

    public void reset() {

        for (final RuleInspector ruleInspector : ruleInspectors.values() ) {
            ruleInspector.clearChecks();
        }

        ruleInspectors.clear();

        for ( final Rule rule : index.getRules()
                .where( Rule.uuid()
                                .any() )
                .select()
                .all() ) {
            add( new RuleInspector( rule,
                                    checkStorage,
                                    this,
                                    configuration ) );
        }
    }

    public void newColumn( final Column column ) {
        index.getColumns()
                .add( column );
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

        index.getRules()
                .remove( rule );

        return remove;
    }

    public Rule getRule( final int rowNumber ) {
        return index.getRules()
                .where( Rule.index()
                                .is( rowNumber ) )
                .select()
                .first();
    }

    public void deleteColumns( final int firstColumnIndex ) {
        final Collection<Column> all = index.getColumns()
                .where( Column.index()
                                .is( firstColumnIndex ) )
                .select()
                .all();

        final Fields.FieldSelector fieldSelector =
                index.getRules()
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

    public RuleInspector addRule( final Rule rule ) {
        this.index.getRules()
                .add( rule );

        final RuleInspector ruleInspector = new RuleInspector( rule,
                                                               checkStorage,
                                                               this,
                                                               configuration );

        add( ruleInspector );

        return ruleInspector;
    }

    public RuleInspector getRuleInspector( final int row ) {
        return ruleInspectors.get( getRule( (int) row ) );
    }

    public AnalyzerConfiguration getConfiguration() {
        return configuration;
    }

    public interface Filter {

        boolean accept( final RuleInspector ruleInspector );

    }
}
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

package org.drools.workbench.services.verifier.api.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.services.verifier.api.client.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.api.client.checks.base.CheckManager;
import org.drools.workbench.services.verifier.api.client.cache.util.HasIndex;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Rule;

public abstract class RuleInspectorCache {

    private final Map<Rule, RuleInspector> ruleInspectors = new HashMap<>();

    private CheckManager checkManager;

    protected final Index index;

    private AnalyzerConfiguration configuration;

    public RuleInspectorCache( final Index index,
                               final AnalyzerConfiguration configuration ) {
        this.index = index;
        this.checkManager=new CheckManager( configuration );
        this.configuration = configuration;
    }

    public void reset() {
        ruleInspectors.clear();

        for ( final Rule rule : index.rules.where( Rule.uuid().any() ).select().all() ) {
            add( new RuleInspector( rule,
                                    checkManager,
                                    this,
                                    configuration ) );
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

        final Rule rule = getRule( (int) rowNumber );

        final RuleInspector remove = ruleInspectors.remove( rule );

        index.rules.remove( rule );

        return remove;
    }

    protected Rule getRule( final int rowNumber ) {
        return index.rules.where( HasIndex.index().is( rowNumber ) ).select().first();
    }

    public RuleInspector addRow( final int index ) {
        final Rule rule = makeRule( index );
        this.index.rules.add( rule );

        final RuleInspector ruleInspector = new RuleInspector( rule,
                                                               checkManager,
                                                               this,
                                                               configuration );

        add( ruleInspector );

        return ruleInspector;
    }

    protected abstract Rule makeRule( int index ) ;

    public RuleInspector getRuleInspector( final int row ) {
        return ruleInspectors.get( getRule( (int) row ) );
    }

    public Collection<RuleInspector> allRuleInspectors() {
        return ruleInspectors.values();
    }

    public interface Filter {

        boolean accept( final RuleInspector ruleInspector );

    }

    public AnalyzerConfiguration getConfiguration() {
        return configuration;
    }
}
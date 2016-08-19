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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ConditionsInspectorMultiMap;

/**
 * This class is for debugging purposes. It is way easier to just dump the inspector into a string than compare the
 * values in the super dev mode debugger.
 */
public class RuleInspectorDumper {

    private StringBuilder dump = new StringBuilder();

    private RuleInspector ruleInspector;

    public RuleInspectorDumper( final RuleInspector ruleInspector ) {
        this.ruleInspector = ruleInspector;
    }

    public String dump() {

        dump.append( "Rule: " );
        dump.append( ruleInspector.getRowIndex() );
        dump.append( "\n" );
        dump.append( "\n" );


        dumpPatterns();

        dumpConditions();

        return dump.toString();
    }

    private void dumpPatterns() {
        dump.append( "Patterns{\n" );
        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            dump.append( "Pattern{\n" );
            dump.append( patternInspector.getPattern().getName() );
            dump.append( "\n" );
            dump.append( "Conditions{\n" );
            dumpCondition( patternInspector.getConditionsInspector() );
            dump.append( "}\n" );
        }
        dump.append( "}\n" );
    }

    private void dumpConditions() {
        dump.append( "Conditions{\n" );
        for ( final ConditionsInspectorMultiMap conditionsInspectorMultiMap : ruleInspector.getConditionsInspectors() ) {
            dumpCondition( conditionsInspectorMultiMap );
        }
        dump.append( "}\n" );
    }

    private void dumpCondition( final ConditionsInspectorMultiMap conditionsInspectorMultiMap ) {
        for ( final ConditionInspector conditionInspector : conditionsInspectorMultiMap.allValues() ) {
            dump.append( "Condition{\n" );
            dump.append( conditionInspector.toHumanReadableString() );
            dump.append( "}\n" );
        }
    }
}

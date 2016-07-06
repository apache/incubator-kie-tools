/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.InspectorList;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;

public abstract class OneToManyCheck
        extends SingleCheck {

    private RuleInspectorCache.Filter filter;

    private final InspectorList<RuleInspector> ruleInspectors = new InspectorList<>();

    public OneToManyCheck( final RuleInspector ruleInspector,
                           final RuleInspectorCache.Filter filter ) {
        super( ruleInspector );
        this.filter = filter;
    }

    public OneToManyCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    protected boolean thereIsAtLeastOneRow() {
        return getOtherRows().size() >= 1;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public InspectorList<RuleInspector> getOtherRows() {
        ruleInspectors.clear();

        ruleInspectors.addAll( ruleInspector.getCache().all( filter ) );

        return ruleInspectors;
    }
}

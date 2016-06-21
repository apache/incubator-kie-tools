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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;

public abstract class PairCheck
        extends CheckBase {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;

    public PairCheck( final RuleInspector ruleInspector,
                      final RuleInspector other ) {
        this.ruleInspector = ruleInspector;
        this.other = other;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public RuleInspector getOther() {
        return other;
    }

    @Override
    public boolean equals( Object other ) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof PairCheck) ) {
            return false;
        }

        final PairCheck pairCheck = ( PairCheck ) other;

        if ( getClass().equals( other.getClass() ) ) {
            return ruleInspector.getRowIndex() == pairCheck.ruleInspector.getRowIndex()
                    && this.other.getRowIndex() == pairCheck.other.getRowIndex();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = ruleInspector != null ? ruleInspector.hashCode() : 0;
        result = 31 * result + getClass().getCanonicalName().hashCode();
        result = 31 * result + ( other != null ? other.hashCode() : 0 );
        return result;
    }
}

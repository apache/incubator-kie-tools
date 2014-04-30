/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.services.refactoring.model.index;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

public class Rule implements IndexElementsGenerator {

    private ValueRuleIndexTerm ruleTerm;
    private ValueRuleIndexTerm parentRuleTerm;

    public Rule( final ValueRuleIndexTerm rule ) {
        this( rule,
              new ValueRuleIndexTerm( null ) );
    }

    public Rule( final ValueRuleIndexTerm ruleTerm,
                 final ValueRuleIndexTerm parentRuleTerm ) {
        this.ruleTerm = PortablePreconditions.checkNotNull( "ruleTerm",
                                                            ruleTerm );
        this.parentRuleTerm = parentRuleTerm;
    }

    public ValueRuleIndexTerm getRule() {
        return ruleTerm;
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();
        indexElements.add( new Pair<String, String>( ruleTerm.getTerm(),
                                                     ruleTerm.getValue() ) );
        if ( parentRuleTerm != null ) {
            indexElements.add( new Pair<String, String>( parentRuleTerm.getTerm(),
                                                         parentRuleTerm.getValue() ) );
        }
        return indexElements;
    }

}

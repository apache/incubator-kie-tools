/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.Set;

import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

class TermsCheck {

    static void checkTermsMatch( final Set<ValueIndexTerm> queryTerms,
                                 final Set<IndexTerm> namedQueryTerms ) {
        for (IndexTerm term : namedQueryTerms) {
            if ( !checkValueTermsContainsRequiredTerm( queryTerms,
                                                       term ) ) {
                throw new IllegalArgumentException( "Expected IndexTerm '" + term.getTerm() + "' was not found." );
            }
        }

        //Validate provided terms against those required for the named query
        for (ValueIndexTerm term : queryTerms) {
            if ( !requiredTermsContainsValueTerm( namedQueryTerms,
                                                  term ) ) {
                //log.warning - term will not be used
            }
        }
    }

    static boolean checkValueTermsContainsRequiredTerm( final Set<ValueIndexTerm> providedTerms,
                                                        final IndexTerm requiredTerm ) {
        for (ValueIndexTerm valueTerm : providedTerms) {
            if ( valueTerm.getTerm().equals( requiredTerm.getTerm() ) ) {
                return true;
            }
        }
        return false;
    }

    static boolean requiredTermsContainsValueTerm( final Set<IndexTerm> requiredTerms,
                                                   final ValueIndexTerm providedTerm ) {
        for (IndexTerm valueTerm : requiredTerms) {
            if ( valueTerm.getTerm().equals( providedTerm.getTerm() ) ) {
                return true;
            }
        }
        return false;
    }

}

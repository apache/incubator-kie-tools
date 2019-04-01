/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.util.Set;
import java.util.function.Predicate;

import org.apache.lucene.search.Query;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.MultipleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.QueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

/**
 *
 */
public abstract class AbstractFindQuery {

    protected Query buildFromMultipleTerms(Set<ValueIndexTerm> terms) {
        final QueryBuilder builder = new MultipleTermQueryBuilder();
        for (ValueIndexTerm term : terms) {
            builder.addTerm(term);
        }
        return builder.build();
    }

    protected Query buildFromSingleTerm(Set<ValueIndexTerm> terms) {
        return buildFromSingleTerm(terms.iterator().next());
    }

    protected Query buildFromSingleTerm(ValueIndexTerm term) {
        final QueryBuilder builder = new SingleTermQueryBuilder(term);
        return builder.build();
    }

    protected void checkNotNullAndNotEmpty(Set<ValueIndexTerm> terms) {
        PortablePreconditions.checkNotNull("terms",
                                           terms);
        if (terms.isEmpty()) {
            throw new IllegalArgumentException("At least 1 term must be submitted when querying referenced resources");
        }
    }

    /**
     * Checks<ol>
     * <li>Whether all terms are valid or not (see parameter <code>validTermTests</code>)</li>
     * <li>Whether the number of terms being submitted is valid</li>
     *
     * @param queryTerms        The terms being validated
     * @param queryName         The name of the query
     * @param requiredTermNames An array of {@link String} where each non-null value indicates that the term is required
     * @param validTermTests    {@link Predicate}s to test the terms
     */
    @SafeVarargs
    protected final void checkInvalidAndRequiredTerms(
            Set<ValueIndexTerm> queryTerms,
            String queryName,
            String[] requiredTermNames,
            Predicate<ValueIndexTerm>... validTermTests) {

        // check invalid
        int found[] = new int[validTermTests.length];
        TERMS:
        for (ValueIndexTerm term : queryTerms) {
            for (int i = 0; i < validTermTests.length; ++i) {
                if (validTermTests[i].test(term)) {
                    found[i]++;
                    continue TERMS;
                }
            }
            throw new IllegalArgumentException("Index term '" + term.getTerm() + "' can not be used with the " + queryName);
        }

        // check size
        if (queryTerms.size() > validTermTests.length) {
            throw new IllegalArgumentException("More terms submitted [" + queryTerms.size() + "] than can be accepted [" + validTermTests.length + "]");
        }

        // check duplicates
        for (int i = 0; i < found.length; ++i) {
            if (found[i] > 1) {
                throw new IllegalArgumentException("Duplicate terms are not accepted by the " + queryName);
            }
        }

        // check required
        for (int i = 0; i < requiredTermNames.length; ++i) {
            if (requiredTermNames[i] != null && found[i] == 0) {
                throw new IllegalArgumentException("Expected '" + requiredTermNames[i] + "' term was not found.");
            }
        }
    }

    protected void checkTermsSize(int expected,
                                  Set<ValueIndexTerm> terms) {
        if (terms.size() > expected) {
            throw new IllegalArgumentException("Expected " + expected + " terms, not " + terms.size() + " terms.");
        }
    }
}

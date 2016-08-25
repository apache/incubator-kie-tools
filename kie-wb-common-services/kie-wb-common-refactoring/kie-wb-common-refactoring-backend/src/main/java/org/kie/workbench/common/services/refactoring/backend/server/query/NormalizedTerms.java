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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.refactoring.model.index.terms.CompositeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

/**
 * In some cases it's important that there is exactly one and only one {@link ValueIndexTerm} implementation type instance
 * in a {@link Set} of {@link ValueIndexTerm} instances.
 * </p>
 * This class "normalizes" a set in order to acheive that.
 */
public class NormalizedTerms {

    private final Map<String, ValueIndexTerm> normalizedTerms = new HashMap<String, ValueIndexTerm>();

    public NormalizedTerms( final Set<ValueIndexTerm> terms,
                            final String... termNames ) {

        checkSize( terms, termNames );

        addTerms( terms );

        checkTermsExist( termNames );
    }

    private void checkSize( final Set<ValueIndexTerm> terms, String[] termNames ) {
        if ( terms.size() != termNames.length ) {
            throw new IllegalArgumentException( getExceptionMessage( termNames ) );
        }
    }

    private void addTerms( final Set<ValueIndexTerm> terms ) {
        for (ValueIndexTerm term : terms) {
            if( term instanceof CompositeIndexTerm ) {
                normalizedTerms.put( ((CompositeIndexTerm) term).getTermBase(), term );
            } else {
                normalizedTerms.put( term.getTerm(), term );
            }
        }
    }

    private void checkTermsExist( final String[] termNames ) {
        for (String termName : termNames) {
            checkTermExists( termName );
        }
    }

    private void checkTermExists( final String termName ) {
        if ( !normalizedTerms.containsKey( termName ) ) {
            throw new IllegalArgumentException( "Required term has not been provided. Required '" + termName + "'." );
        }
    }

    public ValueIndexTerm get( final String term ) {
        checkTermExists( term );
        return normalizedTerms.get( term );
    }

    private String getExceptionMessage( final String[] termNames ) {
        StringBuilder message = new StringBuilder( "Required terms have not been provided. Require '" );

        Iterator<String> iterator = Arrays.asList( termNames ).iterator();
        while (iterator.hasNext()) {
            message.append( iterator.next() );
            if ( iterator.hasNext() ) {
                message.append( ", " );
            }
        }

        message.append( "'." );

        return message.toString();
    }

}

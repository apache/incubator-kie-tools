/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.backend.server.query.standard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.BasicQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

@ApplicationScoped
public class FindJavaTypeQuery implements NamedQuery {

    @Inject
    private DefaultResponseBuilder responseBuilder;

    @Override public String getName() {
        return "FindJavaTypeQuery";
    }

    /**
     * Required term:
     *      JavaTypeIndexTerm
     *
     * Other allowed terms:
     *      JavaTypeNameIndexTerm
     *      JavaTypeParentIndexTerm
     *      JavaTypeInterfaceIndexTerm
     *      FieldNameIndexTerm
     *      FieldTypeTypeIndexTerm
     *
     *
     * @return
     */
    @Override
    public Set<IndexTerm> getTerms() {
        Set<IndexTerm> terms = new HashSet<IndexTerm>();
        //only JavaTypeIndexTerm is required.
        terms.add( new JavaTypeIndexTerm() );
        /*
        terms.add( new JavaTypeParentIndexTerm() );
        terms.add( new JavaTypeInterfaceIndexTerm() );
        terms.add( new FieldNameIndexTerm() );
        terms.add( new FieldTypeIndexTerm() );
        terms.add( new TypeIndexTerm() );
        */
        return terms;
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms,
                          final boolean useWildcards ) {

        PortablePreconditions.checkNotNull( "terms", terms );

        if ( terms.size() == 0 ) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + JavaTypeIndexTerm.TERM + "'." );
        }

        final Map<String, ValueIndexTerm> normalizedTerms = normalizeTerms( terms );
        final ValueIndexTerm javaTypeTerm = normalizedTerms.get( JavaTypeIndexTerm.TERM );
        if ( javaTypeTerm == null ) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + JavaTypeIndexTerm.TERM + "'." );
        }

        //This is the guts of the NamedQuery. It builds a Lucene Query using the terms provided.
        //QueryBuilder is a simple class I wrote to build a Lucene Query. It is very restricted and has limited re-use capabilities beyond what I wrote it for
        final BasicQueryBuilder builder = new BasicQueryBuilder( );
        builder.useWildcards();
        for ( ValueIndexTerm valueIndexTerm : normalizedTerms.values() ) {
            builder.addTerm( valueIndexTerm );
        }
        //Return a built Query
        return builder.build();
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    private Map<String, ValueIndexTerm> normalizeTerms( final Set<ValueIndexTerm> terms ) {
        final Map<String, ValueIndexTerm> normalizedTerms = new HashMap<String, ValueIndexTerm>();
        for ( ValueIndexTerm term : terms ) {
            normalizedTerms.put( term.getTerm(),
                    term );
        }
        return normalizedTerms;
    }
}

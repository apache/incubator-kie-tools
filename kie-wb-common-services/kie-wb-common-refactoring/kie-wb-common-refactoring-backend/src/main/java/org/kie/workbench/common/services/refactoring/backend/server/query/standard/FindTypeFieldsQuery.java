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
package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.NormalizedTerms;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.BasicQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.FieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.TypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

@ApplicationScoped
public class FindTypeFieldsQuery implements NamedQuery {

    public static String FIND_TYPE_FIELDS_QUERY = "FindTypeFieldsQuery";

    @Inject
    private DefaultResponseBuilder responseBuilder;

    @Override
    public String getName() {
        return FIND_TYPE_FIELDS_QUERY;
    }

    @Override
    public Set<IndexTerm> getTerms() {
        return new HashSet<IndexTerm>() {{
            add( new TypeIndexTerm() );
            add( new FieldIndexTerm() );
        }};
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms,
                          final boolean useWildcards ) {
        PortablePreconditions.checkNotNull( "terms",
                                            terms );

        final NormalizedTerms normalizedTerms = new NormalizedTerms( terms,
                                                                     TypeIndexTerm.TERM,
                                                                     FieldIndexTerm.TERM );

        final BasicQueryBuilder builder = new BasicQueryBuilder(useWildcards);
        builder.addTerm( normalizedTerms.get( TypeIndexTerm.TERM ) )
                .addTerm( normalizedTerms.get( FieldIndexTerm.TERM ) );
        return builder.build();
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

}

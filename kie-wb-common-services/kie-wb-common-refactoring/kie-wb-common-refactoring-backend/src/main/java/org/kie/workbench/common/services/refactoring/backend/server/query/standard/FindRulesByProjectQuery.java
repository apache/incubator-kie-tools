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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.kie.uberfire.metadata.model.KObject;
import org.kie.uberfire.metadata.model.KProperty;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.uberfire.paging.PageResponse;

import static org.apache.lucene.search.BooleanClause.Occur.*;

@ApplicationScoped
public class FindRulesByProjectQuery implements NamedQuery {

    private RuleNameResponseBuilder responseBuilder = new RuleNameResponseBuilder();

    @Override
    public String getName() {
        return "FindRulesByProjectQuery";
    }

    @Override
    public Set<IndexTerm> getTerms() {
        return new HashSet<IndexTerm>() {{
            add( new PackageNameIndexTerm() );
            add( new ProjectRootPathIndexTerm() );
        }};
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms,
                          final boolean useWildcards ) {
        PortablePreconditions.checkNotNull( "terms",
                                            terms );

        if ( terms.size() != 2 ) {
            throw new IllegalArgumentException( "Required terms have not been provided. Require '" + PackageNameIndexTerm.TERM + "' and '" + ProjectRootPathIndexTerm.TERM + "'." );
        }
        final Map<String, ValueIndexTerm> normalizedTerms = normalizeTerms( terms );
        final ValueIndexTerm packageNameValueTerm = normalizedTerms.get( PackageNameIndexTerm.TERM );
        final ValueIndexTerm projectPathValueTerm = normalizedTerms.get( ProjectRootPathIndexTerm.TERM );
        if ( packageNameValueTerm == null || projectPathValueTerm == null ) {
            throw new IllegalArgumentException( "Required terms have not been provided. Require '" + PackageNameIndexTerm.TERM + "' and '" + ProjectRootPathIndexTerm.TERM + "'." );
        }

        final BooleanQuery query = new BooleanQuery();
        if ( !useWildcards ) {
            query.add( new TermQuery( new Term( packageNameValueTerm.getTerm(),
                                                packageNameValueTerm.getValue().toLowerCase() ) ),
                       MUST );
            query.add( new TermQuery( new Term( projectPathValueTerm.getTerm(),
                                                projectPathValueTerm.getValue().toLowerCase() ) ),
                       MUST );
        } else {
            query.add( new WildcardQuery( new Term( packageNameValueTerm.getTerm(),
                                                    packageNameValueTerm.getValue().toLowerCase() ) ),
                       MUST );
            query.add( new WildcardQuery( new Term( projectPathValueTerm.getTerm(),
                                                    projectPathValueTerm.getValue().toLowerCase() ) ),
                       MUST );
        }
        query.add( new WildcardQuery( new Term( RuleIndexTerm.TERM,
                                                "*" ) ),
                   MUST );

        return query;
    }

    private Map<String, ValueIndexTerm> normalizeTerms( final Set<ValueIndexTerm> terms ) {
        final Map<String, ValueIndexTerm> normalizedTerms = new HashMap<String, ValueIndexTerm>();
        for ( ValueIndexTerm term : terms ) {
            normalizedTerms.put( term.getTerm(),
                                 term );
        }
        return normalizedTerms;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    /**
     * Custom ResponseBuilder to return a String Rule Names
     */
    private static class RuleNameResponseBuilder implements ResponseBuilder {

        @Override
        public PageResponse<RefactoringPageRow> buildResponse( final int pageSize,
                                                               final int startRow,
                                                               final List<KObject> kObjects ) {
            final int hits = kObjects.size();
            final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
            final List<RefactoringPageRow> result = buildResponse( kObjects );
            response.setTotalRowSize( hits );
            response.setPageRowList( result );
            response.setTotalRowSizeExact( true );
            response.setStartRowIndex( startRow );
            response.setLastPage( ( pageSize * startRow + 2 ) >= hits );

            return response;
        }

        @Override
        public List<RefactoringPageRow> buildResponse( final List<KObject> kObjects ) {
            //Both "child" rule and "parent" rule (when one extends another) are stored
            //in the index. We therefore need to build a set of unique Rule Names
            final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>( kObjects.size() );
            final Set<String> uniqueRuleNames = new HashSet<String>();
            for ( final KObject kObject : kObjects ) {
                final Set<String> ruleNames = getRuleNamesFromKObject( kObject );
                uniqueRuleNames.addAll( ruleNames );
            }

            for ( String ruleName : uniqueRuleNames ) {
                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue( ruleName );
                result.add( row );
            }

            return result;
        }

        private Set<String> getRuleNamesFromKObject( final KObject kObject ) {
            //Some resources (e.g. Decision Tables etc) contain multiple rule names so add them all
            final Set<String> ruleNames = new HashSet<String>();
            if ( kObject == null ) {
                return ruleNames;
            }
            for ( KProperty property : kObject.getProperties() ) {
                if ( property.getName().equals( RuleIndexTerm.TERM ) ) {
                    ruleNames.add( property.getValue().toString() );
                }
            }
            return ruleNames;
        }

    }

}

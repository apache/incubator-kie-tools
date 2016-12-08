/*
 * Copyright 2016  Red Hat, Inc. and/or its affiliates.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class FindDataTypesQuery extends AbstractFindQuery implements NamedQuery {

    private DataTypesResponseBuilder responseBuilder = new DataTypesResponseBuilder();

    public static final String NAME = FindDataTypesQuery.class.getSimpleName();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms ) {

        // terms check is done in validateTerms
        ValueIndexTerm term = terms.iterator().next();

        return buildFromSingleTerm(term);
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery#validateTerms(java.util.Set)
     */
    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        checkNotNullAndNotEmpty(queryTerms);

        checkInvalidAndRequiredTerms(queryTerms, NAME,
                new String [] {
                        ResourceType.JAVA.toString()
                },
                (t) -> (t.getTerm().equals(ResourceType.JAVA.toString()))
        );
    }

    private static class DataTypesResponseBuilder implements ResponseBuilder {

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
            final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>( kObjects.size() );
            final Set<String> uniqueDataTypeNames = new HashSet<>();
            for ( final KObject kObject : kObjects ) {
                final Set<String> dataTypeNames = getDataTypeNamesFromKObject(kObject);
                uniqueDataTypeNames.addAll(dataTypeNames);
            }

            for( String dataTypeName : uniqueDataTypeNames ) {
                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue(dataTypeName);
                result.add(row);
            }
            return result;
        }

        private Set<String> getDataTypeNamesFromKObject( final KObject kObject ) {
            final Set<String> dataTypeNames = new HashSet<>();
            if ( kObject == null ) {
                return dataTypeNames;
            }
            for ( KProperty property : kObject.getProperties() ) {
                if ( property.getName().equals( ResourceType.JAVA.toString() ) ) {
                    dataTypeNames.add(property.getValue().toString());
                }
            }
            return dataTypeNames;
        }

    }

}

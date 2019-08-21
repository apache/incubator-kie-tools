/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SearchEmptyQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class FindScorecardNamesQuery extends AbstractFindQuery implements NamedQuery {

    public static final String NAME = FindScorecardNamesQuery.class.getSimpleName();
    private static final ValueSharedPartIndexTerm scorecardTerm = new ValueSharedPartIndexTerm("not-used", PartType.SCORECARD_MODEL_NAME);
    private ScorecardResponseBuilder responseBuilder = new ScorecardResponseBuilder();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    @Override
    public Query toQuery(Set<ValueIndexTerm> terms) {
        final SearchEmptyQueryBuilder queryBuilder = new SearchEmptyQueryBuilder();

        for (ValueIndexTerm term : terms) {
            if (term instanceof ValueSharedPartIndexTerm) {
                queryBuilder.addTerm(term);
            } else if (term instanceof ValueModuleRootPathIndexTerm) {
                queryBuilder.addTerm(term);
            } else if (term instanceof ValuePackageNameIndexTerm) {
                queryBuilder.addTerm(term);
            }
        }

        return queryBuilder.build();
    }
    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        checkNotNullAndNotEmpty(queryTerms);

        checkInvalidAndRequiredTerms(queryTerms,
                                     NAME,
                                     new String[]{
                                             null, null,  // not required
                                             scorecardTerm.getTerm()
                                     },
                                     (t) -> (t instanceof ValueModuleRootPathIndexTerm),
                                     (t) -> (t instanceof ValuePackageNameIndexTerm),
                                     (t) -> (Objects.equals(t.getTerm(), scorecardTerm.getTerm())));
    }

    private static class ScorecardResponseBuilder implements ResponseBuilder {

        @Override
        public PageResponse<RefactoringPageRow> buildResponse(final int pageSize,
                                                              final int startRow,
                                                              final List<KObject> kObjects) {
            final int hits = kObjects.size();
            final PageResponse<RefactoringPageRow> response = new PageResponse();
            final List<RefactoringPageRow> result = buildResponse(kObjects);
            response.setTotalRowSize(hits);
            response.setPageRowList(result);
            response.setTotalRowSizeExact(true);
            response.setStartRowIndex(startRow);
            response.setLastPage((pageSize * startRow + 2) >= hits);

            return response;
        }

        @Override
        public List<RefactoringPageRow> buildResponse(final List<KObject> kObjects) {
            final List<RefactoringPageRow> result = new ArrayList(kObjects.size());

            for (final KObject kObject : kObjects) {
                for (String name : getScorecardModelNamesNamesFromKObject(kObject)) {
                    RefactoringPageRow row = new RefactoringStringPageRow();
                    row.setValue(name);
                    result.add(row);
                }
            }

            return result;
        }

        private Set<String> getScorecardModelNamesNamesFromKObject(final KObject kObject) {
            Set<String> result = new HashSet<>();
            if (kObject == null) {
                return result;
            }
            for (KProperty property : kObject.getProperties()) {

                if (Objects.equals("shared:scorecardModelName", property.getName())) {
                    result.add(property.getValue().toString());
                }
            }

            return result;
        }
    }
}


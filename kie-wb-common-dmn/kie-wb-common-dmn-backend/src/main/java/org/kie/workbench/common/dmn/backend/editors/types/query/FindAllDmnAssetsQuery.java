/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.backend.editors.types.query;

import java.util.Set;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.FileDetailsResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.AbstractFindQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

import static org.apache.lucene.search.SortField.Type.STRING;
import static org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory.FILE_NAME_FIELD_SORTED;

@ApplicationScoped
public class FindAllDmnAssetsQuery extends AbstractFindQuery implements NamedQuery {

    public static String NAME = "FindAllDmnAssetsQuery";

    private final FileDetailsResponseBuilder responseBuilder;

    @Inject
    public FindAllDmnAssetsQuery(final FileDetailsResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Query toQuery(final Set<ValueIndexTerm> terms) {
        checkNotNullAndNotEmpty(terms);
        return buildFromMultipleTerms(terms);
    }

    @Override
    public Sort getSortOrder() {
        return new Sort(new SortField(FILE_NAME_FIELD_SORTED, STRING));
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    @Override
    public void validateTerms(final Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        checkTerms(queryTerms, NAME, requiredTermNames(), repositoryRootIndexTermPredicate(), fileExtensionIndexTermPredicate());
        checkTermsSize(2, queryTerms);
    }

    void checkTerms(final Set<ValueIndexTerm> queryTerms,
                    final String name,
                    final String[] requiredTermNames,
                    final Predicate<ValueIndexTerm> repositoryRootIndexTermPredicate,
                    final Predicate<ValueIndexTerm> fileExtensionIndexTermPredicate) {
        checkInvalidAndRequiredTerms(queryTerms, name, requiredTermNames, repositoryRootIndexTermPredicate, fileExtensionIndexTermPredicate);
    }

    Predicate<ValueIndexTerm> fileExtensionIndexTermPredicate() {
        return (t) -> (t instanceof DMNValueFileExtensionIndexTerm);
    }

    Predicate<ValueIndexTerm> repositoryRootIndexTermPredicate() {
        return (t) -> (t instanceof DMNValueRepositoryRootIndexTerm);
    }

    String[] requiredTermNames() {
        return new String[]{
                DMNValueRepositoryRootIndexTerm.TERM,
                DMNValueFileExtensionIndexTerm.TERM,
        };
    }
}

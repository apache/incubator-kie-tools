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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.FileDetailsResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.mockito.Mock;

import static org.apache.lucene.search.SortField.Type.STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.backend.editors.types.query.FindAllDmnAssetsQuery.NAME;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory.FILE_NAME_FIELD_SORTED;

public class FindAllDmnAssetsQueryTest {

    private FindAllDmnAssetsQueryFake query;

    @Mock
    private FileDetailsResponseBuilder responseBuilder;

    @Before
    public void setup() {
        query = spy(new FindAllDmnAssetsQueryFake(responseBuilder));
    }

    @Test
    public void testGetName() {
        assertEquals(NAME, query.getName());
    }

    @Test
    public void testToQuery() {

        final Set<ValueIndexTerm> terms = new HashSet<>();
        final Query expectedResult = mock(Query.class);

        doNothing().when(query).checkNotNullAndNotEmpty(terms);
        doReturn(expectedResult).when(query).buildFromMultipleTerms(terms);

        final Query actualResult = query.toQuery(terms);

        verify(query).checkNotNullAndNotEmpty(terms);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetSortOrder() {

        final Sort expected = new Sort(new SortField(FILE_NAME_FIELD_SORTED, STRING));
        final Sort actual = query.getSortOrder();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetResponseBuilder() {
        assertEquals(responseBuilder, query.getResponseBuilder());
    }

    @Test
    public void testValidateTerms() {

        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        final String[] requiredTermNames = new String[]{};
        final Predicate<ValueIndexTerm> fileExtensionIndexTermPredicate = (t) -> true;
        final Predicate<ValueIndexTerm> repositoryRootIndexTermPredicate = (t) -> true;

        doReturn(requiredTermNames).when(query).requiredTermNames();
        doReturn(fileExtensionIndexTermPredicate).when(query).repositoryRootIndexTermPredicate();
        doReturn(repositoryRootIndexTermPredicate).when(query).fileExtensionIndexTermPredicate();

        query.validateTerms(queryTerms);

        verify(query).checkTermsSize(2, queryTerms);
        verify(query).checkTerms(queryTerms,
                                 NAME,
                                 requiredTermNames,
                                 fileExtensionIndexTermPredicate,
                                 repositoryRootIndexTermPredicate);
    }

    @Test
    public void testFileExtensionIndexTermPredicateWhenItReturnsTrue() {
        assertTrue(query.fileExtensionIndexTermPredicate().test(new DMNValueFileExtensionIndexTerm()));
    }

    @Test
    public void testFileExtensionIndexTermPredicateWhenItReturnsFalse() {
        assertFalse(query.fileExtensionIndexTermPredicate().test(new DMNValueRepositoryRootIndexTerm("")));
    }

    @Test
    public void testRepositoryRootIndexTermPredicateWhenItReturnsTrue() {
        assertTrue(query.repositoryRootIndexTermPredicate().test(new DMNValueRepositoryRootIndexTerm("")));
    }

    @Test
    public void testRepositoryRootIndexTermPredicateWhenItReturnsFalse() {
        assertFalse(query.repositoryRootIndexTermPredicate().test(new DMNValueFileExtensionIndexTerm()));
    }

    @Test
    public void testRequiredTermNames() {

        final String[] expected = {DMNValueRepositoryRootIndexTerm.TERM, DMNValueFileExtensionIndexTerm.TERM,};
        final String[] actual = query.requiredTermNames();

        assertEquals(expected.length, actual.length);
        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    class FindAllDmnAssetsQueryFake extends FindAllDmnAssetsQuery {

        FindAllDmnAssetsQueryFake(final FileDetailsResponseBuilder responseBuilder) {
            super(responseBuilder);
        }

        @Override
        protected void checkTermsSize(final int expected, final Set<ValueIndexTerm> terms) {
            // empty
        }

        @Override
        protected void checkNotNullAndNotEmpty(final Set<ValueIndexTerm> terms) {
            // empty
        }

        @Override
        protected Query buildFromMultipleTerms(final Set<ValueIndexTerm> terms) {
            return null;
        }
    }
}

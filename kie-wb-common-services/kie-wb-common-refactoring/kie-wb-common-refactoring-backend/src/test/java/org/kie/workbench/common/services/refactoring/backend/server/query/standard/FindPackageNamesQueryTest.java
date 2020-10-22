/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FindPackageNamesQueryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testName() {
        assertEquals("FindPackageNamesQuery", new FindPackageNamesQuery().getName());
        assertEquals(FindPackageNamesQuery.NAME, new FindPackageNamesQuery().getName());
    }

    @Test
    public void testResponseBuilder() {
        assertNotNull(new FindPackageNamesQuery().getResponseBuilder());
    }

    @Test
    public void testToQuery() {
        final Query query = new FindPackageNamesQuery().toQuery(new HashSet<>());
        assertEquals("", query.toString());
    }

    @Test
    public void testToQueryValueModuleRootPathIndexTerm() {
        final HashSet<ValueIndexTerm> terms = new HashSet<>();
        terms.add(new ValueModuleRootPathIndexTerm(""));

        final Query query = new FindPackageNamesQuery().toQuery(terms);
        assertEquals("-projectRoot:*", query.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTermsNoTerms() {
        new FindPackageNamesQuery().validateTerms(new HashSet<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTermsNoPackageNameIndexTerm() {
        final HashSet<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new ValueModuleRootPathIndexTerm());
        new FindPackageNamesQuery().validateTerms(queryTerms);
    }

    @Test
    public void testValidateTerms() {
        final HashSet<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new ValuePackageNameIndexTerm("*",
                                                     ValueIndexTerm.TermSearchType.WILDCARD));
        queryTerms.add(new ValueModuleRootPathIndexTerm());
        new FindPackageNamesQuery().validateTerms(queryTerms);

        // Should always pass
    }

    @Test
    public void testToQueryValuePackageNameIndexTerm() {
        final HashSet<ValueIndexTerm> terms = new HashSet<>();
        terms.add(new ValuePackageNameIndexTerm(""));

        final Query query = new FindPackageNamesQuery().toQuery(terms);
        assertEquals("-packageName:*", query.toString());
    }

    @Test
    public void testToQueryValuePackageAnyExistingValueTerm() {
        final HashSet<ValueIndexTerm> terms = new HashSet<>();
        terms.add(new ValuePackageNameIndexTerm("*"));

        final Query query = new FindPackageNamesQuery().toQuery(terms);
        assertEquals("+packageName:*", query.toString());
    }
}
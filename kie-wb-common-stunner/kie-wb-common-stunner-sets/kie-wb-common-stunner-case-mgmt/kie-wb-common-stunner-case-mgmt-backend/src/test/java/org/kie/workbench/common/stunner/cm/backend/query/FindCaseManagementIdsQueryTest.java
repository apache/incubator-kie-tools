/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend.query;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.assertEquals;

public class FindCaseManagementIdsQueryTest {

    private FindCaseManagementIdsQuery tested = new FindCaseManagementIdsQuery();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetProcessIdResourceType() throws Exception {
        assertEquals(tested.getProcessIdResourceType(), ResourceType.BPMN_CM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTerms_noQueryTerms() {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();

        tested.validateTerms(queryTerms);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTerms_inValidQueryTerms() {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new ValueResourceIndexTerm("not-bpmn2-resources",
                                                  ResourceType.JAVA));

        tested.validateTerms(queryTerms);
    }

    @Test
    public void testValidateTerms() {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new ValueResourceIndexTerm("*",
                                                  ResourceType.BPMN_CM,
                                                  ValueIndexTerm.TermSearchType.WILDCARD));

        try {
            tested.validateTerms(queryTerms);
        } catch (IllegalArgumentException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
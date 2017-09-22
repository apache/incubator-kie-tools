/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.dataprovider;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleFlowNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RuleFlowGroupFormProviderTest {

    private final static String ERROR_MSG = PartType.ACTIVATION_GROUP.toString() + "' can not be used";

    @Test
    public void findRuleFlowNamesQueryTermsTest() {
        FindRuleFlowNamesQuery query = new FindRuleFlowNamesQuery();

        Set<ValueIndexTerm> queryTerms = new HashSet<>();
        try {
            query.validateTerms(queryTerms);
            fail("The required rule-flow term is missing, but no exception was thrown.");
        } catch (IllegalArgumentException iae) {
            assertTrue("Incorrect error message: " + iae.getMessage(),
                       iae.getMessage().contains("At least 1 term"));
        }

        queryTerms = new HashSet<>();
        queryTerms.add(new ValueSharedPartIndexTerm("not-rule-flow",
                                                    PartType.ACTIVATION_GROUP));
        try {
            query.validateTerms(queryTerms);
            fail("The required rule-flow term is missing, but no exception was thrown.");
        } catch (IllegalArgumentException iae) {
            assertTrue("Incorrect error message: " + iae.getMessage(),
                       iae.getMessage().contains(ERROR_MSG));
        }

        queryTerms = new HashSet<>();
        queryTerms.add(new ValueSharedPartIndexTerm("not-rule-flow",
                                                    PartType.ACTIVATION_GROUP));
        queryTerms.add(new ValueSharedPartIndexTerm("rule-flow",
                                                    PartType.RULEFLOW_GROUP));
        try {
            query.validateTerms(queryTerms);
            fail("The activation term is not acceptable here, but no exception was thrown.");
        } catch (IllegalArgumentException iae) {
            assertTrue("Incorrect error message: " + iae.getMessage(),
                       iae.getMessage().contains(ERROR_MSG));
        }

        queryTerms = new HashSet<>();
        queryTerms.add(new ValueSharedPartIndexTerm("rule-flow",
                                                    PartType.RULEFLOW_GROUP));
        query.validateTerms(queryTerms);
    }
}

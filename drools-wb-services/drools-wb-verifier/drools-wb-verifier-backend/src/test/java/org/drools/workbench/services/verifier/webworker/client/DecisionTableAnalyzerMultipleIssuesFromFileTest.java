/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.webworker.client;

import java.util.HashSet;

import org.drools.workbench.services.verifier.api.client.index.DataType;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertOnlyContains;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerMultipleIssuesFromFileTest extends AnalyzerUpdateTestBase {

    @Override
    @Before
    public void setUp() throws
            Exception {
        super.setUp();
        analyzerProvider.getFactTypes().add(new FactTypes.FactType("LoanApplication",
                                                                   new HashSet<FactTypes.Field>() {{
                                                                       add(new FactTypes.Field("amount",
                                                                                               DataType.TYPE_NUMERIC_INTEGER));
                                                                       add(new FactTypes.Field("lengthYears",
                                                                                               DataType.TYPE_NUMERIC_INTEGER));
                                                                       add(new FactTypes.Field("approvedRate",
                                                                                               DataType.TYPE_NUMERIC_INTEGER));
                                                                       add(new FactTypes.Field("explanation",
                                                                                               DataType.TYPE_STRING));
                                                                       add(new FactTypes.Field("approved",
                                                                                               DataType.TYPE_BOOLEAN));
                                                                   }}));
    }

    @Test
    public void testMissingRangeAndRedundantRows() throws Exception, UpdateException {

        analyze("missingRangeAndRedundantRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE,
                           REDUNDANT_ROWS);

        assertContains(MISSING_RANGE_TITLE,
                       new HashSet<Integer>() {{
                           add(1);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(MISSING_RANGE_TITLE,
                       new HashSet<Integer>() {{
                           add(2);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(REDUNDANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(2);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingRangeAndSubsumptantRows() throws Exception {
        analyze("missingRangeAndSubsumptantRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE,
                           SUBSUMPTANT_ROWS);

        assertContains(MISSING_RANGE_TITLE,
                       new HashSet<Integer>() {{
                           add(5);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(SUBSUMPTANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(6);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(SUBSUMPTANT_ROWS,
                       new HashSet<Integer>() {{
                           add(2);
                           add(6);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingRangeAndConflictingRows() throws Exception {
        analyze("missingRangeAndConflictingRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE,
                           CONFLICTING_ROWS);

        assertContains(MISSING_RANGE_TITLE,
                       new HashSet<Integer>() {{
                           add(5);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(CONFLICTING_ROWS,
                       new HashSet<Integer>() {{
                           add(3);
                           add(6);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(CONFLICTING_ROWS,
                       new HashSet<Integer>() {{
                           add(4);
                           add(6);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingRangeAndMissingColumns() throws Exception {
        analyze("missingRangeAndMissingColumns.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE,
                           RULE_HAS_NO_ACTION);

        assertContains(MISSING_RANGE_TITLE,
                       new HashSet<Integer>() {{
                           add(5);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(RULE_HAS_NO_ACTION,
                       new HashSet<Integer>() {{
                           add(5);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testSubsumptionAndRedundancy() throws Exception {
        analyze("subsumptionAndRedundancy.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SUBSUMPTANT_ROWS,
                           REDUNDANT_ROWS);

        assertContains(SUBSUMPTANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(2);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(SUBSUMPTANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(3);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(REDUNDANT_ROWS,
                       new HashSet<Integer>() {{
                           add(2);
                           add(3);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testSubsumptionAndConflict() throws Exception {
        analyze("subsumptionAndConflict.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SUBSUMPTANT_ROWS,
                           CONFLICTING_ROWS);

        assertContains(SUBSUMPTANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(2);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(CONFLICTING_ROWS,
                       new HashSet<Integer>() {{
                           add(2);
                           add(4);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testRedundancyAndConflicts() throws Exception {
        analyze("redundancyAndConflicts.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           REDUNDANT_ROWS,
                           CONFLICTING_ROWS);

        assertContains(REDUNDANT_ROWS,
                       new HashSet<Integer>() {{
                           add(1);
                           add(3);
                       }},
                       analyzerProvider.getAnalysisReport());

        assertContains(CONFLICTING_ROWS,
                       new HashSet<Integer>() {{
                           add(2);
                           add(4);
                       }},
                       analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingConditionAndMissingAction() throws Exception {
        analyze("missingConditionAndMissingAction.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           RULE_HAS_NO_RESTRICTIONS_AND_WILL_ALWAYS_FIRE,
                           RULE_HAS_NO_ACTION,
                           SINGLE_HIT_LOST);
    }
}
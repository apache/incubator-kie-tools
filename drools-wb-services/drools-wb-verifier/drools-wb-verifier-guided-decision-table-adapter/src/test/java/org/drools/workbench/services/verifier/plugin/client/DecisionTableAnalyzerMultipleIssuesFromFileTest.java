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

package org.drools.workbench.services.verifier.plugin.client;

import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertOnlyContains;
import static org.junit.Assert.assertTrue;

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
    public void testMissingRangeAndRedundantRows() throws Exception {

        analyze("missingRangeAndRedundantRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RANGE,
                           CheckType.REDUNDANT_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       1);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);
    }

    @Test
    public void testMissingRangeAndSubsumptantRows() throws Exception {
        analyze("missingRangeAndSubsumptantRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RANGE,
                           CheckType.SUBSUMPTANT_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       5);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       6);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       2,
                       6);
    }

    @Test
    public void testMissingRangeAndConflictingRows() throws Exception {
        analyze("missingRangeAndConflictingRows.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RANGE,
                           CheckType.CONFLICTING_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       5);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       3,
                       6);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       4,
                       6);
    }

    @Test
    public void testMissingRangeAndMissingColumns() throws Exception {
        analyze("missingRangeAndMissingColumns.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RANGE,
                           CheckType.MISSING_ACTION);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       5);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_ACTION,
                       Severity.WARNING,
                       5);
    }

    @Test
    public void testSubsumptionAndRedundancy() throws Exception {
        analyze("subsumptionAndRedundancy.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.SUBSUMPTANT_ROWS,
                           CheckType.REDUNDANT_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       3);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       2,
                       3);
    }

    @Test
    public void testSubsumptionAndRedundancyMultipleFields() throws Exception {
        analyze("subsumptionAndRedundancyMultipleFields.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       3);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       2,
                       3);

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.SUBSUMPTANT_ROWS,
                           CheckType.REDUNDANT_ROWS);
    }

    @Test
    public void testSubsumptionAndConflict() throws Exception {
        analyze("subsumptionAndConflict.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.SUBSUMPTANT_ROWS,
                           CheckType.CONFLICTING_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2,
                       4);
    }

    @Test
    public void testSubsumptionAndMissingAction() throws Exception {
        analyze("subsumptionAndMissingAction.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_ACTION,
                       Severity.WARNING,
                       3);

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.SUBSUMPTANT_ROWS,
                           CheckType.MISSING_ACTION);
    }

    @Test
    public void testRedundancyAndConflicts() throws Exception {
        analyze("redundancyAndConflicts.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.REDUNDANT_ROWS,
                           CheckType.CONFLICTING_ROWS);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       3);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2,
                       4);
    }

    @Test
    public void testRedundancyAndConflictsMultipleFields() throws Exception {
        analyze("redundancyAndConflictsMultipleFields.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       1,
                       3);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2,
                       3);

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.REDUNDANT_ROWS,
                           CheckType.CONFLICTING_ROWS);
    }

    @Test
    public void testMissingConditionAndMissingAction() throws Exception {
        analyze("missingConditionAndMissingAction.gdst");

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RESTRICTION,
                           CheckType.MISSING_ACTION,
                           CheckType.SINGLE_HIT_LOST);
    }

    @Test
    public void testGapAnalysis() throws Exception {
        // GUVNOR-2990
        analyze("gapAnalysis.gdst");
        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());
    }

    @Test
    public void testBidimensionalGapAnalysis() throws Exception {
        // GUVNOR-3010
        analyze("gapAnalysis2D.gdst");
        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           CheckType.MISSING_RANGE);
    }

    @Test
    public void testBidimensionalGapAnalysisOK() throws Exception {
        // GUVNOR-3010
        analyze("gapAnalysis2DOK.gdst");
        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());
    }
}
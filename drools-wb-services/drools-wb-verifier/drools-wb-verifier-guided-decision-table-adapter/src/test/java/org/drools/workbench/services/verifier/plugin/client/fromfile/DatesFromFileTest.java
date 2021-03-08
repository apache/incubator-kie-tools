/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client.fromfile;

import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.AnalyzerUpdateTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.loadResource;

@RunWith(MockitoJUnitRunner.class)
public class DatesFromFileTest extends AnalyzerUpdateTestBase {

    @Test
    public void testLowEdge() throws
            Exception {

        final String xml = loadResource("Dates-missing-edge-low.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       1, 2, 3);
    }

    @Test
    public void testHighEdge() throws
            Exception {

        final String xml = loadResource("Dates-missing-edge-high.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       1, 2, 3);
    }

    @Test
    public void testComplete() throws
            Exception {

        final String xml = loadResource("Dates-complete.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertDoesNotContain(CheckType.MISSING_RANGE,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testGap() throws
            Exception {

        final String xml = loadResource("Dates-mid-gap.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertContains(analysisReport,
                       CheckType.MISSING_RANGE,
                       Severity.NOTE,
                       1, 2);
    }
}
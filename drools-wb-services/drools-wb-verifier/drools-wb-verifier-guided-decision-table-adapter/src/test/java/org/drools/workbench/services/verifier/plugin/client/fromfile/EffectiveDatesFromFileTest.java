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

package org.drools.workbench.services.verifier.plugin.client.fromfile;

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.AnalyzerUpdateTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.loadResource;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class EffectiveDatesFromFileTest extends AnalyzerUpdateTestBase {

    @Test
    public void testUpdateNotNullColumn() throws
            Exception {

        // Table contains two redundant rows, but the rows are active at different times.
        final String xml = loadResource("EffectiveDates.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        // First run
        analyzer.resetChecks();
        analyzer.analyze();

        Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertTrue(analysisReport.isEmpty());
    }
}
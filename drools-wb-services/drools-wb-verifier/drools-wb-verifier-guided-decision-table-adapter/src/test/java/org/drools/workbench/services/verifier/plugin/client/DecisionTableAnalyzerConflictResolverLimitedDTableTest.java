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

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.services.verifier.plugin.client.testutil.LimitedGuidedDecisionTableBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerConflictResolverLimitedDTableTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testConflict() throws Exception {

        table52 = new LimitedGuidedDecisionTableBuilder("org.test",
                                                        new ArrayList<Import>(),
                                                        "mytable")
                .withIntegerColumn("a",
                                   "Person",
                                   "age",
                                   "==",
                                   0)
                .withAction("a",
                            "approved",
                            DataType.TYPE_BOOLEAN,
                            new DTCellValue52() {
                                {
                                    setBooleanValue(true);
                                }
                            })
                .withAction("a",
                            "approved",
                            DataType.TYPE_BOOLEAN,
                            new DTCellValue52() {
                                {
                                    setBooleanValue(false);
                                }
                            })
                .withData(new Object[][]{
                        {1, "", "description", true, true, false},
                        {2, "", "description", true, false, true}})
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       1);
    }
}
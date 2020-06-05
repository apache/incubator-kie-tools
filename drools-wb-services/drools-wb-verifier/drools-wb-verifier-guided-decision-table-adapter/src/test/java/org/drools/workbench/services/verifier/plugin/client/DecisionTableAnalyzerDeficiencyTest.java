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
import org.drools.workbench.services.verifier.plugin.client.testutil.ExtendedGuidedDecisionTableBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerDeficiencyTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testRuleIsNotDeficient() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            "==")
                .withStringColumn("a",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("a",
                                  "Person",
                                  "lastName",
                                  "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", null, "Eder", null, 100},
                        {2, "", "description", 10, null, null, 100},
                        {3, "", "description", null, "Toni", "Rikkola", 100},
                        {4, "", "description", null, null, null, null}
                })
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testRuleIsDeficient001() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            "==")
                .withStringColumn("a",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("a",
                                  "Person",
                                  "lastName",
                                  "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", null, "Eder", null, 100},
                        {2, "", "description", 10, null, null, 200},
                        {3, "", "description", null, "Toni", "Rikkola", 300},
                        {4, "", "description", null, null, null, null}
                })
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.DEFICIENT_ROW,
                       Severity.WARNING,
                       2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);
    }

    @Test
    public void testRuleIsDeficient002() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            "==")
                .withStringColumn("a",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("a",
                                  "Person",
                                  "lastName",
                                  "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("a",
                                    "description",
                                    DataType.TYPE_STRING)
                .withData(new Object[][]{
                        {1, "", "description", 10, "", "", 100, "ok"},
                        {2, "", "description", null, "", "", 200, "ok"},
                        {3, "", "description", 12, "", "Rikkola", 300, "ok"},
                        {4, "", "description", null, "", "", null, ""}
                })
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);

        setValue(1,
                 4,
                 "Toni");

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.DEFICIENT_ROW,
                       Severity.WARNING,
                       2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);
    }

    @Test
    public void testRuleIsDeficient003() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            "==")
                .withStringColumn("a",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("a",
                                  "Person",
                                  "lastName",
                                  "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("a",
                                    "description",
                                    DataType.TYPE_STRING)
                .withData(new Object[][]{
                        {1, "", "description", 10, "", "", 100, "ok"},
                        {2, "", "description", null, "Eder", "", 200, "ok"},
                        {3, "", "description", 12, "", "Rikkola", 300, "ok"},
                        {4, "", "description", null, "", "", null, ""}
                })
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.DEFICIENT_ROW,
                       Severity.WARNING,
                       2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);

        setValue(2,
                 4,
                 "Toni");

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);
    }

    @Test
    public void testRuleIsDeficient004() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            "==")
                .withStringColumn("a",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("a",
                                  "Person",
                                  "lastName",
                                  "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("a",
                                    "description",
                                    DataType.TYPE_STRING)
                .withData(new Object[][]{
                        {1, "", "description", 10, "", "", 100, "ok"},
                        {2, "", "description", null, "Eder", "", 200, "ok"},
                        {3, "", "description", 12, "Toni", "Rikkola", 300, "ok"},
                        {4, "", "description", null, "", "", null, ""}
                })
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);

        setValue(2,
                 4,
                 "");

        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.DEFICIENT_ROW,
                       Severity.WARNING,
                       2);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             3);
        assertDoesNotContain(CheckType.DEFICIENT_ROW,
                             analyzerProvider.getAnalysisReport(),
                             4);
    }
}
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
import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.drools.workbench.services.verifier.plugin.client.testutil.ExtendedGuidedDecisionTableBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerSubsumptionResolverTest extends AnalyzerUpdateTestBase {

    @Before
    public void setUp() throws
            Exception {
        super.setUp();

        analyzerProvider.getFactTypes()
                .add(new FactTypes.FactType("LoanApplication",
                                            new HashSet<FactTypes.Field>() {
                                                {
                                                    add(new FactTypes.Field("amount",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("lengthYears",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("deposit",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("approved",
                                                                            DataType.TYPE_BOOLEAN));
                                                    add(new FactTypes.Field("insuranceCost",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("approvedRate",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                }
                                            }));

        analyzerProvider.getFactTypes()
                .add(new FactTypes.FactType("IncomeSource",
                                            new HashSet<FactTypes.Field>() {
                                                {
                                                    add(new FactTypes.Field("type",
                                                                            DataType.TYPE_STRING));
                                                }
                                            }));
        analyzerProvider.getFactTypes()
                .add(new FactTypes.FactType("Person",
                                            new HashSet<FactTypes.Field>() {
                                                {
                                                    add(new FactTypes.Field("name",
                                                                            DataType.TYPE_STRING));
                                                }
                                            }));
    }

    @Test
    public void testNoIssues() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "amount",
                                            ">")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "amount",
                                            "<=")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "lengthYears",
                                            "==")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "deposit",
                                            "<")
                .withStringColumn("income",
                                  "IncomeSource",
                                  "type",
                                  "==")
                .withActionSetField("application",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withActionSetField("application",
                                    "insuranceCost",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("application",
                                    "approvedRate",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2},
                        {2, "", "description", 10000, 100000, 20, 2000, "Job", true, 0, 4},
                        {3, "", "description", 100001, 130000, 20, 3000, "Job", true, 10, 6},
                        {4, "", "description", null, null, null, null, null, null, null, null},
                        {5, "", "description", null, null, null, null, null, null, null, null}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testNoIssues2() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "amount",
                                            ">")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "amount",
                                            "<=")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "lengthYears",
                                            "==")
                .withConditionIntegerColumn("application",
                                            "LoanApplication",
                                            "deposit",
                                            "<")
                .withStringColumn("income",
                                  "IncomeSource",
                                  "type",
                                  "==")
                .withActionSetField("application",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withActionSetField("application",
                                    "insuranceCost",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("application",
                                    "approvedRate",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2},
                        {2, "", "description", 1000, 200000, 30, 20000, "Asset", true, 0, 2},
                        {3, "", "description", 100001, 130000, 20, 3000, "Job", true, 10, 6}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    @Ignore("This randomly does not pick up the issue. Better that way, I'm hoping future changes will find the cause. Every other test works 100%")
    public void testRedundantRows001() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withNumericColumn("application",
                                   "LoanApplication",
                                   "amount",
                                   ">")
                .withNumericColumn("application",
                                   "LoanApplication",
                                   "amount",
                                   "<=")
                .withNumericColumn("application",
                                   "LoanApplication",
                                   "lengthYears",
                                   "==")
                .withNumericColumn("application",
                                   "LoanApplication",
                                   "deposit",
                                   "<")
                .withStringColumn("income",
                                  "IncomeSource",
                                  "type",
                                  "==")
                .withActionSetField("application",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withActionSetField("application",
                                    "insuranceCost",
                                    DataType.TYPE_NUMERIC)
                .withActionSetField("application",
                                    "approvedRate",
                                    DataType.TYPE_NUMERIC)
                .withData(new Object[][]{
                        {1, "", "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2},
                        {2, "", "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2},
                        {3, "", "description", 100001, 130000, 20, 3000, "Job", true, 10, 6}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);
    }

    @Test
    public void testRedundantRows002() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withStringColumn("application",
                                  "LoanApplication",
                                  "amount",
                                  ">")
                .withStringColumn("person",
                                  "Person",
                                  "name",
                                  "==")
                .withStringColumn("income",
                                  "IncomeSource",
                                  "type",
                                  "==")
                .withActionSetField("application",
                                    "approved",
                                    DataType.TYPE_STRING)
                .withData(new Object[][]{
                        {1, "", "description", "131000", "Toni", "Asset", "true"},
                        {2, "", "description", "131000", "Toni", "Asset", "true"},
                        {3, "", "description", "100001", "Michael", "Job", "true"}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);
    }

    @Test
    public void testRedundantRows003() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withStringColumn("application",
                                  "LoanApplication",
                                  "amount",
                                  ">")
                .withStringColumn("person",
                                  "Person",
                                  "name",
                                  "==")
                .withEnumColumn("income",
                                "IncomeSource",
                                "type",
                                "==",
                                "Asset,Job")
                .withActionSetField("application",
                                    "approved",
                                    DataType.TYPE_STRING)
                .withData(new Object[][]{
                        {1, "", "description", "131000", "Toni", "Asset", "true"},
                        {2, "", "description", "131000", "Toni", "Asset", "true"},
                        {3, "", "description", "100001", "Michael", "Job", "true"}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1,
                       2);
    }

    @Test
    public void testRedundantConditions001() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withEnumColumn("a",
                                "Person",
                                "name",
                                "==",
                                "Toni,Eder")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "name",
                                            "==")
                .withData(new Object[][]{{1, "", "description", "Toni", "Toni"}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_CONDITIONS_TITLE,
                       Severity.NOTE);
    }

    @Test
    public void testRedundantRowsWithConflict() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "age",
                                            ">")
                .withConditionDoubleColumn("d",
                                           "Account",
                                           "deposit",
                                           "<")
                .withActionSetField("a",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withActionSetField("a",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withData(new Object[][]{
                        {1, "", "description", 100, 0.0, true, true},
                        {2, "", "description", 100, 0.0, true, false}})
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testRedundantActionsInOneRow001() throws
            Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "name",
                                            "==")
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("a",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", "Toni", 100, 100},
                        {2, "", "description", "Eder", 200, null},
                        {3, "", "description", "Michael", null, 300},
                        {4, "", "description", null, null, null, null, null}
                })
                .buildTable();

        Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.VALUE_FOR_ACTION_IS_SET_TWICE,
                       Severity.WARNING
        );
    }

    @Test
    public void testRedundantActionsInOneRow002() throws
            Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                                                     new ArrayList<Import>(),
                                                                                     "mytable")
                .withConditionIntegerColumn("a",
                                            "Person",
                                            "name",
                                            "==")
                .withActionInsertFact("Person",
                                      "b",
                                      "salary",
                                      DataType.TYPE_NUMERIC_INTEGER)
                .withActionSetField("b",
                                    "salary",
                                    DataType.TYPE_NUMERIC_INTEGER)
                .withData(new Object[][]{
                        {1, "", "description", "Toni", 100, 100},
                        {2, "", "description", "Eder", 200, null},
                        {3, "", "description", "Michael", null, 300},
                        {4, "", "description", null, null, null, null, null}
                })
                .buildTable();

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.VALUE_FOR_ACTION_IS_SET_TWICE,
                       Severity.WARNING
        );
    }
}
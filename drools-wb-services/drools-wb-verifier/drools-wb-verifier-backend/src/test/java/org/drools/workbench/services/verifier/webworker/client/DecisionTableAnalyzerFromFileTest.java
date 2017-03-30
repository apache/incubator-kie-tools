/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.core.main.Analyzer;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerFromFileTest extends AnalyzerUpdateTestBase {

    @Test
    public void testUpdateNotNullColumn() throws
            Exception,
            UpdateException {

        final String xml = loadResource("Is Null Table.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        // First run
        analyzer.resetChecks();
        analyzer.analyze();

        assertContains(RULE_HAS_NO_RESTRICTIONS_AND_WILL_ALWAYS_FIRE,
                       analyzerProvider.getAnalysisReport());

        // Update
        table52.getData()
                .get(0)
                .get(2)
                .setBooleanValue(true);
        final List<Coordinate> updates = new ArrayList<>();
        updates.add(new Coordinate(0,
                                   2));

        analyzerProvider.getUpdateManager(table52,
                                          analyzer)
                .update(table52,
                        updates);

        // Update
        table52.getData()
                .get(1)
                .get(2)
                .setBooleanValue(true);
        final List<Coordinate> updates2 = new ArrayList<>();
        updates2.add(new Coordinate(1,
                                    2));
        analyzerProvider.getUpdateManager(table52,
                                          analyzer)
                .update(table52,
                        updates2);

        assertDoesNotContain(RULE_HAS_NO_RESTRICTIONS_AND_WILL_ALWAYS_FIRE,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testFilePricingLoansGDST() throws
            Exception {
        final String xml = loadResource("Pricing loans.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE);
    }

    @Test
    public void testFileLargeFileGDST() throws
            Exception {
        final String xml = loadResource("Large file.gdst");

        final Analyzer analyzer = analyzerProvider.makeAnalyser(GuidedDTXMLPersistence.getInstance()
                                                                        .unmarshal(xml));

        analyzer.resetChecks();
        analyzer.analyze();

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SINGLE_HIT_LOST,
                           EMPTY_RULE);
    }

    @Test
    public void testFile3() throws
            Exception {
        final String xml = loadResource("Pricing loans version 2.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertDoesNotContain("ThisRowIsRedundantTo",
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testFileScoreAchievementsGDST() throws
            Exception {
        analyzerProvider.getFactTypes()
                .add(new FactTypes.FactType("Player",
                                            new HashSet<FactTypes.Field>() {
                                                {
                                                    add(new FactTypes.Field("score",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                }
                                            }));

        final String xml = loadResource("Score Achievements.gdst");

        final Analyzer analyzer = analyzerProvider.makeAnalyser(GuidedDTXMLPersistence.getInstance()
                                                                        .unmarshal(xml));

        analyzer.resetChecks();
        analyzer.analyze();

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE,
                           SINGLE_HIT_LOST);
    }

    @Test
    public void testFileBaseEntitlementGDST() throws
            Exception {
        final String xml = loadResource("Base entitlement.gdst");

        final Analyzer analyzer = analyzerProvider.makeAnalyser(GuidedDTXMLPersistence.getInstance()
                                                                        .unmarshal(xml));

        analyzer.resetChecks();
        analyzer.analyze();

        assertTrue(analyzerProvider.getAnalysisReport()
                           .isEmpty());
    }

    @Test
    public void testFileLargeFileGDSTWithUpdate() throws
            Exception,
            UpdateException {
        long baseline = System.currentTimeMillis();
        final String xml = loadResource("Large file.gdst");
        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);
        long now = System.currentTimeMillis();
        System.out.println("Loading of model took.. " + (now - baseline) + " ms");
        baseline = now;

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        now = System.currentTimeMillis();
        System.out.println("Indexing took.. " + (now - baseline) + " ms");

        analyzer.resetChecks();
        analyzer.analyze();
        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SINGLE_HIT_LOST,
                           EMPTY_RULE);
        now = System.currentTimeMillis();
        System.out.println("Initial analysis took.. " + (now - baseline) + " ms");
        baseline = now;

        table52.getData()
                .get(2)
                .get(6)
                .clearValues();
        final List<Coordinate> updates = new ArrayList<>();
        updates.add(new Coordinate(2,
                                   6));
        analyzerProvider.getUpdateManager(table52,
                                          analyzer)
                .update(table52,
                        updates);
        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SINGLE_HIT_LOST,
                           EMPTY_RULE);
        now = System.currentTimeMillis();
        System.out.println("Partial analysis took.. " + (now - baseline) + " ms");
    }

    @Test
    public void testFileLargeFileGDSTWithDeletes() throws
            Exception,
            UpdateException {
        final String xml = loadResource("Large file.gdst");
        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           SINGLE_HIT_LOST,
                           EMPTY_RULE);
        long baseline = System.currentTimeMillis();

        for (int iterations = 0; iterations < 10; iterations++) {
            analyzer.removeRule(100);
            table52.getData()
                    .remove(100);
            List<Coordinate> canBeUpdated = new ArrayList<>();
            canBeUpdated.add(new Coordinate(0,
                                            0));
            analyzerProvider.getUpdateManager(table52,
                                              analyzer)
                    .update(table52,
                            canBeUpdated);
            long now = System.currentTimeMillis();
            System.out.println("Partial analysis took.. " + (now - baseline) + " ms");
            baseline = now;
            assertOnlyContains(analyzerProvider.getAnalysisReport(),
                               SINGLE_HIT_LOST,
                               EMPTY_RULE);
        }
    }
}
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;

@Ignore("https://issues.jboss.org/browse/DROOLS-2485 - randomly failing test. " +
        "Also it's performance test (runs 110-180s on jenkins) - shouldn't run as part of daily builds")
@RunWith(GwtMockitoTestRunner.class)
public class SpeedTest extends AnalyzerUpdateTestBase {

    Logger logger = LoggerFactory.getLogger(SpeedTest.class);

    @Test
    public void subsumptionTable() throws
            Exception {
        long baseline = System.currentTimeMillis();

        final DataBuilderProvider.DataBuilder builder = DataBuilderProvider.row(true,
                                                                                null,
                                                                                true);
        for (int i = 0; i < 100; i++) {

            builder.row(null,
                        false,
                        true);
        }

        final Object[][] data = builder.end();

        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionBooleanColumn("$p",
                                            "Person",
                                            "approved",
                                            "==")
                .withConditionBooleanColumn("$p",
                                            "Person",
                                            "approved",
                                            "!=")
                .withActionSetField("$p",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withData(data)
                .buildTable();

        long now = System.currentTimeMillis();
        logger.debug("Loading of model took.. " + (now - baseline) + " ms");
        baseline = now;

        fireUpAnalyzer();

        now = System.currentTimeMillis();
        logger.debug("Analyzing took.. " + (now - baseline) + " ms");
        baseline = now;

        setValue(1,
                 2,
                 false);

        now = System.currentTimeMillis();
        logger.debug("Update.. " + (now - baseline) + " ms");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING);
    }

    @Test
    public void noConflictTable() throws
            Exception {
        long baseline = System.currentTimeMillis();

        final DataBuilderProvider.DataBuilder builder = DataBuilderProvider
                .row(-1,
                     true);
        for (int i = 0; i < 1000; i++) {
            builder
                    .row(i,
                         true);
        }

        final Object[][] data = builder.end();

        table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
                                                         new ArrayList<Import>(),
                                                         "mytable")
                .withConditionIntegerColumn("$p",
                                            "Person",
                                            "age",
                                            "==")
                .withActionSetField("$p",
                                    "approved",
                                    DataType.TYPE_BOOLEAN)
                .withData(data)
                .buildTable();

        long now = System.currentTimeMillis();
        logger.debug("Loading of model took.. " + (now - baseline) + " ms");
        baseline = now;

        fireUpAnalyzer();

        now = System.currentTimeMillis();
        logger.debug("Analyzing took.. " + (now - baseline) + " ms");
        baseline = now;

        setValue(1,
                 2,
                 3);

        now = System.currentTimeMillis();
        logger.debug("Update.. " + (now - baseline) + " ms");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING);
    }
}
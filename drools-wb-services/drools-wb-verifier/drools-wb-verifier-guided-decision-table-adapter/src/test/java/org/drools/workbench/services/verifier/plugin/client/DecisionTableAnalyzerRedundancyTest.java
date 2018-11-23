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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerRedundancyTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void twoNewRulesShouldNotBeRedundant() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .conditionColumn().person("aa").age(">")
                .actionColumn().retract()
                .withData(DataBuilderProvider
                                  .row(22,
                                       "aa")
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        appendRow(DataType.DataTypes.NUMERIC_INTEGER,
                  DataType.DataTypes.STRING);

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void twoNewRulesShouldNotBeRedundantScenario() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .conditionColumn().person("aa").age(">")
                .actionColumn().retract()
                .buildTable();

        fireUpAnalyzer();

        appendRow(DataType.DataTypes.NUMERIC_INTEGER,
                  DataType.DataTypes.STRING);

        setCoordinate().row(0).column(2).toValue(22);
        setCoordinate().row(0).column(3).toValue("aa");

        appendRow(DataType.DataTypes.NUMERIC_INTEGER,
                  DataType.DataTypes.STRING);

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }
}
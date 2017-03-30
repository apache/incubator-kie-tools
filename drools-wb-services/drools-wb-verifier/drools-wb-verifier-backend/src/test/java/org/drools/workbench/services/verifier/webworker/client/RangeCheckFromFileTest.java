/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.services.verifier.core.main.Analyzer;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.drools.workbench.services.verifier.webworker.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.*;

@RunWith(GwtMockitoTestRunner.class)
public class RangeCheckFromFileTest extends AnalyzerUpdateTestBase {

    @Override
    @Before
    public void setUp() throws
            Exception {
        super.setUp();

        analyzerProvider.getFactTypes()
                .add(new FactTypes.FactType("Employee",
                                            new HashSet<FactTypes.Field>() {
                                                {
                                                    add(new FactTypes.Field("age",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("yearsService",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                    add(new FactTypes.Field("vacationEntitlement",
                                                                            DataType.TYPE_NUMERIC_INTEGER));
                                                }
                                            }));
    }

    @Test
    public void testFileExtraDays() throws
            Exception {
        final String xml = loadResource("Extra 5 days.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);
        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        assertOnlyContains(analyzerProvider.getAnalysisReport(),
                           MISSING_RANGE_TITLE);
    }
}
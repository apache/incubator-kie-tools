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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerGapsTest {

    @GwtMock
    AnalysisConstants analysisConstants;

    @Mock
    AsyncPackageDataModelOracle oracle;

    @Before
    public void setUp() throws Exception {
        oracle = mock( AsyncPackageDataModelOracle.class );

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

    }

    @Test
    public void testTrue() throws Exception {
        assertTrue( true );
    }

//    @Test
//    public void testIntegerNoGaps() throws Exception {
//        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
//                                                                               new ArrayList<Import>(),
//                                                                               "mytable")
//                .withIntegerColumn("a", "Person", "age", ">")
//                .withIntegerColumn("a", "Person", "age", "<=")
//                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
//                .withData(new Object[][]{{1, "description", 0, null, true},
//                                         {1, "description", null, 0, true}})
//                .build();
//
//        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
//                                                                   table52);
//
//        assertEmpty(analyzer.analyze());
//
//    }
//
//    @Test
//    /**
//     * There is a gap when age is 1 ... 10
//     */
//    public void testIntegerNoGap001() throws Exception {
//        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder("org.test",
//                                                                               new ArrayList<Import>(),
//                                                                               "mytable")
//                .withIntegerColumn("a", "Person", "age", ">")
//                .withIntegerColumn("a", "Person", "age", "<=")
//                .withActionSetField("a", "approved", DataType.TYPE_BOOLEAN)
//                .withData(new Object[][]{{1, "description", 10, null, true},
//                                         {1, "description", null, 0, true}})
//                .build();
//
//        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer(oracle,
//                                                                   table52);
//
//        assertContains("There is a gap when age is 1 ... 10", analyzer.analyze());
//
//    }
}
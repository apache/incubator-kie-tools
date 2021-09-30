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
package org.dashbuilder.dataset;

import org.dashbuilder.Bootstrap;
import org.dashbuilder.test.BaseCDITest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class DataSetSubsystemCDITest extends BaseCDITest {

    @Inject
    Bootstrap bootstrap;

    DataSetFilterTest dataSetFilterTest;
    DataSetGroupTest dataSetGroupTest;
    DataSetNestedGroupTest nestedGroupTest;
    DataSetSortTest dataSetSortTest;
    DataSetTrimTest dataSetTrimTest;

    @Before
    public void setUp() throws Exception {
        bootstrap.init();

        dataSetFilterTest = new DataSetFilterTest();
        dataSetGroupTest = new DataSetGroupTest();
        nestedGroupTest = new DataSetNestedGroupTest();
        dataSetSortTest = new DataSetSortTest();
        dataSetTrimTest = new DataSetTrimTest();

        dataSetFilterTest.setUp();
        dataSetGroupTest.setUp();
        nestedGroupTest.setUp();
        dataSetSortTest.setUp();
        dataSetTrimTest.setUp();
    }

    @Test
    public void testFilter() throws Exception {
        dataSetFilterTest.testColumnTypes();
        dataSetFilterTest.testFilterByString();
        dataSetFilterTest.testFilterByNumber();
        dataSetFilterTest.testFilterByDate();
        dataSetFilterTest.testFilterMultiple();
        dataSetFilterTest.testANDExpression();
        dataSetFilterTest.testNOTExpression();
        dataSetFilterTest.testORExpression();
        dataSetFilterTest.testORExpressionMultilple();
        dataSetFilterTest.testLogicalExprNonEmpty();
        dataSetFilterTest.testCombinedExpression();
        dataSetFilterTest.testCombinedExpression2();
        dataSetFilterTest.testCombinedExpression3();
        dataSetFilterTest.testLikeOperatorCaseSensitive();
        dataSetFilterTest.testLikeOperatorNonCaseSensitive();
        dataSetFilterTest.testFilterByStringWithPreProcessor();
    }

    @Test
    public void testGroup() throws Exception {
        dataSetGroupTest.testDataSetFunctions();
        dataSetGroupTest.testDateMinMaxFunctions();
        dataSetGroupTest.testNumberMinMaxFunctions();
        dataSetGroupTest.testGroupByLabelDynamic();
        dataSetGroupTest.testGroupByExcludeLabelColumn();
        dataSetGroupTest.testGroupByYearDynamic();
        dataSetGroupTest.testGroupByMonthDynamic();
        dataSetGroupTest.testGroupByMonthDynamicNonEmpty();
        dataSetGroupTest.testGroupByDayOfWeekDynamic();
        dataSetGroupTest.testGroupByDayOfWeekFixed();
        dataSetGroupTest.testGroupByMonthReverse();
        dataSetGroupTest.testGroupByFixedTrim();
        dataSetGroupTest.testGroupByMonthFixed();
        dataSetGroupTest.testGroupByMonthFirstMonth();
        dataSetGroupTest.testGroupByMonthFirstMonthReverse();
        dataSetGroupTest.testGroupByQuarter();
        dataSetGroupTest.testGroupByDateOneRow();
        dataSetGroupTest.testGroupByDateOneDay();
        dataSetGroupTest.testGroupAndCountSameColumn();
        dataSetGroupTest.testGroupNumberAsLabel();
    }

    @Test
    public void testNestedGroups() throws Exception {
        nestedGroupTest.testGroupSelectionFilter();
        nestedGroupTest.testNestedGroupFromMultipleSelection();
        nestedGroupTest.testNestedGroupRequiresSelection();
        nestedGroupTest.testNoResultsSelection();
        nestedGroupTest.testThreeNestedLevels();
        nestedGroupTest.testGroupByQuarter();
    }

    @Test
    public void testSort() throws Exception {
        dataSetSortTest.testSortByString();
        dataSetSortTest.testSortByNumber();
        dataSetSortTest.testSortByDate();
        dataSetSortTest.testSortMultiple();
        dataSetSortTest.testGroupAndSort();
    }

    @Test
    public void testTrim() throws Exception {
        dataSetTrimTest.testTrim();
        dataSetTrimTest.testTrimGroup();
        dataSetTrimTest.testDuplicatedColumns();
    }
}

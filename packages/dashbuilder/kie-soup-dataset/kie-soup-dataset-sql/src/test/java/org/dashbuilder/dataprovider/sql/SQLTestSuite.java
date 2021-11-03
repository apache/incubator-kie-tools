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
package org.dashbuilder.dataprovider.sql;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;

public class SQLTestSuite extends SQLDataSetTestBase {

    protected <T extends SQLDataSetTestBase> T setUp(T test) throws Exception {
        test.testSettings = testSettings;
        test.conn = conn;
        return test;
    }

    protected List<SQLDataSetTestBase> sqlTestList = new ArrayList<SQLDataSetTestBase>();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        sqlTestList.add(setUp(new SQLDataSetDefTest()));
        sqlTestList.add(setUp(new SQLDataSetTrimTest()));
        sqlTestList.add(setUp(new SQLTableDataSetLookupTest()));
        sqlTestList.add(setUp(new SQLQueryDataSetLookupTest()));
        sqlTestList.add(setUp(new SQLInjectionAttacksTest()));
        sqlTestList.add(setUp(new SQLColumnsTypeTest()));
    }

    public void testAll() throws Exception {
        for (SQLDataSetTestBase testBase : sqlTestList) {
            testBase.testAll();
        }
    }
}

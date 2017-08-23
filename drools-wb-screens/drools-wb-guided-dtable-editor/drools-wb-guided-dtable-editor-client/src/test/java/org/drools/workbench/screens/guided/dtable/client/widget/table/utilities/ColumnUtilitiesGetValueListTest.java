/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockito;
import org.appformer.project.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ColumnUtilitiesGetValueListTest {

    private static final String DATE_CONVERSION_VALUE_LIST = "a,10-10-2010,b, d";

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    private Pattern52 pattern;
    private ConditionCol52 column;
    private ColumnUtilities utilities;

    @Parameters(name = "valueList:{0} dataType:{1} validValuesCount:{2} validValues:{3}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a,b,c", DataType.TYPE_STRING, 3, new String[]{"a", "b", "c"}},
                {"a, b b b  ,c", DataType.TYPE_STRING, 3, new String[]{"a", " b b b  ", "c"}},
                {"a,1,b", DataType.TYPE_STRING, 3, new String[]{"a", "1", "b"}},
                {"@,#,$", DataType.TYPE_STRING, 3, new String[]{"@", "#", "$"}},
                {",a", DataType.TYPE_STRING, 2, new String[]{"", "a"}},
                {"a,,b", DataType.TYPE_STRING, 3, new String[]{"a", "", "b"}},
                {"a,10-10-2010,b", DataType.TYPE_DATE, 3, new String[]{"a", "10-10-2010", "b"}},
                {DATE_CONVERSION_VALUE_LIST, DataType.TYPE_DATE, 1, new String[]{"10-10-2010"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_BIGDECIMAL, 2, new String[]{"1", "1.0"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_BIGINTEGER, 1, new String[]{"1"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_BYTE, 1, new String[]{"1"}},
                {"1,1.0,a,1d", DataType.TYPE_NUMERIC_DOUBLE, 3, new String[]{"1", "1.0", "1d"}},
                {"1,1.0,a,1f", DataType.TYPE_NUMERIC_FLOAT, 3, new String[]{"1", "1.0", "1f"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_INTEGER, 1, new String[]{"1"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_LONG, 1, new String[]{"1"}},
                {"1,1.0,a", DataType.TYPE_NUMERIC_SHORT, 1, new String[]{"1"}},
                {"1,true,false", DataType.TYPE_BOOLEAN, 2, new String[]{"true", "false"}}
        });
    }

    @Parameter(0)
    public String valueList;

    @Parameter(1)
    public String fieldDataType;

    @Parameter(2)
    public int validValuesCount;

    @Parameter(3)
    public String[] validValues;

    public ColumnUtilitiesGetValueListTest() {

    }

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
        utilities = new ColumnUtilities(model,
                                        oracle);
        pattern = new Pattern52();
        column = new ConditionCol52();
        when(model.getPattern(column)).thenReturn(pattern);

        final Map<String, String> properties = new HashMap<>();
        if (valueList.compareTo(DATE_CONVERSION_VALUE_LIST) == 0) {
            properties.put(ApplicationPreferences.DATE_FORMAT,
                           "dd-MM-yyyy");
        } else {
            properties.put(ApplicationPreferences.DATE_FORMAT,
                           null);
        }
        ApplicationPreferences.setUp(properties);
    }

    @Test
    public void testGetValueList() {
        column.setValueList(valueList);
        column.setFieldType(fieldDataType);
        String[] values = utilities.getValueList(column);
        assertEquals(validValuesCount,
                     values.length);
        for (int i = 0; i < validValuesCount; i++) {
            assertEquals(validValues[i],
                         values[i]);
        }
    }
}
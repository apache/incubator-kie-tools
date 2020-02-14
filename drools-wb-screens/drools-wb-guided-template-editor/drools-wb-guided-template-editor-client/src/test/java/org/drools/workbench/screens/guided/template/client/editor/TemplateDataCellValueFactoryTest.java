/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.client.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DateConverter;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class TemplateDataCellValueFactoryTest {

    private String datatype;
    private String operator;
    private Comparable cellValue;
    private Object result;
    private TemplateDataCellValueFactory templateDataCellValueFactory;

    public TemplateDataCellValueFactoryTest(final String datatype,
                                            final String operator,
                                            final Comparable cellValue,
                                            final String result) {
        this.datatype = datatype;
        this.operator = operator;
        this.cellValue = cellValue;
        this.result = result;
    }

    @Before
    public void setUp() throws Exception {
        templateDataCellValueFactory = new TemplateDataCellValueFactory(mock(TemplateModel.class),
                                                                        mock(AsyncPackageDataModelOracle.class));
        templateDataCellValueFactory.DATE_CONVERTOR = new DateConverter() {
            @Override
            public String format(Date date) {
                return "1.1.1111";
            }

            @Override
            public Date parse(String text) {
                return null;
            }
        };
    }

    @Test
    public void testConvertToModelCell() {
        final String actualResult = templateDataCellValueFactory.convertToModelCell(new TemplateDataColumn("var",
                                                                                                           datatype,
                                                                                                           "Person",
                                                                                                           "age",
                                                                                                           operator),
                                                                                    new CellValue(cellValue));
        assertEquals(result, actualResult);
    }

    @Parameterized.Parameters
    public static Collection testParameters() {
        return Arrays.asList(new Object[][]{
                {DataType.TYPE_STRING, "in", null, null},
                {DataType.TYPE_STRING, "==", null, null},
                {DataType.TYPE_STRING, "==", "hi", "hi"},
                {DataType.TYPE_BOOLEAN, "in", null, null},
                {DataType.TYPE_BOOLEAN, "==", null, null},
                {DataType.TYPE_BOOLEAN, "==", true, "true"},
                {DataType.TYPE_DATE, "in", null, null},
                {DataType.TYPE_DATE, "==", null, null},
                {DataType.TYPE_DATE, "==", new Date(), "1.1.1111"},
                {DataType.TYPE_NUMERIC, "in", null, null},
                {DataType.TYPE_NUMERIC, "==", null, null},
                {DataType.TYPE_NUMERIC, "==", new BigDecimal(2.0), "2"},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, "in", null, null},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, "==", null, null},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, "==", new BigDecimal(4.0), "4"},
                {DataType.TYPE_NUMERIC_BIGINTEGER, "in", null, null},
                {DataType.TYPE_NUMERIC_BIGINTEGER, "==", null, null},
                {DataType.TYPE_NUMERIC_BIGINTEGER, "==", new BigInteger("5"), "5"},
                {DataType.TYPE_NUMERIC_BYTE, "in", null, null},
                {DataType.TYPE_NUMERIC_BYTE, "==", null, null},
                {DataType.TYPE_NUMERIC_BYTE, "==", new Byte("6"), "6"},
                {DataType.TYPE_NUMERIC_DOUBLE, "in", null, null},
                {DataType.TYPE_NUMERIC_DOUBLE, "==", null, null},
                {DataType.TYPE_NUMERIC_DOUBLE, "==", 7.0, "7.0"},
                {DataType.TYPE_NUMERIC_FLOAT, "in", null, null},
                {DataType.TYPE_NUMERIC_FLOAT, "==", null, null},
                {DataType.TYPE_NUMERIC_FLOAT, "==", 8.0f, "8.0"},
                {DataType.TYPE_NUMERIC_INTEGER, "in", null, null},
                {DataType.TYPE_NUMERIC_INTEGER, "==", null, null},
                {DataType.TYPE_NUMERIC_INTEGER, "==", 11, "11"},
                {DataType.TYPE_NUMERIC_LONG, "in", null, null},
                {DataType.TYPE_NUMERIC_LONG, "==", null, null},
                {DataType.TYPE_NUMERIC_LONG, "==", 12l, "12"},
                {DataType.TYPE_NUMERIC_SHORT, "in", null, null},
                {DataType.TYPE_NUMERIC_SHORT, "==", null, null},
                {DataType.TYPE_NUMERIC_SHORT, "==", new Short("0"), "0"},
        });
    }
}
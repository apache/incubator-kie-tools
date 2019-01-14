/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DTCellValueUtilitiesTest {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private String type;
    private DTCellValue52 provided;
    private DataType.DataTypes expectedDataType;
    private Object expectedValue;
    private boolean hasConversionError;

    @BeforeClass
    public static void setup() {
        setupPreferences();
    }

    private static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                DATE_FORMAT);
        }};
        ApplicationPreferences.setUp(preferences);
    }

    public DTCellValueUtilitiesTest(final String type,
                                    final DTCellValue52 provided,
                                    final DataType.DataTypes expectedDataType,
                                    final Object expectedValue,
                                    final boolean hasConversionError) {
        this.type = type;
        this.provided = provided;
        this.expectedDataType = expectedDataType;
        this.expectedValue = expectedValue;
        this.hasConversionError = hasConversionError;
    }

    @Parameterized.Parameters(name = "{0}:{1}:{2}:{3}:{4}")
    public static Collection testParameters() throws ParseException {
        return Arrays.asList(new Object[][]{
                {DataType.TYPE_NUMERIC, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_BIGDECIMAL, new BigDecimal(100), false},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_BIGDECIMAL, new BigDecimal(100), false},
                {DataType.TYPE_NUMERIC_BIGINTEGER, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_BIGINTEGER, new BigInteger("100"), false},
                {DataType.TYPE_NUMERIC_BYTE, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_BYTE, new Byte("100"), false},
                {DataType.TYPE_NUMERIC_DOUBLE, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_DOUBLE, 100.0d, false},
                {DataType.TYPE_NUMERIC_FLOAT, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_FLOAT, 100.0f, false},
                {DataType.TYPE_NUMERIC_INTEGER, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_INTEGER, 100, false},
                {DataType.TYPE_NUMERIC_LONG, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_LONG, 100l, false},
                {DataType.TYPE_NUMERIC_SHORT, new DTCellValue52("100"), DataType.DataTypes.NUMERIC_SHORT, new Short("100"), false},
                {DataType.TYPE_BOOLEAN, new DTCellValue52("true"), DataType.DataTypes.BOOLEAN, true, false},
                {DataType.TYPE_DATE, new DTCellValue52("31-12-2016"), DataType.DataTypes.DATE, FORMATTER.parse("31-12-2016"), false},
                {DataType.TYPE_STRING, new DTCellValue52("String"), DataType.DataTypes.STRING, "String", false},

                {DataType.TYPE_NUMERIC, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_BIGDECIMAL, new BigDecimal(100), false},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_BIGDECIMAL, new BigDecimal(100), false},
                {DataType.TYPE_NUMERIC_BIGINTEGER, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_BIGINTEGER, new BigInteger("100"), false},
                {DataType.TYPE_NUMERIC_BYTE, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_BYTE, new Byte("100"), false},
                {DataType.TYPE_NUMERIC_DOUBLE, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_DOUBLE, 100.0d, false},
                {DataType.TYPE_NUMERIC_FLOAT, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_FLOAT, 100.0f, false},
                {DataType.TYPE_NUMERIC_INTEGER, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_INTEGER, 100, false},
                {DataType.TYPE_NUMERIC_LONG, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_LONG, 100l, false},
                {DataType.TYPE_NUMERIC_SHORT, new DTCellValue52("\"100\""), DataType.DataTypes.NUMERIC_SHORT, new Short("100"), false},
                {DataType.TYPE_DATE, new DTCellValue52("\"31-12-2016\""), DataType.DataTypes.DATE, FORMATTER.parse("31-12-2016"), false},

                {DataType.TYPE_NUMERIC, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_BIGDECIMAL, null, true},
                {DataType.TYPE_NUMERIC_BIGDECIMAL, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_BIGDECIMAL, null, true},
                {DataType.TYPE_NUMERIC_BIGINTEGER, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_BIGINTEGER, null, true},
                {DataType.TYPE_NUMERIC_BYTE, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_BYTE, null, true},
                {DataType.TYPE_NUMERIC_DOUBLE, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_DOUBLE, null, true},
                {DataType.TYPE_NUMERIC_FLOAT, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_FLOAT, null, true},
                {DataType.TYPE_NUMERIC_INTEGER, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_INTEGER, null, true},
                {DataType.TYPE_NUMERIC_LONG, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_LONG, null, true},
                {DataType.TYPE_NUMERIC_SHORT, new DTCellValue52("a"), DataType.DataTypes.NUMERIC_SHORT, null, true},
                {DataType.TYPE_BOOLEAN, new DTCellValue52("a"), DataType.DataTypes.BOOLEAN, false, false},
                {DataType.TYPE_DATE, new DTCellValue52("a"), DataType.DataTypes.DATE, null, true}
        });
    }

    @Test
    public void conversion() {
        DTCellValueUtilities.assertDTCellValue(type,
                                               provided,
                                               (final String value,
                                                final DataType.DataTypes dataType) ->
                                                       assertTrue("Conversion error callback was called unexpectedly",
                                                                  hasConversionError));
        assertEquals(expectedDataType,
                     provided.getDataType());
        assertEquals(expectedValue,
                     extractValue(provided));
    }

    private Object extractValue(final DTCellValue52 dcv) {
        switch (dcv.getDataType()) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return dcv.getNumericValue();
            case BOOLEAN:
                return dcv.getBooleanValue();
            case DATE:
                return dcv.getDateValue();
            default:
                return dcv.getStringValue();
        }
    }
}

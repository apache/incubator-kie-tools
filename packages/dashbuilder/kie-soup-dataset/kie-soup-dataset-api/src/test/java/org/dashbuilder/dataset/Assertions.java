/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.*;

public class Assertions {

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param x The x position of the cell to check (starting at 0).
     * @param y The y position of the cell to check (starting at 0).
     * @param expected The expected value in the given cell.
     */
    public static void assertDataSetValue(DataSet dataSet, int x, int y, String expected) {
        assertDataSetValue(dataSet, new DataSetFormatter(), x, y, expected);
    }

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param formatter The class used to format and compare the data set values with the expected array
     * @param x The x position of the cell to check (starting at 0).
     * @param y The y position of the cell to check (starting at 0).
     * @param expected The expected value in the given cell.
     */
    public static void assertDataSetValue(DataSet dataSet, DataSetFormatter formatter, int x, int y, String expected) {
        DataColumn col = dataSet.getColumnByIndex(y);
        String displayedValue = formatter.formatValueAt(dataSet, x, y);
        if (!displayedValue.equals(expected)) {
            fail("Data set value [" + x + "," + y + "] is different. " +
                    "Column=\"" + col.getId() + "\" " +
                    "Actual=\"" + displayedValue + "\" Expected=\"" + expected + "\"");
        }
    }

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param expected The expected row values.
     * @param index The starting data set row index where the comparison starts.
     */
    public static void assertDataSetValues(DataSet dataSet, String[][] expected, int index) {
        assertDataSetValues(dataSet, new DataSetFormatter(), expected, index);
    }

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param formatter The class used to format and compare the data set values with the expected array
     * @param expected The expected row values.
     * @param index The starting data set row index where the comparison starts.
     */
    public static void assertDataSetValues(DataSet dataSet, DataSetFormatter formatter, String[][] expected, int index) {
        // Check size
        assertEquals(dataSet.getRowCount(), expected.length-index);

        for (int i = index; i < expected.length; i++) {
            String[] row = expected[i];

            // Check row values
            for (int j = 0; j < row.length; j++) {
                DataColumn col = dataSet.getColumnByIndex(j);

                String expectedValue = row[j];
                if (expectedValue == null) continue;

                // Compare the data set value with the value the user is expecting to see.
                String displayedValue = formatter.formatValueAt(dataSet, i, j);
                if (!displayedValue.equals(expectedValue)) {
                    fail("Data set value [" + i + "," + j + "] is different. " +
                            "Column=\"" + col.getId() + "\" " +
                            "Actual=\"" + displayedValue + "\" Expected=\"" + expectedValue + "\"");
                }
            }
        }
    }
}

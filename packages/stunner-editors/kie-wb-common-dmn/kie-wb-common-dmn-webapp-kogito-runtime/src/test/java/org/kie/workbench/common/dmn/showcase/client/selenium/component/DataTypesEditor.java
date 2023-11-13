/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.showcase.client.selenium.component;

import org.kie.workbench.common.dmn.showcase.client.common.wait.WaitUtils;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DataTypesEditorLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.EditorXPathLocator;

import static org.junit.Assert.assertTrue;

/**
 * DMN Data Types editor - 'Data Types' tab
 */
public class DataTypesEditor {

    private WaitUtils waitUtils;

    private DataTypesEditor(final WaitUtils waitUtils) {
        this.waitUtils = waitUtils;
    }

    /**
     * Opens the editor by clicking 'Data Types' tab
     */
    public static DataTypesEditor open(final WaitUtils waitUtils) {
        waitUtils.waitUntilElementIsVisible(EditorXPathLocator.dataTypesTab(),
                                            "Was not able to click 'Data Types' tab")
                .click();

        return new DataTypesEditor(waitUtils);
    }

    /**
     * Expands all data type entries
     */
    public DataTypesEditor expandAll() {
        waitUtils.waitUntilElementIsVisible(DataTypesEditorLocator.expandAll(),
                                            "Was not able to click 'Expand all'")
                .click();
        return this;
    }

    /**
     * Search for highlighted root entry. Then assert it is as expected.
     */
    public void assertHighlightedRootDataType(final DataTypeEntry dataType) {
        final String entryText = waitUtils.waitUntilElementIsVisible(DataTypesEditorLocator.highlightedEntry(),
                                            "Was not able to find highlighted entry")
                .getText();

        assertTrue( "Highlighted entry seems be different as expected",
                    entryText.contains(dataType.name));

        assertTrue( "Highlighted entry seems be different as expected",
                    entryText.contains(dataType.type));
    }

    /**
     * Opens a pop over with details of given entry
     */
    public DataTypePopOverDetails viewDetails(final DataTypeEntry dataType) {
        waitUtils.waitUntilElementIsVisible(DataTypesEditorLocator.details(dataType),
                                            String.format("Was not able to open '%s':'%s' details",
                                                          dataType.name,
                                                          dataType.type))
                .click();

        return new DataTypePopOverDetails();
    }

    public class DataTypePopOverDetails {

        private DataTypePopOverDetails() {

        }

        /**
         * Check presence of entries in the pop over
         */
        public DataTypePopOverDetails assertDetailEntries(final DataTypeEntry... entries) {
            for (DataTypeEntry e : entries) {
                waitUtils.waitUntilElementIsVisible(DataTypesEditorLocator.dataTypeDetailEntry(e),
                                                    "Detail not found: " + e);
            }
            return this;
        }

        /**
         * Navigates to root of the data type definition, that is shown in current pop over
         */
        public void viewThisDataType() {
            waitUtils.waitUntilElementIsVisible(DataTypesEditorLocator.viewThisDataType(),
                                                "Was not able to navigate to data type details")
                    .click();
        }
    }

    /**
     * Each custom data type has a list of entries - its fields.
     * Such entry has at least name and type
     *
     * e.g tPerson - Structure, Age - number ...
     */
    public static class DataTypeEntry {

        private final String name;
        private final String type;

        public DataTypeEntry(final String name, final String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "DataTypePopOverDetailsEntry{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}

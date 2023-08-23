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

package org.kie.workbench.common.dmn.showcase.client.selenium.locator;

import org.kie.workbench.common.dmn.showcase.client.selenium.component.DataTypesEditor;

/**
 * General Locators of a data types editor
 */
public class DataTypesEditorLocator implements XPathLocator {

    private String xPathLocator;

    private DataTypesEditorLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    /**
     * Locates UI link for expanding all data type entries
     */
    public static DataTypesEditorLocator expandAll() {
        return new DataTypesEditorLocator("//a[@data-field='expand-all']");
    }

    /**
     * Locates button for invoking data type field details pop over
     */
    public static DataTypesEditorLocator details(final DataTypesEditor.DataTypeEntry entry) {
        return new DataTypesEditorLocator(String.format("//div[contains(., '%s')]/following-sibling::div[1]//div[@data-field='type-text'][text()='%s']",
                                                        entry.getName(),
                                                        entry.getType()));
    }

    /**
     * Locates entry of data type fields list in details() pop over
     */
    public static DataTypesEditorLocator dataTypeDetailEntry(DataTypesEditor.DataTypeEntry entry) {
        return new DataTypesEditorLocator(String.format("//li[@data-field='data-type-field'][text()='%s']/span[text()='%s']",
                                                        entry.getName(),
                                                        entry.getType())
        );
    }

    /**
     * Locates highlighted entry
     */
    public static DataTypesEditorLocator highlightedEntry() {
        return new DataTypesEditorLocator("//div[contains(@class, 'key-highlight')]");
    }

    /**
     * Locates link to switching context from details() pop over
     * @return
     */
    public static DataTypesEditorLocator viewThisDataType() {
        return new DataTypesEditorLocator("//a[text()='View this data type']");
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }
}

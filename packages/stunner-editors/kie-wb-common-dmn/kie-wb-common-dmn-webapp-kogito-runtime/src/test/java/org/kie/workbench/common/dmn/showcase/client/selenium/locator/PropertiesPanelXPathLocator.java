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

/**
 * Locators of DMN Properties panel
 */
public class PropertiesPanelXPathLocator implements XPathLocator {

    private String xPathLocator;

    private PropertiesPanelXPathLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    /**
     * Locator of properties grouped under 'groupName'.
     * In UI this is a group of an accordion that can be collapsed or expanded.
     * @param groupName name of the group
     * @return xPath Locator for the group of properties
     */
    public static PropertiesPanelXPathLocator group(final String groupName) {
        return new PropertiesPanelXPathLocator(String.format(".//div[@class='panel-title']/a/span[text()='%s']",
                                                             groupName));
    }

    /**
     * Locator of property input. It is a single input element to change a property of selected node.
     * To see it the holding properties panel group needs to be expanded firstly.
     * @param propertyName name of the property
     * @return xPath Locator of the property input
     */
    public static PropertiesPanelXPathLocator property(final String propertyName) {
        return new PropertiesPanelXPathLocator(String.format(".//label/span[text()='%s']/../../div[@data-field='fieldContainer']/input",
                                                             propertyName));
    }

    /**
     * Locator of items under the specified group of Decision Service Details in the properties panel.
     *
     * @param detailsGroup group of decision service details
     * @return xPath Locator of all items belonging the group
     */
    public static PropertiesPanelXPathLocator decisionServiceDetails(final DecisionServiceDetails detailsGroup) {
        return new PropertiesPanelXPathLocator(String.format(".//div[@data-i18n-prefix='ParameterGroup.']/div[text()='%s']/../ul[@id='parameters']/li/div/span[@data-field='parameter-name']",
                                                             detailsGroup.getLabel()));
    }
    public static PropertiesPanelXPathLocator propertiesPanelButton() {
        return new PropertiesPanelXPathLocator(".//div[@data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }

    public enum DecisionServiceDetails {
        INPUT_DATA("Input Data"),
        ENCAPSULATED_DECISIONS("Encapsulated Decisions"),
        OUTPUT_DECISIONS("Output Decisions");

        private final String label;

        DecisionServiceDetails(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

    }
}

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

package org.kie.workbench.common.dmn.showcase.client.selenium.locator;

/**
 * Locators of nodes in DMN Decision navigator panel
 */
public class DecisionNavigatorXPathLocator implements XPathLocator {

    private String xPathLocator;

    private static final String BASE = "//div[@id='decision-graphs-content']//ul/li[@title='%s']/ul/li[@title='%s']/div";

    private DecisionNavigatorXPathLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    /**
     * Construct a xPath locator of Decision Table expression
     * @param nodeName node name of the Decision Table parent node
     * @return xPath Locator of decision table in decision navigator panel
     */
    public static DecisionNavigatorXPathLocator decisionTable(final String nodeName) {
        return new DecisionNavigatorXPathLocator(String.format(BASE, nodeName, "Decision Table"));
    }

    /**
     * Construct a xPath locator of Function expression
     * @param nodeName node name of the Function parent node
     * @return xPath Locator of function in decision navigator panel
     */
    public static DecisionNavigatorXPathLocator function(final String nodeName) {
        return new DecisionNavigatorXPathLocator(String.format(BASE, nodeName, "Function"));
    }

    /**
     * Construct a xPath locator of List expression
     * @param nodeName node name of the List parent node
     * @return xPath Locator of list in decision navigator panel
     */
    public static DecisionNavigatorXPathLocator list(final String nodeName) {
        return new DecisionNavigatorXPathLocator(String.format(BASE, nodeName, "List"));
    }

    /**
     * Construct a xPath locator of List expression
     * @param nodeName node name of the List parent node
     * @return xPath Locator of a node in decision navigator panel
     */
    public static DecisionNavigatorXPathLocator node(final String nodeName) {
        return new DecisionNavigatorXPathLocator(
                String.format("//div[@id='decision-graphs-content']//ul/li[@title='%s']/div",
                              nodeName));
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }
}

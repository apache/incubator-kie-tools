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
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.CommonCSSLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DecisionNavigatorXPathLocator;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionNavigator {

    private static final String NOT_PRESENT_IN_NAVIGATOR = "'%s' was not present in the decision navigator";

    private WaitUtils waitUtils;

    private DecisionNavigator(final WaitUtils waitUtils) {
        this.waitUtils = waitUtils;
    }

    public static DecisionNavigator initialize(final WaitUtils waitUtils) {
        if (waitUtils.isElementInvisible(CommonCSSLocator.expandedNavigator())) {
            waitUtils.waitUntilElementIsVisible(
                    CommonCSSLocator.expandNavigator(),
                    "Decision navigator needs to be expanded before we start test")
                    .click();

            waitUtils.waitUntilElementIsVisible(
                    CommonCSSLocator.expandedNavigator(),
                    "Decision navigator took too long time to expand");
        }

        return new DecisionNavigator(waitUtils);
    }

    public void assertItemIsPresent(final DecisionNavigatorXPathLocator item) {
        waitUtils.waitUntilElementIsVisible(
                item,
                String.format(NOT_PRESENT_IN_NAVIGATOR, item.getXPathLocator()));
    }

    /**
     * Asserts if expected amount of items match the provided locator
     * @param item
     * @param count
     */
    public void assertItemsMatch(final DecisionNavigatorXPathLocator item, final int count) {
        assertThat(
                waitUtils.waitUntilAllElementsAreVisible(item,
                                                         String.format(NOT_PRESENT_IN_NAVIGATOR, item.getXPathLocator())))
                .hasSize(count);
    }

    public void selectItem(final DecisionNavigatorXPathLocator item) {
        waitUtils.waitUntilElementIsVisible(item,
                                            String.format(NOT_PRESENT_IN_NAVIGATOR, item.getXPathLocator()))
                .click();
    }
}

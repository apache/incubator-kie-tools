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

import org.junit.Assert;
import org.kie.workbench.common.dmn.showcase.client.common.wait.WaitUtils;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.EditorXPathLocator;
import org.openqa.selenium.Keys;

/**
 * DRD name editor
 */
public class DrdNameEditor {

    private WaitUtils waitUtils;

    private DrdNameEditor(final WaitUtils waitUtils) {
        this.waitUtils = waitUtils;
    }

    /**
     * Wait until DRD name editor is present
     */
    public static DrdNameEditor initialize(final WaitUtils waitUtils) {
        waitUtils.waitUntilElementIsVisible(EditorXPathLocator.drdNameEditor(),
                                            "DRD Name editor was not found");

        return new DrdNameEditor(waitUtils);
    }

    /**
     * Assert the current value of DRD name editor
     * @param expectedValue - expected DRD name
     */
    public void assertValueEqualTo(final String expectedValue) {
        final String drdName = waitUtils.waitUntilElementIsVisible(EditorXPathLocator.drdNameViewMode(),
                                                                   "Was not able to find drd name value")
                .getText();

        Assert.assertEquals("Expected drd name was different",
                            expectedValue,
                            drdName);
    }

    /**
     * Start DRD name edit mode and append provided text
     * @param text - text typed into
     */
    public void appendText(final String text) {
        waitUtils.waitUntilElementIsVisible(EditorXPathLocator.drdNameViewMode(),
                                            "Was not able to find drd name value")
                .click();

        waitUtils.waitUntilElementIsVisible(EditorXPathLocator.drdNameEditMode(),
                                            "drd name edit mode was not activated")
                .sendKeys(text + Keys.ENTER);
    }
}

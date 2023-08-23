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
 * Locators of Context Menu entries
 */
public class ContextMenuXPathLocator implements XPathLocator {

    private static final String MENU_ENTRY = "//div[@data-field='cellEditorControlsContainer']//ul/li/a/span[text()='%s']";

    private String xPathLocator;

    private ContextMenuXPathLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    /**
     * 'Insert below' can be shown when editing DMN expression grid
     * @return XPath locator of 'Insert below' context menu entry
     */
    public static ContextMenuXPathLocator insertBelow() {
        return new ContextMenuXPathLocator(String.format(MENU_ENTRY, "Insert below"));
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }
}

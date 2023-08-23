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

import org.openqa.selenium.By;

/**
 * Locators of nodes in DMN Decision navigator panel
 */
public class CommonCSSLocator implements DMNDesignerLocator {

    private static final String DECISION_NAVIGATOR_EXPAND = "docks-item-E-org.kie.dmn.decision.navigator";
    private static final String DECISION_NAVIGATOR_EXPANDED = "expanded-docks-bar-E";

    private String cssLocator;

    private CommonCSSLocator(final String cssLocator) {
        this.cssLocator = cssLocator;
    }

    /**
     * Locates button for expanding the Decision Navigator panel
     * @return
     */
    public static CommonCSSLocator expandNavigator() {
        return new CommonCSSLocator(DECISION_NAVIGATOR_EXPAND);
    }

    public static CommonCSSLocator expandedNavigator() {
        return new CommonCSSLocator(DECISION_NAVIGATOR_EXPANDED);
    }

    public static CommonCSSLocator collapseDecisionNavigatorButton() {
        return new CommonCSSLocator("fa-chevron-left");
    }

    /**
     * Locates wrapper of the editor pages - Model, Documentation, Data Types.
     * @return
     */
    public static CommonCSSLocator multiPageEditor() {
        return new CommonCSSLocator("uf-multi-page-editor");
    }

    @Override
    public By locator() {
        return By.className(cssLocator);
    }
}

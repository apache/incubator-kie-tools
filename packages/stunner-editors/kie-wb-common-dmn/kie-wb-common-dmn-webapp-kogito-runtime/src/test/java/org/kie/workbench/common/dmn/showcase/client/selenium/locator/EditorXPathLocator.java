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
 * General Locators of an editor
 */
public class EditorXPathLocator implements XPathLocator {

    private String xPathLocator;

    private EditorXPathLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    public static EditorXPathLocator expressionEditor() {
        return new EditorXPathLocator("//div[@class='kie-dmn-expression-editor']/div/div/input");
    }

    public static EditorXPathLocator expressionAutocompleteEditor() {
        return new EditorXPathLocator("//div[contains(@class,'monaco-editor')]//textarea");
    }

    /**
     * Such element exists just if expression is being edited
     * @return XPath Locator for currently opened expression title
     */
    public static EditorXPathLocator expressionEditorTitle() {
        return new EditorXPathLocator("//p[@class='expression-info-name pf-v5-u-text-truncate name']");
    }

    public static EditorXPathLocator dataTypesTab() {
        return new EditorXPathLocator("//li[@data-ouia-component-type='editor-nav-tab'][@data-ouia-component-id='Data Types']/a");
    }

    /**
     * Component for displaying and renaming the active DRD name
     */
    public static EditorXPathLocator drdNameEditor() {
        return new EditorXPathLocator("//div[@data-field='drdNameEditor']");
    }

    /**
     * Component for displaying active DRD name
     */
    public static EditorXPathLocator drdNameViewMode() {
        return new EditorXPathLocator(drdNameEditor().getXPathLocator() + "/div/span[@data-field='drdName']");
    }

    /**
     * Component for changing active DRD name
     */
    public static EditorXPathLocator drdNameEditMode() {
        return new EditorXPathLocator(drdNameEditor().getXPathLocator() + "/div/input[@data-field='drdNameInput']");
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }
}

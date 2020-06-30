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
 * General Locators of an editor
 */
public class EditorXPathLocator implements XPathLocator {

    private String xPathLocator;

    private EditorXPathLocator(final String xPathLocator) {
        this.xPathLocator = xPathLocator;
    }

    /**
     * Ace editor is shown in case of invalid model is opened. It is an XML editor.
     * @return xPath Locator of an xml/ace editor
     */
    public static EditorXPathLocator aceEditor() {
        return new EditorXPathLocator("//div[@class='ace_content']");
    }

    /**
     * Such element exists just if expression is being edited
     * @return XPath Locator for currently opened expression title
     */
    public static EditorXPathLocator expressionEditorTitle() {
        return new EditorXPathLocator("//div[@class='kie-dmn-expression-type']/span[@data-field='expressionName']");
    }

    @Override
    public String getXPathLocator() {
        return xPathLocator;
    }
}

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

package org.kie.workbench.common.dmn.showcase.client.selenium;

import org.junit.Ignore;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DecisionNavigatorXPathLocator;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.xmlunit.assertj.XmlAssert;

/**
 * Selenium test DMN IntelliSense suggestions
 */
public class DMNDesignerIntelliSenseIT extends DMNDesignerBaseIT {

    @Ignore("No more compatible with React Based Boxed Expression")
    public void testNumberSuggestions() throws Exception {
        final String expected = loadResource("decision-expression-literal.xml");
        setContent(expected);

        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.literalExpression("Decision-1"));

        // start edit mode
        final WebElement expressionEditor = getEditor();
        expressionEditor.sendKeys(Keys.ENTER);

        // simulate and we do sum '123 +'
        getAutocompleteEditor().sendKeys(Keys.END, " + ");

        // invoke suggestions
        getAutocompleteEditor().sendKeys(Keys.CONTROL, Keys.SPACE);

        // Skip the first suggestion (Decision-1) and confirm choice
        getAutocompleteEditor().sendKeys(Keys.ARROW_DOWN, Keys.ENTER);

        // finish edit mode
        getAutocompleteEditor().sendKeys(Keys.TAB);

        final String actual = getContent();

        XmlAssert.assertThat(actual)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmn:decision[@id='_4FEA7589-823B-4880-BCFA-AF2F9B145785']" +
                                  "/dmn:literalExpression[@id='_35DB53A6-97E7-4D48-9E5A-59CE0015CEF8']" +
                                  "/dmn:text[text()='123 + number(, , )']");
    }
}

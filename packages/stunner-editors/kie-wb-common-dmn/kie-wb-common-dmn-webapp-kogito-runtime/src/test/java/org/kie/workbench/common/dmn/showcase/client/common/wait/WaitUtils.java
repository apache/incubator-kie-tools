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

package org.kie.workbench.common.dmn.showcase.client.common.wait;

import java.time.Duration;
import java.util.List;

import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DMNDesignerLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class WaitUtils {

    private final WebDriver driver;

    public WaitUtils(final WebDriver driver) {
        this.driver = driver;
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WebElement waitUntilElementIsVisible(final DMNDesignerLocator elementLocator,
                                                final String customErrorMessage) {
        return waitOperation()
                .withMessage(customErrorMessage)
                .until(visibilityOfElementLocated(elementLocator.locator()));
    }

    public WebElement waitUntilElementIsPresent(final DMNDesignerLocator elementLocator,
                                                final String customErrorMessage) {
        return waitOperation()
                .withMessage(customErrorMessage)
                .until(presenceOfElementLocated(elementLocator.locator()));
    }

    public List<WebElement> waitUntilAllElementsAreVisible(final DMNDesignerLocator elementLocator,
                                                           final String customErrorMessage) {
        return waitOperation()
                .withMessage(customErrorMessage)
                .until(visibilityOfAllElementsLocatedBy(elementLocator.locator()));
    }

    public boolean isElementInvisible(final DMNDesignerLocator elementLocator) {
        return waitOperation()
                .until(invisibilityOfElementLocated(elementLocator.locator()));
    }
}

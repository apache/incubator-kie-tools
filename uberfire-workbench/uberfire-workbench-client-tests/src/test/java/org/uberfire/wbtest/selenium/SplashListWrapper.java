/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A Selenium Page Object representing the MenuSplashList widget.
 */
public class SplashListWrapper {

    private final WebElement splashList;

    public SplashListWrapper(WebDriver driver,
                             String elementId) {
        splashList = driver.findElement(By.id(elementId));
    }

    /**
     * Returns a list of the items in the dropdown list. Each string in the result is trimmed of leading and trailing
     * whitespace.
     */
    public List<String> getContents() {
        List<String> contents = new ArrayList<String>();
        splashList.findElement(By.className("dropdown-toggle")).click();
        for (WebElement e : splashList.findElements(By.tagName("li"))) {
            contents.add(e.getText());
        }
        splashList.findElement(By.className("dropdown-toggle")).click();
        return contents;
    }
}

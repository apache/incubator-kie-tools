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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

/**
 * Abstraction over the state and behaviour of the stuff in the {@code org.uberfire.wbtest.client.panels.custom} package.
 */
public class CustomPanelScreenWrapper {

    private final WebDriver driver;

    public CustomPanelScreenWrapper(WebDriver driver) {
        this.driver = checkNotNull("driver",
                                   driver);
    }

    public int getLiveInstanceCount() {
        WebElement label = findElement("liveCustomPanelInstances");
        Pattern p = Pattern.compile("Live Instances: ([0-9]*)");
        Matcher m = p.matcher(label.getText());
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalStateException("Couldn't find live instance count label on page");
    }

    public int getTotalInstanceCount() {
        WebElement label = findElement("totalCustomPanelInstances");
        Pattern p = Pattern.compile("Total Instances: ([0-9]*)");
        Matcher m = p.matcher(label.getText());
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalStateException("Couldn't find total instance count label on page");
    }

    public String createNewCustomPopup() {
        int previousTotalInstances = getTotalInstanceCount();
        WebElement button = findElement("open");
        button.click();
        return "CustomPanelContentScreen-" + previousTotalInstances;
    }

    public boolean customPopupExistsInDom(String id) {
        List<WebElement> found = driver.findElements(By.id("gwt-debug-" + id));
        return !found.isEmpty();
    }

    public void closeLatestNewPopupUsingPlaceManager() {
        WebElement button = findElement("closeWithPlaceManager");
        button.click();
    }

    public void closeLatestNewPopupByRemovingFromDom() {
        WebElement button = findElement("closeByRemovingFromDom");
        button.click();
    }

    public String createReusableCustomPopup() {
        WebElement button = findElement("openReusable");
        button.click();
        return "CustomPanelContentScreen-reusable";
    }

    public void closeReusablePopupUsingPlaceManager() {
        WebElement button = findElement("closeReusableWithPlaceManager");
        button.click();
    }

    public void closeReusablePopupByRemovingFromDom() {
        WebElement button = findElement("closeReusableByRemovingFromDom");
        button.click();
    }

    /**
     * Allows looking up CustomPanelContentScreen elements by their unqualified name.
     *
     * @param shortName the name without the gwt-debug-CustomPanelMakerScreen- prefix.
     */
    private WebElement findElement(String shortName) {
        return driver.findElement(By.id("gwt-debug-CustomPanelMakerScreen-" + shortName));
    }
}

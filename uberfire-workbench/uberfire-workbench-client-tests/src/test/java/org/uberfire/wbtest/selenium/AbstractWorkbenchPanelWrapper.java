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

import org.kie.soup.commons.validation.PortablePreconditions;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Base class for Selenium Page Objects that wrap UberFire workbench panels.
 */
public abstract class AbstractWorkbenchPanelWrapper {

    protected WebDriver driver;
    protected String panelId;

    public AbstractWorkbenchPanelWrapper(WebDriver driver,
                                         String panelId) {
        this.driver = PortablePreconditions.checkNotNull("driver",
                                                         driver);
        this.panelId = PortablePreconditions.checkNotNull("panelId",
                                                          panelId);
    }

    /**
     * Clicks the maximize toggle button on the panel that this wrapper wraps.
     */
    public void clickMaximizeButton() {
        WebElement button = driver.findElement(By.id("gwt-debug-" + panelId + "-maximizeButton"));
        button.click();
    }

    public boolean isObscuredBy(AbstractWorkbenchPanelWrapper bigger) {
        Dimension mySize = getSize();
        Point myPos = getLocation();
        Dimension biggerSize = bigger.getSize();
        Point biggerPos = bigger.getLocation();

        return biggerPos.x <= myPos.x &&
                biggerPos.y <= myPos.y &&
                biggerSize.width >= mySize.width &&
                biggerSize.height >= mySize.height;
    }

    public Dimension getSize() {
        return driver.findElement(By.id(panelId)).getSize();
    }

    public Point getLocation() {
        return driver.findElement(By.id(panelId)).getLocation();
    }
}

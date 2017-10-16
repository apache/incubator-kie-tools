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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

/**
 * A Selenium "page object" for the TopHeader, which contains facilities for adding child panels to the root panel.
 */
public class TopHeaderWrapper {

    private final WebDriver driver;

    public TopHeaderWrapper(WebDriver driver) {
        this.driver = checkNotNull("driver",
                                   driver);
    }

    public void addPanelToRoot(CompassPosition position,
                               Class<? extends WorkbenchPanelPresenter> panelType,
                               Class<? extends WorkbenchScreenActivity> screen,
                               String... params) {

        WebElement placeIdBox = driver.findElement(By.id("newPanelPartPlace"));
        placeIdBox.clear();
        StringBuilder placeWithParams = new StringBuilder();
        placeWithParams.append(screen.getName());
        for (int i = 0; i < params.length; i += 2) {
            if (i == 0) {
                placeWithParams.append("?");
            } else {
                placeWithParams.append("&");
            }
            placeWithParams.append(params[i]).append("=").append(params[i + 1]);
        }
        placeIdBox.sendKeys(placeWithParams);

        WebElement panelTypeBox = driver.findElement(By.id("newPanelType"));
        panelTypeBox.clear();
        panelTypeBox.sendKeys(panelType.getName());

        WebElement positionBox = driver.findElement(By.id("newPanelPosition"));
        positionBox.clear();
        positionBox.sendKeys(position.name());

        WebElement newPanelButton = driver.findElement(By.id("newPanelButton"));
        newPanelButton.click();
    }
}

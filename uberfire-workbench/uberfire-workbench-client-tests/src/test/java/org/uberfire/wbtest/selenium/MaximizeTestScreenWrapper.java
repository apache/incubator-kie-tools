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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MaximizeTestScreenWrapper {

    private final WebDriver driver;
    private final String id;

    public MaximizeTestScreenWrapper(WebDriver driver,
                                     String id) {
        this.driver = driver;
        this.id = "MaximizeTestScreen-" + id;
    }

    /**
     * Searches the DOM for the screen having the ID this wrapper cares about. Throws a timeout exception if the
     * widget is not found within the driver's current implicit wait period.
     */
    public WebElement find() {
        return driver.findElement(By.id(id));
    }

    /**
     * Returns the size that the resize test widget's element actually has (according to the DOM).
     */
    public Dimension getActualSize() {
        WebElement element = find();
        return element.getSize();
    }

    /**
     * Returns the size that the screen reports it has.
     */
    public Dimension getReportedSize() {
        WebElement element = driver.findElement(By.id(id + "-sizeLabel"));
        String text = element.getText();
        Matcher matcher = Pattern.compile("([0-9]+)x([0-9]+)").matcher(text);
        if (matcher.matches()) {
            return new Dimension(Integer.parseInt(matcher.group(1)),
                                 Integer.parseInt(matcher.group(2)));
        }
        throw new IllegalStateException("Couldn't understand reported size \"" + text + "\"");
    }
}

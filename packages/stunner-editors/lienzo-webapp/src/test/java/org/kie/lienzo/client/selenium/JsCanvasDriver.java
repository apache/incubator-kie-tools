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


package org.kie.lienzo.client.selenium;

import java.io.File;
import java.time.Duration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JsCanvasDriver extends JsCanvasExecutor {

    private static final String INDEX_HTML = "target/lienzo-webapp-0.0.0/LienzoShowcase.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();
    private final WebDriver driver;
    // TODO: Refactor/remove use of loadTimeMillis by using selenium until conditions
    private long loadTimeMillis = 1000;

    public static void init() {
        WebDriverManager.firefoxdriver().setup();
    }

    public static JsCanvasDriver devMode() {
        JsCanvasDriver instance = build("http://127.0.0.1:8888/LienzoShowcase.html");
        instance.loadTimeMillis = 3000;
        return instance;
    }

    public static JsCanvasDriver build() {
        return build(INDEX_HTML_PATH);
    }

    public static JsCanvasDriver build(String url) {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        WebDriver driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();
        driver.get(url);
        return new JsCanvasDriver(driver);
    }

    public JsCanvasDriver(WebDriver driver) {
        super((JavascriptExecutor) driver);
        this.driver = driver;
    }

    public void openTest(int index) {
        sleep(loadTimeMillis);
        executor.executeScript("window.jsCanvasExamples.goToExample(arguments[0])", index);
        sleep(500);
    }

    public void closeTest() {
        sleep(1000);
        // TODO driver.close();
    }

    private WebDriverWait waitOperation(long seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    private WebDriverWait waitAppLoaded() {
        return new WebDriverWait(driver, Duration.ofSeconds(5));
    }
}

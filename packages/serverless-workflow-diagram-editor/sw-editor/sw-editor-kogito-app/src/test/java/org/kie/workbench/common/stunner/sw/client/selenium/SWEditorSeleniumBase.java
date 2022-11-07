/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.selenium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class SWEditorSeleniumBase {

    protected static final Logger LOG = LoggerFactory.getLogger(SWEditorSeleniumBase.class);

    protected static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"SWDiagramEditor\").get().setContent(\"\", '%s')";
    protected static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"SWDiagramEditor\").get().getContent()";

    protected static final String CONTENT_REGULAR_CHAR = "\\\"";
    protected static final String CONTENT_EXECUTOR_CHAR = "&quot;";

    private static final String INDEX_HTML = "target/sw-editor-kogito-app/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String DIAGRAM_PANEL = "qe-static-workbench-panel-view";
    protected static final String CANVAS_PANEL = "canvas-panel";
    private static final Boolean HEADLESS = Boolean.valueOf(System.getProperty("org.kie.sw.editor.browser.headless"));

    /**
     * Selenium web driver
     */
    protected WebDriver driver;

    protected JsCanvasHelper jsHelper;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().useMirror().setup();
    }

    @Before
    public void openSWEditor() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(HEADLESS);
        driver = new FirefoxDriver(firefoxOptions);

        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        jsHelper = new JsCanvasHelper((JavascriptExecutor) driver);

        final WebElement designer = waitOperation()
                .until(presenceOfElementLocated(className(DIAGRAM_PANEL)));
        assertThat(designer)
                .as("Diagram panel is a prerequisite for all tests. " +
                            "its absence is indicator of designer load fail.")
                .isNotNull();
    }

    protected void waitCanvasPanel() {
        final WebElement canvasPanelDiv = waitOperation()
                .until(visibilityOfElementLocated(className(CANVAS_PANEL)));
        assertThat(canvasPanelDiv)
                .as("Once content is set canvas panel visibility is a prerequisite" +
                            "for further test execution.")
                .isNotNull();
    }

    protected void setContent(final String xml) {
        try {
            String content = String.format(SET_CONTENT_TEMPLATE, xml);
            content = content.replace(CONTENT_REGULAR_CHAR, CONTENT_EXECUTOR_CHAR);
            ((JavascriptExecutor) driver).executeScript(content);
        } catch (Exception e) {
            LOG.error("Exception during JS execution. Ex: {}", e.getMessage());
        }
    }

    protected String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_CONTENT_TEMPLATE));
        assertThat(result).isInstanceOf(String.class);

        String content = (String) result;
        content = content.replace(CONTENT_EXECUTOR_CHAR, CONTENT_REGULAR_CHAR);
        return content;
    }

    /**
     * Use this for loading serverless workflow definitions placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    protected String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    protected ExpectedCondition<WebElement> element(final String xpathLocator, final String... parameters) {
        return visibilityOfElementLocated(xpath(String.format(xpathLocator, parameters)));
    }

    protected WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    }
}

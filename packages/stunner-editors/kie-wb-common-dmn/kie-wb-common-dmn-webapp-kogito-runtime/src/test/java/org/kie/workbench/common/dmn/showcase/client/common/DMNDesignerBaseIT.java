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

package org.kie.workbench.common.dmn.showcase.client.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.workbench.common.dmn.showcase.client.common.wait.WaitUtils;
import org.kie.workbench.common.dmn.showcase.client.selenium.component.DecisionNavigator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.CommonCSSLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.EditorXPathLocator;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.PropertiesPanelXPathLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.assertj.XmlAssert;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.DC;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.DI;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.DMN;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.DMNDI;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.KIE;

public class DMNDesignerBaseIT {

    protected static final Map<String, String> NAMESPACES =
            Stream.of(new AbstractMap.SimpleEntry<>(DMN.getPrefix(), DMN.getUri()),
                      new AbstractMap.SimpleEntry<>(DMNDI.getPrefix(), DMNDI.getUri()),
                      new AbstractMap.SimpleEntry<>(DI.getPrefix(), DI.getUri()),
                      new AbstractMap.SimpleEntry<>(DC.getPrefix(), DC.getUri()),
                      new AbstractMap.SimpleEntry<>(KIE.getPrefix(), KIE.getUri()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static final Logger LOG = LoggerFactory.getLogger(DMNDesignerBaseIT.class);

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get('DMNDiagramEditor').get().setContent('', `%s`)";

    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get('DMNDiagramEditor').get().getContent()";

    private static final String INDEX_HTML = "target/kie-wb-common-dmn-webapp-kogito-runtime/index.html";

    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String SCREENSHOTS_DIR = System.getProperty("org.kie.dmn.kogito.screenshots.dir");

    private WebDriver driver;

    protected WaitUtils waitUtils;

    protected DecisionNavigator decisionNavigator;

    protected WebElement propertiesPanel;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @Before
    public void openDMNDesigner() {
        driver = new FirefoxDriver(getFirefoxOptions());
        driver.get(INDEX_HTML_PATH);
        driver.manage().window().maximize();

        waitUtils = new WaitUtils(driver);
        decisionNavigator = DecisionNavigator.initialize(waitUtils);

        waitDMNDesignerElements();
    }

    private FirefoxOptions getFirefoxOptions() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
       // firefoxOptions.addArguments("--headless");
        return firefoxOptions;
    }

    @Rule
    public TestWatcher takeScreenShotAndCleanUp = new TestWatcher() {
        @Override
        protected void failed(final Throwable e,
                              final Description description) {
            saveScreenShot(description);
        }

        @Override
        protected void finished(final Description description) {
            quitDriver();
        }
    };

    protected void resetPage() {
        quitDriver();
        openDMNDesigner();
    }

    private void quitDriver() {
        getDriver().ifPresent(WebDriver::quit);
    }

    private Optional<WebDriver> getDriver() {
        return Optional.ofNullable(driver);
    }

    private void waitDMNDesignerElements() {
        propertiesPanel = waitUtils.waitUntilElementIsVisible(
                PropertiesPanelXPathLocator.propertiesPanelButton(),
                "Presence of properties panel expand button is prerequisite for all tests");
    }

    private final File screenshotDirectory = initScreenshotDirectory();

    /**
     * Use this for loading DMN model placed in src/test/resources
     * @param filePath relative path of the file
     * @return Text content of the file
     */
    protected String loadResource(final String filePath) throws IOException {
        final InputStream stream = this.getClass().getResourceAsStream(filePath);
        return String.join("", IOUtils.readLines(stream, StandardCharsets.UTF_8));
    }

    protected void setContent(final String xml) {
        ((JavascriptExecutor) driver).executeScript(String.format(SET_CONTENT_TEMPLATE, xml));
        waitUtils.waitUntilElementIsVisible(
                CommonCSSLocator.multiPageEditor(),
                "Designer was not loaded");
    }

    protected String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(GET_CONTENT_TEMPLATE);
        assertThat(result).isInstanceOf(String.class);
        return (String) result;
    }

    protected void selectAutomaticLayout() {
        try {
            driver.findElement(By.xpath("//button[@data-field='yes-button']")).click();
        } catch (NoSuchElementException e) {
            // if there is no button, allow test to continue
        }
    }

    protected void executeDMNTestCase(final String directory,
                                      final String file,
                                      final String logMessage) throws IOException {
        final List<String> ignoredAttributes = asList("id", "dmnElementRef");

        LOG.trace(logMessage);
        setContent(loadResource(directory + "/" + file));

        selectAutomaticLayout();

        final String actual = getContent();
        assertThat(actual).isNotBlank();
        final String expected = loadResource(directory + "-expected/" + file);

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ignoredAttributes.contains(attr.getName()))
                .areSimilar();
    }

    /**
     * Returned element is a handle for navigating and invoking edit mode of expression cells
     *
     * getEditor().sendKeys(Keys.ENTER) - start edit mode of the cell
     * getEditor().sendKeys(Keys.ARROW_DOWN) - move cell selection down by one
     */
    protected WebElement getEditor() {
        final WebElement editor = waitUtils.waitUntilElementIsPresent(EditorXPathLocator.expressionEditor(),
                                                                      "Expression editor probably not activated");

        return editor;
    }

    /**
     * Returned element is a handle for typing text into expression
     *
     * Prerequisite:
     * getEditor().sendKeys(Keys.ENTER) - start edit mode of the cell
     *
     * getAutocompleteEditor().sendKeys(Keys.CONTROL, Keys.SPACE) - display autocomplete suggestions
     * getAutocompleteEditor().sendKeys(Keys.TAB) - finish edit mode
     */
    protected WebElement getAutocompleteEditor() {
        final WebElement editor = waitUtils.waitUntilElementIsPresent(EditorXPathLocator.expressionAutocompleteEditor(),
                                                                      "Autocompletion not shown");

        return editor;
    }

    protected void saveScreenShot(final String... prefixes) {

        final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        final List<String> fileNameParts = new ArrayList<>(asList(prefixes));
        final String filename = String.join("_", fileNameParts);

        try {
            copyFile(screenshotFile, new File(screenshotDirectory, filename + ".png"));
        } catch (IOException ioe) {
            LOG.error("Unable to take screenshot", ioe);
        }
    }

    private void saveScreenShot(final Description description) {
        final String testClassName = description.getTestClass().getSimpleName();
        final String testMethodName = description.getMethodName();
        saveScreenShot(testClassName, testMethodName);
    }

    private File initScreenshotDirectory() {
        if (SCREENSHOTS_DIR == null) {
            throw new IllegalStateException(
                    "Property org.kie.dmn.kogito.screenshots.dir (where screenshot taken by WebDriver will be put) was null");
        }
        File scd = new File(SCREENSHOTS_DIR);
        if (!scd.exists()) {
            boolean mkdirSuccess = scd.mkdir();
            if (!mkdirSuccess) {
                throw new IllegalStateException("Creation of screenshots dir failed " + scd);
            }
        }
        if (!scd.canWrite()) {
            throw new IllegalStateException("The screenshotDir must be writable" + scd);
        }
        return scd;
    }
}

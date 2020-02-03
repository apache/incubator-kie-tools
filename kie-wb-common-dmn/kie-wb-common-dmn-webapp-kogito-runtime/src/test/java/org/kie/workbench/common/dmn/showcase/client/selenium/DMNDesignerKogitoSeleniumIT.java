/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.selenium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xmlunit.assertj.XmlAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * Selenium test for DMN Designer - client site marshalling version
 * The Designer is represented by single webpage - index.html
 */
public class DMNDesignerKogitoSeleniumIT {

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"DMNDiagramEditor\").get().setContent(\"\",\"%s\")";

    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"DMNDiagramEditor\").get().getContent()";

    private static final String INDEX_HTML = "target/kie-wb-common-dmn-webapp-kogito-runtime/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String DECISON_NAVIGATOR_EXPAND = "qe-docks-item-W-org.kie.dmn.decision.navigator";
    private static final String DECISON_NAVIGATOR_EXPANDED = "qe-docks-bar-expanded-W";
    private static final String GRAPH_NODES = "decision-graphs-content";

    private WebDriver driver;

    private WebElement decisionNavigatorExpandButton;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @Before
    public void openDMNDesigner() {

        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(true);
        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        decisionNavigatorExpandButton = waitOperation()
                .until(visibilityOfElementLocated(By.className(DECISON_NAVIGATOR_EXPAND)));
        assertThat(decisionNavigatorExpandButton)
                .as("Presence of decision navigator expand button is prerequisite for all tests")
                .isNotNull();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testNewDiagram() throws Exception {
        final String expected = loadResource("new-diagram.xml");
        setContent("");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        //Skip, id, name and namespace in the comparison as they are dynamically created at runtime
        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !(Objects.equals(attr.getName(), "id")
                        || Objects.equals(attr.getName(), "name")
                        || Objects.equals(attr.getName(), "namespace")))
                .areIdentical();
    }

    @Test
    public void testBasicModel() throws Exception {
        final String expected = loadResource("basic-model.xml");
        setContent(expected);

        assertDiagramNodeIsPresentInDecisionNavigator("CurrentIndex");
        assertDiagramNodeIsPresentInDecisionNavigator("NextIndex");

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testBusinessKnowledgeModel() throws Exception {
        final String expected = loadResource("business-knowledge-model.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testInputData() throws Exception {
        final String expected = loadResource("input-data.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testKnowledgeSource() throws Exception {
        final String expected = loadResource("knowledge-source.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDecisionEmpty() throws Exception {
        final String expected = loadResource("decision-empty.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testDecisionServiceEmpty() throws Exception {
        final String expected = loadResource("decision-service-empty.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual).and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .areIdentical();
    }

    private void assertDiagramNodeIsPresentInDecisionNavigator(final String nodeName) {
        expandDecisionNavigatorDock();
        final By nodeLocator = By.xpath(String.format(".//ul/li[@title='%s']", nodeName));
        final WebElement node = waitOperation()
                .until(visibilityOf(graphNodesList().findElement(nodeLocator)));
        assertThat(node)
                .as("Node '" + nodeName + "'was not present in the list of nodes")
                .isNotNull();
        collapseDecisionNavigatorDock();
    }

    private void setContent(final String xml) {
        ((JavascriptExecutor) driver).executeScript(String.format(SET_CONTENT_TEMPLATE, xml));
        final WebElement designer = waitOperation()
                .until(visibilityOfElementLocated(By.className("uf-multi-page-editor")));
        assertThat(designer)
                .as("Designer was not loaded")
                .isNotNull();
    }

    private String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_CONTENT_TEMPLATE));
        assertThat(result).isInstanceOf(String.class);
        return (String) result;
    }

    /**
     * Use this for loading DMN model placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    private WebElement graphNodesList() {
        final WebElement graphNodes = waitOperation()
                .until(visibilityOfElementLocated(By.id(GRAPH_NODES)));
        assertThat(graphNodes)
                .as("List of graph nodes is not present")
                .isNotNull();
        return graphNodes;
    }

    private void expandDecisionNavigatorDock() {
        decisionNavigatorExpandButton.click();
    }

    private void collapseDecisionNavigatorDock() {
        final WebElement expandedDecisionNavigator = waitOperation()
                .until(visibilityOfElementLocated(By.className(DECISON_NAVIGATOR_EXPANDED)));
        assertThat(expandedDecisionNavigator)
                .as("Unable to locate expanded decision navigator dock")
                .isNotNull();

        expandedDecisionNavigator.findElement(By.className("fa-chevron-left")).click();
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    }
}

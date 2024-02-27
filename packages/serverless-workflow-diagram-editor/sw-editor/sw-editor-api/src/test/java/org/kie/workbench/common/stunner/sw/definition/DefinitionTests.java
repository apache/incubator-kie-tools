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


package org.kie.workbench.common.stunner.sw.definition;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class DefinitionTests {

    private static final Logger LOG = LoggerFactory.getLogger(DefinitionTests.class);

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"SWDiagramEditor\").get().setContent(\"\", '%s')";
    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"SWDiagramEditor\").get().getContent()";

    private static final String CONTENT_REGULAR_CHAR = "\\\"";
    private static final String CONTENT_EXECUTOR_CHAR = "&quot;";

    private static final String INDEX_HTML = "../sw-editor-kogito-app/target/sw-editor-kogito-app/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String DIAGRAM_PANEL = "root-container";
    private static final String CANVAS_PANEL = "canvas-panel";
    /**
     * Selenium web driver
     */
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @Before
    public void openSWEditor() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        driver = new FirefoxDriver(firefoxOptions);

        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        final WebElement designer = waitOperation()
                .until(presenceOfElementLocated(className(DIAGRAM_PANEL)));
        assertThat(designer)
                .as("Diagram panel is a prerequisite for all tests. " +
                            "its absence is indicator of designer load fail.")
                .isNotNull();
    }

    @Rule
    public TestWatcher endTestAndCleanUp = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            LOG.info("Test passed: " +testClassName + " - " + testMethodName);
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            LOG.warn("Test skipped: " + testClassName + " - " + testMethodName);
            LOG.warn("Message: " + e.getMessage());
            LOG.warn("Stack Trace: " + e.getStackTrace());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            LOG.error("Test failed: " + testClassName + " - " + testMethodName);
            LOG.error("Message: " + e.getMessage());
            LOG.error("Stack Trace: " + e.getStackTrace());
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
            }
        }
    };

    @Test
    public void testActionNode() throws Exception {
        testExample("ActionNode.sw.json");
    }

    @Test
    public void testActionsContainer() throws Exception {
        testExample("ActionsContainer.sw.json");
    }

    @Test
    public void testActionTransition() throws Exception {
        testExample("ActionTransition.sw.json");
    }

    @Test
    public void testCallbackState() throws Exception {
        testExample("CallbackState.sw.json");
    }

    @Test
    public void testCallEventAction() throws Exception {
        testExample("CallEventAction.sw.json");
    }

    @Test
    public void testCallFunctionAction() throws Exception {
        testExample("CallFunctionAction.sw.json");
    }

    @Test
    public void testCallSubflowAction() throws Exception {
        testExample("CallSubflowAction.sw.json");
    }

    @Test
    public void testCompensationTransition() throws Exception {
        testExample("CompensationTransition.sw.json");
    }

    @Test
    public void testDataConditionTransition() throws Exception {
        testExample("DataConditionTransition.sw.json");
    }

    @Test
    public void testDefaultConditionTransition() throws Exception {
        testExample("DefaultConditionTransition.sw.json");
    }

    @Test
    public void testEnd() throws Exception {
        testExample("End1.sw.json");
        testExample("End2.sw.json");
    }

    @Test
    public void testErrorTransition() throws Exception {
        testExample("ErrorTransition.sw.json");
    }

    @Test
    public void testEvent() throws Exception {
        testExample("Event.sw.json");
    }

    @Test
    public void testEventConditionTransition() throws Exception {
        testExample("EventConditionTransition.sw.json");
    }

    @Test
    public void testEventRef() throws Exception {
        testExample("EventRef.sw.json");
    }

    @Test
    public void testEventState() throws Exception {
        testExample("EventState.sw.json");
    }

    @Test
    public void testEventTimeout() throws Exception {
        testExample("EventTimeout.sw.json");
    }

    @Test
    public void testForEachState() throws Exception {
        testExample("ForEachState.sw.json");
    }

    @Test
    public void testInjectState() throws Exception {
        testExample("InjectState.sw.json");
    }


    @Test
    public void testOnEvent() throws Exception {
        testExample("OnEvent.sw.json");
    }

    @Test
    public void testOperationState() throws Exception {
        testExample("OperationState.sw.json");
    }

    @Test
    public void testParallelState() throws Exception {
        testExample("ParallelState.sw.json");
    }

    @Test
    public void testSleepState() throws Exception {
        testExample("SleepState.sw.json");
    }

    @Test
    public void testStart() throws Exception {
        testExample("Start1.sw.json");
        testExample("Start2.sw.json");
        testExample("Start3.sw.json");
        testExample("Start4.sw.json");
        testExample("Start5.sw.json");
    }

    @Test
    public void testStartTransition() throws Exception {
        testExample("StartTransition.sw.json");
    }

    @Test
    public void testState() throws Exception {
        testExample("State.sw.json");
    }

    @Test
    public void testSwitchState() throws Exception {
        testExample("SwitchState.sw.json");
    }

    @Test
    public void testTimeout() throws Exception {
        testExample("Timeout.sw.json");
    }

    @Test
    public void testTransition() throws Exception {
        testExample("Transition.sw.json");
    }

    @Test
    public void testWorkflow() throws Exception {
        testExample("Workflow.sw.json");
    }

    private void testExample(String exampleName) throws Exception {
        final String expected = loadResource(exampleName);
        setContent(expected);

        waitCanvasPanel();

        final String actual = getContent();
        assertThatJson(json(actual)).isEqualTo(json(expected));
    }

    private void waitCanvasPanel() {
        final WebElement canvasPanelDiv = waitOperation()
                .until(visibilityOfElementLocated(className(CANVAS_PANEL)));
        assertThat(canvasPanelDiv)
                .as("Once content is set canvas panel visibility is a prerequisite" +
                            "for further test execution.")
                .isNotNull();
    }

    private void setContent(final String xml) {
        try {
            String content = String.format(SET_CONTENT_TEMPLATE, xml);
            content = content.replace(CONTENT_REGULAR_CHAR, CONTENT_EXECUTOR_CHAR);
            ((JavascriptExecutor) driver).executeScript(content);
        } catch (Exception e) {
            LOG.error("Exception during JS execution. Ex: {}", e.getMessage());
        }
    }

    private String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_CONTENT_TEMPLATE));
        assertThat(result).isInstanceOf(String.class);

        String content = (String) result;
        content = content.replace(CONTENT_EXECUTOR_CHAR, CONTENT_REGULAR_CHAR);
        return content;
    }

    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    private ExpectedCondition<WebElement> element(final String xpathLocator, final String... parameters) {
        return visibilityOfElementLocated(xpath(String.format(xpathLocator, parameters)));
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(2));
    }
}

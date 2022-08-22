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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class SWEditorSeleniumIT {

    private static final Logger LOG = LoggerFactory.getLogger(SWEditorSeleniumIT.class);

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"SWDiagramEditor\").get().setContent(\"\", '%s')";
    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"SWDiagramEditor\").get().getContent()";

    private static final String CONTENT_REGULAR_CHAR = "\\\"";
    private static final String CONTENT_EXECUTOR_CHAR = "&quot;";

    private static final String INDEX_HTML = "target/sw-editor-kogito-app/index.html";
    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String DIAGRAM_PANEL = "qe-static-workbench-panel-view";
    private static final String CANVAS_PANEL = "canvas-panel";
    private static final Boolean HEADLESS = Boolean.valueOf(System.getProperty("org.kie.sw.editor.browser.headless"));
    private static final String SCREENSHOTS_DIR = System.getProperty("org.kie.sw.editor.screenshots.dir");

    /**
     * Selenium web driver
     */
    private WebDriver driver;

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

        final WebElement designer = waitOperation()
                .until(presenceOfElementLocated(className(DIAGRAM_PANEL)));
        assertThat(designer)
                .as("Diagram panel is a prerequisite for all tests. " +
                            "its absence is indicator of designer load fail.")
                .isNotNull();
    }

    private final File screenshotDirectory = initScreenshotDirectory();

    @Rule
    public TestWatcher takeScreenShotAndCleanUp = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            final String filename = testClassName + "_" + testMethodName;
            try {
                copyFile(screenshotFile, new File(screenshotDirectory, filename + ".png"));
            } catch (IOException ioe) {
                LOG.error("Unable to take screenshot", ioe);
            }
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
            }
        }
    };

    @Test
    public void testHelloWorldExample() throws Exception {
        testExample("HelloWorldExample.sw.json");
    }

    @Test
    public void testEventBasedGreetingsExample() throws Exception {
        testExample("EventBasedGreetingExample.sw.json");
    }

    @Test
    public void testSolveMathProblemsExample() throws Exception {
        testExample("SolveMathProblemsExample.sw.json");
    }

    @Test
    public void testParallelExecutionExample() throws Exception {
        testExample("ParallelExecutionExample.sw.json");
    }

    @Test
    public void testAsyncFunctionInvocationExample() throws Exception {
        testExample("AsyncFunctionInvocationExample.sw.json");
    }

    @Test
    public void testAsyncSubFlowInvocationExample() throws Exception {
        testExample("AsyncSubFlowInvocationExample.sw.json");
    }

    @Test
    public void testEventBasedSwitchStateExample() throws Exception {
        testExample("EventBasedSwitchStateExample.sw.json");
    }

    @Test
    public void testProvisionOrdersExample() throws Exception {
        testExample("ProvisionOrdersExample.sw.json");
    }

    @Test
    public void testCustomerCreditCheckExample() throws Exception {
        testExample("CustomerCreditCheckExample.sw.json");
    }

    @Test
    public void testJobMonitoringExample() throws Exception {
        testExample("JobMonitoringExample.sw.json");
    }

    @Test
    public void testSendCloudEventOnProvisionExample() throws Exception {
        testExample("SendCloudEventOnProvisionExample.sw.json");
    }  

    @Test
    public void testApplicantRequestDecisionExample() throws Exception {
        testExample("ApplicantRequestDecisionExample.sw.json");
    }

    @Test
    public void testMonitorPatientVitalSignsExample() throws Exception {
        testExample("MonitorPatientVitalSignsExample.sw.json");
    }

    @Test
    public void testFinalizeCollegeExample() throws Exception {
        testExample("FinalizeCollegeApplicationExample.sw.json");
    }
    
    @Test
    public void testHandleCarAuctionBidExample() throws Exception {
        testExample("HandleCarAuctionBidExample.sw.json");
    } 

    @Test
    public void testCheckInboxPeriodicallyExample() throws Exception {
        testExample("CheckInboxPeriodicallyExample.sw.json");
    }
    
    @Test
    public void testEventBasedServiceInvocation() throws Exception {
        testExample("EventBasedServiceInvocationExample.sw.json");
    }

    @Test
    public void testNewPatientOnboardingExample() throws Exception {
        testExample("NewPatientOnboardingExample.sw.json");
    }

    @Test
    public void testPurchaseOrderDeadlineExample() throws Exception {
        testExample("PurchaseOrderDeadlineExample.sw.json");
    }

    @Test
    public void testAccumulateRoomReadingsExample() throws Exception {
        testExample("AccumulateRoomReadingsExample.sw.json");
    }

    @Test
    public void testCarVitalsCheckExample() throws Exception {
        testExample("CarVitalsCheckExample.sw.json");
    }

    @Test
    public void testBookLendingExample() throws Exception {
        testExample("BookLendingExample.sw.json");
    }

    @Test
    public void testFillGlassOfWaterExample() throws Exception {
        testExample("FillGlassOfWaterExample.sw.json");
    }

    @Test
    public void testNotifyCustomerWorkflowExample() throws Exception {
        testExample("NotifyCustomerWorkflowExample.sw.json");
    }

    @Test
    public void testProcessTransactionsExample() throws Exception {
        testExample("ProcessTransactionsExample.sw.json");
    }

    private void testExample(String exampleName) throws Exception {
        final String expected = loadResource(exampleName);
        setContent(expected);

        waitCanvasPanel();

        final String actual = getContent();
        assertThatJson(json(actual)).isEqualTo(json(expected));
    }
    private File initScreenshotDirectory() {
        if (SCREENSHOTS_DIR == null) {
            throw new IllegalStateException(
                    "Property org.kie.sw.editor.screenshots.dir (where screenshot taken by WebDriver will be put) was null");
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

    /**
     * Use this for loading serverless workflow definitions placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    private ExpectedCondition<WebElement> element(final String xpathLocator, final String... parameters) {
        return visibilityOfElementLocated(xpath(String.format(xpathLocator, parameters)));
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    }
}

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
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;

public class SWEditorSeleniumIT extends SWEditorSeleniumBase {

    protected static final String SCREENSHOTS_DIR = System.getProperty("org.kie.sw.editor.screenshots.dir");

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

    @Test
    public void testAutoLayoutMultipleConnectionsCPs() throws Exception {
        String resource = "EventBasedSwitchStateExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(8);

        // First connector
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 0);
        assertThat(points.size()).isEqualTo(10);
        assertThat(points.get(0)).isEqualTo(558.5348837209302D); // X
        assertThat(points.get(1)).isEqualTo(319L); // Y
        assertThat(points.get(2)).isEqualTo(558.5348837209302D); // X
        assertThat(points.get(3)).isEqualTo(329L); // Y
        assertThat(points.get(4)).isEqualTo(558.5348837209302D); // X
        assertThat(points.get(5)).isEqualTo(349.1033464566929D); // Y
        assertThat(points.get(6)).isEqualTo(783L); // X
        assertThat(points.get(7)).isEqualTo(349.1033464566929D); // Y
        assertThat(points.get(8)).isEqualTo(783L); // X
        assertThat(points.get(9)).isEqualTo(434L); // Y

        // Second connector
        List<Object> points2 = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 1);
        assertThat(points2.size()).isEqualTo(10);
        assertThat(points2.get(0)).isEqualTo(399.4651162790698D); // X
        assertThat(points2.get(1)).isEqualTo(319L); // Y
        assertThat(points2.get(2)).isEqualTo(399.4651162790698D); // X
        assertThat(points2.get(3)).isEqualTo(329L); // Y
        assertThat(points2.get(4)).isEqualTo(399.4651162790698D); // X
        assertThat(points2.get(5)).isEqualTo(349.1033464566929D); // Y
        assertThat(points2.get(6)).isEqualTo(175L); // X
        assertThat(points2.get(7)).isEqualTo(349.1033464566929D); // Y
        assertThat(points2.get(8)).isEqualTo(175L); // X
        assertThat(points2.get(9)).isEqualTo(434L); // Y

        // Third connector
        List<Object> points3 = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 2);
        assertThat(points3.size()).isEqualTo(8);
        assertThat(points3.get(0)).isEqualTo(479L); // X
        assertThat(points3.get(1)).isEqualTo(319L); // Y
        assertThat(points3.get(2)).isEqualTo(479L); // X
        assertThat(points3.get(3)).isEqualTo(329L); // Y
        assertThat(points3.get(4)).isEqualTo(479L); // X
        assertThat(points3.get(5)).isEqualTo(392.3333333333333D); // Y
        assertThat(points3.get(6)).isEqualTo(479L); // X
        assertThat(points3.get(7)).isEqualTo(434L); // Y
    }

    @Test
    public void testAutoLayoutWithSameSourceAndTargetConnectionCPs() throws Exception {
        String resource = "ApplicantRequestDecisionExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(6);

        // First connector
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 0);
        assertThat(points.size()).isEqualTo(10);
        assertThat(points.get(0)).isEqualTo(366.7674418604651D); // X
        assertThat(points.get(1)).isEqualTo(319L); // Y
        assertThat(points.get(2)).isEqualTo(366.7674418604651D); // X
        assertThat(points.get(3)).isEqualTo(329L); // Y
        assertThat(points.get(4)).isEqualTo(366.7674418604651D); // X
        assertThat(points.get(5)).isEqualTo(369.20669291338584D); // Y
        assertThat(points.get(6)).isEqualTo(479L); // X
        assertThat(points.get(7)).isEqualTo(369.20669291338584D); // Y
        assertThat(points.get(8)).isEqualTo(479L); // X
        assertThat(points.get(9)).isEqualTo(434L); // Y

        // Second connector
        List<Object> points2 = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 1);
        assertThat(points2.size()).isEqualTo(10);
        assertThat(points2.get(0)).isEqualTo(287.2325581395349D); // X
        assertThat(points2.get(1)).isEqualTo(319L); // Y
        assertThat(points2.get(2)).isEqualTo(287.2325581395349D); // X
        assertThat(points2.get(3)).isEqualTo(329L); // Y
        assertThat(points2.get(4)).isEqualTo(287.2325581395349D); // X
        assertThat(points2.get(5)).isEqualTo(369.20669291338584D); // Y
        assertThat(points2.get(6)).isEqualTo(175L); // X
        assertThat(points2.get(7)).isEqualTo(369.20669291338584D); // Y
        assertThat(points2.get(8)).isEqualTo(175L); // X
        assertThat(points2.get(9)).isEqualTo(434L); // Y

        // Third connector
        List<Object> points3 = jsHelper.getConnectorPoints(nodeIds.get(1), 0, 2);
        assertThat(points3.size()).isEqualTo(10);
        assertThat(points3.get(0)).isEqualTo(358.52073732718895D); // X
        assertThat(points3.get(1)).isEqualTo(319L); // Y
        assertThat(points3.get(2)).isEqualTo(358.52073732718895D); // X
        assertThat(points3.get(3)).isEqualTo(329L); // Y
        assertThat(points3.get(4)).isEqualTo(358.52073732718895D); // X
        assertThat(points3.get(5)).isEqualTo(379.7258858267717D); // Y
        assertThat(points3.get(6)).isEqualTo(447.47926267281105D); // X
        assertThat(points3.get(7)).isEqualTo(379.7258858267717D); // Y
        assertThat(points3.get(8)).isEqualTo(447.47926267281105D); // X
        assertThat(points3.get(9)).isEqualTo(434L); // Y
    }

    @Test
    public void testAutoLayoutLongBackwardConnectionCPs() throws Exception {
        String resource = "BookLendingExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(13);

        // Long backward connector
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(10), 3, 0);
        assertThat(points.size()).isEqualTo(14);
        assertThat(points.get(0)).isEqualTo(504L); // X
        assertThat(points.get(1)).isEqualTo(1621L); // Y
        assertThat(points.get(2)).isEqualTo(504L); // X
        assertThat(points.get(3)).isEqualTo(1631L); // Y
        assertThat(points.get(4)).isEqualTo(962L); // X
        assertThat(points.get(5)).isEqualTo(1631L); // Y
        assertThat(points.get(6)).isEqualTo(962L); // X
        assertThat(points.get(7)).isEqualTo(1491L); // Y
        assertThat(points.get(8)).isEqualTo(962L); // X
        assertThat(points.get(9)).isEqualTo(578L); // Y
        assertThat(points.get(10)).isEqualTo(962L); // X
        assertThat(points.get(11)).isEqualTo(514.7445414847161D); // Y
        assertThat(points.get(12)).isEqualTo(641L); // X
        assertThat(points.get(13)).isEqualTo(514.7445414847161D); // Y
    }

    @Test
    public void testAutoLayoutBackwardConnectionLayerAboveCPs() throws Exception {
        String resource = "CarVitalsCheckExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        assertThat(nodeIds.size()).isEqualTo(5);

        // Backward connector
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(3), 4, 0);
        assertThat(points.size()).isEqualTo(10);
        assertThat(points.get(0)).isEqualTo(50L); // X
        assertThat(points.get(1)).isEqualTo(708L); // Y
        assertThat(points.get(2)).isEqualTo(40L); // X
        assertThat(points.get(3)).isEqualTo(708L); // Y
        assertThat(points.get(4)).isEqualTo(40L); // X
        assertThat(points.get(5)).isEqualTo(608L); // Y
        assertThat(points.get(6)).isEqualTo(149.07834101382488D); // X
        assertThat(points.get(7)).isEqualTo(608L); // Y
        assertThat(points.get(8)).isEqualTo(149.07834101382488D); // X
        assertThat(points.get(9)).isEqualTo(548L); // Y
    }

    private void testExample(String exampleName) throws Exception {
        final String expected = loadResource(exampleName);
        setContent(expected);

        waitCanvasPanel();

        final String actual = getContent();
        assertThatJson(json(actual)).isEqualTo(json(expected));
    }

    protected final File screenshotDirectory = initScreenshotDirectory();

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
}

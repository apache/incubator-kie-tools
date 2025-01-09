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


package org.kie.workbench.common.stunner.sw.client.selenium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
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
    public void testStateExecTimeoutExample() throws Exception {
        testExample("StateExecTimeoutExample.sw.json");
    }

    @Test
    public void testAutoLayoutMultipleConnectionsCPs() throws Exception {
        String resource = "EventBasedSwitchStateExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(8);

        // First connector
        // Pick switch state then get points for the first connector connected to the center magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 0);
        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(5);
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
        // Pick switch state then get points for the second connector connected to the center magnet
        List<Object> points2 = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 1);
        // Number of points in the line
        assertThat(points2.size() / 2).isEqualTo(5);
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
        // Pick switch state then get points for the third connector connected to the center magnet
        List<Object> points3 = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 2);
        // Number of points in the line
        assertThat(points3.size() / 2).isEqualTo(4);
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
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(6);

        // First connector
        // Pick state with double connections to the same target
        // then get points for the first connector connected to the center magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 0);
        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(5);
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
        // Pick state with double connections to the same target
        // then get points for the second connector connected to the center magnet
        List<Object> points2 = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 1);
        // Number of points in the line
        assertThat(points2.size() / 2).isEqualTo(5);
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
        // Pick state with double connections to the same target
        // then get points for the third connector connected to the center magnet
        List<Object> points3 = jsHelper.getConnectorPoints(nodeIds.get(1), MagnetConnection.MAGNET_CENTER, 2);
        // Number of points in the line
        assertThat(points3.size() / 2).isEqualTo(5);
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
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(13);

        // Long backward connector
        // Pick timeout state then get points for the backward connector connected to the right magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(10), MagnetConnection.MAGNET_CENTER, 1);
        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(9);
        assertThat(points.get(0)).isEqualTo(629L); // X
        assertThat(points.get(1)).isEqualTo(1541.8842794759826D); // Y
        assertThat(points.get(2)).isEqualTo(639L); // X
        assertThat(points.get(3)).isEqualTo(1541.8842794759826D); // Y
        assertThat(points.get(4)).isEqualTo(962L); // X
        assertThat(points.get(5)).isEqualTo(1541.8842794759826D); // Y
        assertThat(points.get(6)).isEqualTo(962L); // X
        assertThat(points.get(7)).isEqualTo(1451L); // Y
        assertThat(points.get(8)).isEqualTo(962L); // X
        assertThat(points.get(9)).isEqualTo(1122L); // Y
        assertThat(points.get(10)).isEqualTo(962L); // X
        assertThat(points.get(11)).isEqualTo(905L); // Y
        assertThat(points.get(12)).isEqualTo(962L); // X
        assertThat(points.get(13)).isEqualTo(578L); // Y
        assertThat(points.get(14)).isEqualTo(962L); // X
        assertThat(points.get(15)).isEqualTo(514.7445414847161D); // Y
        assertThat(points.get(16)).isEqualTo(641L); // X
        assertThat(points.get(17)).isEqualTo(514.7445414847161D); // Y
    }

    @Test
    public void testAutoLayoutBackwardConnectionLayerAboveCPs() throws Exception {
        String resource = "CarVitalsCheckExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(5);

        // Backward connector
        // Pick switch state then get points for the backward connector connected to the left magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(3), MagnetConnection.MAGNET_LEFT, 0);
        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(5);
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

    @Test
    // Line shall not overlap the operation state in the way up towards the target
    public void testAutoLayoutShortBackwardConnectionCPs() throws Exception {
        String resource = "JobMonitoringExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(9);

        // Backward connector
        // Pick switch state then get points for the backward connector connected to the right magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(4), MagnetConnection.MAGNET_CENTER, 2);
        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(7);
        assertThat(points.get(0)).isEqualTo(382.44D); // X
        assertThat(points.get(1)).isEqualTo(880L); // Y
        assertThat(points.get(2)).isEqualTo(382.44D); // X
        assertThat(points.get(3)).isEqualTo(870L); // Y
        assertThat(points.get(4)).isEqualTo(382.44D); // X
        assertThat(points.get(5)).isEqualTo(800L); // Y
        assertThat(points.get(6)).isEqualTo(481L); // X
        assertThat(points.get(7)).isEqualTo(800L); // Y
        assertThat(points.get(8)).isEqualTo(481L); // X
        assertThat(points.get(9)).isEqualTo(638.6666666666666D); // Y
        assertThat(points.get(10)).isEqualTo(349.4271844660194D); // X
        assertThat(points.get(11)).isEqualTo(638.6666666666666D); // Y
        assertThat(points.get(12)).isEqualTo(349.4271844660194D); // X
        assertThat(points.get(13)).isEqualTo(548L); // Y
    }

    @Test
    // Line shall not overlap the state beside in the way up towards the target
    public void testAutoLayoutBackwardConnectionFromTopCPs() throws Exception {
        String resource = "LoanBroker.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(14);

        // Backward connector
        // Pick operation state then get points for the backward connector connected to the center magnet
        List<Object> points = jsHelper.getConnectorPoints(nodeIds.get(12), MagnetConnection.MAGNET_CENTER, 0);

        // Number of points in the line
        assertThat(points.size() / 2).isEqualTo(7);
        assertThat(points.get(0)).isEqualTo(758L); // X
        assertThat(points.get(1)).isEqualTo(753L); // Y
        assertThat(points.get(2)).isEqualTo(758L); // X
        assertThat(points.get(3)).isEqualTo(763L); // Y
        assertThat(points.get(4)).isEqualTo(758L); // X
        assertThat(points.get(5)).isEqualTo(815L); // Y
        assertThat(points.get(6)).isEqualTo(1064L); // X
        assertThat(points.get(7)).isEqualTo(815L); // Y
        assertThat(points.get(8)).isEqualTo(1064L); // X
        assertThat(points.get(9)).isEqualTo(988.3333333333334D); // Y
        assertThat(points.get(10)).isEqualTo(1083.834862385321D); // X
        assertThat(points.get(11)).isEqualTo(988.3333333333334D); // Y
        assertThat(points.get(12)).isEqualTo(1083.834862385321D); // X
        assertThat(points.get(13)).isEqualTo(1085L); // Y
    }

    @Test
    public void testStateDimension() throws Exception {
        String resource = "LoanBroker.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();

        List<Long> dimension = new ArrayList<>();
        dimension.add(new Long(250));
        dimension.add(new Long(90));

        assertThat(jsHelper.getDimension(nodeIds.get(1))).isEqualTo(dimension);
    }

    @Test
    public void testPreserveColorAfterStateChange() throws Exception {
        String resource = "LoanBroker.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        String nodeId = nodeIds.get(1);
        jsHelper.setBackgroundColor(nodeId, "red");
        assertThat(jsHelper.getGetBackgroundColor(nodeId)).isEqualTo("red");
        jsHelper.draw();

        jsHelper.applyState(nodeId, "selected");
        assertThat(jsHelper.getGetBackgroundColor(nodeId)).isEqualTo("#E7F1FA");
        jsHelper.applyState(nodeId, "none");

        assertThat(jsHelper.getGetBackgroundColor(nodeId)).isEqualTo("red");
    }

    @Test
    // End nodes defined as objects in transitions must be shown
    public void testEndNodeCreationFomObject() throws Exception {
        String resource = "NotifyCustomerWorkflowExample.sw.json";
        final String expected = loadResource(resource);
        setContent(expected);
        waitCanvasPanel();

        List<String> nodeIds = jsHelper.getNodeIds();
        // Number of states in the diagram
        assertThat(nodeIds.size()).isEqualTo(4);

        // Pick End state location
        List<Object> location = jsHelper.getLocation(nodeIds.get(3));

        assertThat(location.get(0)).isEqualTo(152L); // X
        assertThat(location.get(1)).isEqualTo(663L); // Y
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

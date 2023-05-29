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

package org.kie.workbench.common.stunner.sw.client.selenium.yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.utils.Pair;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.impl.Yaml;
import org.kie.workbench.common.stunner.sw.client.selenium.SWEditorSeleniumBase;
import org.openqa.selenium.JavascriptExecutor;

import static org.junit.Assert.assertTrue;

public class SWEditorYamlSeleniumIT extends SWEditorSeleniumBase {

    protected static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"SWDiagramEditor\").get().setContent(\"\", %s\")";

    @Test
    public void testAccumulateRoomReadingsExample() throws Exception {
        testExampleYaml("AccumulateRoomReadingsExample.sw.yaml");
    }

    @Test
    public void testApplicantRequestDecisionExample() throws Exception {
        testExampleYaml("ApplicantRequestDecisionExample.sw.yaml");
    }

    @Test
    public void testAsyncFunctionInvocationExample() throws Exception {
        testExampleYaml("AsyncFunctionInvocationExample.sw.yaml");
    }

    @Test
    public void testAsyncSubFlowInvocationExample() throws Exception {
        testExampleYaml("AsyncSubFlowInvocationExample.sw.yaml");
    }

    @Test
    public void testBookLendingExample() throws Exception {
        testExampleYaml("BookLendingExample.sw.yaml");
    }

    @Test
    public void testCarVitalsCheckExample() throws Exception {
        testExampleYaml("CarVitalsCheckExample.sw.yaml");
    }

    @Test
    public void testCheckInboxPeriodicallyExample() throws Exception {
        testExampleYaml("CheckInboxPeriodicallyExample.sw.yaml");
    }

    @Test
    public void testCustomerCreditCheckExample() throws Exception {
        testExampleYaml("CustomerCreditCheckExample.sw.yaml");
    }

    @Test
    public void testEventBasedGreetingExample() throws Exception {
        testExampleYaml("EventBasedGreetingExample.sw.yaml");
    }

    @Test
    public void testEventBasedServiceInvocationExample() throws Exception {
        testExampleYaml("EventBasedServiceInvocationExample.sw.yaml");
    }

    @Test
    public void testEventBasedSwitchStateExample() throws Exception {
        testExampleYaml("EventBasedSwitchStateExample.sw.yaml");
    }

    @Test
    public void testFillGlassOfWaterExample() throws Exception {
        testExampleYaml("FillGlassOfWaterExample.sw.yaml");
    }

    @Test
    public void testFinalizeCollegeApplicationExample() throws Exception {
        testExampleYaml("FinalizeCollegeApplicationExample.sw.yaml");
    }

    @Test
    public void testHandleCarAuctionBidExample() throws Exception {
        testExampleYaml("HandleCarAuctionBidExample.sw.yaml");
    }

    @Test
    public void testHelloWorldExample() throws Exception {
        testExampleYaml("HelloWorldExample.sw.yaml");
    }

    @Test
    public void testJobMonitoringExample() throws Exception {
        testExampleYaml("JobMonitoringExample.sw.yaml");
    }

    @Test
    public void testMonitorPatientVitalSignsExample() throws Exception {
        testExampleYaml("MonitorPatientVitalSignsExample.sw.yaml");
    }

    @Test
    public void testNewPatientOnboardingExample() throws Exception {
        testExampleYaml("NewPatientOnboardingExample.sw.yaml");
    }

    @Test
    public void testNotifyCustomerWorkflowExample() throws Exception {
        testExampleYaml("NotifyCustomerWorkflowExample.sw.yaml");
    }

    @Test
    public void testParallelExecutionExample() throws Exception {
        testExampleYaml("ParallelExecutionExample.sw.yaml");
    }
    @Test
    public void testProcessTransactionsExample() throws Exception {
        testExampleYaml("ProcessTransactionsExample.sw.yaml");
    }

    @Test
    public void testProvisionOrdersExample() throws Exception {
        testExampleYaml("ProvisionOrdersExample.sw.yaml");
    }

    @Test
    public void testPurchaseOrderDeadlineExample() throws Exception {
        testExampleYaml("PurchaseOrderDeadlineExample.sw.yaml");
    }

    @Test
    public void testSendCloudEventOnProvisionExample() throws Exception {
        testExampleYaml("SendCloudEventOnProvisionExample.sw.yaml");
    }

    @Test
    public void testSolveMathProblemsExample() throws Exception {
        testExampleYaml("SolveMathProblemsExample.sw.yaml");
    }

    private void testExampleYaml(String exampleName) throws Exception {
        final String expected = loadResourceYaml(exampleName);
        final String yaml = loadResourceEscapedYaml(exampleName);
        setContentYaml(yaml);
        waitCanvasPanel();
        final String actual = getContent();
        assertTrue(checkYamlAreEqual(actual, expected));
    }


    protected String loadResourceEscapedYaml(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .map(s -> s.replace("\"", "\\\""))
                .map(s ->  "\"" + s)
                .collect(Collectors.joining("\\n\" + "));
    }

    protected String loadResourceYaml(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    protected void setContentYaml(final String yaml) {
        try {
            String content = String.format(SET_CONTENT_TEMPLATE, yaml);
            ((JavascriptExecutor) driver).executeScript(content);
        } catch (Exception e) {
            LOG.error("Exception during JS execution. Ex: {}", e.getMessage());
        }
    }

    private boolean checkYamlAreEqual(String yaml1, String yaml2) {
        final YamlMapping mapping1 = Yaml.fromString(yaml1);
        final YamlMapping mapping2 = Yaml.fromString(yaml2);
        Pair<YamlNode, YamlNode> pair = new Pair<>(mapping1, mapping2);
        Queue<Pair<YamlNode, YamlNode>> queue = new LinkedList<>();
        queue.add(pair);

        while (!queue.isEmpty()) {
            Pair<YamlNode, YamlNode> current = queue.poll();
            if (current.key.type() != current.value.type()) {
                return false;
            }
            if (current.key.type() == NodeType.MAPPING) {
                if (current.key.asMapping().keys().size() != current.value.asMapping().keys().size()) {
                    return false;
                }

                for (String key : current.key.asMapping().keys()) {
                    if (current.value.asMapping().getNode(key) == null) {
                        return false;
                    }
                    queue.add(new Pair<>(current.key.asMapping().getNode(key), current.value.asMapping().getNode(key)));
                }
            } else if (current.key.type() == NodeType.SEQUENCE) {
                if (current.key.asSequence().size() != current.value.asSequence().size()) {
                    return false;
                }

                for (int i = 0; i < current.key.asSequence().size(); i++) {
                    queue.add(new Pair<>(current.key.asSequence().node(i), current.value.asSequence().node(i)));
                }
            } else if (current.key.type() == NodeType.SCALAR) {
                if (!current.key.asScalar().value().equals(current.value.asScalar().value())) {
                    return false;
                }
            }
        }
        return true;
    }
}

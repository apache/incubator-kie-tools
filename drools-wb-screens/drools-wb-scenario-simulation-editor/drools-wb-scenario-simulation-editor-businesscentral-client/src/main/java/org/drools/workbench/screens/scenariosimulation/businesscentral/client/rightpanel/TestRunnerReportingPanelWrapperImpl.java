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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestRunnerReportingPanelWrapper;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingPanel;

@Dependent
public class TestRunnerReportingPanelWrapperImpl implements TestRunnerReportingPanelWrapper {

    protected final TestRunnerReportingPanel testRunnerReportingPanel;

    @Inject
    public TestRunnerReportingPanelWrapperImpl(TestRunnerReportingPanel testRunnerReportingPanel) {
        this.testRunnerReportingPanel = testRunnerReportingPanel;
    }

    @Override
    public void reset() {
        testRunnerReportingPanel.reset();
    }

    @Override
    public void onTestRun(TestResultMessage testResultMessage) {
        testRunnerReportingPanel.onTestRun(testResultMessage);
    }

    @Override
    public IsWidget asWidget() {
        return testRunnerReportingPanel.asWidget();
    }
}

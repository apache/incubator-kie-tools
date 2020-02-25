/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.rightpanel;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestRunnerReportingPanelWrapper;
import org.guvnor.common.services.shared.test.TestResultMessage;

@Dependent
public class TestRunnerReportingPanelKogitoWrapperImpl implements TestRunnerReportingPanelWrapper {

    @Override
    public void reset() {
        GWT.log(this.toString() + " reset");
    }

    @Override
    public void onTestRun(TestResultMessage testResultMessage) {
        GWT.log(this.toString() + " onTestRun " + testResultMessage);
    }

    @Override
    public IsWidget asWidget() {
        GWT.log(this.toString() + " asWidget ");
        return null;
    }
}

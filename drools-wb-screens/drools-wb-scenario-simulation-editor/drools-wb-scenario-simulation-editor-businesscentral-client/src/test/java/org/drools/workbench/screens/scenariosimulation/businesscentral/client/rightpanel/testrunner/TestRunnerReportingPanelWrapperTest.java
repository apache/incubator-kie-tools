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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.testrunner;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingPanel;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestRunnerReportingPanelWrapperTest {

    @Mock
    private TestRunnerReportingPanel testRunnerReportingPanelMock;
    @Mock
    private TestResultMessage testResultMessageMock;
    @Mock
    private Widget widgetMock;

    private TestRunnerReportingPanelWrapper testRunnerReportingPanelWrapperSpy;

    @Before
    public void setup() {
        testRunnerReportingPanelWrapperSpy = new TestRunnerReportingPanelWrapper(testRunnerReportingPanelMock);
        when(testRunnerReportingPanelMock.asWidget()).thenReturn(widgetMock);
    }

    @Test
    public void reset() {
        testRunnerReportingPanelWrapperSpy.reset();
        verify(testRunnerReportingPanelMock, times(1)).reset();
    }

    @Test
    public void onTestRun() {
        testRunnerReportingPanelWrapperSpy.onTestRun(testResultMessageMock);
        verify(testRunnerReportingPanelMock, times(1)).onTestRun(eq(testResultMessageMock));
    }

    @Test
    public void asWidget() {
        IsWidget panel = testRunnerReportingPanelWrapperSpy.asWidget();
        assertTrue(panel instanceof FlowPanel);
        verify(testRunnerReportingPanelMock, times(1)).asWidget();
    }

}

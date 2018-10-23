/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.workbench.client.test;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.events.SystemMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestRunnerReportingScreenTest {

    private TestRunnerReportingView view;
    private TestRunnerReportingScreen screen;
    private Failure failure;

    @Captor
    ArgumentCaptor<ArrayList<SystemMessage>> captor;

    @Before
    public void setUp() throws Exception {
        view = mock(TestRunnerReportingView.class);
        screen = new TestRunnerReportingScreen(view);
        failure = mock(Failure.class);
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).setPresenter(screen);
    }

    @Test
    public void testSuccessfulRun() {
        TestResultMessage testResultMessage = new TestResultMessage("id",
                                                                    1,
                                                                    250,
                                                                    new ArrayList<>());
        screen.onTestRun(testResultMessage);
        verify(view).showSuccess();
        verify(view).setRunStatus(any(),
                                  eq("1"),
                                  eq("250 milliseconds"));
    }

    @Test
    public void testUnSuccessfulRun() {
        when(failure.getDisplayName()).thenReturn("Expected true but was false.");
        when(failure.getMessage()).thenReturn("This is a non-null message");
        TestResultMessage testResultMessage = new TestResultMessage("id",
                                                                    1,
                                                                    2500,
                                                                    Collections.singletonList(failure));
        screen.onTestRun(testResultMessage);
        verify(view).setSystemMessages(captor.capture());
        assertThat("Expected true but was false. : This is a non-null message").isEqualTo(captor.getValue().get(0).getText());
        verify(view).showFailure();
        verify(view).setRunStatus(any(),
                                  eq("1"),
                                  eq("2 seconds and 500 milliseconds"));
    }

    @Test
    public void testRunTimeInMinutes() {
        TestResultMessage testResultMessage = new TestResultMessage("id",
                                                                    150,
                                                                    125000,
                                                                    new ArrayList<>());
        screen.onTestRun(testResultMessage);
        verify(view).showSuccess();
        verify(view).setRunStatus(any(),
                                  eq("150"),
                                  eq("2 minutes and 5 seconds"));
    }

}
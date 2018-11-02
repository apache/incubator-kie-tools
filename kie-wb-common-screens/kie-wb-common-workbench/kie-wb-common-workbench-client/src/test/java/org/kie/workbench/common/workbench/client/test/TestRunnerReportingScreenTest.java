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
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestRunnerReportingScreenTest {

    @Captor
    private ArgumentCaptor<PublishBatchMessagesEvent> publishEventCaptor;
    @Mock
    private TestRunnerReportingView view;
    @Mock
    private Failure failure;

    @Mock
    private EventSourceMock event;

    private TestRunnerReportingScreen screen;

    @Before
    public void setUp() throws Exception {
        screen = new TestRunnerReportingScreen(view,
                                               event);
    }

    @Test
    public void onViewAlerts() {

        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();

        assertTrue(value.isCleanExisting());
        assertTrue(value.getMessagesToPublish().isEmpty());
    }

    @Test
    public void onViewAlertsSendMessage() {

        TestResultMessage testResultMessage = new TestResultMessage("id",
                                                                    1,
                                                                    2500,
                                                                    Collections.singletonList(failure));
        screen.onTestRun(testResultMessage);
        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();

        assertFalse(value.getMessagesToPublish().isEmpty());
    }

    @Test
    public void onViewAlerts_resetBetweenRuns() {

        screen.onTestRun(new TestResultMessage("id",
                                               1,
                                               2500,
                                               Collections.singletonList(failure)));

        screen.onTestRun(new TestResultMessage("id",
                                               1,
                                               2500,
                                               new ArrayList<>()));
        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();

        assertTrue(value.getMessagesToPublish().isEmpty());
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
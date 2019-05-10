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
package org.kie.workbench.common.workbench.client.test;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestRunnerReportingPanelTest {

    @Captor
    private ArgumentCaptor<PublishBatchMessagesEvent> publishEventCaptor;
    @Mock
    private TestRunnerReportingView view;
    @Mock
    private Failure failure;
    @Mock
    private EventSourceMock event;

    private TestRunnerReportingPanel screen;

    @Before
    public void setUp() {
        screen = new TestRunnerReportingPanel(view, event);
    }

    @Test
    public void testEventGoesThroughCorrectly() {

        when(failure.getDisplayName()).thenReturn("Display name.");
        when(failure.getMessage()).thenReturn("Message");
        final Path path = mock(Path.class);
        when(failure.getPath()).thenReturn(path);

        screen.onTestRun(createTRMWithFailures());

        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();
        assertTrue(value.isCleanExisting());
        assertEquals(1, value.getMessagesToPublish().size());
        final SystemMessage systemMessage = value.getMessagesToPublish().get(0);
        assertEquals("TestResults", systemMessage.getMessageType());
        assertEquals(Level.ERROR, systemMessage.getLevel());
        assertEquals(path, systemMessage.getPath());
        assertEquals("Display name. : Message", systemMessage.getText());
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
        screen.onTestRun(createTRMWithFailures());
        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();
        assertFalse(value.getMessagesToPublish().isEmpty());
    }

    @Test
    public void onViewAlerts_resetBetweenRuns() {
        screen.onTestRun(createTRMWithFailures());
        screen.onTestRun(createTRMWithoutFailures(2500));
        screen.onViewAlerts();

        verify(event).fire(publishEventCaptor.capture());

        final PublishBatchMessagesEvent value = publishEventCaptor.getValue();
        assertTrue(value.getMessagesToPublish().isEmpty());
    }

    @Test
    public void testSetPresenter() {
        verify(view).setPresenter(screen);
    }

    @Test
    public void initFailureDiagram() {
        verify(view).resetDonut();
    }

    @Test
    public void testSuccessfulRun() {
        screen.onTestRun(createTRMWithoutFailures(250));
        verify(view).showSuccess();
        verify(view).setRunStatus(any(),
                                  eq("1"),
                                  eq("250 milliseconds"));
    }

    @Test
    public void testUnSuccessfulRun() {
        reset(view);

        when(failure.getDisplayName()).thenReturn("Expected true but was false.");
        when(failure.getMessage()).thenReturn("This is a non-null message");

        screen.onTestRun(createTRMWithFailures());
        verify(view).showFailure();
        verify(view).showSuccessFailureDiagram(0,
                                               1);
        verify(view).setRunStatus(any(),
                                  eq("1"),
                                  eq("2 seconds and 500 milliseconds"));
    }

    @Test
    public void testRunTimeInMinutes() {
        reset(view);

        screen.onTestRun(createTRMWithoutFailures(125000));
        verify(view).showSuccess();
        verify(view).showSuccessFailureDiagram(1,
                                               0);
        verify(view).setRunStatus(any(),
                                  eq("1"),
                                  eq("2 minutes and 5 seconds"));
    }

    @Test
    public void testMillisecondsFormatting() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(screen.formatMilliseconds("0")).isEqualTo("0");
            softly.assertThat(screen.formatMilliseconds("00")).isEqualTo("0");
            softly.assertThat(screen.formatMilliseconds("110")).isEqualTo("110");
            softly.assertThat(screen.formatMilliseconds("0101")).isEqualTo("101");
            softly.assertThat(screen.formatMilliseconds("000110")).isEqualTo("110");
        });
    }

    private TestResultMessage createTRMWithoutFailures(long runtime) {
        return new TestResultMessage("id", 1, runtime, new ArrayList<>());
    }

    private TestResultMessage createTRMWithFailures() {
        return new TestResultMessage("id", 1, 2500, Collections.singletonList(failure));
    }
}
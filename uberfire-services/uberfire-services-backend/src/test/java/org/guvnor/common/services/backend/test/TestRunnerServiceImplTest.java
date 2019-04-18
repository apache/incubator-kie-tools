/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.backend.test;

import java.util.ArrayList;
import java.util.Collections;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestRunnerServiceImplTest {

    @Mock
    private TestService testService1;

    @Mock
    private TestService testService2;

    @Mock
    private Event<TestResultMessage> defaultTestResultMessageEvent;

    @Captor
    private ArgumentCaptor<TestResultMessage> testResultMessageArgumentCaptor;

    private TestRunnerServiceImpl testRunnerService;

    @Before
    public void setUp() throws Exception {
        final Instance services = mock(Instance.class);

        final ArrayList<Object> list = new ArrayList<>();

        list.add(testService1);
        list.add(testService2);
        doReturn(list.iterator()).when(services).iterator();

        testRunnerService = new TestRunnerServiceImpl(services,
                                                      defaultTestResultMessageEvent);
    }

    @Test
    public void runAllTests() throws Exception {

        setUpTestService(testService1, 1, 200, 1);
        setUpTestService(testService2, 2, 300, 2);

        final Path path = mock(Path.class);
        testRunnerService.runAllTests("id", path);

        verify(defaultTestResultMessageEvent, only()).fire(testResultMessageArgumentCaptor.capture());

        final TestResultMessage testResultMessage = testResultMessageArgumentCaptor.getValue();
        assertEquals("id", testResultMessage.getIdentifier());
        assertEquals(3, testResultMessage.getRunCount());
        assertEquals(500, testResultMessage.getRunTime());
        assertEquals(3, testResultMessage.getFailures().size());

        verify(testService1).runAllTests(eq("id"), eq(path));
        verify(testService2).runAllTests(eq("id"), eq(path));
    }

    @Test
    public void runAllTestsCustomTestResultEvent() throws Exception {
        setUpTestService(testService1, 2, 500, 1);
        setUpTestService(testService2, 2, 300, 0);

        final Path path = mock(Path.class);
        final Event event = mock(Event.class);
        testRunnerService.runAllTests("id", path, event);

        verify(event).fire(testResultMessageArgumentCaptor.capture());

        final TestResultMessage testResultMessage = testResultMessageArgumentCaptor.getValue();
        assertEquals("id", testResultMessage.getIdentifier());
        assertEquals(4, testResultMessage.getRunCount());
        assertEquals(800, testResultMessage.getRunTime());
        assertEquals(1, testResultMessage.getFailures().size());

        verify(testService1).runAllTests(eq("id"), eq(path));
        verify(testService2).runAllTests(eq("id"), eq(path));
    }

    private void setUpTestService(final TestService testService,
                                  final int runCount,
                                  final int runTime,
                                  final int failureCount) {
        doAnswer(invocationOnMock -> {

            final ArrayList<Failure> failures = new ArrayList<>();

            for (int i = 0; i < failureCount; i++) {
                failures.add(new Failure());
            }

            return Collections.singletonList(new TestResultMessage((String) invocationOnMock.getArguments()[0],
                                                                   runCount,
                                                                   runTime,
                                                                   failures));
        }).when(testService).runAllTests(anyString(), any());
    }
}
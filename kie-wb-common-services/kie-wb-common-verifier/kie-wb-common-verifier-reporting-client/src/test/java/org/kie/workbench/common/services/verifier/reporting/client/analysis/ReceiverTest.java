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
package org.kie.workbench.common.services.verifier.reporting.client.analysis;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.IllegalVerifierStateIssue;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Issues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ReceiverTest {

    @Mock
    private Worker worker;
    @Captor
    private ArgumentCaptor<MessageHandler> messageHandlerArgumentCaptor;
    @Captor
    private ArgumentCaptor<Set> setArgumentCaptor;
    @Mock
    private AnalysisReporter reporter;
    private Receiver receiver;

    private Object returnObject;

    @Before
    public void setUp() {

        receiver = new Receiver(reporter) {
            @Override
            public Object fromJSON(final String json) {
                return returnObject;
            }
        };

        receiver.setUp(worker);
        verify(worker).setOnMessage(messageHandlerArgumentCaptor.capture());
    }

    @Test
    public void status() {
        returnObject = new Status();

        messageHandlerArgumentCaptor.getValue().onMessage(mock(MessageEvent.class));

        verify(reporter).sendStatus((Status) returnObject);
    }

    @Test
    public void issues() {
        returnObject = new Issues("id",
                                  new HashSet<>());

        messageHandlerArgumentCaptor.getValue().onMessage(mock(MessageEvent.class));

        verify(reporter).sendReport(anySet());
    }

    @Test
    public void webWorkerException() {
        returnObject = new WebWorkerException("error");

        messageHandlerArgumentCaptor.getValue().onMessage(mock(MessageEvent.class));

        verify(reporter, never()).sendStatus(any());
        verify(reporter).sendReport(setArgumentCaptor.capture());
        verify(worker).terminate();

        final Set<Issue> issues = setArgumentCaptor.getValue();
        assertEquals(1, issues.size());
        assertTrue(issues.iterator().next() instanceof IllegalVerifierStateIssue);
    }
}
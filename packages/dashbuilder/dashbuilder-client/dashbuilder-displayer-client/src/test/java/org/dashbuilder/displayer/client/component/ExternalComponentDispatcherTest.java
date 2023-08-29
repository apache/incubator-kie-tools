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


package org.dashbuilder.displayer.client.component;

import java.util.HashSet;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.dashbuilder.displayer.external.ExternalComponentMessageType;
import org.dashbuilder.displayer.external.ExternalFilterRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Optional.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalComponentDispatcherTest {

    private static final String DEST1 = "DEST1";

    @Mock
    ExternalComponentMessageHelper messageHelper;

    @Mock
    ExternalComponentListener listener;

    @InjectMocks
    ExternalComponentDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher.listeners = new HashSet<>();
        when(listener.getId()).thenReturn(DEST1);
        dispatcher.register(listener);
    }

    @Test
    public void testFilterWithoutFilterRequest() {
        ExternalComponentMessage message = filterMessage();
        withId(message, Optional.of(DEST1));

        dispatcher.onMessage(message);

        verify(listener, times(0)).onFilter(any());
    }

    @Test
    public void testFilterWithoutDestination() {
        ExternalComponentMessage message = filterMessage();
        noId(message);

        dispatcher.onMessage(message);

        verify(listener, times(0)).onFilter(any());
    }

    @Test
    public void testFilter() {
        ExternalFilterRequest filterRequest = mock(ExternalFilterRequest.class);
        Optional<ExternalFilterRequest> filterRequestOp = Optional.of(filterRequest);
        ExternalComponentMessage message = filterMessage(filterRequestOp);

        withId(message, Optional.of(DEST1));

        dispatcher.onMessage(message);

        verify(listener).onFilter(eq(filterRequest));
    }

    @Test
    public void testFixConfigurationWithoutMessage() {
        ExternalComponentMessage configurationIssueMsg = mock(ExternalComponentMessage.class);
        withId(configurationIssueMsg, Optional.of(DEST1));

        when(messageHelper.messageType(eq(configurationIssueMsg))).thenReturn(ExternalComponentMessageType.FIX_CONFIGURATION);
        when(messageHelper.getConfigurationIssue(eq(configurationIssueMsg))).thenReturn(empty());

        dispatcher.onMessage(configurationIssueMsg);

        verify(listener).onConfigurationIssue(CommonConstants.INSTANCE.componentConfigDefaultMessage());
    }

    @Test
    public void testFixConfigurationWithMessage() {
        ExternalComponentMessage configurationIssueMsg = mock(ExternalComponentMessage.class);
        String configurationMessage = "config error";

        withId(configurationIssueMsg, Optional.of(DEST1));
        when(messageHelper.messageType(eq(configurationIssueMsg))).thenReturn(ExternalComponentMessageType.FIX_CONFIGURATION);
        when(messageHelper.getConfigurationIssue(eq(configurationIssueMsg))).thenReturn(Optional.of(configurationMessage));

        dispatcher.onMessage(configurationIssueMsg);

        verify(listener).onConfigurationIssue(configurationMessage);
    }

    @Test
    public void testOkConfigurationWithMessage() {
        ExternalComponentMessage configurationOkMsg = mock(ExternalComponentMessage.class);

        withId(configurationOkMsg, Optional.of(DEST1));
        when(messageHelper.messageType(eq(configurationOkMsg))).thenReturn(ExternalComponentMessageType.CONFIGURATION_OK);

        dispatcher.onMessage(configurationOkMsg);

        verify(listener).configurationOk();
    }

    private void noId(ExternalComponentMessage message) {
        withId(message, empty());
    }

    private ExternalComponentMessage filterMessage() {
        return filterMessage(empty());
    }

    private void withId(ExternalComponentMessage message, Optional<String> id) {
        when(messageHelper.getComponentId(eq(message))).thenReturn(id);
    }

    private ExternalComponentMessage filterMessage(Optional<ExternalFilterRequest> filterRequestOp) {
        ExternalComponentMessage message = Mockito.mock(ExternalComponentMessage.class);
        when(messageHelper.messageType(eq(message))).thenReturn(ExternalComponentMessageType.FILTER);
        when(messageHelper.filterRequest(eq(message))).thenReturn(filterRequestOp);
        return message;
    }

}
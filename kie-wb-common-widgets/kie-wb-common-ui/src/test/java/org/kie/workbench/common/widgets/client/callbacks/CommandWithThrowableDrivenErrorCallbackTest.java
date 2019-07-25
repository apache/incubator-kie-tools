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

package org.kie.workbench.common.widgets.client.callbacks;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommandWithThrowableDrivenErrorCallbackTest {

    @Mock
    private Message message;

    @Mock
    private HasBusyIndicator hasBusyIndicator;

    @Mock
    private CommandWithThrowableDrivenErrorCallback.CommandWithThrowable runtimeException;

    @Mock
    private CommandWithThrowableDrivenErrorCallback.CommandWithThrowable nullPointerException;

    @Mock
    private CommandWithThrowableDrivenErrorCallback.CommandWithThrowable defaultCommand;

    private Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> commands = new HashMap<>();

    @Before
    public void init() throws Exception {
        commands.put(RuntimeException.class, runtimeException);
        commands.put(NullPointerException.class, nullPointerException);
    }

    @Test
    public void testCallbackWithDefaultCommand() {
        CommandWithThrowableDrivenErrorCallback callback = new CommandWithThrowableDrivenErrorCallback(hasBusyIndicator, commands, defaultCommand);

        callback.error(message, new RuntimeException());

        verify(runtimeException).execute(any());
        verify(nullPointerException, never()).execute(any());
        verify(defaultCommand, never()).execute(any());
        verify(hasBusyIndicator, never()).hideBusyIndicator();

        callback.error(message, new NullPointerException());

        verify(runtimeException).execute(any());
        verify(nullPointerException).execute(any());
        verify(defaultCommand, never()).execute(any());
        verify(hasBusyIndicator, never()).hideBusyIndicator();

        callback.error(message, new Exception());

        verify(runtimeException).execute(any());
        verify(nullPointerException).execute(any());
        verify(defaultCommand).execute(any());
        verify(hasBusyIndicator, never()).hideBusyIndicator();
    }

}

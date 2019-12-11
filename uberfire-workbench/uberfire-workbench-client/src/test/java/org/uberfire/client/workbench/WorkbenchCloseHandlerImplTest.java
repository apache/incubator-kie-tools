/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench;

import com.google.gwt.user.client.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchCloseHandlerImplTest {

    private WorkbenchCloseHandlerImpl workbenchCloseHandler;

    @Before
    public void setup() {
        workbenchCloseHandler = new WorkbenchCloseHandlerImpl();
    }

    @Test
    public void onWindowClosingWithoutEventTest() {
        final ParameterizedCommand<Window.ClosingEvent> command = mock(ParameterizedCommand.class);

        workbenchCloseHandler.onWindowClosing(command);

        verify(command).execute(null);
    }

    @Test
    public void onWindowClosingWithEventTest() {
        final ParameterizedCommand<Window.ClosingEvent> command = mock(ParameterizedCommand.class);
        final Window.ClosingEvent event = mock(Window.ClosingEvent.class);

        workbenchCloseHandler.onWindowClosing(command,
                                              event);

        verify(command).execute(event);
    }

    @Test
    public void onWindowCloseEventTest() {
        final Command command = mock(Command.class);

        workbenchCloseHandler.onWindowClose(command);

        verify(command).execute();
    }
}

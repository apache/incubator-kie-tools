/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.popup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmBoxTest {
    
    @Mock ConfirmBox.View view;
    private ConfirmBox presenter;

    @Before
    public void setup() {
        presenter = new ConfirmBox(view);
    }

    @Test
    public void testYesCommand() throws Exception {
        final String title = "title";
        final String message = "message";
        final Command yesCommand = mock(Command.class);
        presenter.show(title, message, yesCommand);
        verify(view, times(1)).show(title, message, yesCommand, presenter.emptyCommand, presenter.emptyCommand);
    }

    @Test
    public void testNoCancelCommands() throws Exception {
        final String title = "title";
        final String message = "message";
        final Command yesCommand = mock(Command.class);
        final Command noCancelCommand = mock(Command.class);
        presenter.show(title, message, yesCommand, noCancelCommand, noCancelCommand);
        verify(view, times(1)).show(title, message, yesCommand, noCancelCommand, noCancelCommand);
    }

    @Test
    public void testAllCommands() throws Exception {
        final String title = "title";
        final String message = "message";
        final Command yesCommand = mock(Command.class);
        final Command noCommand = mock(Command.class);
        final Command cancelCommand = mock(Command.class);
        presenter.show(title, message, yesCommand, noCommand, cancelCommand);
        verify(view, times(1)).show(title, message, yesCommand, noCommand, cancelCommand);
    }
    
}
